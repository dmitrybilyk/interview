# Snyk and OWASP Top 10 — What Snyk Actually Finds

Snyk is not a replacement for knowing OWASP. It is a scanner that detects **specific
concrete instances** of OWASP issues in your code, dependencies, and config files.
Knowing OWASP tells you what category a problem belongs to. Snyk finds the actual line.

---

## What Snyk scans and which OWASP issues it catches

### Snyk Open Source — scans your `build.gradle` dependencies

Finds: every library in your dependency tree (direct and transitive) that has a known CVE.

→ **Covers A06 (Vulnerable Components) almost completely.**

Example: you added `kafka-clients:2.8.0`. Kafka pulls in `log4j-core:2.14.1`.
Snyk reports: "log4j-core 2.14.1 — CVE-2021-44228 (Log4Shell) — CRITICAL — fix: upgrade to 2.17.1"
It even opens a PR to update `build.gradle` automatically.

---

### Snyk Code — scans your Java/Kotlin source files (SAST)

Reads your code without running it. Traces how data flows from an entry point
(HTTP request, user input) to a dangerous sink (SQL query, HTML output, shell command).

**A03 Injection** — this is Snyk Code's primary job:

- SQL injection: sees `"SELECT * FROM users WHERE name = '" + username + "'"` and flags it
- XSS: sees `response.getWriter().write(request.getParameter("q"))` and flags it
- Command injection: sees `Runtime.exec("ping " + ip)` and flags it

It shows you the full data flow: `HTTP param → variable → SQL string → DB` so you can
see the exact path the attacker would take.

**A02 Cryptographic Failures** — partial coverage:

- Flags use of `MD5`, `SHA1`, `DES`, `AES/ECB` — weak algorithms
- Flags hardcoded secrets: `private static final String KEY = "abc123"` in source code
- Does NOT check whether your HTTPS config is correct or whether cookies have `Secure` flag

**A07 Auth Failures** — partial coverage:

- Flags hardcoded credentials: `if (password.equals("admin"))` in source
- Does NOT detect missing rate limiting, missing session invalidation, or weak password policies
  (those are runtime behaviors, invisible to static analysis)

**A05 Security Misconfiguration** — minimal coverage from Snyk Code:

- May flag some obviously insecure Spring Security configs in code
- For real misconfiguration detection, you need Snyk IaC (see below)

---

### Snyk IaC — scans Dockerfile, Kubernetes YAML, Terraform

→ **Covers A05 (Security Misconfiguration) for infrastructure.**

Examples of what it finds:
- `USER root` in a Dockerfile — container runs as root, a breakout gives full host access
- Missing `readOnlyRootFilesystem: true` in a K8s pod spec
- S3 bucket with `public-read` ACL in Terraform

---

## What Snyk CANNOT catch — and why

These OWASP categories require understanding business logic or runtime behavior.
No static tool can see them.

| OWASP Issue | Why Snyk misses it |
|---|---|
| **A01 Broken Access Control** | Snyk can't know your business rules. It can't tell that `orderRepo.findById(id)` should also filter by owner — it sees valid Java code. |
| **A04 Insecure Design** | The flaw exists at the design level before any code is written (no rate limiting designed in, predictable token scheme). Static analysis sees code, not architecture decisions. |
| **A09 Logging/Monitoring Failures** | Snyk can't tell whether you have alerting set up, or whether your logs go to a SIEM. It may flag "you're logging a password" but won't flag "you have no security events logged at all." |
| **A10 SSRF** | Partially. Snyk Code detects some SSRF patterns, but complex cases — especially through indirect flows — are often missed. |

---

## The practical picture

```
You write code
      ↓
Snyk Code (in IDE / CI) → flags injection, weak crypto, hardcoded secrets
      ↓
You add a dependency
      ↓
Snyk Open Source (in CI) → flags known CVEs in that library and its transitive deps
      ↓
You write a Dockerfile
      ↓
Snyk IaC (in CI) → flags root user, missing security context, public buckets
      ↓
What's left uncovered: access control logic, design flaws, monitoring gaps
      ↑
Those require code review, threat modeling, and manual testing
```

---

## In short

Snyk reliably catches A03 (injection patterns in code), A06 (CVEs in dependencies),
and A05 (infra misconfig). It partially catches A02 and A07.

It cannot catch A01, A04, A08, A09, or SSRF reliably — because those depend on
what your system is *supposed* to do, not just what the code says.

OWASP Top 10 knowledge fills that gap. When Snyk can't see it, you have to.
