# Microservices: Quick Overview

Microservices is an architectural style where a large application is built as a
collection of small, autonomous services. Each service runs its own process,
focuses on a single business capability, has its own database, and communicates
via lightweight protocols (like HTTP/REST, gRPC, or message brokers).

---

### The Core Idea
Instead of building a single, massive **Monolith** (where all code, logic, and
the database are bundled together), you break the system into independent,
specialized units (e.g., Auth Service, Order Service, Notification Service).

---

### Top 3 Pros (Why use them?)

* **Independent Deployment:** You can update, patch, or add features to one
  service without redeploying or risking the downtime of the entire system.
* **Granular Scalability:** If only one feature experiences high traffic (e.g.,
  payment processing during a sale), you can scale up just that specific service
  instead of replicating the whole application.
* **Fault Isolation:** If a bug causes an `OutOfMemoryError` in the reporting
  service, the checkout and login services keep working fine. The whole system
  doesn't crash.

---

### Top 3 Cons (The price you pay)

* **Distributed System Complexity:** Debugging, logging, and monitoring become
  harder. Tracking a single user request across 10 network calls requires complex
  distributed tracing tools.
* **Data Consistency:** Since each service owns its database (Database-per-service),
  maintaining strict data consistency across boundaries is tough, often requiring
  event-driven architectures or patterns like Saga.
* **Infrastructure Overhead:** You need robust CI/CD pipelines, containerization
  (Docker/Kubernetes), and API Gateways from day one, which drastically increases
  initial setup costs.


# Why "Database-per-Service" is a Must in Microservices

In a microservices architecture, giving each service its own private database
is considered a fundamental rule. If multiple services share a single database,
you lose almost all the benefits of microservices and end up with a **Distributed
Monolith**.

Here is why strict database isolation is mandatory:

---

## 1. Complete Loose Coupling
If Service A and Service B query the exact same database tables, they are tightly
coupled at the data layer.
* **The Problem:** If Team A wants to change a column name or modify a schema
  to implement a new feature, they will break Team B's service.
* **The Solution:** With Database-per-Service, a service's database is completely
  private. It can only be accessed via that service's public API. Team A can
  change their schema whenever they want without impacting anyone else.

---

## 2. Polyglot Persistence (Right Tool for the Job)
Different microservices handle different types of data and workloads. A single
database engine rarely fits every single use case.
* **The Problem:** A monolithic DB forces you to store everything in one format
  (usually Relational).
* **The Solution:** You can choose the database that perfectly matches the
  service's business logic:
    * **Order Service:** Needs ACID transactions $\rightarrow$ PostgreSQL.
    * **Product Catalog:** Needs fast read/write for dynamic attributes $\rightarrow$ MongoDB.
    * **Recommendation Engine:** Needs graph relations $\rightarrow$ Neo4j.
    * **Session/Cache Service:** Needs ultra-fast key-value lookups $\rightarrow$ Redis.



---

## 3. Independent Scaling and Performance Isolation
In a shared database, one poorly written query or a massive spike in traffic
on a single feature can lock tables, exhaust the connection pool, and take
down the entire system.
* **The Problem:** A traffic spike on the Notification Service shouldn't slow
  доун critical payment processing.
* **The Solution:** Isolating the databases ensures that each service has its
  own dedicated CPU, memory, and connection limits. If the Inventory DB is
  struggling under heavy load, the Auth DB remains completely unaffected.

---

