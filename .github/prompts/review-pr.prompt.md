---
mode: agent
---

Review the current branch changes as a senior Java developer.

First run: `git diff main...HEAD`

Then check for:
- Business logic bugs
- `@Transactional` misuse (missing, wrong propagation, exception swallowing)
- N+1 queries (JPA lazy loading in loops)
- Security issues (OWASP top 10: injection, broken auth, sensitive data exposure)
- Violations of project conventions (see copilot-instructions.md)
- Missing error handling at system boundaries (external APIs, user input)

Output format — per file:
**File:** `path/to/File.java`
- 🔴 Critical: ...
- 🟡 Warning: ...
- 🟢 Suggestion: ...

Skip files with no issues.
