#!/bin/bash
# Деплой news-agent на Oracle Cloud VM.
# nginx проксіює /news-agent/ → localhost:8600 (порт 8600 не відкритий назовні).
# Telegram-бот: webhook у web.py, розсилка через broadcaster.py+systemd timer.
#
# Потребує: key.txt (Anthropic), telegram_token.txt (BotFather). Без них — не запуститься.
# Groq замість Claude: LLM_PROVIDER=groq ./deploy.sh  (потрібен groq_key.txt).
#
# Використання:
#   ./deploy.sh
#   LLM_PROVIDER=groq ./deploy.sh
set -e

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
KEY_FILE="$PROJECT_DIR/../key.txt"
TELEGRAM_TOKEN_FILE="$PROJECT_DIR/telegram_token.txt"
GROQ_KEY_FILE="$PROJECT_DIR/groq_key.txt"
LLM_PROVIDER="${LLM_PROVIDER:-anthropic}"

REMOTE="ubuntu@92.5.42.35"
REMOTE_DIR="/opt/news-agent"
SERVICE_NAME="news-agent"
BROADCAST_SERVICE_NAME="news-agent-broadcast"
PORT=8600
NGINX_CONF="/etc/nginx/sites-enabled/apps.conf"   # the file nginx actually loads on this box
URL_PATH="/news-agent/"
WEBHOOK_URL="https://cozy-planner.duckdns.org${URL_PATH}telegram-webhook"

# ── 1. Ship the code ────────────────────────────────────────────────────────
echo "📦 Syncing app files..."
ssh "$REMOTE" "sudo mkdir -p '$REMOTE_DIR' && sudo chown \$(whoami) '$REMOTE_DIR'"

# Не використовуємо --delete: subscribers.json / sent_state.json / history.json — стан сервера, не код.
# classification_cache.json очищається нижче, бо prompts могли змінитися.
rsync -avz \
  --exclude 'venv' --exclude '__pycache__' --exclude '*.pyc' \
  "$PROJECT_DIR/web.py" "$PROJECT_DIR/tg.py" "$PROJECT_DIR/telegram_bot.py" "$PROJECT_DIR/broadcaster.py" \
  "$PROJECT_DIR/cli.py" "$PROJECT_DIR/agent" \
  "$PROJECT_DIR/requirements.txt" "$PROJECT_DIR/templates" \
  "$REMOTE:$REMOTE_DIR/"

# Видаляємо застарілі файли і кеш класифікації (prompts змінилися → стара класифікація хибна).
ssh "$REMOTE" "rm -f '$REMOTE_DIR/news_agent.py' '$REMOTE_DIR/app.py' '$REMOTE_DIR/classification_cache.json' && find '$REMOTE_DIR' -name '__pycache__' -exec rm -rf {} + 2>/dev/null; true"

if [ -f "$KEY_FILE" ]; then
  rsync -avz "$KEY_FILE" "$REMOTE:$REMOTE_DIR/key.txt"
  echo "🔑 key.txt uploaded"
else
  echo "⚠️  No local key.txt found at $KEY_FILE — make sure ANTHROPIC_API_KEY"
  echo "    or a key.txt already exists on the server, or the app will fail to start."
fi

if [ -f "$TELEGRAM_TOKEN_FILE" ]; then
  rsync -avz "$TELEGRAM_TOKEN_FILE" "$REMOTE:$REMOTE_DIR/telegram_token.txt"
  echo "🔑 telegram_token.txt uploaded"
else
  echo "ℹ️  No local telegram_token.txt — Telegram subscribe feature will stay disabled."
fi

if [ -f "$GROQ_KEY_FILE" ]; then
  rsync -avz "$GROQ_KEY_FILE" "$REMOTE:$REMOTE_DIR/groq_key.txt"
  echo "🔑 groq_key.txt uploaded"
elif [ "$LLM_PROVIDER" = "groq" ]; then
  echo "⚠️  LLM_PROVIDER=groq but no local groq_key.txt found at $GROQ_KEY_FILE"
fi

# ── 2. Install deps in a venv on the server ─────────────────────────────────
echo "🐍 Installing dependencies on server..."
ssh "$REMOTE" bash -s <<EOF
set -e
cd "$REMOTE_DIR"
[ -d venv ] || python3 -m venv venv
venv/bin/pip install --quiet --upgrade pip
venv/bin/pip install --quiet -r requirements.txt
EOF

