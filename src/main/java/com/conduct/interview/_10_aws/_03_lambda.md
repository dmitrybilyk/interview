# Lambda — Serverless Functions

## What it is
Run code without provisioning servers. AWS manages scaling, patching, availability.
Billed per request + per GB-second of execution (first 1M requests/month free).

## How it works
1. Event source triggers Lambda (API Gateway, SQS, S3, etc.)
2. AWS provisions an execution environment (container + JVM)
3. Handler runs, returns result
4. Environment stays warm for a few minutes (reused for next invocation)

## Java handler
```java
// RequestHandler — Lambda serializes/deserializes JSON automatically
public class MyHandler implements RequestHandler<Map<String, String>, String> {
    @Override
    public String handleRequest(Map<String, String> event, Context ctx) {
        ctx.getLogger().log("Input: " + event);
        return "OK";
    }
}
// RequestStreamHandler — for raw InputStream/OutputStream (more control)
```

## Key config
| Setting | Notes |
|---|---|
| Memory | 128 MB – 10 GB. CPU scales proportionally with memory. |
| Timeout | Max **15 minutes**. Design short handlers; use Step Functions for longer workflows. |
| Concurrency | 1000 per account by default (soft limit). Reserved concurrency = guarantee + cap for one function. |
| Runtime | Java 17 / Java 21 (Corretto). Prefer Java 21 for best performance. |

## Cold start (Java-specific problem)
JVM startup + class loading makes the first invocation 1–10 s slower.
**Mitigations:**
- **SnapStart** (Corretto 21) — Lambda snapshots the initialized JVM state; restores it instead of booting from scratch. Add `@SnapStart` to handler. Only for synchronous invocations.
- Minimize dependencies (no full Spring Boot — use Micronaut, Quarkus, or plain SDK).
- Provisioned concurrency — keeps environments warm (costs money even when idle).

## Invocation types
| Type | Behavior |
|---|---|
| **Synchronous** (RequestResponse) | Caller waits. API Gateway, SDK calls. Errors returned to caller. |
| **Asynchronous** (Event) | Caller gets 202 immediately. S3 events, SNS. Lambda retries 2x on failure → DLQ. |
| **Polling** | Lambda polls SQS/Kinesis/DynamoDB Streams. Batch size configurable. |

## Event sources (Java dev perspective)
- **API Gateway** → HTTP handler, return `{statusCode, body}` map
- **SQS** → batch of messages, partial batch failure support
- **S3** → process uploaded files
- **DynamoDB Streams** → react to table changes (CDC)
- **EventBridge** → scheduled cron or event-driven

## Environment variables & config
- Env vars for config (DB URL, queue URL). Never put secrets in env vars plain — use Secrets Manager.
- `/tmp` — 512 MB (up to 10 GB) ephemeral storage per execution environment.
- Execution role — assign IAM role so Lambda can call S3, DynamoDB, etc.

## Interview points
- Lambda is stateless — no shared memory between invocations (use DynamoDB/ElastiCache for state).
- Max payload: 6 MB sync, 256 KB async (use S3 for large data).
- SnapStart vs provisioned concurrency: SnapStart is cheaper (no idle cost), provisioned is more predictable.
- Lambda inside VPC: needs ENI creation → adds cold start latency. Only put in VPC if you need RDS/ElastiCache access.
