#!/bin/bash
# Deploys the news agent to the Oracle Cloud VM as a systemd service,
# reachable through the existing nginx/443 setup at:
#   https://cozy-planner.duckdns.org/news-agent/
#
# We do NOT open a new port. This box's cloud security list only allows
# 22/80/443 in — nothing else gets through no matter what ufw says
# locally, so a bare "http://IP:8600" approach can't work here without a
# console change on the cloud provider's side. Instead we bind the app to
# localhost only and let nginx (which already terminates TLS on 443 for
# the planner app and /remindly) proxy an extra path to it.
#
# Also sets up the Telegram side: a broadcaster.py run on a timer, and the
# webhook that lets people /start the bot to subscribe (see telegram_bot.py).
# Needs a bot token — create one with @BotFather on Telegram, then save it
# locally as ../key.txt's sibling: telegram_token.txt (gitignored, same
# pattern as key.txt). If it's missing, Telegram features are simply skipped.
#
# LLM provider is switchable: LLM_PROVIDER=groq ./deploy.sh deploys with Groq
# active instead of Claude (needs groq_key.txt, same pattern as key.txt).
# Default is anthropic. See agent/config.py (PROVIDER) and agent/providers.py
# (call_llm) — this env var is what config.py's PROVIDER reads.
#
# Fetch source is switchable too: FETCH_SOURCE=html ./deploy.sh. Default here
# is "rss", NOT agent/config.py's local-dev default of "html" — this VM's
# outbound requests to censor.net's homepage get a 403 (its anti-scraping
# protection, presumably blocking the whole datacenter IP range; the RSS
# feed on assets.censor.net has never had this problem). "html" works fine
# from most home/office connections for local dev — it just can't be this
# server's default until/unless that changes.
#
# Usage: ./deploy.sh
#        LLM_PROVIDER=groq ./deploy.sh
#        FETCH_SOURCE=html ./deploy.sh   # currently broken on THIS server, see above
set -e

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
KEY_FILE="$PROJECT_DIR/../key.txt"
TELEGRAM_TOKEN_FILE="$PROJECT_DIR/telegram_token.txt"
GROQ_KEY_FILE="$PROJECT_DIR/groq_key.txt"
LLM_PROVIDER="${LLM_PROVIDER:-anthropic}"
FETCH_SOURCE="${FETCH_SOURCE:-rss}"

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

# No --delete here: subscribers.json / sent_state.json / classification_cache.json /
# history.json live only on the server (runtime state, not code) and must survive redeploys.
rsync -avz \
  --exclude 'venv' --exclude '__pycache__' --exclude '*.pyc' \
  "$PROJECT_DIR/app.py" "$PROJECT_DIR/telegram_bot.py" "$PROJECT_DIR/broadcaster.py" \
  "$PROJECT_DIR/cli.py" "$PROJECT_DIR/agent" \
  "$PROJECT_DIR/requirements.txt" "$PROJECT_DIR/templates" \
  "$REMOTE:$REMOTE_DIR/"

# news_agent.py was replaced by the agent/ package (this project used to be
# one flat file; see README's "What each file teaches"). Remove the stale
# copy + its bytecode cache so nothing on the server accidentally still
# imports the old dead file.
ssh "$REMOTE" "rm -f '$REMOTE_DIR/news_agent.py' && find '$REMOTE_DIR' -name '__pycache__' -exec rm -rf {} + 2>/dev/null; true"

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
Environment=FETCH_SOURCE=$FETCH_SOURCE
ExecStart=$REMOTE_DIR/venv/bin/python $REMOTE_DIR/app.py
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
Environment=FETCH_SOURCE=$FETCH_SOURCE
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
echo "✅ Done! (LLM_PROVIDER=$LLM_PROVIDER, FETCH_SOURCE=$FETCH_SOURCE) https://cozy-planner.duckdns.org$URL_PATH"