# ── 3. Create/refresh the web service (localhost-only) ─────────────────────
echo "⚙️  Configuring systemd service..."
ssh "$REMOTE" sudo tee "/etc/systemd/system/$SERVICE_NAME.service" > /dev/null <<EOF
[Unit]
Description=Ukraine News Agent
After=network.target

[Service]
Type=simple
User=ubuntu
WorkingDirectory=$REMOTE_DIR
Environment=HOST=127.0.0.1
Environment=PORT=$PORT
Environment=LLM_PROVIDER=$LLM_PROVIDER
ExecStart=$REMOTE_DIR/venv/bin/python $REMOTE_DIR/web.py
Restart=on-failure

[Install]
WantedBy=multi-user.target
EOF

ssh "$REMOTE" "sudo systemctl daemon-reload && sudo systemctl enable --now $SERVICE_NAME && sudo systemctl restart $SERVICE_NAME"

# ── 4. Create/refresh the broadcaster timer (pushes new items to Telegram) ──
echo "⏱️  Configuring broadcaster timer..."
ssh "$REMOTE" sudo tee "/etc/systemd/system/$BROADCAST_SERVICE_NAME.service" > /dev/null <<EOF
[Unit]
Description=Ukraine News Agent — Telegram broadcaster (one-shot)

[Service]
Type=oneshot
User=ubuntu
WorkingDirectory=$REMOTE_DIR
Environment=LLM_PROVIDER=$LLM_PROVIDER
ExecStart=$REMOTE_DIR/venv/bin/python $REMOTE_DIR/broadcaster.py
EOF

ssh "$REMOTE" sudo tee "/etc/systemd/system/$BROADCAST_SERVICE_NAME.timer" > /dev/null <<EOF
[Unit]
Description=Run the Telegram broadcaster every 15 minutes

[Timer]
OnBootSec=2min
OnUnitActiveSec=15min
Unit=$BROADCAST_SERVICE_NAME.service

[Install]
WantedBy=timers.target
EOF

ssh "$REMOTE" "sudo systemctl daemon-reload && sudo systemctl enable --now $BROADCAST_SERVICE_NAME.timer"

# ── 5. Add an nginx location for it, if not already there ──────────────────
echo "🌐 Wiring up nginx at $URL_PATH ..."
ssh "$REMOTE" bash -s <<EOF
set -e
if sudo grep -q "location $URL_PATH" "$NGINX_CONF"; then
  echo "  nginx location already present, skipping edit."
else
  sudo mkdir -p /etc/nginx/backups
  sudo cp "$NGINX_CONF" "/etc/nginx/backups/apps.conf.bak.\$(date +%s)"
  sudo python3 - <<'PYEOF'
import re
path = "$NGINX_CONF"
text = open(path).read()
block = '''      location = /news-agent {
          return 301 /news-agent/;
      }

      location /news-agent/ {
          proxy_pass http://127.0.0.1:$PORT/;
          proxy_set_header Host \$http_host;
          proxy_set_header X-Real-IP \$remote_addr;
          proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
          proxy_set_header X-Forwarded-Proto https;
      }

      location / {'''
new_text = text.replace("      location / {", block, 1)
assert new_text != text, "could not find insertion point in nginx config"
open(path, "w").write(new_text)
PYEOF
  sudo nginx -t
  sudo systemctl reload nginx
  echo "  nginx location added and reloaded."
fi
EOF

# ── 6. Point the Telegram bot's webhook at us ───────────────────────────────
if [ -f "$TELEGRAM_TOKEN_FILE" ]; then
  echo "🤖 Registering Telegram webhook..."
  TOKEN="$(tr -d '[:space:]' < "$TELEGRAM_TOKEN_FILE")"
  curl -s "https://api.telegram.org/bot$TOKEN/setWebhook" -d "url=$WEBHOOK_URL" > /dev/null
  echo "  webhook set to $WEBHOOK_URL"
fi

echo ""
echo "✅ Готово! (LLM_PROVIDER=$LLM_PROVIDER) https://cozy-planner.duckdns.org$URL_PATH"
