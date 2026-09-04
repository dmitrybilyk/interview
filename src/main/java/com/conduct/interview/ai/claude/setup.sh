#!/usr/bin/env bash
# Usage: source setup.sh
#
# Must be SOURCED (not executed) so that venv activation and
# ANTHROPIC_API_KEY export persist in your current shell session.
#
# First time on any PC:  source setup.sh   ← creates venv + installs deps
# Every time after:      source setup.sh   ← activates venv + loads key

# Guard: warn if run directly instead of sourced
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
    echo "ERROR: run with 'source setup.sh', not 'bash setup.sh'"
    echo "       Direct execution can't export env vars to your shell."
    exit 1
fi

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# ── 1. Python check ───────────────────────────────────────────────────────
if ! command -v python3 &>/dev/null; then
    echo "ERROR: python3 not found. Install Python 3.8+ first."
    return 1
fi

PY_VERSION=$(python3 -c "import sys; print(f'{sys.version_info.major}.{sys.version_info.minor}')")
echo "Python $PY_VERSION found."

# ── 2. Create venv if it doesn't exist ───────────────────────────────────
VENV="$SCRIPT_DIR/venv"
if [ ! -d "$VENV" ]; then
    echo "Creating venv..."
    python3 -m venv "$VENV"
    echo "✓ venv created at $VENV"
else
    echo "✓ venv already exists"
fi

# ── 3. Install / upgrade dependencies ─────────────────────────────────────
echo "Installing dependencies..."
"$VENV/bin/pip" install --quiet --upgrade pip
"$VENV/bin/pip" install --quiet --upgrade anthropic
echo "✓ anthropic $("$VENV/bin/pip" show anthropic | grep Version | awk '{print $2}') installed"

# ── 4. Activate venv in current shell ─────────────────────────────────────
source "$VENV/bin/activate"
echo "✓ venv activated — using $(which python3)"

# ── 5. Load API key from key.txt ──────────────────────────────────────────
KEY_FILE="$SCRIPT_DIR/key.txt"
if [ -f "$KEY_FILE" ]; then
    export ANTHROPIC_API_KEY
    ANTHROPIC_API_KEY="$(tr -d '[:space:]' < "$KEY_FILE")"
    echo "✓ ANTHROPIC_API_KEY loaded from key.txt"
else
    echo "WARNING: key.txt not found at $KEY_FILE"
    echo "         Create it with your API key, or run:"
    echo "         export ANTHROPIC_API_KEY=sk-ant-..."
fi

# ── Done ──────────────────────────────────────────────────────────────────
echo ""
echo "Ready. Run any lesson:"
echo "  cd _1_basic_call  && python script.py"
echo "  cd _2_prompting   && python script.py"
echo "  cd _3_streaming   && python script.py"
echo "  ...up to _11_agent_loop"
