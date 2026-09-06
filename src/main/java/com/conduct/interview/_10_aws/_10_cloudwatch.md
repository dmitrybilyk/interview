# CloudWatch, X-Ray & CloudTrail — Observability

---

## CloudWatch — Metrics & Logs

### Metrics
Time-series numeric data. Every AWS service publishes metrics automatically.

| Concept | Detail |
|---|---|
| **Namespace** | `AWS/EC2`, `AWS/Lambda`, `AWS/RDS`, or your custom namespace |
| **Dimension** | Key-value that identifies a specific resource (e.g. `FunctionName=my-lambda`) |
| **Resolution** | Standard = 1-minute granularity. High-resolution custom metrics = 1 second. |
| **Alarm** | Watches one metric. Triggers: SNS notification, Auto Scaling action, EC2 action. States: OK / ALARM / INSUFFICIENT_DATA |

**Useful Lambda metrics:** `Invocations`, `Errors`, `Duration`, `Throttles`, `ConcurrentExecutions`, `InitDuration` (cold start).

### Logs
- **Log Group** — per application/service (e.g. `/aws/lambda/my-function`).
- **Log Stream** — per instance/invocation within a group.
- **Retention** — set explicitly (1 day – 10 years) or logs never expire (expensive).
- **CloudWatch Logs Insights** — SQL-like query language to search and aggregate logs.
  ```
  fields @timestamp, @message
  | filter @message like /ERROR/
  | sort @timestamp desc
  | limit 50
  ```
- **Metric Filter** — extract a numeric metric from log text (e.g. count ERROR lines → alert).
- **Log subscription** → stream logs to Lambda, Kinesis, Firehose for real-time processing.

### Publishing custom metrics from Java
```java
CloudWatchClient cw = CloudWatchClient.create();
cw.putMetricData(r -> r
    .namespace("MyApp")
    .metricData(MetricDatum.builder()
        .metricName("OrderProcessingTime")
        .value(123.0)
        .unit(StandardUnit.MILLISECONDS)
        .dimensions(Dimension.builder().name("Service").value("checkout").build())
        .build()));
```

---

## X-Ray — Distributed Tracing
End-to-end request tracing across Lambda, API Gateway, SQS, DynamoDB, HTTP calls.

- **Trace** — one request's full journey through your system.
- **Segment** — one service's contribution to the trace.
- **Subsegment** — a specific operation (DB call, HTTP call) within a segment.
- **Service map** — visual graph of all connected services with latency and error rates.

### Java setup (Lambda)
```java
// 1. Enable active tracing on Lambda (console or IaC)
// 2. Add X-Ray SDK dependency
// 3. Wrap AWS clients to auto-instrument
DynamoDbClient ddb = DynamoDbClient.builder()
    .overrideConfiguration(ClientOverrideConfiguration.builder()
        .addExecutionInterceptor(new TracingInterceptor())  // X-Ray
        .build())
    .build();

// Custom subsegment:
Subsegment subsegment = AWSXRay.beginSubsegment("callExternalApi");
try { /* ... */ } finally { AWSXRay.endSubsegment(); }
```

Spring Boot: `aws-xray-recorder-sdk-spring` auto-instruments all HTTP requests.

---

## CloudTrail — API Audit Log
Records every AWS API call made in your account: who called what, when, from where.

- **Event types:** Management events (IAM, EC2 start/stop — on by default) and Data events (S3 GetObject, Lambda invoke — opt-in, costs extra).
- **Trail** — saves events to S3 + optionally CloudWatch Logs. Create a multi-region trail for full coverage.
- **Use for:** security audits, compliance, debugging "who deleted that S3 bucket?", alerting on sensitive API calls.

---

## Interview points
- CloudWatch = metrics + logs + alarms (your app's runtime health).
- X-Ray = distributed tracing (latency breakdown, which service is slow).
- CloudTrail = API audit log (security, compliance, forensics).
- CloudWatch != CloudTrail: CloudWatch tells you what the system is doing; CloudTrail tells you what humans/services did to the system.
- Lambda automatically sends logs to CloudWatch (`/aws/lambda/<function-name>`). No code needed.
- Set log retention explicitly — default is "never expire" = costs grow unboundedly.
