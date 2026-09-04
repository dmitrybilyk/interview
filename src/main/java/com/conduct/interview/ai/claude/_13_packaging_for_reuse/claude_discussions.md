# Discussion topics — _13_packaging_for_reuse

- **A real bug hit while building this folder:** the shared helper module
  here was first named `_common.py`, same as `_12_agent_loop/_common.py`.
  Once `_1_parameterize_agent_template.py` did `sys.path.insert(0,
  "../_12_agent_loop")` and then `from _common import ...`, Python's module
  cache resolved `_common` to whichever one loaded first — not necessarily
  the one in this folder — and `audit_log`/`load_config` came back missing.
  Renamed this folder's file to `_pkg.py` to fix it. General lesson for
  packaging: two "reusable cores" that both use the generic filename
  `_common.py` are fine in isolation but silently unsafe to import into the
  same process — worth a naming convention (or real packages with
  `__init__.py`) before this leaves the lab.
- `_1_parameterize_agent_template.py` — where's the actual line between
  "customer-specific, goes in config" and "core, stays in code"? The
  destructive-tools set moved to config here. Should the tool schemas
  themselves ever become customer-editable, or does the asset stop being
  the same asset once they do?
- `_2_document_mcp_server_manifest.py` — imports `_12_agent_loop/_common.py`
  just for `TOOLS`, and that import builds a live Anthropic client as a
  side effect, so a schema-only manifest script needs `ANTHROPIC_API_KEY`
  set for no real reason. A cleanly packaged MCP Server Package would
  probably split tool *schemas* from tool *execution* into separate
  modules so the manifest step has zero runtime dependencies. Worth fixing
  before this leaves the lab.
- `_3_eval_gate.py` — the eval forces `require_approval: False` to run
  unattended, then checks the approval gate structurally instead (is the
  tool listed in `destructive_tools` with approval on). Is a structural
  check enough, or does a real deployment gate need to actually simulate a
  human rejecting the approval and confirm the agent recovers cleanly?
- Baseline pinning: `eval_baseline.json` pins a score and a model string,
  but if `claude-haiku-4-5-20251001` gets silently upgraded server-side,
  does the baseline still mean the same thing? Should the eval also pin a
  model snapshot/version if the provider offers one?
- Audit log: right now `audit_log()` just appends JSON lines to a local
  file. What would a regulated customer actually want here — structured
  shipping to their SIEM, a retention period, who signs off on the schema?
- How much of this generalizes past agent loops? Would the same
  three-asset-type split (template / server / eval) make sense for, say,
  `_4_rag` packaged as an accelerator — what's the "MCP Server" equivalent
  for a RAG pipeline?
