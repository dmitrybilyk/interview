# Fixing Snyk Issues in Practice

---

## Step 1: Triage before fixing

Not every Snyk finding needs the same response. Before touching anything, answer:

**Is the vulnerable code path actually reachable in your app?**

Example: Snyk flags a CVE in `jackson-databind` for polymorphic deserialization.
But your app only deserializes known, fixed DTO classes — you never deserialize
arbitrary user-supplied type names. The CVE's attack vector doesn't apply.

Snyk calls this **reachability analysis** — in paid tiers it marks findings as
"reachable" or "not reachable" based on whether your code actually calls the
vulnerable function. Use this to prioritize.

Severity triage:
- **CRITICAL / HIGH + reachable** → fix now, block the build
- **HIGH + not reachable** → fix in the next sprint
- **MEDIUM / LOW** → schedule, don't panic
- **No fix available** → document, mitigate, accept risk formally

---

## Part A: Fixing Dependency Vulnerabilities

### Case 1: patch or minor version available — just update

This is the easy case. Snyk says: "upgrade from 2.14.1 to 2.17.1"
and it's a patch release. Update and run your tests.

```groovy
// build.gradle — before
implementation 'com.fasterxml.jackson.core:jackson-databind:2.13.0'

// after — patch bump, almost never breaks anything
implementation 'com.fasterxml.jackson.core:jackson-databind:2.13.4.2'
```

Snyk can open this PR automatically — enable it in the Snyk GitHub integration.

---

### Case 2: you don't own the vulnerable library — it's transitive

You added `spring-boot-starter-web`. It pulled in `snakeyaml:1.29` which has a CVE.
You never wrote `snakeyaml` in your `build.gradle`.

You can't "update" what you don't declare. But you can **force** the safe version:

```groovy
// build.gradle
configurations.all {
    resolutionStrategy {
        force 'org.yaml:snakeyaml:2.0'  // override whatever the parent declared
    }
}
```

Gradle will use `2.0` everywhere in the dependency tree, even though the parent
asked for `1.29`. Run your tests — if the parent library is compatible with the
newer version, you're done.

Check what's actually being resolved:
```bash
./gradlew dependencies --configuration runtimeClasspath | grep snakeyaml
# before: \--- org.yaml:snakeyaml:1.29
# after:  \--- org.yaml:snakeyaml:2.0 (forced)
```

---

### Case 3: major version required — breaking changes

Snyk says: "fix requires upgrading from v1 to v3 — major version jump."

You can't blindly bump it. The API probably changed. Strategy:

**1. Check the changelog first**
Read the library's migration guide. Many "major" bumps have only a few actual
breaking changes, and your code may not use any of them.

**2. Upgrade in a branch, run all tests**
```bash
git checkout -b upgrade/jackson-v3
# update version in build.gradle
./gradlew test
```

If tests pass, the breaking changes didn't affect your code paths. Merge.

**3. If tests fail — identify exactly what broke**
The compiler or tests will tell you which API changed. Usually it's a renamed class,
a method that was removed, or a new required config. Fix those specific usages.

**4. If the upgrade is too large for now — force the highest safe minor version**
```groovy
// Buys you time while you plan the major upgrade
configurations.all {
    resolutionStrategy {
        force 'com.example:some-lib:1.9.9'  // last safe version before v2
    }
}
```

Document this as technical debt with a ticket. Don't leave it undocumented.

---

### Case 4: no fix exists yet (0-day or unpatched CVE)

The CVE is published, the library has no patched release yet.

**Option A: Can you remove the library entirely?**
Sometimes the library does something your framework already does.
If you added `commons-io` just for `FileUtils.readFileToString()`,
Java 11+ has `Files.readString(path)` built in. Remove the dependency.

**Option B: Can you replace it with an alternative?**
Vulnerable XML parser → switch to a different XML parser.
Snyk's advisory usually lists alternatives.

