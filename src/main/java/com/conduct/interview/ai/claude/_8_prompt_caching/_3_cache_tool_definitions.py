"""
Topic: Prompt caching — caching the tool list (same cache_control pattern)
Cert notes section: "2 API features reduce what you pay: Prompt caching, Token counting"
Run: ../venv/bin/python _3_cache_tool_definitions.py

cache_control goes on the LAST tool — that marks the cache boundary.
Everything up to and including that tool gets cached.
Tool list must exceed 1024 tokens for Sonnet to actually cache.
"""

import os
from anthropic import Anthropic

client = Anthropic(api_key=os.environ["ANTHROPIC_API_KEY"])

# Large tool list — detailed schemas to push past the 1024-token minimum
TOOLS = [
    {
        "name": "query_database",
        "description": (
            "Execute a SQL query against the production database. "
            "Supports SELECT, INSERT, UPDATE, DELETE. "
            "Always use parameterised queries to avoid SQL injection. "
            "Returns rows as a list of dicts. Raises on syntax errors or connection failure. "
            "Use for: fetching records, aggregations, joins, filtered lookups. "
            "Do not use for DDL statements (CREATE, ALTER, DROP) — use migrate_schema instead. "
            "Connection pool size: 20. Timeout: 30s. Max rows returned: 10000."
        ),
        "input_schema": {
            "type": "object",
            "properties": {
                "sql":    {"type": "string", "description": "Parameterised SQL query string"},
                "params": {"type": "array",  "description": "Positional parameters for the query", "items": {"type": "string"}},
                "db":     {"type": "string", "description": "Database name. Defaults to 'primary'. Options: primary, replica, analytics"},
            },
            "required": ["sql"],
        },
    },
    {
        "name": "call_external_api",
        "description": (
            "Make an HTTP request to an external REST API. "
            "Handles retries (3 attempts, exponential backoff), timeouts (10s default), "
            "and automatic JSON serialisation of the body. "
            "Supports: GET, POST, PUT, PATCH, DELETE. "
            "Auth: pass Bearer token via headers dict. "
            "Use for: third-party integrations, webhook delivery, downstream service calls. "
            "Returns: status code, response headers, parsed JSON body (or raw text if not JSON). "
            "Raises on network errors after all retries exhausted."
        ),
        "input_schema": {
            "type": "object",
            "properties": {
                "method":  {"type": "string",  "description": "HTTP method: GET | POST | PUT | PATCH | DELETE"},
                "url":     {"type": "string",  "description": "Full URL including scheme and path"},
                "headers": {"type": "object",  "description": "Key-value HTTP headers (e.g. Authorization, Content-Type)"},
                "body":    {"type": "object",  "description": "Request body (serialised to JSON automatically)"},
                "timeout": {"type": "integer", "description": "Timeout in seconds. Default 10."},
            },
            "required": ["method", "url"],
        },
    },
    {
        "name": "send_notification",
        "description": (
            "Send a notification to a user or team via one of the supported channels. "
            "Channels: email (SendGrid), Slack (webhook), PagerDuty (for on-call alerts), SMS (Twilio). "
            "For email: subject and html_body are required. "
            "For Slack: message is required, channel defaults to #general. "
            "For PagerDuty: severity must be one of: critical, error, warning, info. "
            "For SMS: E.164 phone format required (+1234567890). "
            "Rate limits: email 100/min, Slack 50/min, SMS 10/min. "
            "Do not use for bulk sends — use batch_notify instead."
        ),
        "input_schema": {
            "type": "object",
            "properties": {
                "channel":   {"type": "string", "description": "Delivery channel: email | slack | pagerduty | sms"},
                "recipient": {"type": "string", "description": "Email address, Slack user ID, PagerDuty service key, or E.164 phone"},
                "subject":   {"type": "string", "description": "Subject line (email only)"},
                "message":   {"type": "string", "description": "Plain-text message body"},
                "html_body": {"type": "string", "description": "HTML message body (email only, overrides message)"},
                "severity":  {"type": "string", "description": "Alert severity (pagerduty only): critical | error | warning | info"},
            },
            "required": ["channel", "recipient", "message"],
        },
        "cache_control": {"type": "ephemeral"},  # cache boundary: tools 1-3 all get cached
    },
]


def call(label):
    r = client.messages.create(
        model="claude-sonnet-4-6",
        max_tokens=64,
        tools=TOOLS,
        messages=[{"role": "user", "content": "What tools do you have?"}],
    )
    u = r.usage
    print(f"{label}")
    print(f"  input_tokens:          {u.input_tokens}")
    print(f"  cache_creation_tokens: {getattr(u, 'cache_creation_input_tokens', 0)}")
    print(f"  cache_read_tokens:     {getattr(u, 'cache_read_input_tokens', 0)}")


call("=== Call 1 — tool list → cache WRITE ===")
call("=== Call 2 — same tool list → cache HIT ===")

print("""
KEY TAKEAWAYS:
- Tool schemas are input tokens on every call — cache them if they don't change
- cache_control on the LAST tool = cache boundary (everything before it is cached too)
- Same 1024-token minimum as system prompt caching (Sonnet/Opus)
""")
