# AWS for Java Developers — Interview Guide

> Combined reference. Individual topic files are in this folder.

---

## [00 — Overview](_00_overview.md)
Amazon Web Services — rent infrastructure instead of owning it.

| Concept | One-liner |
|---|---|
| **Region** | Geographic cluster (e.g. eu-west-1). Services are region-scoped. |
| **AZ** | Isolated data center inside a region. Spread across AZs for HA. |
| **Edge Location** | CloudFront CDN POP. 400+ globally. |
| **Shared responsibility** | AWS = infra. You = OS, code, IAM, data. |
| **Pricing** | On-demand / Reserved (–75%) / Spot (–90%, interruptible) / Savings Plans |

---

## [01 — IAM](_01_iam.md)
Controls who can do what on which AWS resource.

- **User** — long-term credentials (people). **Role** — temporary credentials assumed by services (use in code, not Users).
- **Policy** — JSON `{Effect, Action, Resource}`. Explicit Deny always wins.
- **Least privilege**: grant only what's needed.
- SDK credential chain: env vars → system props → `~/.aws` → container → EC2/Lambda metadata role.
- Never embed access keys in code. Use roles.

---

## [02 — EC2](_02_ec2.md)
Virtual machines. Full OS control (IaaS).

- **Instance families**: t (burstable), m (general), c (compute), r (memory). `g` = Graviton (ARM, cheaper).
- **AMI** = OS template. **EBS** = persistent block storage. **Security Group** = stateful firewall.
- **Auto Scaling Group** + **ALB** = standard HA pattern (multi-AZ, health check + replace).
- For Java: size instance for heap + OS overhead. r6i for large heaps.

---

## [03 — Lambda](_03_lambda.md)
Serverless function triggered by events. No server management.

- Java handler: `implements RequestHandler<Input, Output>`.
- **Cold start**: JVM startup delay (1–10 s). Fix: **SnapStart** (Corretto 21, free), provisioned concurrency (costs), or native (Quarkus/Micronaut).
- Max timeout: **15 minutes**. Payload limit: 6 MB sync.
- Execution role provides credentials — no manual key management.
- Inside VPC only if you need RDS/ElastiCache — adds cold start latency; needs NAT GW for internet.

---

## [04 — S3](_04_s3.md)
Object storage. Virtually unlimited. 11 nines durability.

- `Bucket` (globally unique name) + `Key` (full path) = object. Max 5 TB per object.
- **Storage classes**: Standard → Standard-IA → Intelligent-Tiering → Glacier. Lifecycle rules auto-transition.
- **Presigned URL**: time-limited access without credentials — use for browser direct upload/download.
- **Multipart upload**: required for > 5 GB, recommended for > 100 MB. TransferManager handles it.
- Strongly consistent (since 2020) — no eventual consistency concern.

---

## [05 — RDS & Aurora](_05_rds.md)
Managed relational databases.

- **Multi-AZ** = HA failover (standby, no reads). **Read Replicas** = read scaling (separate endpoint).
- **Aurora** = AWS-native MySQL/PostgreSQL compatible. Faster, auto-scaling storage, up to 15 replicas.
- **Aurora Serverless v2** = auto-scale ACUs, good for variable load.
- **RDS Proxy** = connection pooling. Mandatory for Lambda → RDS.
- Standard JDBC. Use IAM DB auth to eliminate passwords from config.

---

## [06 — DynamoDB](_06_dynamodb.md)
Fully managed NoSQL. Single-digit ms latency. Serverless.

- **Partition key** (required) + **Sort key** (optional) = primary key.
- **GSI** = different PK, eventually consistent, own throughput. **LSI** = same PK, different SK, strongly consistent.
- Default = eventual consistency. Strongly consistent = 2× cost.
- **DynamoDB Streams** → Lambda for CDC. **TTL** = auto-expire items.
- Java: `DynamoDbEnhancedClient` with `@DynamoDbBean`, `@DynamoDbPartitionKey`.
- Hot partition = anti-pattern. Max item size 400 KB.

---

## [07 — SQS, SNS & EventBridge](_07_sqs_sns.md)
Messaging and event routing.

| Service | Pattern | Key fact |
|---|---|---|
| **SQS Standard** | Queue (pull) | At-least-once, best-effort order, unlimited throughput |
| **SQS FIFO** | Queue (pull) | Exactly-once, strict order, 300 msg/s |
| **SNS** | Pub/Sub (push) | Topic → many subscribers (SQS, Lambda, HTTP, email) |
| **EventBridge** | Event router | Rich JSON pattern matching, SaaS integration, cron |

- **Visibility timeout** > Lambda timeout (or messages re-deliver).
- **DLQ** = messages that fail N times land here. Monitor DLQ depth.
- **Fan-out**: SNS → multiple SQS queues.
- Spring Cloud AWS: `@SqsListener("queue-name")` auto-polls and acks.

---

## [08 — API Gateway](_08_api_gateway.md)
Managed HTTP API front door.

