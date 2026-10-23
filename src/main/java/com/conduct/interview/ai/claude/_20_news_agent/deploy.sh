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
LLM_PROVIDER="${LLM_PROVIDER:-groq}"

REMOTE="ubuntu@92.5.42.35"
REMOTE_DIR="/opt/news-agent"
SERVICE_NAME="news-agent"
BROADCAST_SERVICE_NAME="news-agent-broadcast"
SHAHED_SERVICE_NAME="shahed-monitor"
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
  "$PROJECT_DIR/cli.py" "$PROJECT_DIR/fb_agent.py" "$PROJECT_DIR/agent" \
  "$PROJECT_DIR/requirements.txt" "$PROJECT_DIR/templates" \
  "$PROJECT_DIR/shahed_monitor.py" \
  "$PROJECT_DIR/shahed_poll.py" \
  "$REMOTE:$REMOTE_DIR/"

# shahed_config.py — містить секрети, копіюємо окремо якщо є
SHAHED_CONFIG="$PROJECT_DIR/shahed_config.py"
if [ -f "$SHAHED_CONFIG" ]; then
  rsync -avz "$SHAHED_CONFIG" "$REMOTE:$REMOTE_DIR/shahed_config.py"
  echo "🔑 shahed_config.py uploaded"
else
  echo "⚠️  No shahed_config.py found — shahed-monitor will not start."
fi

# Видаляємо застарілі файли і кеш класифікації (prompts змінилися → стара класифікація хибна).
ssh "$REMOTE" "rm -f '$REMOTE_DIR/news_agent.py' '$REMOTE_DIR/app.py' '$REMOTE_DIR/classification_cache.json' '$REMOTE_DIR/tech_cache.json' && find '$REMOTE_DIR' -name '__pycache__' -exec rm -rf {} + 2>/dev/null; true"

# product_info.txt у .gitignore — може не бути локально; тоді лишаємо серверну копію.
if [ -f "$PROJECT_DIR/product_info.txt" ]; then
  rsync -avz "$PROJECT_DIR/product_info.txt" "$REMOTE:$REMOTE_DIR/product_info.txt"
  echo "📄 product_info.txt uploaded"
else
  echo "ℹ️  No local product_info.txt — keeping the server copy."
fi

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
Description=Run the Telegram broadcaster every 30 minutes

[Timer]
OnBootSec=2min
OnUnitActiveSec=30min
Unit=$BROADCAST_SERVICE_NAME.service

[Install]
WantedBy=timers.target
EOF

ssh "$REMOTE" "sudo systemctl daemon-reload && sudo systemctl enable --now $BROADCAST_SERVICE_NAME.timer"

# ── 5. Shahed monitor service ────────────────────────────────────────────────
echo "🚨 Configuring shahed-monitor service..."
ssh "$REMOTE" sudo tee "/etc/systemd/system/$SHAHED_SERVICE_NAME.service" > /dev/null <<EOF
[Unit]
Description=Shahed Monitor — Lviv channel keyword alerts
After=network.target

[Service]
Type=simple
User=ubuntu
WorkingDirectory=$REMOTE_DIR
ExecStart=$REMOTE_DIR/venv/bin/python $REMOTE_DIR/shahed_monitor.py
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
EOF

ssh "$REMOTE" "sudo systemctl daemon-reload && sudo systemctl enable $SHAHED_SERVICE_NAME"
# Не стартуємо автоматично — потрібна ручна авторизація першого разу (shahed.session)
echo "⚠️  shahed-monitor НЕ запущено автоматично."
echo "   Зайди на сервер і виконай:"
echo "   ssh ubuntu@92.5.42.35"
echo "   cd /opt/news-agent && venv/bin/python shahed_monitor.py"
echo "   (введи номер телефону і код — створить shahed.session)"
echo "   Потім: sudo systemctl start shahed-monitor"

# ── shahed-poll (HTTP polling, без авторизації) ──────────────────────────────
echo "📡 Configuring shahed-poll service..."
ssh "$REMOTE" sudo tee "/etc/systemd/system/shahed-poll.service" > /dev/null <<EOF
[Unit]
Description=Shahed Poll — Lviv channel HTTP polling alerts
After=network.target

[Service]
Type=simple
User=ubuntu
WorkingDirectory=$REMOTE_DIR
ExecStart=$REMOTE_DIR/venv/bin/python $REMOTE_DIR/shahed_poll.py
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
EOF

echo "ℹ️  shahed-poll вимкнено — використовується окремий @LvyvDistrictAlertBot"

# ── shahed-bot (окремий бот з вибором зони) ──────────────────────────────────
echo "🤖 Syncing shahed_bot..."
ssh "$REMOTE" "mkdir -p $REMOTE_DIR/shahed_bot"
rsync -avz \
  --exclude '__pycache__' --exclude '*.pyc' \
  "$PROJECT_DIR/shahed_bot/bot.py" \
  "$PROJECT_DIR/shahed_bot/zones.py" \
  "$REMOTE:$REMOTE_DIR/shahed_bot/"

SHAHED_BOT_TOKEN="$PROJECT_DIR/shahed_bot/token.txt"
if [ -f "$SHAHED_BOT_TOKEN" ]; then
  rsync -avz "$SHAHED_BOT_TOKEN" "$REMOTE:$REMOTE_DIR/shahed_bot/token.txt"
  echo "🔑 shahed_bot/token.txt uploaded"