## 4. Clear Team Ownership
Microservices align with organizational structures (Conway's Law). A small
team should fully own a service from the API down to the storage.
* **The Problem:** When multiple teams share a database, data ownership becomes
  blurry. Who is responsible for optimizing index performance? Who approves a
  data migration?
* **The Solution:** One team owns the service, the code, and the database.
  They have full autonomy and accountability for its performance and uptime.

---

## The Trade-off: What Makes It Hard?
While mandatory, Database-per-Service introduces major distributed data challenges:
* **No Distributed Joins:** You cannot write a simple `JOIN` query across two
  services. You must fetch data via API calls or aggregate data asynchronously.
* **Eventual Consistency:** Traditional ACID transactions across multiple
  databases are impossible. You must rely on event-driven architectures and
  patterns like **Saga** to manage distributed transactions.

# Service Discovery in Microservices

Service discovery is the dynamic process by which microservices automatically
detect and locate each other's network addresses (IP addresses and ports)
within a distributed system.

---

## Why It Is Crucial

In modern cloud-native and containerized environments (like Kubernetes or Cloud
environments), service instances are highly dynamic. You cannot rely on static,
hardcoded IP addresses because:

* **Dynamic Scaling:** Instances are constantly created or destroyed to handle
  traffic changes.
* **Upgrades & Deployments:** Rolling updates spin up new containers with new
  IPs and kill the old ones.
* **Self-Healing:** If a container or node crashes, the orchestrator replaces
  it immediately on a different host with a completely different network location.

---

## Core Components

1.  **Service Registry:** A centralized database containing the network locations
    of all available service instances (e.g., Netflix Eureka, Consul, or
    built-in Kubernetes DNS).
2.  **Registration:** When a microservice instance starts up, it automatically
    registers its network location and health status with the Service Registry.
3.  **Discovery Mechanism:**
    * **Client-Side:** The calling service queries the registry directly to
      get a list of available endpoints and chooses one to call.
    * **Server-Side:** The calling service sends the request to a router/load
      balancer, which queries the registry and routes the traffic to an active
      instance.

> **Key Takeaway:** Service discovery abstracts the physical infrastructure,
> allowing services to communicate seamlessly using logical names rather than
> fragile, unstable network locations.


# Architectural Resiliency in Microservices

When moving away from a monolith, network communication becomes the default. Because
distributed systems are prone to partial failures (network latency, service crashes,
unresponsive dependencies), building resiliency directly into your architecture is
critical to prevent a single failure from cascading across the entire system.

---

## Core Resiliency Patterns

To prevent a single slow or broken microservice from consuming all resources (such
as thread pools or memory) and bringing down other dependent services, you must
implement specific design patterns.

### 1. Circuit Breaker
Acts like an electrical circuit breaker. When a dependent service fails repeatedly
beyond a specified threshold, the circuit opens, and all subsequent requests fail
immediately without waiting for a timeout. This protects the calling service's
resources and gives the failing service time to recover.



* **Closed State:** Traffic flows normally. Failures are tracked.
* **Open State:** Traffic is blocked immediately. Fallback behavior is executed.
* **Half-Open State:** Periodically allows a limited number of requests through
  to test if the underlying service has recovered.

### 2. Timeouts
Never let a thread wait indefinitely for a response. Always enforce a maximum
waiting time for inter-service HTTP, gRPC, or database calls. If a service does
not respond within the timeout window, the calling service releases the thread
and handles the failure gracefully.

### 3. Retries with Exponential Backoff
If a request fails due to a transient issue (e.g., momentary network jitter),
retrying the request can solve the problem. However, immediate, continuous
retries can overload an already struggling service. Always use **Exponential
Backoff** (increasing the wait time between each retry) combined with **Jitter** (randomizing the wait times) to spread out the load.

### 4. Bulkhead Pattern
Isolates elements of an application into pools so that if one fails, the others
will continue to function. Named after the partitioned sections of a ship's hull
that prevent the entire ship from sinking if one section takes on water. In
software, this means isolating resource pools (like separate thread pools or
HTTP client instances) for different microservices.

---

## Observability Connection

You cannot maintain a resilient system without visibility. When a circuit breaker
opens or timeouts trigger, your system must immediately emit metrics and traces
so you can diagnose the root cause before a cascading failure occurs.


# Distributed Transactions and Data Consistency with MongoDB

Transitioning from a monolith to a microservice architecture introduces a major
challenge: **maintaining data consistency across distributed boundaries.** In a monolithic application, you can rely on MongoDB's native multi-document
ACID transactions to guarantee that either all updates succeed or they all roll
back together. Once you split your application into three distinct services (each
owning its own database), a single local transaction cannot span across network
boundaries.

---

## Key Problems You Will Face

### 1. Dual Writes and Partial Failures
If a business process requires updating data in Service A, Service B, and Service
C, you have to make three separate network calls. If Service A and Service B
succeed, but Service C fails (due to a crash, network error, or validation
failure), your system enters an inconsistent, "half-baked" state.

### 2. Lack of Isolation (The "I" in ACID)
In a single database transaction, changes are hidden from other users until the
transaction commits. In microservices, as soon as Service A commits its local
transaction, those changes are immediately visible to the rest of the world,
even though Service B and Service C haven't finished their work yet. This can
lead to dirty reads or incorrect data reporting.

### 3. Distributed Deadlocks
If Service A is waiting for a response from Service B, and Service B is waiting
for an update to finish in Service A, you can end up with a deadlock across the
network that is incredibly difficult to detect and resolve.

---

## How to Solve It in Microservices

Because traditional two-phase commit (2PC) protocols are slow, introduce heavy
locking, and don't scale well in cloud-native environments, you typically have
to choose alternative architectural patterns:

### The Saga Pattern (Recommended)
A Saga is a sequence of local transactions. Each local transaction updates the
database within a single service and triggers the next step via an event or
message. If a step fails, the Saga executes **compensating transactions**—explicit
reverse operations (like a delete or an undo update) that run backward to restore
the system to a balanced state.
* **Orchestration:** A central controller service directs the participants.
* **Choreography:** Services listen to events from each other and react
  autonomously.

### Outbox Pattern for Reliable Messaging
When using asynchronous message brokers (like Kafka or RabbitMQ) to coordinate
these services, you face the risk of updating MongoDB but failing to publish the
corresponding integration event. The Outbox pattern solves this by saving the
event directly into a dedicated `outbox` collection in your local MongoDB instance
within the *same* local transaction, and then a separate process polls that
collection and publishes the messages.

### Designing for Eventual Consistency
You must shift your mindset from *immediate consistency* to *eventual consistency*.
The system will not be perfectly consistent at every microsecond, but it will
eventually reach a consistent state once all messages and compensating actions
finish processing.


# Authentication Architecture in Microservices

Implementing authentication in a microservice architecture requires moving away
from monolithic session-based security (like HTTP sessions stored in memory)
toward stateless, token-based security (typically using JSON Web Tokens - JWT).

When design decisions are made around *where* and *how* tokens are validated,
two primary patterns emerge: Centralized Gateway Authentication and Shared
Security Auto-Configuration.

---

## Pattern 1: Centralized Authentication via API Gateway

In this architecture, a single API Gateway acts as the entry point for all
external client traffic. The gateway handles the heavy lifting of security,
shielding the internal microservices from unauthenticated requests.



### How It Works
1.  **Login:** The client sends credentials to an Auth Service and receives a JWT.
2.  **Request:** The client passes the JWT in the `Authorization: Bearer <token>`
    header to the API Gateway.
3.  **Validation:** The API Gateway validates the token's signature, expiration,
    and claims.
4.  **Forwarding:** If valid, the gateway strips or mutates the header, passing
    down stream internal user context (like User ID and Roles) to the downstream
    microservices via custom HTTP headers (e.g., `X-User-Id`, `X-User-Roles`).

### Pros & Cons
* **Pros:** Internal microservices do not need to know about cryptography, JWT
  signing keys, or token expiration. They simply trust the incoming headers
  from the gateway.
* **Cons:** The gateway becomes a single point of failure and a potential
  performance bottleneck for cryptographic verification.

---

## Pattern 2: Decentralized Shared Security Library (Auto-Configuration)

If you prefer not to rely on a central gateway to validate every single token—or
if your microservices need to validate tokens directly for internal mesh
communication—you can distribute the security configuration using an
**Auto-Configuration Library** (e.g., a custom Spring Boot Starter).

### How It Works
Instead of writing security configurations repeatedly in all three of your
microservices, you extract the security layer into a shared library.

[ Custom Security Starter ]
│
├─► Embedded in ──► Service A (Validates JWT locally)
├─► Embedded in ──► Service B (Validates JWT locally)
└─► Embedded in ──► Service C (Validates JWT locally)

1.  **The Shared Artifact:** You build a common library containing your Spring
    Security configuration, custom `AuthenticationManager`, `JwtAuthenticationFilter`,
    and token parsing logic.
2.  **Auto-Configuration:** Using Spring/Kotlin factories (`META-INF/spring.factories`
    or the new `Spring.factories` in Boot 3), this configuration automatically
    activates whenever the library is included as a dependency in a microservice's
    `build.gradle.kts`.
3.  **Local Validation:** Each microservice independently intercepts incoming
    HTTP/gRPC requests, extracts the JWT, and verifies its signature using a shared
    secret or a public key fetched from a central identity provider (like Keycloak
    via a JWKS endpoint).

### Critical Challenges with Shared Auto-Configuration

While this avoids a centralized gateway bottleneck, it introduces specific
distributed systems traps:

* **Dependency Coupling:** If you need to update the security logic, fix a
  vulnerability, or rotate a key strategy, you must update the library version,
  recompile, and redeploy **all three microservices** simultaneously.
* **Language Lock-in:** This approach binds your architecture to a specific
  ecosystem. If you write a Spring Boot starter, you cannot easily introduce a
  Node.js or Go microservice into your system later without rewriting the entire
  security layer in that language.
* **Resource Overhead:** Every single microservice instance performs CPU-heavy
  cryptographic signature verification on every incoming request, increasing
  overall CPU utilization across your cluster.