**Option C: Can you sandbox the vulnerable code path?**
If the CVE requires the attacker to supply a specific input type (e.g., a YAML file),
and your app never accepts YAML from users — add input validation at the boundary
to ensure that path can never be reached, and document why the CVE doesn't apply.

**Option D: Accept the risk formally**
In Snyk UI: mark the issue as "accepted risk" with a justification and an expiry date.
This removes it from your dashboard noise but keeps the record.
This is a business decision — not a technical one. Needs sign-off.

---

## Part B: Fixing Code Issues (Snyk Code findings)

These are in your source code. No version to bump — you have to change the code.

### SQL Injection

Snyk flags:
```java
// Snyk: SQL Injection — user input flows into SQL string at UserDao.java:34
String sql = "SELECT * FROM users WHERE email = '" + email + "'";
```

Fix:
```java
PreparedStatement ps = conn.prepareStatement(
    "SELECT * FROM users WHERE email = ?");
ps.setString(1, email);
```

Or with Spring Data — just use a derived query or `@Query` with `:param`:
```java
Optional<User> findByEmail(String email);  // Spring generates the safe query
```

---

### Hardcoded Secret

Snyk flags:
```java
// Snyk: Hardcoded credentials at PaymentService.java:12
private static final String API_KEY = "sk_live_abc123xyz";
```

Fix — move it out of code entirely:
```java
@Value("${payment.api-key}")
private String apiKey;
```

```yaml
# application.yml — reads from environment at runtime
payment:
  api-key: ${PAYMENT_API_KEY}
```

```bash
# Set in deployment environment, not in any file committed to git
export PAYMENT_API_KEY=sk_live_abc123xyz
```

The value never touches source control. Rotating it is a config change, not a code deploy.

---

### Weak Cryptography

Snyk flags:
```java
// Snyk: Use of weak hashing algorithm MD5 at UserService.java:78
MessageDigest.getInstance("MD5")
```

Fix depends on what you're hashing:

```java
// For passwords — use BCrypt, not any MessageDigest
PasswordEncoder encoder = new BCryptPasswordEncoder(12);
String hash = encoder.encode(rawPassword);

// For checksums / data integrity (not passwords) — use SHA-256
MessageDigest.getInstance("SHA-256")

// For encryption — use AES-GCM, never ECB
Cipher.getInstance("AES/GCM/NoPadding")
```

---

### Path Traversal

Snyk flags:
```java
// Snyk: Path Traversal — user input used in file path at FileController.java:55
File file = new File("/uploads/" + filename);
```

Input `../../etc/passwd` resolves to `/etc/passwd`.

Fix:
```java
Path base = Path.of("/uploads").toRealPath();
Path resolved = base.resolve(filename).normalize();

if (!resolved.startsWith(base)) {
    throw new SecurityException("Path traversal detected");
}
// now safe to use resolved
```

`normalize()` collapses `../..`. `startsWith(base)` verifies the result is still
inside your intended directory.

---

## The overall workflow

```
Snyk reports an issue
        ↓
Is it reachable / does the attack vector apply?
   No → accept risk with justification + expiry date
   Yes ↓
Is it a dependency CVE?
   Patch/minor available → bump version, run tests
   Transitive → force the safe version in resolutionStrategy
   Major jump → upgrade in a branch, fix breaking changes
   No fix yet → remove lib / replace / sandbox / accept formally
Is it a code finding?
   Injection → parameterized query / safe API
   Hardcoded secret → environment variable
   Weak crypto → replace algorithm
   Path traversal → normalize + prefix check
```

---

## Keeping it under control long-term

- Run `./gradlew dependencyCheckAnalyze` (OWASP) or `snyk test` in CI.
  **Fail the build on CVSS ≥ 7 that are unaccepted.**
  This means no new HIGH/CRITICAL vulnerabilities can silently enter production.

- Enable Snyk's automatic PRs for dependency updates.
  Small, frequent updates are far easier than one massive version catch-up.

- Set a policy: accepted-risk items must have an expiry date and an owner.
  "We'll accept this for 60 days while we plan the major upgrade" — not "we'll deal with it someday."