- **REST API** = full-featured. **HTTP API** = simpler, 70% cheaper — prefer for new projects.
- Lambda proxy integration: receive `APIGatewayProxyRequestEvent`, return `{statusCode, headers, body}`.
- Auth options: IAM, Cognito JWT, Lambda authorizer.
- Hard limit: **29-second timeout** (even if Lambda timeout is higher).
- CORS: configure at API Gateway level.

---

## [09 — VPC](_09_vpc.md)
Your private isolated network in AWS.

- **Public subnet** = has route to Internet Gateway. **Private subnet** = no direct internet.
- **NAT Gateway** = lets private subnet reach internet outbound. Per-AZ (deploy in each AZ for HA).
- **Security Group** = stateful, instance-level, allow-only rules.
- **NACL** = stateless, subnet-level, allow + deny rules.
- **VPC Endpoint** = access S3/DynamoDB/SQS without leaving AWS network.
- Self-invocation bypass: Lambda in VPC needs NAT GW to call other AWS APIs (unless VPC endpoints).

---

## [10 — CloudWatch, X-Ray, CloudTrail](_10_cloudwatch.md)
Observability stack.

| Service | What it gives you |
|---|---|
| **CloudWatch Metrics** | Numeric time-series. Alarms → SNS / Auto Scaling. |
| **CloudWatch Logs** | Log groups/streams. Logs Insights for queries. Metric Filters. |
| **X-Ray** | Distributed tracing. Trace → Segments → Subsegments. Service map. |
| **CloudTrail** | Every AWS API call. Who did what, when. Security audit. |

- Lambda logs auto → CloudWatch. Set retention explicitly (default = never expire = $$).
- X-Ray: `TracingInterceptor` on SDK clients. `AWSXRay.beginSubsegment()` for custom spans.

---

## [11 — ElastiCache](_11_elasticache.md)
Managed Redis or Memcached. Sub-millisecond reads.

- Redis almost always wins: rich data types, persistence, pub/sub, sorted sets.
- Patterns: cache-aside, session store, rate limiting, distributed lock, leaderboard.
- Spring: `@Cacheable` / `@CacheEvict` with `spring.data.redis.*` config.
- Always in private subnet. Set TTL on every key. Eviction policy: `allkeys-lru`.

---

## [12 — ECS, EKS & ECR](_12_ecs_eks.md)
Managed containers.

- **ECR** = private Docker registry.
- **ECS + Fargate** = serverless containers. Task Definition → Service → ALB. Simplest AWS-native option.
- **EKS** = managed Kubernetes. Choose when team knows K8s or needs K8s ecosystem.
- Secrets in Task Definition via `secrets` → Secrets Manager/SSM (never bake into image).
- Task role (app) ≠ Execution role (ECS agent).

---

## [13 — Secrets Manager & KMS](_13_secrets_kms.md)
Secrets storage and encryption keys.

- **Secrets Manager**: store DB passwords, API keys. Auto-rotation. Spring Cloud AWS injects at startup via `spring.config.import`.
- **Parameter Store (SSM)**: config + lightweight secrets. Free tier. No auto-rotation.
- **KMS**: managed encryption keys. Envelope encryption: KMS generates data key → you encrypt data locally → store encrypted key + ciphertext.
- **Cognito**: User Pools (auth, issues JWT) + Identity Pools (exchange JWT for AWS credentials).

---

## [14 — AWS SDK v2 & Spring Cloud AWS](_14_java_sdk.md)
Java integration essentials.

- SDK v2: `software.amazon.awssdk`. Builder pattern. Sync + async clients. Thread-safe — make `@Bean`.
- Credential auto-discovery: env → system props → `~/.aws` → container → EC2 metadata.
- SDK retries transient errors automatically — don't add your own retry loop.
- Spring Cloud AWS 3.x: `@SqsListener`, `spring.config.import: aws-secretsmanager:...`, ResourceLoader for S3.
- Lambda: init SDK clients in static block (survives warm invocations). Use `aws-lambda-java-events` for typed events.
- Cold start options: SnapStart (Corretto 21), GraalVM native (Quarkus/Micronaut), provisioned concurrency.

---

## Architecture patterns (interview scenarios)

### REST API (serverless)
```
Client → API Gateway → Lambda → DynamoDB / RDS (via RDS Proxy)
                              → S3 (presigned URL for uploads)
                              → SQS (async work)
```

### Microservice (containerized)
```
Client → ALB → ECS Fargate (Spring Boot) → RDS Aurora (private subnet)
                                          → ElastiCache Redis (session/cache)
                                          → SQS (async events)
                                          → Secrets Manager (credentials)
```

### Event-driven pipeline
```
S3 upload → S3 Event → SNS → [SQS → Lambda (process)] × N services
DynamoDB change → DynamoDB Streams → Lambda (index in OpenSearch / notify)
```

### CI/CD
```
Git push → CodePipeline → CodeBuild (mvn package, docker build, push to ECR)
         → CodeDeploy → ECS rolling update / Lambda version alias shift
```
