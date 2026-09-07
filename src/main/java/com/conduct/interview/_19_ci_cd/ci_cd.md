# CI/CD

Continuous Integration + Continuous Delivery. Automate the path from code commit to production.

---

## CI — Continuous Integration

Every commit triggers: build → test → static analysis → artifact creation.
Goal: find problems fast, on every change, not before release.

**Typical pipeline**:
```
push → compile → unit tests → integration tests → build Docker image → push to registry
```

**GitHub Actions example**:
```yaml
on: [push, pull_request]
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with: { java-version: '21', distribution: 'temurin' }
      - run: ./gradlew test build
      - run: docker build -t myapp:${{ github.sha }} .
      - run: docker push myapp:${{ github.sha }}
```

**Quality gates** (fail the pipeline if violated):
- Test coverage < 80%.
- Static analysis (Sonar, Checkstyle) violations.
- Dependency vulnerabilities (Snyk, Dependabot).

---

## CD — Continuous Delivery vs Deployment

**Delivery**: artifact is always ready to deploy. Deploy is manual (one click).
**Deployment**: every passing build automatically deploys to production.

Most teams do **Delivery** with automated deployment to staging and manual to production.

---

## Deployment Strategies

### Rolling Update
Replace instances one at a time. Zero downtime. Both old and new code run simultaneously for a window.

```yaml
# Kubernetes
strategy:
  type: RollingUpdate
  rollingUpdate: { maxSurge: 1, maxUnavailable: 0 }
```

**Risk**: incompatible DB schema changes will break old pods while new ones run.
**Rule**: DB migrations must be backward-compatible with the previous version.

### Blue/Green
Two identical environments. Switch traffic from blue (old) to green (new) at once.

```
Traffic → BLUE (v1)
         GREEN (v2) ← deploy here, test, then switch traffic
Traffic → GREEN (v2)  ← instant cutover
```

Easy rollback (switch back to blue). Expensive (double infrastructure).

### Canary
Route a small percentage of traffic to the new version first. Gradually increase.

```
95% → v1 (stable)
 5% → v2 (canary) ← monitor errors/latency
```

Safest. Catch issues with real traffic before full rollout. Requires good observability.

### Feature Flags
Deploy code disabled. Enable for % of users without a new deployment.

```java
if (featureFlags.isEnabled("new-checkout", userId)) {
    newCheckout.process(cart);
} else {
    oldCheckout.process(cart);
}
```

Decouples deploy from release. Enables A/B testing. Libraries: LaunchDarkly, Unleash, Flagsmith.

---

## Docker in CI/CD

```dockerfile
# Multi-stage build — small production image
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY . .
RUN ./gradlew bootJar

FROM eclipse-temurin:21-jre
COPY --from=build /app/build/libs/app.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Tag images with git SHA (not `latest`) for traceability. Store in ECR, GCR, or Docker Hub.

---

## Database Migrations in CI/CD

Use **Flyway** or **Liquibase** — version-controlled, auto-applied on startup.

```sql
-- V3__add_tracking_column.sql
ALTER TABLE orders ADD COLUMN tracking_number VARCHAR(50);  -- nullable = backward compatible
```

**Rules for zero-downtime migrations:**
1. Never rename or drop a column used by the current version.
2. Add nullable columns (old code ignores them).
3. Drop columns only after old code is gone (two deployments later).

---

## Interview Points

- CI without CD is just expensive linting — the value is in fast deployments.
- Canary + feature flags = safest deploy strategy for senior engineers.
- DB migrations must run before code deployment AND be backward-compatible with previous code.
- Never tag images as `latest` in CI — you lose traceability. Use git SHA.
- Rollback plan is mandatory: either blue/green switch-back or feature flag off.
- Shift left: find bugs in CI, not in production. Static analysis + tests in CI gate the deployment.
