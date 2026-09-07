# SSE and Polling — Real-Time Push to the Browser

## What is it?

Techniques for delivering server-side events to a **browser** (or any HTTP client) in real time,
without a message broker. These are the patterns you reach for when the consumer is a frontend —
Kafka and RabbitMQ are server-side tools; browsers speak HTTP.

Three approaches from simplest to most capable:

| | Short Polling | Long Polling | SSE | WebSocket |
|---|---|---|---|---|
| Idea | Client asks repeatedly | Client waits for answer | Server pushes | Both sides push |
| Protocol | HTTP | HTTP | HTTP (streaming) | WS |
| Latency | Up to interval | Near real-time | Real-time | Real-time |

**When to use:**
- Live order status, progress bars, log streaming, notifications → **SSE** (simplest server-push).
- Chat, collaborative editing, gaming → **WebSocket** (bidirectional).
- Infrequent updates, legacy compatibility → **polling**.

No docker needed — all patterns work with a plain Spring Boot app.

---

Ways to deliver events from server to browser (or any HTTP client) without a message broker.

---

## Short Polling

Client asks "anything new?" on a fixed interval.

```
Client                     Server
  |── GET /orders/status ──→|
  |←──── {status: pending}──|
  ... wait 3s ...
  |── GET /orders/status ──→|
  |←──── {status: shipped}──|
```

```javascript
// Browser
setInterval(async () => {
    const res = await fetch('/orders/status');
    const data = await res.json();
    updateUI(data);
}, 3000);
```

**Trade-off:**
- Simple to implement.
- Wasted requests when nothing changes. High load at scale.
- Latency = up to polling interval (3s in example above).
- Use: admin dashboards, low-frequency updates (minutes), simple cases.

---

## Long Polling

Client asks, server **holds the request open** until there is new data (or timeout).
Client immediately reconnects after receiving a response.

```
Client                     Server
  |── GET /events/next ────→| (server holds request, no response yet)
  |                          | ... event happens ...
  |←───── {event: OrderShipped} ──|
  |── GET /events/next ────→| (immediately reconnects)
```

```java
// Spring MVC — hold request open with DeferredResult
@GetMapping("/events/next")
public DeferredResult<ResponseEntity<String>> nextEvent() {
    DeferredResult<ResponseEntity<String>> result = new DeferredResult<>(30_000L); // 30s timeout
    result.onTimeout(() -> result.setResult(ResponseEntity.noContent().build()));
    eventQueue.register(result); // notified when an event arrives
    return result;
}
```

**Trade-off:**
- Lower latency than short polling (response is immediate when event happens).
- One open connection per client — 10k clients = 10k held connections.
- Still HTTP: each response requires a full new request-response cycle.
- Use: chat apps, notifications, when SSE is not available.

---

## Server-Sent Events (SSE)

One persistent HTTP connection. Server pushes events whenever it wants. Client never re-requests.
Native browser support via `EventSource` API. Unidirectional (server → client only).

```
Client                         Server
  |── GET /stream (keep-alive) →|
  |                              | event happens
  |←── data: {"event":"shipped"}|
  |                              | event happens
  |←── data: {"event":"paid"}   |
  |                              | ...
```

### Spring WebFlux (reactive, non-blocking)

```java
@RestController
public class OrderStreamController {

    private final Sinks.Many<String> sink = Sinks.many().multicast().onBackpressureBuffer();

    @GetMapping(value = "/stream/orders", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> streamOrders() {
        return sink.asFlux()
                .map(event -> ServerSentEvent.<String>builder()
                        .event("order-update")
                        .data(event)
                        .build());
    }

    // Called when an order event occurs (from Kafka listener, service, etc.)
    public void pushEvent(String json) {
        sink.tryEmitNext(json);
    }
}
```

### Browser client

```javascript
const es = new EventSource('/stream/orders');
es.addEventListener('order-update', e => {
    const order = JSON.parse(e.data);
    updateOrderTable(order);
});
es.onerror = () => console.log('SSE error — browser auto-reconnects');
```

**Trade-off:**
- Very efficient — one persistent connection, server pushes when ready.
- Browser auto-reconnects on disconnect (`EventSource` does this natively).
- Unidirectional — client can't send data back on the same connection (use regular HTTP for that).
- HTTP/1.1: limited to ~6 connections per origin. HTTP/2: no limit (multiplexing).
- Use: live order tracking, notifications, dashboards, progress bars, logs streaming.

---

## WebSocket (bonus — bidirectional)

Full-duplex, both sides can send any time. Different protocol (WS/WSS, not HTTP after handshake).

```java
// Spring WebSocket
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    public void registerWebSocketHandlers(WebSocketHandlerRegistry r) {
        r.addHandler(new OrderWebSocketHandler(), "/ws/orders").setAllowedOrigins("*");
    }
}

public class OrderWebSocketHandler extends TextWebSocketHandler {
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // client sent something (e.g., subscribe to specific order)
        session.sendMessage(new TextMessage("{\"status\":\"acknowledged\"}"));
    }
}
```

**When SSE > WebSocket:**
- You only need server → client (SSE is simpler, works over HTTP/2, no special server config).

**When WebSocket > SSE:**
- You need client → server (chat, collaborative editing, game state).

---

## Comparison

| | Short Poll | Long Poll | SSE | WebSocket |
|---|---|---|---|---|
| Direction | Client pull | Client pull | Server push | Bidirectional |
| Protocol | HTTP | HTTP | HTTP | WS |
| Latency | Up to interval | Near real-time | Real-time | Real-time |
| Connections | Many (1/request) | 1 held/client | 1 held/client | 1 held/client |
| Browser support | Universal | Universal | All modern | All modern |
| Reconnect | Manual | Manual | Auto (EventSource) | Manual |
| Use case | Simple updates | Compatibility | Live feeds | Chat/gaming |

---

## Interview Points

- SSE is underused — for server-push it's simpler than WebSocket and works over HTTP/2 multiplexing.
- Long polling was the pre-SSE standard; still useful when SSE is blocked by proxies.
- Short polling is fine for infrequent updates (every minute) but kills servers at scale.
- WebSocket requires sticky sessions or a shared pub/sub (Redis) to broadcast to all connected clients across pods.
- SSE with Spring WebFlux (Flux + `TEXT_EVENT_STREAM_VALUE`) is non-blocking — one thread serves thousands of connections.
- For Kubernetes/load balancer: SSE/WS need sticky sessions or a shared event bus (Redis pub/sub feeds all pod's SSE sinks).
