# API Gateway

## What it is
Managed front door for HTTP APIs. Handles routing, auth, throttling, TLS, CORS.
You define routes → they call Lambda, HTTP backends, or other AWS services.

## Types
| Type | Use case | Notes |
|---|---|---|
| **REST API** | Full-featured: API keys, usage plans, request transformation, caching | Older, more config |
| **HTTP API** | Simpler, cheaper (70% less), lower latency | JWT auth built-in, no usage plans |
| **WebSocket API** | Persistent bidirectional connections (chat, live data) | |

For new projects: **HTTP API** unless you need REST API features (caching, API keys, response transformation).

## Lambda proxy integration (most common)
API Gateway passes the entire request as a JSON event to Lambda.
Lambda must return a specific shape:
```json
{
  "statusCode": 200,
  "headers": { "Content-Type": "application/json" },
  "body": "{\"message\": \"ok\"}"
}
```
```java
public class ApiHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent req, Context ctx) {
        String body = req.getBody();
        Map<String, String> params = req.getPathParameters();
        return new APIGatewayProxyResponseEvent()
            .withStatusCode(200)
            .withBody("{\"result\":\"ok\"}");
    }
}
```

## Authorization
| Method | How |
|---|---|
| **IAM auth** | Caller signs request with SigV4. Good for service-to-service. |
| **Cognito User Pools** | API GW validates JWT issued by Cognito automatically. |
| **Lambda authorizer** | Your Lambda validates any token/header → returns IAM policy. Flexible. |
| **API key** | Simple key in `x-api-key` header. Rate limiting per key (REST API only). |

## Throttling & Quotas
- Account-level default: 10 000 req/s, 5 000 burst.
- Per-stage or per-method limits override account default.
- Returns `429 Too Many Requests` when throttled.

## Stages & Deployment
- **Stage** = deployment snapshot (dev, staging, prod).
- **Stage variables** = per-stage config (Lambda alias, backend URL, etc.).
- Changes to routes/integrations require a new deployment to take effect (REST API). HTTP API auto-deploys.

## Caching (REST API only)
- TTL 0–3600 s. Cache per stage, per method, optionally per query param/header.
- Reduces Lambda invocations for identical requests.

## Interview points
- API Gateway has 29-second timeout limit — Lambda must respond within 29 s even if its own timeout is higher.
- For high throughput (>10k RPS sustained): consider ALB → Lambda or ALB → ECS instead.
- HTTP API supports JWT authorizer natively; REST API needs Lambda authorizer for JWT.
- CORS: configure at API Gateway level (not in Lambda) — API GW adds `Access-Control-Allow-Origin` headers.