else
  echo "⚠️  Немає shahed_bot/token.txt — створи бота у @BotFather і поклади токен туди"
fi

SHAHED_BOT_GROQ="$PROJECT_DIR/shahed_bot/groq_key.txt"
if [ -f "$SHAHED_BOT_GROQ" ]; then
  rsync -avz "$SHAHED_BOT_GROQ" "$REMOTE:$REMOTE_DIR/shahed_bot/groq_key.txt"
  echo "🔑 shahed_bot/groq_key.txt uploaded"
fi

ssh "$REMOTE" sudo tee "/etc/systemd/system/shahed-bot.service" > /dev/null <<EOF
[Unit]
Description=Shahed Alert Bot — Lviv zones
After=network.target

[Service]
Type=simple
User=ubuntu
WorkingDirectory=$REMOTE_DIR/shahed_bot
ExecStart=$REMOTE_DIR/venv/bin/python $REMOTE_DIR/shahed_bot/bot.py
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
EOF

ssh "$REMOTE" "sudo systemctl daemon-reload && sudo systemctl enable shahed-bot"
if [ -f "$SHAHED_BOT_TOKEN" ]; then
  ssh "$REMOTE" "sudo systemctl restart shahed-bot"
  echo "✅ shahed-bot запущено!"
else
  echo "⚠️  shahed-bot не запущено — потрібен token.txt"
fi

# ── kyiv-bot ─────────────────────────────────────────────────────────────────
echo "🤖 Syncing kyiv_bot..."
ssh "$REMOTE" "mkdir -p $REMOTE_DIR/kyiv_bot"
rsync -avz \
  --exclude '__pycache__' --exclude '*.pyc' \
  "$PROJECT_DIR/kyiv_bot/bot.py" \
  "$PROJECT_DIR/kyiv_bot/zones.py" \
  "$REMOTE:$REMOTE_DIR/kyiv_bot/"

KYIV_BOT_TOKEN="$PROJECT_DIR/kyiv_bot/token.txt"
if [ -f "$KYIV_BOT_TOKEN" ]; then
  rsync -avz "$KYIV_BOT_TOKEN" "$REMOTE:$REMOTE_DIR/kyiv_bot/token.txt"
  echo "🔑 kyiv_bot/token.txt uploaded"
fi

KYIV_BOT_GROQ="$PROJECT_DIR/kyiv_bot/groq_key.txt"
if [ -f "$KYIV_BOT_GROQ" ]; then
  rsync -avz "$KYIV_BOT_GROQ" "$REMOTE:$REMOTE_DIR/kyiv_bot/groq_key.txt"
  echo "🔑 kyiv_bot/groq_key.txt uploaded"
fi

ssh "$REMOTE" sudo tee "/etc/systemd/system/kyiv-bot.service" > /dev/null <<EOF
[Unit]
Description=Shahed Alert Bot — Kyiv zones
After=network.target

[Service]
Type=simple
User=ubuntu
WorkingDirectory=$REMOTE_DIR/kyiv_bot
ExecStart=$REMOTE_DIR/venv/bin/python $REMOTE_DIR/kyiv_bot/bot.py
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
EOF

ssh "$REMOTE" "sudo systemctl daemon-reload && sudo systemctl enable kyiv-bot"
if [ -f "$KYIV_BOT_TOKEN" ]; then
  ssh "$REMOTE" "sudo systemctl restart kyiv-bot"
  echo "✅ kyiv-bot запущено!"
fi

# ── donate-reminder (щотижнево) ───────────────────────────────────────────────
echo "💛 Configuring donate-reminder timer..."
rsync -avz "$PROJECT_DIR/donate_reminder.py" "$REMOTE:$REMOTE_DIR/donate_reminder.py"

ssh "$REMOTE" sudo tee "/etc/systemd/system/donate-reminder.service" > /dev/null <<EOF
[Unit]
Description=Weekly donate reminder to all bot subscribers

[Service]
Type=oneshot
User=ubuntu
WorkingDirectory=$REMOTE_DIR
ExecStart=$REMOTE_DIR/venv/bin/python $REMOTE_DIR/donate_reminder.py
EOF

ssh "$REMOTE" sudo tee "/etc/systemd/system/donate-reminder.timer" > /dev/null <<EOF
[Unit]
Description=Run donate reminder daily (script decides if it's time to send)

[Timer]
OnCalendar=daily
Persistent=true

[Install]
WantedBy=timers.target
EOF

ssh "$REMOTE" "sudo systemctl daemon-reload && sudo systemctl enable --now donate-reminder.timer"
echo "✅ donate-reminder timer запущено (щонеділі о 12:00)"

# ── 6. Add an nginx location for it, if not already there ──────────────────
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

# ── 7. Point the Telegram bot's webhook at us ───────────────────────────────
if [ -f "$TELEGRAM_TOKEN_FILE" ]; then
  echo "🤖 Registering Telegram webhook..."
  TOKEN="$(tr -d '[:space:]' < "$TELEGRAM_TOKEN_FILE")"
  curl -s "https://api.telegram.org/bot$TOKEN/setWebhook" -d "url=$WEBHOOK_URL" > /dev/null
  echo "  webhook set to $WEBHOOK_URL"
fi

echo ""
echo "✅ Готово! (LLM_PROVIDER=$LLM_PROVIDER) https://cozy-planner.duckdns.org$URL_PATH"
