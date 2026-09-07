# Webhooks

## What is it?

A **webhook** is event delivery over plain HTTP: when something happens in system A, it sends
an HTTP POST to a URL registered by system B. No broker, no polling — B provides a URL and waits.

```
Event happens in A
      ↓
A → POST https://b.com/webhook  {"event": "order.placed", "orderId": "123"}
      ↓
B processes and responds 200 OK
```

Used by every major SaaS: GitHub (push events), Stripe (payment events),
Shopify (order events), Twilio (SMS delivery receipts).

**When to use:**
- Crossing organizational boundaries — you can't give a third party access to your Kafka cluster,
  but they can receive HTTP calls.
- Partners and external integrations.
- Simple event notification without shared infrastructure.

**Not a good fit when:** you need fan-out to many consumers, high throughput, replay, or
guaranteed ordering — use a broker instead.

---

## Pattern

```
Event happens in producer
        ↓
Producer → POST https://consumer.com/webhook  { "event": "order.placed", "orderId": "123" }
        ↓
Consumer processes and responds 200 OK
        ↓
Producer marks delivery as done
```

If the consumer returns non-2xx or times out → producer retries with backoff.

---

## Delivery

```java
// Minimal webhook sender with retry
public class WebhookSender {

    private final HttpClient http = HttpClient.newHttpClient();

    public void deliver(String url, String payload) throws Exception {
        int attempts = 0;
        while (attempts < 5) {
            try {
                HttpRequest req = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Content-Type", "application/json")
                        .header("X-Signature", sign(payload))   // HMAC verification
                        .POST(HttpRequest.BodyPublishers.ofString(payload))
                        .timeout(Duration.ofSeconds(5))
                        .build();

                HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());

                if (resp.statusCode() >= 200 && resp.statusCode() < 300) return; // done

                System.out.println("Non-2xx " + resp.statusCode() + ", retrying...");
            } catch (IOException | InterruptedException e) {
                System.out.println("Timeout/error, retrying...");
            }
            Thread.sleep((long) Math.pow(2, attempts++) * 1000); // exponential backoff
        }
        // After 5 failures → move to DLQ / alert
        throw new RuntimeException("Webhook delivery failed after " + attempts + " attempts");
    }

    private String sign(String payload) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec("my-secret".getBytes(), "HmacSHA256"));
        return HexFormat.of().formatHex(mac.doFinal(payload.getBytes()));
    }
}
```

---

## Receiving a Webhook (Spring)

```java
@RestController
public class WebhookReceiver {

    @PostMapping("/webhook")
    public ResponseEntity<Void> receive(
            @RequestBody String payload,
            @RequestHeader("X-Signature") String signature) {

        // 1. Verify signature — reject forged requests
        if (!isValidSignature(payload, signature)) {
            return ResponseEntity.status(401).build();
        }

        // 2. Return 200 IMMEDIATELY — do not process synchronously
        //    If processing is slow and you timeout, the sender will retry → duplicates
        asyncProcessor.submit(() -> process(payload));

        return ResponseEntity.ok().build();
    }
}
```

**Critical:** respond 200 fast. Offload processing to a queue/thread. If you block and timeout,
the sender retries — you process it twice.

---

## Delivery Guarantees

Webhooks are inherently **at-least-once**:
- Producer retries on failure → consumer may receive duplicates.
- Consumer must be **idempotent** — dedup by `event_id` in the payload.

```json
{
  "event_id": "evt_abc123",
  "event": "order.placed",
  "orderId": "order-42",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

```java
// Idempotent receiver
if (processedEventRepo.existsById(event.eventId())) return; // already done
processedEventRepo.save(event.eventId());
handle(event);
```

---

## Security: HMAC Signature Verification

```
Producer computes: HMAC-SHA256(secret, payload) → sends as X-Signature header
Consumer verifies: HMAC-SHA256(shared_secret, raw_body) == X-Signature
```

**Always verify the signature** — without it, anyone can POST to your webhook endpoint.
Use `MessageDigest.isEqual()` (constant-time) for comparison to prevent timing attacks.

---

## Vs Message Broker

| | Webhooks | Kafka / RabbitMQ |
|---|---|---|
| Transport | HTTP | TCP (binary protocol) |
| Consumer discovery | Consumer registers URL | Consumer connects to broker |
| Persistence | Producer's retry queue | Broker stores messages |
| Fan-out | Send to each URL separately | One topic, many consumers |
| Setup | Zero (just an HTTP endpoint) | Broker infrastructure |
| Use case | SaaS integrations, external partners | Internal service-to-service |

**Rule:** Webhooks for crossing organizational boundaries (external partners, SaaS).
Brokers for internal service communication you control.

---

## Interview Points

- Always return 200 immediately and process async — synchronous webhook receivers cause cascading retries.
- Always verify HMAC signature — otherwise your endpoint is open to spoofing.
- Idempotency key (`event_id`) is mandatory — retries will happen.
- Webhook delivery is at-least-once. Consumers must handle duplicates.
- Retry with exponential backoff + jitter to avoid thundering herd on the receiver.
- Expose a DLQ / alert when all retries exhausted — don't silently drop events.
