# Claude Certified Developer — Notes

## MFO Foundations

- **Tokens** — unit of input, output and cost.
- **Context window** — total number of tokens the model can take in for a single request (prompt, conversation, attached docs, tool results, model output).
- **Sampling** is the process an AI model uses to choose the next token from all possible tokens. Temperature affects the sampling — the lower, the more probability-dependent.
- AI is non-deterministic — even temperature 0 doesn't guarantee the same response.

**Models**
- Fable — smartest
- Opus — quite demanding
- Sonnet — the most balanced
- Haiku — fastest, less quality
- Also reasoning per request matters — usually we can select level of reasoning.

**Prompting modes**
- Zero-shot — no example is provided in prompt
- One-shot — one example
- Multi-shot — few examples provided to AI

**Ways to communicate with Claude**
- REST
- SDK

**Request types**
- Synchronous
- Streams

**Async patterns for high-volume work**
- Fire-and-forget SDKs (Python, TypeScript)
- Message Batches API

---

## 4 Techniques That Give Claude a Reliable Output Shape

- An **output constraint** — "give me one of: constants — pos, neg, medium"
- **System prompt** — rules that apply to every response. Can be added to the 1st message strictly and then repeated (if long chat), can be set in Project as instructions, "system" field can be set.
- **Few-shot examples** so that Claude knows exactly in which form your response must be.
- **Cover edge cases** in prompts, like the case when some field is empty, some exception — how to handle it.

Sometimes it makes sense to use descriptive XML tags to describe options. In API calls we can use structured outputs.

**Extended thinking**
- Effort — how deep to think.

