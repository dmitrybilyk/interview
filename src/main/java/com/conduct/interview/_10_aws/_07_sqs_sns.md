# SQS, SNS & EventBridge — Messaging

---

## SQS — Simple Queue Service
Managed message queue. Decouples producers from consumers.
Producer puts message → SQS stores it → Consumer polls and deletes.

### Standard vs FIFO
| | Standard | FIFO |
|---|---|---|
| Ordering | Best-effort | Strict (per message group) |
| Delivery | At-least-once (duplicates possible) | Exactly-once |
| Throughput | Unlimited | 300 msg/s (3000 with batching) |
| Use case | High throughput, order doesn't matter | Order matters (payment, inventory) |

### Key settings
| Setting | Meaning |
|---|---|
| **Visibility timeout** | After consumer reads a message, it's hidden for this duration. If not deleted → reappears. Default 30 s. Set to > max processing time. |
| **Message retention** | 1 min – 14 days (default 4 days). Message deleted after expiry. |
| **Long polling** | `WaitTimeSeconds=20` — waits up to 20 s for a message. Reduces empty receives and cost. Always prefer over short polling. |
| **DLQ (Dead Letter Queue)** | Separate queue for messages that failed `maxReceiveCount` times. Inspect + alert on DLQ depth. |
| **Batch** | Send/receive up to 10 messages in one API call (reduces cost and latency). |

### Java — Spring Cloud AWS
```java
@SqsListener("my-queue")          // auto polls, acks on success
public void handle(MyEvent event) {
    // process event (deserialized from JSON automatically)
}

// Or SDK directly
SqsClient sqs = SqsClient.create();
sqs.sendMessage(r -> r.queueUrl(queueUrl).messageBody(json).delaySeconds(0));

List<Message> msgs = sqs.receiveMessage(r -> r.queueUrl(url).waitTimeSeconds(20)
    .maxNumberOfMessages(10)).messages();
// after processing:
sqs.deleteMessage(r -> r.queueUrl(url).receiptHandle(msg.receiptHandle()));
```

### Lambda trigger
Lambda polls SQS (event source mapping). On success → auto-delete batch. On failure → partial batch failure (`ReportBatchItemFailures`) to avoid reprocessing good messages.

---

## SNS — Simple Notification Service
Managed pub/sub. One topic → push to multiple subscribers simultaneously (fan-out).

**Subscribers:** SQS queue, Lambda, HTTP endpoint, Email, SMS, mobile push.

```
SNS Topic
├── SQS Queue A  (orders service)
├── SQS Queue B  (analytics service)
└── Lambda C     (real-time notification)
```

**Fan-out pattern:** One S3 event → SNS → multiple SQS queues (decouple processing pipelines).

**Message filtering:** Subscriber can define filter policy — only receives messages matching attribute criteria.

```java
SnsClient sns = SnsClient.create();
sns.publish(r -> r.topicArn(topicArn)
    .message(json)
    .messageAttributes(Map.of("eventType",
        MessageAttributeValue.builder().dataType("String").stringValue("ORDER_PLACED").build())));
```

---

## EventBridge — Event Bus
Advanced event routing. Receives events from AWS services, your apps, SaaS (Datadog, Zendesk...).

- **Event bus** — default (AWS service events) or custom.
- **Rule** — matches event pattern (JSON path filter) → routes to target (Lambda, SQS, Step Functions...).
- **Scheduled rules** — cron/rate expressions (replace CloudWatch Events).

```json
// Event pattern: catch only EC2 instance-stop events in us-east-1
{
  "source": ["aws.ec2"],
  "detail-type": ["EC2 Instance State-change Notification"],
  "detail": { "state": ["stopped"] }
}
```

---

## SQS vs SNS vs EventBridge
| | SQS | SNS | EventBridge |
|---|---|---|---|
| Pattern | Queue (pull) | Push pub/sub | Event router |
| Persistence | Yes (up to 14 days) | No | No |
| Fan-out | No (1 consumer) | Yes | Yes |
| Filtering | No (FIFO has groups) | Attribute filter | Rich JSON pattern matching |
| Best for | Work queue, retry, DLQ | Fan-out to multiple targets | Complex routing, SaaS integration, cron |

## Interview points
- SQS is pull (consumers poll); SNS is push (subscribers receive).
- Visibility timeout < Lambda timeout = message re-delivered before processing finishes → duplicates. Set visibility timeout = 6× Lambda timeout.
- FIFO queue URL ends in `.fifo`. Deduplication ID prevents duplicate processing within 5-min window.
- EventBridge Pipes: SQS/DynamoDB/Kinesis → transform → target without writing code.
