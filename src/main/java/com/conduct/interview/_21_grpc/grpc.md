# gRPC

## What is it?

RPC framework by Google. Uses **HTTP/2** as transport and **Protocol Buffers** (protobuf) as serialization.
Define your API in a `.proto` file → generate client + server stubs in any language.

```protobuf
syntax = "proto3";

service OrderService {
  rpc GetOrder (GetOrderRequest) returns (Order);
  rpc StreamOrders (GetOrdersRequest) returns (stream Order);  // server streaming
}

message GetOrderRequest { string order_id = 1; }
message Order {
  string id = 1;
  double amount = 2;
  string status = 3;
}
```

```java
// Generated stub usage (client)
OrderServiceGrpc.OrderServiceBlockingStub stub = OrderServiceGrpc.newBlockingStub(channel);
Order order = stub.getOrder(GetOrderRequest.newBuilder().setOrderId("123").build());
```

---

## Why gRPC over REST?

| | REST (JSON/HTTP1.1) | gRPC (Protobuf/HTTP2) |
|---|---|---|
| Payload | Text JSON (~verbose) | Binary protobuf (~3-10× smaller) |
| Speed | Slower (text parsing) | Faster (binary, multiplexed) |
| Contract | Optional (OpenAPI) | Mandatory `.proto` (strict) |
| Streaming | Workaround (SSE/WS) | Native (4 modes) |
| Browser support | Universal | Limited (needs grpc-web proxy) |
| Code generation | Optional | Built-in for all languages |
| Error model | HTTP status codes | Rich status codes + details |

**Choose gRPC when**: internal service-to-service, polyglot microservices, low latency, streaming needed.
**Choose REST when**: public API, browser clients, simple CRUD, team unfamiliar with protobuf.

---

## 4 Communication Modes

```protobuf
rpc Unary       (Request)        returns (Response);          // classic req/resp
rpc ServerStream(Request)        returns (stream Response);   // server pushes N responses
rpc ClientStream(stream Request) returns (Response);          // client sends N, server responds once
rpc BiDiStream  (stream Request) returns (stream Response);   // chat-style, both sides stream
```

---

## HTTP/2 Benefits (why gRPC is fast)

- **Multiplexing**: multiple requests over one TCP connection, no head-of-line blocking.
- **Header compression** (HPACK): repeated headers (auth token, content-type) sent once.
- **Binary framing**: no text parsing overhead.
- **Server push**: server can proactively send data (used less in practice).

REST over HTTP/1.1 opens a new TCP connection (or reuses but serially) per request.

---

## Spring Boot Integration

```xml
<dependency>
  <groupId>net.devh</groupId>
  <artifactId>grpc-spring-boot-starter</artifactId>
  <version>3.1.0.RELEASE</version>
</dependency>
```

```java
@GrpcService
public class OrderGrpcService extends OrderServiceGrpc.OrderServiceImplBase {
    @Override
    public void getOrder(GetOrderRequest req, StreamObserver<Order> resp) {
        Order order = orderService.find(req.getOrderId());
        resp.onNext(order);
        resp.onCompleted();
    }
}
```

---

## Error Handling

gRPC has richer status codes than HTTP: `OK`, `NOT_FOUND`, `INVALID_ARGUMENT`, `UNAVAILABLE`,
`DEADLINE_EXCEEDED`, `UNAUTHENTICATED`, `PERMISSION_DENIED`, `INTERNAL`, etc.

```java
resp.onError(Status.NOT_FOUND
    .withDescription("Order " + id + " not found")
    .asRuntimeException());
```

---

## Deadlines / Timeouts

```java
stub.withDeadlineAfter(2, TimeUnit.SECONDS).getOrder(request);
// → DEADLINE_EXCEEDED if not completed in 2s
```

Always set deadlines — without them a slow server blocks the client indefinitely.

---

## Interview Points

- Protobuf is ~3-10× smaller and faster to serialize than JSON. Fields are numbered not named — safe to add new fields (backward compatible), never reuse field numbers.
- HTTP/2 multiplexing = one connection for all calls → lower overhead than HTTP/1.1 connection-per-request.
- gRPC-web is needed for browser clients (browser can't use raw HTTP/2 framing) → adds proxy complexity.
- Streaming is gRPC's killer feature vs REST. Bi-directional streaming ≈ WebSocket but with typed contract.
- Always set deadlines. Propagate them through call chains (deadline propagation).
- Schema evolution: add fields (backward compat), never remove or change field numbers.