**Tool use**
1. Pre-built connectors (Gdrive, Gmail) — someone wrote them, we just toggle them on.
2. Custom tools via API — I write the schema and execution code myself (Android app's "create-event").

Same mechanism for both — Claude never runs code, it requests a "tool use" block (name + args) as JSON, my app executes, sends back "tool_result", Claude continues.

---

## Streaming, Context Management & RAG

**Rule of thumb**
1 sentence = when to use, 1 sentence = when not to. Still came separated with exclusions → both are really the one tool, merge them and use a "type" parameter instead of 2 schemas.

**Streaming**
Fetch response from AI in chunks — text you can use at any moment, tool use (JSON) you need to wait for closing blocks.
If looping is done but message stop is not received — we must discard the whole message. Don't add to history. Retry original request.

**Model selection and keeping multi-turn session in budget**
You need to decide in advance what goes into the context window, what comes back as summary, and what never enters at all — this is context engineering.

4 strategies for staying in budget:
- Pruning — go back to smaller message
- Compacting — summarize history into condensed version
- Clearing — fresh session
- Sub-agent handoffs — give subtask to agent

2 API features reduce what you pay:
- Prompt caching — Claude remembers system prompt, tool lists between messages
- Token counting

**RAG failure points**
- Improper chunk sizes
- Embedding match — similar meaning text
- Assembly — chunk is not put into prompt

---

## Building Production Agents

Agent is a loop that calls tools, manages context, and has a goal.
It's a **workflow** if you can list exact steps.
It's an **Agent** if steps depend on user input and are not predictable.
Don't default to agent!

**3 ways to build the loop**
1. Raw Messages API — you write everything yourself.
2. Agent SDK — library handles loop and context, you still execute tools.
3. Managed agents — Anthropic runs the whole loop + sandbox, server-side.

Pick based on: how much infra you want to own, and legal/compliance stuff (managed agents can't be used for HIPAA etc.).

**Every Agent loop needs 4 things**
1. Tools registered
2. System prompt is not vague
3. Every tool called gets a result back
4. Exit condition defined (tools)

**Human in the loop** (pause where human checks before continuing)
- Before anything destructive/deleted
- After Claude makes a plan
- When tool result looks broken

Common mistake — too many tools.

---

## Claude Agent Execution Loop

1. **Explore** — read files, build context of code
2. **Plan** — generate structured proposal
3. **Code** — execute/make changes after approval

**Permission modes**
- Plan
- Default — confirm on tool calls and actions
- Accept Edits — auto-approves routine
- Bypass Permissions (calls except `rm -rf` etc.)

**Configuration hierarchy**
1. Enterprise level (managed settings.json) — admin controlled
2. User level
3. Project level
4. Shared project level

Deny rule always overrides Allow rule.

**Project context**
1. `claude.md` — project-wide constraints, framework conventions etc. Gets loaded into every session. If too big, some parts can be ignored.
2. Scoped rules files — depends on paths glob in YAML.
3. Lifecycle hooks — intercept tool calls and session events via custom scripts. Key events: `PreToolUse`, `SessionStart`, etc.
4. Sub-agents — isolated context execution. They can load `claude.md`/skills or not.

---

## Memory Scope, Multimodal & Batch Processing

**Choose the right scope for state that survives sessions.**

**Memory scope** = what the agent remembers after session ends. 4 options:
1. In-context — just in this session, persists
2. External storage (DB)
3. Across sessions, condensed
4. Stateless

**Skill** — reusable instructions that load only when needed.
A skill = `.md` file + description used for matching + instructions.
Skill → loads only when relevant.
`claude.md` → loads always. Use for team conventions.
In-context instructions → just part of the conversation, grows with it, gone when session ends.

Sub-agents do **NOT** automatically get the parent's skills — need to grant explicitly. Sub-agents don't inherit permissions, skills, or history.

**Images, PDFs and high-volume processing**
- PDF is a document, treated the same as image file.
- Multimodal prompting: same as text formatting, just resolve visual ambiguity.
- Message Batches API — for bulk, non-real-time jobs.

---

## MCP Servers

MCP server — a layer that exposes tools to Claude from outside your codebase. Before building an MCP server, need to determine transport mechanism and define the server's scope.

**Model Context Protocol (MCP)** — separates tool definitions from individual applications and turns them into a process called a server.
MCP server exposes tools, resources, and prompts that MCP clients can use. Claude Code has a built-in MCP client.

- **MCP Resource** — URL to some doc we can fetch instead of asking Claude to fetch (faster and cheaper).
- **MCP Prompt** — pre-packaged system templates (refactor-code etc.).

**Transport (how Claude talks to server)**
1. stdio — running MCP server locally (via std input/output)
2. HTTP — remote server
3. (old, not recommended)

**Cost** — good practice is to disable unused MCP servers to keep context clean.
**Cache** — set up cache and save up to 90%; a single character change invalidates the cache.
**RAG** — attaching docs in a smart way.

---

## Packaging Workflow as a Plugin

**1. Skills** — reusable workflows the agent loads on demand. It's a portable `.md` file placed in `.claude/skills`.

**How a skill runs**
1. Claude Code — found in filesystem based on description match or when invoked by name. Runs in terminal session. It's FS-based and governed by the settings layer.
2. Messages API — running in Anthropic's code execution container.
3. Agent SDK — running in the process the SDK runs. Important to set "setting sources".
4. Claude managed agents — runs in Anthropic sandbox. Your app sends "user events" and reads streamed result back. Sub-agents don't inherit skills by default.

Skills are for "on-demand" portable procedures. A skill can be run by command `/skill-name` or Claude loads it when relevant.

**Plugin** — a single, versioned and auditable unit that bundles skills, hooks, sub-agents, and MCP servers. Enables quick installation and consistency across teams. In the Plugin Manifest we describe bundles and wire components.

Enterprise and Governance Controls.

---

## Evals

**Eval** — a measurable number that confirms everything is working fine.

First, need to write a document. It should contain:
- Success criteria
- Failure handling — describe failure, what to retry, what we should see
- Cost and latency budget
- Trust boundary

With eval, "done" from feeling turns into a score.

**3 ways to produce the signal**
1. Exact or string match
2. Code-graded checks (valid JSON, number inside range, field is present, etc.)
3. LLM-as-judge

Makes sense to ask the judge to provide strengths, weaknesses, reasoning alongside the score. Ask Claude to reason first.

**Loop:** set a goal → write initial prompt → run the eval → read where it failed → apply prompt-engineering change → run the eval again.

**Testing and tracing**
1. Unit test — check single aspect
2. Functional test — validates call to Claude and response
3. Integration test — complex test, real multi-stage etc.
4. E2E test — user-like test

---

## Tracing & Observability

**Tracing** — step-by-step timeline log recording every intermediate input, output, tool call, and latency.

**Observability** for a Claude system means instrumenting 3 metrics per call:
- Token usage (input and output tokens)
- Latency
- Error rate

**What affects budget**
1. Model selection for the task
2. Optimizing prompt and context size
3. Optimizing number of tool calls
4. Select stream or batch

**Orchestrator-Worker Multi-Agent Pattern** — use for work which can be parallelized. Powerful model as the lead, cheaper model for sub-agents. Set SLAs first (e.g. max 4s latency, or max 1% error rate).

**Securing the integration against untrusted input and a regulated review**
- Prompt injection comes from untrusted fetched data (web pages, DBs, tool results).
- Jailbreak comes from direct user inputs.

---

## Security

Prompt instruction is never a security control — security must be enforced at the **Action Layer** (hooks, OS sandboxing, least-privilege roles).

- **Least privilege** — agent has as few roles as possible.
- **Secrets** live in env variables or secret stores.
- **OS-level sandboxing** — restrict access to a particular directory, restrict outbound requests to just some hosts, etc.

**Rule precedence:** Deny > Ask > Allow.

Always log every privileged tool call with `PreToolUse` / `PostToolUse`.

---

## Connectors

Allow Claude to read and perform actions on the user's behalf, to/from 1st/3rd party services, or add additional information to context (add-on to Desktop).

Model Context Protocol connections.

Recommended connectors: Claude.ai directory.

Kind of pre-built project where your company's knowledge is already loaded — **Enterprise search**.

Research-enabled Claude — search engine that can research from your company's knowledge base / structure — company research.
- Include specified projects/structure?
- Ask Claude to use the Enterprise search function?

**Ways to use Claude**
1. Claude Code (terminal / IDE / browser)
2. Claude Desktop
3. Claude Design
4. Claude for Excel / PowerPoint / Word
5. Claude for Chrome (web, desktop, mobile)
6. Claude AI app (Chrome, mobile)
