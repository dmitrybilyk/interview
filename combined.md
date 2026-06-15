# Interview Knowledge Base — Combined

> Generated: 2026-06-09  
> Source: all `.md` files in this repository (excluding `node_modules` and bundled third-party tools)

---


---

## `aws/README.md`

AWS Lambda function


---

## `certificated/certificates.md`

Certificate

A signed document that binds:
    - identity (CN / SAN)
    - public key
    - usage rules to a signature of a CA

Private key
    - proves identity
    - NEVER shared
    - stored only in keystores

CA (Certificate Authority)
A certificate with:
    BasicConstraints: CA=true
    KeyUsage: keyCertSign

Root CA
    - Self-signed
    - Trust anchor
    - Lives in truststores

Leaf certificate
    - Used by server/client
    - CA=false
    - Has serverAuth or clientAuth


@startuml
participant Client
participant Server
participant CA as "CA / Truststore"

Client -> Server: ClientHello\n(TLS versions, ciphers, ClientRandom)

Server -> Client: ServerHello\n(Chosen cipher, ServerRandom)
Server -> Client: Certificate Chain\n(Leaf + Intermediate)

Client -> CA: Verify certificate chain
CA --> Client: Root CA trusted

note right of Client
- Verify signatures
- Check SAN / hostname
- Check expiry
- Check EKU=serverAuth
  end note

Client -> Server: (implicit challenge)

Server -> Client: Signature\n(Signed handshake hash\nusing server PRIVATE key)

Client -> Client: Verify signature\n(using PUBLIC key from cert)

note right of Client
Only private-key holder
can create valid signature
end note

Client -> Server: Encrypted traffic starts
Server -> Client: Encrypted traffic starts

@enduml





1. Create Root CA
   (The only cert client must trust)

keytool -genkeypair \
-alias rootA \
-keyalg RSA \
-keysize 4096 \
-validity 3650 \
-storetype PKCS12 \
-keystore rootA.p12 \
-storepass changeit \
-ext bc=ca:true \
-ext ku=keyCertSign,cRLSign \
-dname "CN=RootCA-A, O=MyCompany, C=UA"

2. Export public cert:
keytool -exportcert \
   -alias rootA \
   -keystore rootA.p12 \
   -storepass changeit \
   -rfc \
   -file rootA.cer

3. Create ROOT CA B (subordinate root)
   (Root B is trusted only because Root A signs it)

keytool -genkeypair \
-alias rootB \
-keyalg RSA \
-keysize 4096 \
-validity 3650 \
-storetype PKCS12 \
-keystore rootB.p12 \
-storepass changeit \
-dname "CN=RootCA-B, O=MyCompany, C=UA"

4. Create CSR

keytool -certreq \
-alias rootB \
-keystore rootB.p12 \
-storepass changeit \
-file rootB.csr

5. Sign Root B with Root A

keytool -gencert \
-alias rootA \
-keystore rootA.p12 \
-storepass changeit \
-infile rootB.csr \
-outfile rootB-signed.cer \
-rfc \
-validity 3650 \
-ext bc=ca:true,pathlen:0 \
-ext ku=keyCertSign,cRLSign

6. Import Root A + signed Root B into Root B keystore

keytool -importcert -alias rootA -file rootA.cer -keystore rootB.p12 -storepass changeit
keytool -importcert -alias rootB -file rootB-signed.cer -keystore rootB.p12 -storepass changeit

7. Create SERVER keypair (leaf cert)

keytool -genkeypair \
-alias server \
-keyalg RSA \
-keysize 2048 \
-validity 825 \
-storetype PKCS12 \
-keystore server-new.p12 \
-storepass changeit \
-dname "CN=myserver.local, O=MyCompany, C=UA"

8. Create CSR

keytool -certreq \
-alias server \
-keystore server-new.p12 \
-storepass changeit \
-file server.csr

9. Sign SERVER cert with Root B

keytool -gencert \
-alias rootB \
-keystore rootB.p12 \
-storepass changeit \
-infile server.csr \
-outfile server-signed.cer \
-rfc \
-validity 825 \
-ext ku=digitalSignature,keyEncipherment \
-ext eku=serverAuth \
-ext san=dns:localhost,dns:myserver.local

10. Build SERVER keystore chain

keytool -importcert -alias rootA -file rootA.cer -keystore server-new.p12 -storepass changeit
keytool -importcert -alias rootB -file rootB-signed.cer -keystore server-new.p12 -storepass changeit
keytool -importcert -alias server -file server-signed.cer -keystore server-new.p12 -storepass changeit

11. Verify
    keytool -list -v -keystore server-new.p12 -storepass changeit



12. Create CLIENT truststore

keytool -importcert \
-alias rootA \
-file rootA.cer \
-keystore client-truststore-new.p12 \
-storetype PKCS12 \
-storepass changeit






---

## `designShortenService/LearningPlan.md`

## 🚀 URL Shortener: Full-Stack & DevOps Learning Plan

This roadmap is designed to take your **URL Generator** and **Redirector** services from 
simple Java apps to a professional, scalable distributed system.

---

### **Phase 1: Foundations & Clean Code (Weeks 1-2)**

* **[ ] OOP Principles:** Refactor code to use interfaces for storage and hashing.
* **[ ] SOLID Implementation:**
* **Single Responsibility:** Separate your REST Controller from Business Logic and Data Access.
* **Dependency Inversion:** Inject database dependencies rather than hardcoding them.


* **[ ] Design Patterns:** Implement **Strategy Pattern** (for switching hashing algorithms) and **Factory Pattern** (for URL object creation).
* **[ ] RESTful Excellence:** Implement proper HTTP status codes ( Created,  Redirects) and API Versioning (`/v1/`).

---

### **Phase 2: Communication & State (Weeks 3-4)**

* **[ ] Protobuf Integration:** Replace JSON with **Protocol Buffers** for faster, smaller data serialization.
* **[ ] gRPC Service:** Implement a high-performance gRPC contract between the Generator and Redirector.
* **[ ] WebSockets:** Build a real-time analytics dashboard that "pushes" live click data to a client.
* **[ ] Session Management:**
* **Stateless:** Implement **JWT (JSON Web Tokens)** for secure API access.
* **Distributed:** Use **Redis** to share session state across multiple service instances.



---

### **Phase 3: Persistence & Reactive Mastery (Weeks 5-6)**

* **[ ] NoSQL with MongoDB:** Migrate URL metadata from SQL/Memory to **MongoDB**.
* **[ ] Reactive Programming:** Refactor the Redirector using **Spring WebFlux** (Project Reactor) to handle high-concurrency "non-blocking" I/O.
* **[ ] Message Brokering:** Use **Kafka** to send "Click Events" asynchronously from the Redirector to an Analytics service.
* **[ ] Caching:** Implement **Redis** as a "Read-Through" cache to optimize the Redirector for viral links.

---

### **Phase 4: DevOps & Orchestration (Weeks 7-9)**

* **[ ] Advanced Docker:** Create multi-stage **Dockerfiles** and optimize image layers for faster builds.
* **[ ] Jenkins CI/CD:** Build a pipeline that triggers on **Bitbucket** pushes to:
* Run Unit/Integration Tests.
* Build & Push Docker images to a registry.


* **[ ] Kubernetes (K8s):** Write Deployment and Service manifests for both services.
* **[ ] Helm Charts:** Package your K8s manifests into **Helm Charts** to manage environment-specific configs (Dev vs. Prod).

---

### **Phase 5: Performance & Observability (Weeks 10-12)**

* **[ ] JVM Tuning:** Profile your application using **JVisualVM**; optimize Heap and Stack memory.
* **[ ] Load Testing:** Use **JMeter** or **Gatling** to find the breaking point of your Redirector service.
* **[ ] Monitoring:** Set up **Prometheus** for metric scraping and **Grafana** for visual dashboards.
* **[ ] Tracing:** Implement **OpenTelemetry** or Zipkin to trace a request as it moves through your distributed system.

---

**Would you like to start with Phase 1 by reviewing your current `TestClass` or URL Generator code for SOLID compliance?**

---

## `designShortenService/mongo-data/init_replica.md`

docker exec -it mongo1 mongosh


rs.initiate({
_id: "rs0",
members: [
{ _id: 0, host: "mongo1:27017" },
{ _id: 1, host: "mongo2:27017" },
{ _id: 2, host: "mongo3:27017" }
]
})



---

## `designShortenService/Readme.md`

docker-compose down -v

docker-compose up --build -d

dmytro@dmytro-N53SN:~/dev/projects/interview/jmetter/apache-jmeter-5.6.3/bin$ sh jmeter.sh

grafana dashboard - 4701



I have shortener service implemented. I have generator and redirector. I have separate service for spring cloud and separate for eureka. I also have postges db used and zookeeper to coordinate generation of short url - so that it's aligned between several instance of my generator service. so, I have also docker compose where I can have many instances of generator and redirector. I also have redis up. So, I would like to explore features of spring cloud as api gateway. Currently spring cloud as api gate way routes requests between 2 services. Explain to me advantage of such routing. Also I have configuration for rate limiting for generator service in api gateway. I don't allow more than 10 requests to generator service per minute. I would also like to explore other features of api gateway. to apply them and try. maybe caching? redis is up

sh /home/dik81/IdeaProjects/interview/jmetter/apache-jmeter-5.6.3/bin/jmeter.sh

http://localhost:5601/app/home#/

---

## `designUrlShortenerGenerator/jmeter/load_test.md`

rm -rf ./report results.jtl && jmeter -n -t load_test.jmx -l results.jtl -e -o ./report

---

## `designUrlShortenerGenerator/readme.md`

docker swarm init



### Get Bearer Token
POST http://localhost:8081/realms/url-shortener/protocol/openid-connect/token
Content-Type: application/x-www-form-urlencoded

client_id=url-generator-service&client_secret=my-secret&username=dmytro&password=test123&grant_type=password


###
POST http://localhost:8080/api/shorten
Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJ1bC15cVlUNG5XRXg5XzZKblNtdUl2NTdid0xSMmFlRjVYT0xLQlJBZ1JnIn0.eyJleHAiOjE3NjE5NDU2ODksImlhdCI6MTc2MTk0NTM4OSwianRpIjoiZjFmMDM2YzItNWY2YS00ZmU1LTgxOGYtMjJjYTE1M2IwZTNmIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgxL3JlYWxtcy91cmwtc2hvcnRlbmVyIiwic3ViIjoiYzA5ZmI2ZGItZGU2MC00N2Q1LThhN2EtMTI4ZjJiYTdlZDQwIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoidXJsLWdlbmVyYXRvci1zZXJ2aWNlIiwic2Vzc2lvbl9zdGF0ZSI6Ijc2YzlmOTI3LTMwYzktNDI3ZS05ZDhhLWE4ZWJiODVkNDFkNiIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiaHR0cDovL2xvY2FsaG9zdDo4MDgwIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJ1c2VyIl19LCJzY29wZSI6InByb2ZpbGUgZW1haWwiLCJzaWQiOiI3NmM5ZjkyNy0zMGM5LTQyN2UtOWQ4YS1hOGViYjg1ZDQxZDYiLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwicHJlZmVycmVkX3VzZXJuYW1lIjoiZG15dHJvIn0.XrijuOhT_PSO5yfiIZuxZk1SLN0hwJMAvE_A9KdjwmR3DZ9vNZWfDHXHjQiPlCkKukwXRRX3_NdyVOUODzbbrlW4oOKb_OS9Uf89wWjL26XXu8yZ46AS8Ob9R8agzZ_G_vmS2e5BdBy39brZTw4PoxJDgNuYtwybdRi2Jd3hmhV906U6Jx6UseE9iZRNk99SJ0if0nP3ECONVc9hCEmdvNj7FCHOuDwcCSKnCe2lrq7TFJhSoHfFYmaQjON8E19PH3F_wDpe859SEdrmIfWy-dfZTPJidcx2A7E-FnVLuFlPp8CpWivZkmUHaq50sU_Z_26ogUFkdPFKoDMrLW16fw
Content-Type: application/json

{
"url": "https://grok.com/c/0c0a78a1-4c79-43bf-bcb0-f25c42e05258"
}

---

## `designUrlShortenerRedirector/jmeter/jmeter.md`

rm -rf results_redirect.jtl report_redirect
jmeter -n -t redirect_test.jmx -l results_redirect.jtl -e -o ./report_redirect

---

## `designUrlShortenerRedirector/Readme.md`

snap install --edge grpcurl

grpcurl -plaintext localhost:9090 list

grpcurl -plaintext \
-d '{"shortCode": "abc123"}' \
localhost:9090 redirector.RedirectorService/Resolve


---

## `frontend/README.md`

# React + TypeScript + Vite

This template provides a minimal setup to get React working in Vite with HMR and some ESLint rules.

Currently, two official plugins are available:

- [@vitejs/plugin-react](https://github.com/vitejs/vite-plugin-react/blob/main/packages/plugin-react) uses [Babel](https://babeljs.io/) (or [oxc](https://oxc.rs) when used in [rolldown-vite](https://vite.dev/guide/rolldown)) for Fast Refresh
- [@vitejs/plugin-react-swc](https://github.com/vitejs/vite-plugin-react/blob/main/packages/plugin-react-swc) uses [SWC](https://swc.rs/) for Fast Refresh

## React Compiler

The React Compiler is not enabled on this template because of its impact on dev & build performances. To add it, see [this documentation](https://react.dev/learn/react-compiler/installation).

## Expanding the ESLint configuration

If you are developing a production application, we recommend updating the configuration to enable type-aware lint rules:

```js
export default defineConfig([
  globalIgnores(['dist']),
  {
    files: ['**/*.{ts,tsx}'],
    extends: [
      // Other configs...

      // Remove tseslint.configs.recommended and replace with this
      tseslint.configs.recommendedTypeChecked,
      // Alternatively, use this for stricter rules
      tseslint.configs.strictTypeChecked,
      // Optionally, add this for stylistic rules
      tseslint.configs.stylisticTypeChecked,

      // Other configs...
    ],
    languageOptions: {
      parserOptions: {
        project: ['./tsconfig.node.json', './tsconfig.app.json'],
        tsconfigRootDir: import.meta.dirname,
      },
      // other options...
    },
  },
])
```

You can also install [eslint-plugin-react-x](https://github.com/Rel1cx/eslint-react/tree/main/packages/plugins/eslint-plugin-react-x) and [eslint-plugin-react-dom](https://github.com/Rel1cx/eslint-react/tree/main/packages/plugins/eslint-plugin-react-dom) for React-specific lint rules:

```js
// eslint.config.js
import reactX from 'eslint-plugin-react-x'
import reactDom from 'eslint-plugin-react-dom'

export default defineConfig([
  globalIgnores(['dist']),
  {
    files: ['**/*.{ts,tsx}'],
    extends: [
      // Other configs...
      // Enable lint rules for React
      reactX.configs['recommended-typescript'],
      // Enable lint rules for React DOM
      reactDom.configs.recommended,
    ],
    languageOptions: {
      parserOptions: {
        project: ['./tsconfig.node.json', './tsconfig.app.json'],
        tsconfigRootDir: import.meta.dirname,
      },
      // other options...
    },
  },
])
```


---

## `k8s/HELP.md`

# Getting Started

### Reference Documentation

For further reference, please consider the following sections:

* [Official Gradle documentation](https://docs.gradle.org)
* [Spring Boot Gradle Plugin Reference Guide](https://docs.spring.io/spring-boot/3.5.9/gradle-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/3.5.9/gradle-plugin/packaging-oci-image.html)
* [Spring Reactive Web](https://docs.spring.io/spring-boot/3.5.9/reference/web/reactive.html)

### Guides

The following guides illustrate how to use some features concretely:

* [Building a Reactive RESTful Web Service](https://spring.io/guides/gs/reactive-rest-service/)

### Additional Links

These additional references should also help you:

* [Gradle Build Scans – insights for your project's build](https://scans.gradle.com#gradle)



---

## `k8s/measure-resources.md`


java \
-Xms1g \
-XX:NativeMemoryTracking=summary \
-XX:+UseContainerSupport \
-Xlog:os+container=info \
-jar k8s-0.0.1-SNAPSHOT.jar



ps aux | grep k8s-0.0.1-SNAPSHOT.jar | grep -v grep

ps -o pid,rss,cmd -p 7764
cat /proc/7764/status | egrep "VmRSS|VmHWM"

top -p 7764

K8s can use a bit more because of no swaps like on vm's and a bit of
container runtime overhead. 



ps -o pid,rss,vsz,cmd -p 6587
cat /proc/6587/status | grep "VmRSS|VmHWM"
rss - current memory
vmhwm - maximum memory usage

On k8s there are no swaps. Memory limit is hard wall. 

memory_limit =
peak_RSS (VmHWM)
+ 20–30% safety buffer

RES = 209,084 KB ✅ (THIS is real RAM)

to see real JVM breakdown:
java -jar -XX:NativeMemoryTracking=summary k8s-0.0.1-SNAPSHOT.jar
jcmd 6124 VM.native_memory summary

curl http://localhost:8080/hello

ab -n 10000 -c 50 http://localhost:8080/hello
jstat -gc 96125 1000
jstat -gc 96125 


ps -p 96125 -o pid,comm,rss,vsz


Quick heap overview:

jcmd 96125 GC.heap_info


java \
-Xms512m \
-Xmx2g \
-Xss1m \
-jar k8s-0.0.1-SNAPSHOT.jar


| Flag   | Meaning               |
| ------ | --------------------- |
| `-Xms` | Initial heap          |
| `-Xmx` | Max heap              |
| `-Xss` | Stack size per thread |


---

## `k8s/readme.md`

eval $(minikube docker-env)

minikube service spring-webflux --url

kubectl exec -it deploy/spring-webflux -- curl http://localhost:8080/hello
minikube service spring-webflux-demo -n demo --url
curl http://192.168.49.2:32373/hello
curl http://192.168.49.2:32614/hello


helm upgrade spring-webflux ./spring-webflux

docker build -t spring-webflux:0.0.1 .

kubectl config view --minify --output 'jsonpath={..namespace}'

helm install spring-webflux ./spring-webflux -n demo

kubectl config set-context --current --namespace=demo






---

## `k8s/src/main/java/com/learn/k8s/brokers/comparison.md`



---

## `k8s/src/main/java/com/learn/k8s/brokers/kafka/kafka.md`

🏗️ Основні поняття Apache Kafka
📂 Topic (Топік)
Це логічний канал або «папка», куди надсилаються повідомлення.

На відміну від черг у RabbitMQ, повідомлення в топіку не видаляються після прочитання. Вони зберігаються протягом налаштованого часу (Retention Policy).

🍰 Partition (Партіція)
Це фізична одиниця масштабування. Один топік розбивається на кілька партіцій.

Повідомлення всередині партіції мають суворий порядок.

Партіції дозволяють кільком консюмерам читати дані з одного топіка паралельно.

✍️ Producer (Продюсер)
Додаток, який публікує (пише) дані в топіки. Продюсер вирішує, у яку партіцію відправити повідомлення (зазвичай на основі ключа повідомлення).

🧐 Consumer (Консюмер)
Додаток, який підписується на топіки та читає повідомлення. Консюмер сам знає, на якому місці він зупинився.

👥 Consumer Group (Група споживачів)
Набір консюмерів, які працюють разом як одна команда.

Кожна партіція топіка обробляється лише одним консюмером всередині групи.

Це дозволяє горизонтально масштабувати обробку даних.

📍 Offset (Офсет)
Це унікальний номер (ID) повідомлення всередині партіції.

Це як «закладка» в книзі: консюмер зберігає свій офсет, щоб знати, 
звідки продовжити читання після перезавантаження.

🏢 Broker (Брокер)
Це сервер Kafka. Кластер Kafka складається з одного або декількох брокерів. 
Вони відповідають за зберігання повідомлень та обробку запитів від продюсерів і консюмерів.

🏁 Commit (Коміт)
Процес фіксації офсета в Kafka. Коли консюмер «комітить» офсет, він каже: 
«Я успішно обробив усі повідомлення до цього номера».

![img.png](img.png)



---

## `k8s/src/main/java/com/learn/k8s/brokers/rabbit/rabbit.md`

RabbitMQ Interview Guide
1. Core Architecture
   RabbitMQ is a Smart Broker. It manages message lifecycle and routing logic internally.

Producer: Sends messages to an Exchange.

Exchange: The routing engine (decides where data goes).

Binding: The link/rule between Exchange and Queue.

Queue: The buffer where messages sit.

Consumer: Grabs messages from the Queue.

2. Exchange Types (Routing)
   Direct: Exact match of Routing Key.

Use: Targeted tasks (e.g., logs.error).

Topic: Pattern match using wildcards.

*: Exactly one word.

#: Zero or more words.

Use: Geographic or categorized routing.

Fanout: Ignores keys; broadcasts to ALL bound queues.

Use: Pub/Sub, configuration updates.

Headers: Uses message header attributes instead of keys.

Use: Complex metadata routing.

3. Delivery Guarantees
   basicAck: Consumer says "I'm done, delete it."

basicNack: Consumer says "I failed."

requeue=true: Back to the queue.

requeue=false: Discard or send to DLX.

Prefetch (QoS): Controls how many unacked messages a worker can hold. basicQos(1) ensures "Fair Dispatch."

4. Reliability Features
   Durability: Queues/Exchanges survive a broker restart.

Persistence: Messages are written to disk.

DLX (Dead Letter Exchange):

Automatic "Trash Bin."

Handles: Rejected messages, Expired (TTL), or Queue length limit hit.

TTL: Time-To-Live. Messages die if not consumed in X ms.

---

## `oauth2/oauth2.md`

# 🔐 OAuth 2.0 & OIDC: Core Concepts

### 📌 Що таке OAuth 2.0?
**OAuth 2.0** — це протокол **авторизації**, який дозволяє додатку отримати обмежений доступ до ресурсів користувача на іншому сервісі (Google, Keycloak) без передачі пароля.
*   **Ключова ідея:** Делегування прав (Delegation of Permissions).
*   **OIDC (OpenID Connect):** Надбудова над OAuth 2.0, яка додає шар **автентифікації** (ID Token), щоб знати, *хто* цей користувач.

---

### 👥 Ролі в OAuth 2.0

| Роль | Опис | Приклад |
| :--- | :--- | :--- |
| **Resource Owner** | Власник даних (користувач). | Ви (Дмитро). |
| **Client** | Додаток, що запитує доступ. | React App, Zoom, Postman. |
| **Authorization Server** | Сервер, що перевіряє юзера і видає токени. | Keycloak, Google Auth Server. |
| **Resource Server** | Сервер, де лежать захищені дані. | Ваш Java/Kotlin Backend. |

---

### 🎫 Типи токенів (JWT)

*   **Access Token:** Квиток для доступу до API. Містить **Scopes** (дозволи).
*   **Refresh Token:** Використовується для оновлення Access Token без повторного логіну.
*   **ID Token (OIDC):** Паспорт користувача. Містить дані про особу (name, email, sub).

---

### 🚀 Основні тези для інтерв'ю

*   **OAuth2 vs OIDC:** OAuth2 — "що мені дозволено" (Authorization). OIDC — "хто я такий" (Authentication).
*   **Scopes (Області доступу):** Межі делегованих прав (наприклад, `read`, `write`).
*   **Stateless:** Бекенд (Resource Server) не зберігає сесію в БД. Він лише перевіряє цифровий підпис JWT-токена публічним ключем.
*   **PKCE:** Захист для публічних клієнтів (SPA/Mobile), що замінює `client_secret` динамічним ключем для запобігання перехопленню коду.
*   **Grant Types:**
    *   `authorization_code`: Стандарт для Web/SPA (безпечний обмін через код).
    *   `client_credentials`: Для Service-to-Service зв'язку (без участі користувача).

---

### 🔄 Стандартний ланцюжок (Auth Code Flow)
1. **Redirect:** Клієнт відправляє юзера на Auth Server.
2. **Auth:** Юзер вводить пароль на стороні Auth Server.
3. **Code:** Auth Server повертає тимчасовий `code` клієнту.
4. **Exchange:** Клієнт обмінює `code` на `Access Token` (через Back-channel).
5. **Access:** Клієнт йде на Resource Server з заголовком `Authorization: Bearer <token>`.

---

## `oauth2/readme.md`

curl http://localhost:8080/realms/my-realm/.well-known/openid-configuration

---

## `OrderDeliveryService/HELP.md`

# Getting Started

### Reference Documentation

For further reference, please consider the following sections:

* [Official Gradle documentation](https://docs.gradle.org)
* [Spring Boot Gradle Plugin Reference Guide](https://docs.spring.io/spring-boot/3.5.6/gradle-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/3.5.6/gradle-plugin/packaging-oci-image.html)
* [Spring Data MongoDB](https://docs.spring.io/spring-boot/3.5.6/reference/data/nosql.html#data.nosql.mongodb)
* [Spring Web](https://docs.spring.io/spring-boot/3.5.6/reference/web/servlet.html)

### Guides

The following guides illustrate how to use some features concretely:

* [Accessing Data with MongoDB](https://spring.io/guides/gs/accessing-data-mongodb/)
* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)

### Additional Links

These additional references should also help you:

* [Gradle Build Scans – insights for your project's build](https://scans.gradle.com#gradle)



---

## `OrderNotificationsService/HELP.md`

notification-service

Kafka consumer that listens to orders topic

Processes Order objects

Simulates intermittent failure and retries with exponential backoff

---

## `OrderPaymentService/HELP.md`

payment-service

REST endpoint /pay

Randomly fails (to simulate transient issues)

Used to demonstrate REST retries from order-service



---

## `OrderService/HELP.md`

Services

order-service

Receives REST requests to create orders

Stores orders in MongoDB

Calls payment-service via REST (synchronous)

Publishes OrderCreated event to Kafka (as Order object)


---

## `OrderSharedLibraries/HELP.md`

db.getCollection("delivery").countDocuments()
db.getCollection("orders").countDocuments()



# --- AnotherBoot1: Create a new order ---
curl -X POST http://localhost:8091/orders \
-H "Content-Type: application/json" \
-d '{"customerName": "Alice", "total": 120.5}'

# --- AnotherBoot1: View all orders in MongoDB ---
curl http://localhost:8091/orders

docker exec -it my-mongo mongosh -u user123 -p pass123 --authenticationDatabase admin



# --- AnotherBoot2: Test payment service directly ---
curl -X POST http://localhost:8092/pay \
-H "Content-Type: application/json" \
-d '{"id": "123", "customerName": "Bob", "total": 50.0}'

# --- AnotherBoot3: (Consumer) no curl endpoints, but see logs for Kafka messages ---

# --- MongoDB check ---
# Run in terminal to see data stored in Mongo
mongosh
use ordersdb
db.order.find().pretty()

# --- Kafka check ---
# List topics
docker exec -it kafka kafka-topics.sh --bootstrap-server localhost:9092 --list

# Consume messages from "orders" topic
docker exec -it kafka kafka-console-consumer.sh \
--bootstrap-server localhost:9092 \
--topic orders \
--from-beginning


---

## `pipeline/docker/readme.md`

./gradlew clean bootJar

docker build -f docker/Dockerfile -t lightspeed-app .

docker run -e ENVIRONMENT=dev -p 8080:8080 lightspeed-app


---

## `readme.md`

### @name getToken
POST http://localhost:8081/realms/url-shortener/protocol/openid-connect/token
Content-Type: application/x-www-form-urlencoded

grant_type=password&
client_id=url-generator-service&
client_secret=my-secret&
username=dmytro&
password=pass
> {% client.global.set("access_token", response.body.access_token); %}

<> 2025-12-22T212955.200.json
<> 2025-12-22T203612.200.json
<> 2025-12-22T203204.200.json

####
#POST http://localhost:8080/api/shorten
#Content-Type: application/json
#Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJJM1l5VkEyVUZKM2M2MVJjRVl4ZHBhMU5HQURrZ0UyS21kQXVtYVBMSlRBIn0.eyJleHAiOjE3NjY0Mjg4NzIsImlhdCI6MTc2NjQyODU3MiwianRpIjoiNDc2NzljMjItN2EyYi00OThlLTk2ZTUtMzg5MTQ3ZDNhYjY3IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgxL3JlYWxtcy91cmwtc2hvcnRlbmVyIiwic3ViIjoiOGE0N2JhMzgtM2UxMS00NDEwLWI5NzMtOTEyYzUwNzA3MDZkIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoidXJsLWdlbmVyYXRvci1zZXJ2aWNlIiwic2Vzc2lvbl9zdGF0ZSI6Ijg4MjlhNDU1LWEzODUtNDU0NC04MDIwLTQyZmRlMjdmNjljZCIsInNjb3BlIjoicm9sZXMtbWFwcGVyIiwic2lkIjoiODgyOWE0NTUtYTM4NS00NTQ0LTgwMjAtNDJmZGUyN2Y2OWNkIn0.Zv4VERIZxJoYmh_EkhQigEuELwNBbfK0_GpKt2rXeos4LxJwhZclNTjkmN4VzMWkSGOuKJQLqn66UcGHCp-OOrAgHrYa8OIv8zAhckYmNZh1Sl7-St45DHhV197goBReEHKJd76SZJF8aKAhIA38oIKvfNJBy20lrYMvCecUn0FTnMtCzd7BG3hHIdaAqVwkp76DHu_0Nn98DR8wRPAEqEyPvW0iyf1hohceJleAKjuYv1i3yuMH5gmNjvLXOtkTu_mNv4nb7Wnh4mCtZMKDj_XjrvDykhLQ448AaHUWRnOrVnQ1JgOVcblwdZbWjdE_iVo8OJzUqPU8UgFqAkvovw
#
#{
#  "url": "https://google.com"
#}


###
GET http://localhost:8080/api/departments
Content-Type: application/json
Authorization: Bearer {{access_token}}




<> 2025-12-22T213035.200.json
<> 2025-12-22T213001.500.json
<> 2025-11-25T233250.201.txt
<> 2025-11-25T233245.201.txt
<> 2025-11-25T232939.201.txt
<> 2025-11-25T232931.201.txt
<> 2025-11-25T232656.201.txt
<> 2025-11-25T232654.201.txt
<> 2025-11-25T232645.201.txt
<> 2025-11-25T231552.503.json
<> 2025-11-25T231532.503.json
<> 2025-11-25T231421.201.txt

###
GET http://localhost:8082/api/get
Authorization: Basic dmytro pass

###
GET http://localhost:8080/redirect/12D

###
GET http://localhost:8080/api/shorten/trigger-rest

###
GET http://localhost:8080/api/shorten/trigger-ws

###
GET http://localhost:8085/api/admin/send-manual?message=777

#Get Code
###
http://localhost:8081/realms/url-shortener/protocol/openid-connect/auth?response_type=code&client_id=url-generator-service&redirect_uri=http://localhost:8082&scope=openid&state=abc12345

#Get Access Token with code
###
POST http://localhost:8081/realms/url-shortener/protocol/openid-connect/token
Content-Type: application/x-www-form-urlencoded

grant_type=authorization_code&
client_id=url-generator-service&
client_secret=my-secret&
code=
12789672-0175-4f56-9a99-550122c5be36.527f8714-51d7-4cfc-a433-3431a183d5ed.e16e2c00-58d4-48c7-b841-85ccc9a99943
&
redirect_uri=http://localhost:8082

#Get Access Token with password
###
# getToken.http
POST http://localhost:8081/realms/url-shortener/protocol/openid-connect/token
Content-Type: application/x-www-form-urlencoded

grant_type=password&
client_id=url-generator-service&
client_secret=my-secret&
username=dmytro&
password=pass

> {% client.global.set("access_token", response.body.access_token); %}

<> 2025-12-22T202651.200.json





###
GET http://localhost:8080/test/api/with/certificate

<> 2025-12-07T120837.200.txt
<> 2025-12-06T215420.200.txt
<> 2025-12-06T214857.500.json
<> 2025-12-06T213207.500.json
<> 2025-12-06T211658.500.json
<> 2025-12-06T195412.200.txt
<> 2025-12-06T195253.500.json

###
GET http://localhost:8080/api/departments
Content-Type: application/json

{
"id": 0,
"name": ""
}

<> 2025-12-22T193746.200.json
<> 2025-12-21T124811.200.json
<> 2025-12-07T102449.200.json

###
POST http://localhost:8081/users
Content-Type: application/json

{
"firstName": "rrr2",
"lastName": "rrr2",
"email": "rrr2@ggg.com",
"password": "rrrdddddd"
}

<> 2025-12-20T105343.201.json
<> 2025-12-20T105329.201.json
<> 2025-12-20T094213.201.json
<> 2025-12-20T093809.201.json
<> 2025-12-20T093801.201.json
<> 2025-12-20T093738.400.json
<> 2025-12-20T093722.400.json

###
GET http://localhost:8081/users/694652614f08705631f17b02

<> 2025-12-20T150807.201.json
<> 2025-12-20T094520.201.json
<> 2025-12-20T094431.201.json
<> 2025-12-20T094417.201.json
<> 2025-12-20T094345.201.json
<> 2025-12-20T094321.201.json
<> 2025-12-20T094313.201.json
<> 2025-12-20T094235.400.json

###
GET http://localhost:8081/users
Content-Type: application/json

{
"firstName": "",
"lastName": "",
"email": "",
"password": ""
}

<> 2025-12-21T091201.201.json
<> 2025-12-21T091139.201.json
<> 2025-12-21T091125.201.json
<> 2025-12-21T091100.201.json

###
GET http://localhost:8081/users/first
Content-Type: application/json

###
GET http://localhost:8081/users/threads

<> 2025-12-20T102951.201.txt
<> 2025-12-20T102849.500.json
<> 2025-12-20T102658.201.txt
<> 2025-12-20T101722.201.txt
<> 2025-12-20T101651.201.txt







Frontend

npm create vite@latest . -- --template react-ts
npm install
npm run dev

npm install axios

---

## `redis-test/readme.md`

http://localhost:8001/

# Redis Developer Guide

## What is Redis?
**Redis** (Remote Dictionary Server) is an **in-memory** data structure store used as a database, 
cache, and message broker. It is designed for high-performance scenarios where sub-millisecond 
response times are required.

### Key Properties:
*   **Memory-First:** Data is stored in RAM for maximum speed.
*   **Persistence:** Supports RDB (snapshots) and AOF (logs) to save data to disk.
*   **Atomic Operations:** Every command is executed sequentially; no race conditions.
*   **Versatility:** Supports more than just strings (Hashes, Lists, Sets, etc.).

---

## 🛠 Redis Learning Roadmap

### 1. Basic Caching (Strings)
*   **Goal:** Store simple key-value pairs with expiration.
*   **Core Commands:** `SET`, `GET`, `SETEX` (Set with Expiry), `TTL`.
*   **Logic:** `GET key` -> if null -> `Fetch from DB` -> `SETEX key 3600 value`.

### 2. Object Management (Hashes)
*   **Goal:** Store objects with multiple fields without serializing a whole JSON string.
*   **Core Commands:** `HSET`, `HGET`, `HINCRBY`.
*   **Logic:** Useful for user sessions or profiles where you only need to update a single field like `last_login`.


### 3. Rate Limiting (Counters)
*   **Goal:** Limit the number of actions a user can perform in a specific timeframe.
*   **Core Commands:** `INCR`, `EXPIRE`.
*   **Logic:** Increment a key for a User ID. If the value exceeds the limit, block the request until the key expires.

### 4. Deduplication (Sets)
*   **Goal:** Store a collection of unique items.
*   **Core Commands:** `SADD`, `SCARD`, `SISMEMBER`.
*   **Logic:** Track unique visitor IDs or tags. Redis automatically discards duplicate entries.

### 5. Task Queues (Lists)
*   **Goal:** Implement a simple message queue.
*   **Core Commands:** `LPUSH` (Producer), `RPOP` or `BRPOP` (Consumer).
*   **Logic:** Move background tasks (like sending emails) from the main application to a worker process.


### 6. Real-time Rankings (Sorted Sets)
*   **Goal:** Maintain a list that is always sorted by a score.
*   **Core Commands:** `ZADD`, `ZINCRBY`, `ZREVRANGE`.
*   **Logic:** Ideal for leaderboards. The sorting happens inside Redis, making retrieval extremely fast.

### 7. Event Broadcasting (Pub/Sub)
*   **Goal:** Send messages to multiple listeners simultaneously.
*   **Core Commands:** `PUBLISH`, `SUBSCRIBE`.
*   **Logic:** Notify all running microservice instances to clear their local cache when a global update occurs.

### 8. Distributed Locking
*   **Goal:** Ensure only one process performs a specific action at a time.
*   **Core Commands:** `SET key value NX PX 30000`.
*   **Logic:** The `NX` (Not Exists) flag ensures only the first caller gets the lock, and `PX` (Expiry) prevents deadlocks if the caller crashes.

---

## 🚀 Recommended Practice Order
1.  **PSVM Phase:** Use the `Lettuce` or `Jedis` library in a simple Java `main` method to run these commands.
2.  **Visualization:** Use **Redis Insight** to monitor how keys appear and expire in real-time.
3.  **Spring Phase:** Move to `RedisTemplate` or `@Cacheable` once the raw commands are understood.

---

## `solr/lucene_search_language.md`

## Notes:
- Lucene is case-insensitive by default
- 

1. Find word occurrence in the `title` field of all documents 

title_t: boot 

2. Find one or another by in field's values:

title_t: search with solr

3. Find documents where in field there is exact match:

title_t: "search with solr"

4. Find with combined logical operators: AND, OR, NOT

- category_s:architecture AND title_t:Search

- title_t:Solr NOT category_s:devops

5. Find with masking: ? or *

description_t: sc*
description_t: scal?

6. Find with range:

- inclusive
  price:[30.00 TO 45.00]
- exclusive
  price:{30.00 TO 45.00}

7. Find with not exact occurrence:

title_t: sear~

---

## `solr/solr.md`

Lucene → Java library that actually does indexing and searching.
Solr and Elasticsearch → server applications built on top of Lucene.
Kibana → visualization/UI tool mainly for Elasticsearch.

Search languages → ways to ask Solr/Elasticsearch for results
# Apache Solr & Lucene: Interview Cheat Sheet

## 1. What is Apache Solr & Lucene?
* **Apache Lucene** is a low-level, high-performance Java search library. It owns the core data structures on disk, handles text analysis, builds the indexes,
  and calculates relevance scores. It is just a library (`.jar`), meaning it has no network interface, no server capabilities, and no UI.
* **Apache Solr** is an enterprise-grade search server built around Lucene. It wraps the Lucene engine and provides a REST API, an Admin UI, security, advanced
  query parsing (DisMax/eDisMax), caching, and client libraries like **SolrJ** for Java/Kotlin integration.

---

## 2. Solr Configuration: Schema & SolrConfig
Solr is highly customizable, and its behavior is defined by two main configuration components:

### A. What is Solr Schema?
The **Schema** defines the structure of your data (similar to a table definition in SQL). It tells Solr what fields exist and how they should be treated.
* **Fields:** Defines name, type (e.g., `string`, `tint`, `text_general`), and properties:
   * `indexed="true"`: Means Lucene will put this field into the Inverted Index, making it searchable.
   * `stored="true"`: Means Lucene will keep the raw value in the Stored Fields blob, making it visible in the search results.
   * `docValues="true"`: Enriches the field with a columnar structure, making it ready for sorting and faceting.
* **Dynamic Fields:** Allows indexing fields that aren't explicitly declared, using wildcards (e.g., `*_txt` automatically maps to a text type).

### B. How to Configure Solr
Solr configuration can be managed in two ways depending on the deployment mode:
1.  **Classic XML Files:**
   * `schema.xml` (or `managed-schema.xml`): Defines fields, field types, and text analyzers.
   * `solrconfig.xml`: Controls server-level settings like caches, request handlers (e.g., mapping `/select` to a specific query parser), and update processors.
2.  **Configsets & ZooKeeper (SolrCloud Mode):**
   * In a distributed environment, configurations are grouped into **Configsets** and uploaded to **Apache ZooKeeper**. Every node in the cluster reads
     configurations from ZooKeeper to stay synchronized.

---

## 3. How Data is Stored: Lucene's 3 Disk Structures
When you send a JSON or XML document over HTTP, Solr validates it against the schema, extracts the raw data, and hands it to Lucene. Lucene completely shatters
the document and stores it in three distinct binary formats on the file system:

* **Inverted Index (The Search Map):** Maps `Term -> List of Doc IDs`. It is used exclusively to find which documents match the query and calculate the score.
* **Stored Fields Blob (The Raw Payload):** A compressed, row-oriented binary blob containing the original text. Lucene completely ignores this during the search
  phase. Only after the Inverted Index identifies the matching Doc IDs, Lucene decompresses these specific rows to return the original fields to Solr.
* **DocValues Blob (The Columnar Data):** A column-oriented binary structure that groups values of a single field (like `price` or `category`) across all
  documents. It is optimized for in-memory columnar operations like sorting, faceting, and mathematical functions.

---

## 4. The Text Analysis Pipeline (How Inverted Index is Built)
Before text fields hit the Inverted Index, Lucene processes them through a strict analysis pipeline defined in the schema:
1.  **Tokenization:** Breaks raw sentences into individual words/tokens (e.g., `"Spring with SolrJ"` becomes `[Spring]`, `[with]`, `[SolrJ]`).
2.  **Lowercasing:** Converts all tokens to lowercase so that searches for `"SOLR"` and `"solr"` yield identical results.
3.  **Stop-words Filtering:** Drops common, non-semantic words (e.g., `"with"`, `"is"`, `"a"`, `"the"`) to prevent index bloat.
4.  **Stemming/Morphology:** Reduces words to their base root (e.g., `"Techniques"` and `"technical"` both map to `techniqu`), allowing a query for `technique`
    to match a document containing `"Techniques"`.

Apache Solr — це open-source платформа для повнотекстового пошуку, побудована на базі бібліотеки Apache Lucene.

Ключова ідея: На відміну від реляційних БД (де ми шукаємо по рядках), 
Solr використовує Inverted Index (інвертований індекс). 
Це схоже на покажчик у кінці книги: замість того, щоб гортати всі сторінки, 
ти дивишся слово "Spring" і бачиш список сторінок (ID документів), де воно зустрічається.

Чому не SQL? SQL погано справляється з нечітким пошуком, морфологією (шукаємо "running" — знаходимо "run") 
та ранжуванням результатів за релевантністю. Solr робить це блискавично.
---

## 5. Division of Labor: Feature Breakdown

| Feature Type | Feature Name | Who actually owns it? | Description |
| :--- | :--- | :--- | :--- |
| **Search & Indexing** | Full-Text Search & Relevance | 🔹 **Pure Lucene** | Low-level Inverted Index management and BM25 scoring. |
| **Search & Indexing** | Fuzzy Search & Spellchecking | 🔹 **Pure Lucene** | Native Levenshtein distance calculations on text tokens. |
| **Search & Indexing** | Analysis Pipeline | 🔹 **Pure Lucene** | Underlying Java Tokenizers and Filters that normalize text. |
| **UI & APIs** | Admin UI & HTTP REST API | 🔸 **Pure Solr** | The web application layer that allows interacting with Lucene. |
| **UI & APIs** | Advanced Query Parsers | 🔸 **Pure Solr** | DisMax/eDisMax parsers that accept raw user text and map it to fields. |
| **Data Analytics** | Faceting & Highlighting | 🤝 **Joint Effort** | Lucene provides fast doc lookups; Solr aggregates counts and wraps tags. |
| **Infrastructure** | Distributed Scalability | 🔸 **Pure Solr** | Sharding, Replication, and cluster orchestration via ZooKeeper. |

---

## `sql/indexes/indexes.md`

# Database Indexing Fundamentals

### 1. Clustered Index
* **Behavior:** Determines the physical storage order of data on the disk.
* **MySQL (InnoDB):** Created automatically for the **Primary Key**.
* **PostgreSQL:** No automatic clustering. Tables are "Heaps" (unordered). 
The `CLUSTER` command is a one-time manual operation.

### 2. Non-Clustered Indexes (Manual)
Most SQL indexes use the **B-Tree** (Balanced Tree) structure, providing $O(\log n)$ lookup performance.

* **Composite Index:** Built on multiple columns (e.g., `last_name, first_name`).
    * **Leftmost Prefix Rule:** The index works for `last_name` or `last_name + first_name`, 
    * but is **ignored** if you search only by `first_name`.
* **Index-Only Scan (Covering Index):** The index contains all the data needed for the query.
    * **Optimization:** The DB skips the table (Heap) entirely. 
    * In Postgres, use the `INCLUDE` clause to add "payload" columns to a search key.
* **Partial Index:** An index created on a subset of data using a `WHERE` clause.
    * **Use Case:** `CREATE INDEX ... WHERE is_active = true;` (Reduces index size and update overhead).

### 3. When to Avoid Indexes
* **Low Cardinality:** If there is poor diversity in values (e.g., `status`, `gender`), 
the DB optimizer will likely ignore the index in favor of a **Sequential Scan**.
* **High Write Volume:** Every index slows down `INSERT`, `UPDATE`, and `DELETE` 
as the tree must be rebalanced and updated.
* **Small Tables:** For tables with only a few hundred rows, 
reading the whole table into memory is faster than navigating an index tree.

### 4. Advanced Postgres Index Types
* **GIN:** Best for searching inside **JSONB** or **Arrays**.
* **GiST:** Optimized for **Geographic coordinates** or **Time ranges** (prevents overlaps).
* **BRIN:** Designed for **massive tables** (millions of rows) where data is naturally sorted, 
such as log timestamps.

---

## `sql/joins_vs_subqueries.md`

Sub-queries may be dangerous in such cases:

1. Correlated sub-queries (when inner depends on outer):



---

## `sql/normalization.md`

None-normalized db is the state where we even save several courses which student attends
in single cell (column) separated by comma. All entities maybe be in single table even.
A lot of nulls and a lot of needing to go via all table even when just single student's data
is changed.

### Fist normal form:
`Atomicity`: Each cell must contain only one value (no lists or comma-separated strings).

`No Repeating Groups`: You shouldn't have columns like course_1, course_2, course_3.

`Unique Rows`: Each record must be identifiable (usually by a Primary Key).

### Second normal form:
The table must be in 1NF AND every non-key column must depend on the entire primary key.

### Third normal flow:
3NF requires that no non-key column depends on another non-key column


---

## `sql/postgres_advantages/postgres_advantages.md`

# Why PostgreSQL? (Key Advantages over MySQL)

### 1. Transactional DDL (Data Definition Language)
* **Feature:** You can wrap schema changes (`CREATE`, `ALTER`, `DROP`) in a transaction.
* **Why it matters:** If a migration script fails halfway, Postgres rolls back the entire change. 
* In MySQL, a failed migration leaves the database in a "half-broken" state, requiring manual cleanup.

### 2. JSONB (Binary JSON)
* **Feature:** Stores JSON in a decomposed binary format rather than a plain string.
* **Why it matters:** Supports **GIN Indexing**, allowing you to query keys/values 
* inside a JSON blob with the speed of a regular column. MySQL's JSON support is less efficient 
* for deep searching.

### 3. Advanced Indexing Types
* **Feature:** Supports more than just B-Trees.
    * **Partial Indexes:** Index only a subset of data (e.g., `WHERE status = 'ACTIVE'`).
    * **Expression Indexes:** Index the result of a function (e.g., `LOWER(email)`).
    * **GIN/GiST:** Essential for Full-Text search, Arrays, and Geospatial data.

### 4. Better Concurrency (MVCC)
* **Feature:** Advanced Multi-Version Concurrency Control.
* **Why it matters:** Readers never block writers and writers never block readers. Postgres handles heavy, 
* complex write/read workloads simultaneously with fewer "Deadlocks" compared to MySQL.

### 5. Richer Data Types
* **Feature:** Native support for Arrays, Ranges (date/time), IP addresses, and custom Enumerated types.
* **Why it matters:** Reduces the need for many-to-many join tables. For example, 
* you can store a list of tags as a `TEXT[]` array directly in the row.

### 6. Extensions (PostGIS, pgvector)
* **Feature:** A robust plugin architecture.
* **Why it matters:** You can add powerful features like **PostGIS** 
* (the industry standard for GPS/Map data) or **pgvector** 
* (for AI/LLM similarity search) without changing the core database.

### 7. Materialized Views
* **Feature:** Caches the result of a complex, heavy query physically on disk.
* **Why it matters:** You can refresh the cache periodically, providing near-instant responses 
* for complex reports that would otherwise take seconds or minutes to calculate.

### 8. Strict Data Integrity
* **Feature:** Hard enforcement of "Check Constraints" and data types.
* **Why it matters:** Postgres will reject invalid data more aggressively than MySQL, 
* ensuring that "garbage" data never enters your system, which is safer for Java/Kotlin 
* enterprise applications.

---

## `src/main/java/com/conduct/interview/_0_bytecode_and_compiler/compilation.md`

# Java Bytecode and Compilation Process

## What is Bytecode?

Bytecode is intermediate, platform-independent code
generated by the Java compiler. It’s a low-level
representation of Java source code, stored in `.class`
files, and ensures portability across systems.

## Compilation Process

1. **Write Source Code**: Save Java code in `.java` files.

2. **Compile Source Code**:
    - **Java Compiler (`javac`)**: Converts `.java` to
      bytecode in `.class` files.
    - **Command**:
      ```bash
      javac MyClass.java
      ```
      Produces `MyClass.class`.

after java -jar
3. **Load Bytecode**:
    - **Class Loader**: Loads `.class` files into memory.

4. **Verify Bytecode**:
    - **Bytecode Verifier**: Checks that code adheres
      to Java safety rules.

5. **Execute Bytecode**:
    - **JVM**: Interprets or compiles bytecode into
      native code, using Just-In-Time (JIT) compilation.

6. **Run Application**:
    - **Execution**: JVM runs bytecode, handling memory
      and garbage collection.

## Summary

Java compilation turns source code into platform-independent
bytecode, which the JVM executes.

Both bytecode and machine code are binary just byte code is 
prepared for virtual CPU - JVM, not for particular CPU. 



---

## `src/main/java/com/conduct/interview/_0_bytecode_and_compiler/jit_compiler/jit.md`

Just-In-Time (JIT) Compiler: Overview
In Java, the Just-In-Time (JIT) compiler is an integral part of the Java Virtual Machine (JVM). 
Java code is compiled twice:

Compile-Time (javac): When you write Java code and compile it using javac, 
it is first converted into bytecode. This bytecode is platform-independent and can run on any machine that has a JVM.

Run-Time (JIT): When the Java application runs, the JIT compiler kicks in. Instead of interpreting 
the bytecode instruction-by-instruction (which is slower), the JVM converts sections of bytecode into native machine 
code (specific to your system’s architecture) as needed. This process happens "just in time" when the code is about to 
be executed, hence the name.

The JIT compiler optimizes frequently executed code paths (hot spots) by compiling them into machine 
code, allowing the JVM to run the program more efficiently. If certain code is run repeatedly, 
the JVM compiles and stores the machine code for reuse, which significantly boosts performance.


Additional Notes:
JVM Options: You can pass various JVM options to observe JIT behavior more clearly. For example, use -Xcomp to 
force compilation or -Xint to run entirely in interpreted mode (without JIT):

Force full JIT compilation:

bash
Copy code
java -Xcomp JITExample
Force interpretation (disable JIT):

bash
Copy code
java -Xint JITExample
You can compare the performance difference between these modes to observe the effect of JIT compilation.

JIT Optimizations:
The JIT compiler performs several optimizations:

Method Inlining: Replaces the method call with the method body to reduce overhead.
Loop Unrolling: Repeats the loop body multiple times to decrease branching.
Dead Code Elimination: Removes code that is never used or executed.
Constant Folding: Pre-computes constant expressions at compile-time.
JIT also has different levels of optimization, such as C1 (client) and C2 (server) compilers, 
depending on the JVM configuration and environment.


cd /home/dmytro/dev/projects/interview/src/main/java

javac com/learn/interview/interviews/_0_bytecode_and_compiler/jit_compiler/JITExample.java

java -Xcomp com.conduct.interview._0_bytecode_and_compiler.jit_compiler.JITExample

java -Xint com.conduct.interview._0_bytecode_and_compiler.jit_compiler.JITExample

---

## `src/main/java/com/conduct/interview/_0_bytecode_and_compiler/run_class/run_class.md`

cd com/conduct/interview/_0_bytecode_and_compiler/run_class/
javac TestClass.java

cd /home/dik81/IdeaProjects/interview/src/main/java
java com.conduct.interview._0_bytecode_and_compiler.run_class.TestClass

if I'm in different directory:
java -cp /home/dik81/IdeaProjects/interview/src/main/java com.conduct.interview._0_bytecode_and_compiler.run_class.TestClass


---

## `src/main/java/com/conduct/interview/_0_why_java/why_java.md`

## Why Java is Good (Short Answer)

- **Object-Oriented**: Promotes modular, reusable code through classes and objects.
- **Platform Independent**: "Write once, run anywhere" via the JVM.
- **Strongly Typed**: Helps catch errors at compile-time.
- **Rich Standard Library**: Built-in support for networking, data structures, concurrency, etc.
- **Huge Ecosystem**: Frameworks like Spring, tools like Maven, and wide community support.
- **Automatic Memory Management**: Garbage collection handles memory cleanup.
- **Multithreading Support**: Simplifies concurrent programming.

Java is reliable, scalable, and widely used for enterprise, mobile, and web applications.


---

## `src/main/java/com/conduct/interview/_11_auth/auth.md`

Oauth2 is the protocol for the authorization.
OpenId is the protocol built on top of Oauth2 for the 
authentication purposes.

---

## `src/main/java/com/conduct/interview/_15_git/git.md`

# Git Overview

Git is a distributed version control system (VCS). It tracks changes in files, 
enabling collaboration and version management.

## Main Principles

- **Repository**: A project that Git tracks.
- **Commit**: A snapshot of changes with a message.
- **Branch**: A separate line of development.
- **Merge**: Combine branches together.
- **Clone**: Copy a repository to your local machine.
- **Pull**: Update your local copy from a remote repository.
- **Push**: Send local changes to the remote repository.


# Branch vs Tag in Git

## Branch
- **Definition**: A pointer to the latest commit in a series of changes.
- **Usage**: For ongoing development. You can add new commits.
- **Movable**: Moves forward as new commits are added.
- **Examples**: `main`, `feature-x`, `bugfix-123`.

## Tag
- **Definition**: A reference to a specific commit, typically used for marking releases.
- **Usage**: For creating static snapshots (e.g., `v1.0`).
- **Immovable**: Remains fixed at the commit where it's created.
- **Examples**: `v1.0`, `release-2023`.

In Git, fast-forward is a type of merge that occurs when the current branch has not diverged from 
the branch being merged into it. Just pointer is moved, no merge commit.

No fast-forward (with merge commit):

![img.png](img.png)

Ra-base vs Merge:

![img_1.png](img_1.png)

![img_2.png](img_2.png)

In Git, cherry-pick is a command that allows you to apply a specific 
commit from one branch into another, without merging the entire branch. 
It’s useful when you want to selectively incorporate individual changes 
from one branch into another.



---

## `src/main/java/com/conduct/interview/_1_bases/_0_types/types.md`

✅ 1. Primitive Types

These are the most basic types of data in Java. They are not objects and are stored directly in memory.
Type	Size	Example	Description
byte	8-bit	-128 to 127	Small integers
short	16-bit	-32,768 to 32,767	Medium integers
int	32-bit	-2^31 to 2^31-1	Standard integers
long	64-bit	Very large	Large integers
float	32-bit	3.14f	Floating point (less precise)
double	64-bit	3.14159	Floating point (more precise)
char	16-bit	'A', 'Ж'	Single Unicode character
boolean	1-bit (logical)	true, false	Logical values


✅ 2. Reference Types

These types refer to objects in memory (unlike primitives).
Type	Example	Description
String	"Hello"	Sequence of characters
Arrays	int[] nums = {1, 2, 3}	Collection of elements
Classes	MyClass obj = new MyClass()	Custom-defined objects
Interfaces	Runnable, List, etc.	Abstract behavior


✅ 3. Wrapper Classes

Java provides object wrappers for each primitive type so they can be used as objects (e.g. in collections).
Primitive	Wrapper
int	Integer
double	Double
boolean	Boolean
char	Character

These are used automatically in many cases (called autoboxing/unboxing).


✅ 4. Generic Types (Generics)

Generics let you define classes or methods with types as parameters.

Examples:

List<String> names = List.of("Anna", "John");
Map<String, Integer> ages = Map.of("John", 30);

This allows type safety when using collections.
🔍 Code Example:

int age = 25;                        // primitive
String name = "Alice";              // reference type
boolean isActive = true;            // primitive
Integer wrappedAge = age;           // wrapper class
int[] scores = {95, 80, 70};        // array
List<String> names = List.of("A", "B"); // generic list


---

## `src/main/java/com/conduct/interview/_1_bases/_1_passing_params/passingParams.md`

# Passing Variables in Java: Primitives vs. Objects

In Java, variables are passed by value. However, the behavior differs
between primitives and objects.

## Primitives

When you pass a **primitive type** (e.g., `int`, `double`, `boolean`)
to a method, Java copies the value. Changes to the parameter do not
affect the original variable.

## Objects

When passing an **object**, Java copies the reference to the object,
not the object itself. Both the original reference and the method
parameter point to the same object.

### Modifying Fields

If you modify the object's **fields** inside the method, the changes
are visible outside the method.

### Changing References

If you reassign the object reference inside the method, it does not
affect the original object. The original reference remains pointing
to the same object.

## Summary

- **Primitives**: Passed by value; changes inside a method do not
  affect the original variable.
- **Objects**: Reference is passed by value; changes to object fields
  are visible outside the method, but reassigning the reference does
  not affect the original object.


---

## `src/main/java/com/conduct/interview/_1_bases/_3_static_members/readme.md`

`static` keyword in java could be applied to:
- variables (fields);
- methods;
- static blocks;
- inner classes;

Characteristics of static members:
- allocate memory just once, memory space is shared by all instances
- you don't need to create object in order to access the static member (associated with class, not instances of it)
- static members can't access none-static content
- static methods can be overloaded, not overridden
- static variable can be created just on the class level
- static blocks and variables are executed in the order they are present in the program

Static methods:
They can only directly call other static methods.
They can only directly access static data.
They cannot refer to this or super in any way.

Advantages:
Memory efficiency: 
Static members are allocated memory only once during the execution of the program, which can result in 
significant memory savings for large programs.
Improved performance: 
Because static members are associated with the class rather than with individual instances, they can be 
accessed more quickly and efficiently than non-static members.
Global accessibility: 
Static members can be accessed from anywhere in the program, regardless of whether an instance of the class 
has been created.
Encapsulation of utility methods: 
Static methods can be used to encapsulate utility functions that don’t require any state information from an object. 
This can improve code organization and make it easier to reuse utility functions across multiple classes.
Constants: 
Static final variables can be used to define constants that are shared across the entire program.
Class-level functionality: 
Static methods can be used to define class-level functionality that doesn’t require any state information from 
an object, such as factory methods or helper functions.

However, static variables have the disadvantage of being global, which means they can be accessed and changed by 
any part of the program, potentially leading to errors or security issues. They also make the code less flexible 
and modular, as they create a tight coupling between the class and the variable.

---

## `src/main/java/com/conduct/interview/_1_bases/_4_annotations/theory.md`

Annotations are used to provide supplemental information about a program. They
start with `@` and do not change the action of the compiled program, but they
help associate metadata (information) with program elements like instance
variables, constructors, methods, and classes.

Unlike pure comments, annotations can influence how a program is treated by the
compiler.

For example, we can create custom annotations and use reflection to check if
they are present, running specific logic if needed, to generate some code
(like creation of builder class etc.)

Key concepts include:

- **`@Target`**: Specifies where the annotation can be applied (methods, fields,
  classes, etc.).
- **`@Retention`**: Determines whether the annotation is available at runtime
  or only at compile-time.


---

## `src/main/java/com/conduct/interview/_1_bases/collections/collections.md`

# 🚀 Java Collections — Ultimate Interview Cheat Sheet

## 1. List

| Collection | Underlying Structure | Time Complexity (Get) | Time Complexity (Insert/Delete) | Best Use Case |
| :--- | :--- | :--- | :--- | :--- |
| **ArrayList** | Dynamic `Object[]` array | **$O(1)$** (Direct index access) | **$O(n)$** in middle (requires element shifting/copying). **$O(1)$** at the end. | **Default choice** for 99% of tasks requiring fast reads and updates at the end. |
| **LinkedList** | Doubly Linked List (Nodes with prev/next pointers) | **$O(n)$** (Must traverse from head or tail) | **$O(1)$** at head/tail. **$O(n)$** in middle due to search time. | Implementing Queues/Deques or heavy modifications strictly at head/tail. |

### 💡 Key Nuances
* **ArrayList Resize:** When full, it creates a new array ($1.5\times$ original size) and copies elements.
* **CPU Cache Friendliness:** ArrayList stores data in contiguous memory blocks, making it highly **CPU cache-friendly**. LinkedList nodes are scattered randomly across the Heap, causing frequent cache misses.
* **Stack:** Extends `Vector`. Obsolete, legacy, synchronized LIFO structure. Use `Deque` implementations instead.
* **Queue / Deque:** Queue is FIFO. Deque is double-ended (FIFO + LIFO operations allowed at both ends).

---

## 2. Map

### HashMap (The Industry Standard)
* **Underlying Structure:** An array of Nodes (`Node[] table`). Default initial capacity is **16**, with a Load Factor of **0.75**.
* **How it works:** Index is calculated using `(capacity - 1) & hash(key)`.
* **Collision Handling (The Tree Evolution):**
    * Originally, collisions formed a standard Linked List ($O(n)$ search).
    * **Modern Java Optimization:** If a bucket reaches **8 elements** (`TREEIFY_THRESHOLD`) and total map capacity is $\ge 64$, the bucket automatically transforms into a **Balanced Tree (Red-Black Tree)**. This drops search time from **$O(n)$** down to **$O(\log n)$**. If elements decrease back to 6, it converts back to a linked list.
* **Memory Traps:** HashMap **never shrinks** its internal array automatically when elements are cleared. To free memory, you must create a new Map copy or clear it completely.

### Other Map Implementations
* **LinkedHashMap:** Extends HashMap but maintains a doubly linked list running through all entries. Preserves **insertion order** or access order.
* **TreeMap:** Implements `SortedMap`. Uses a **Red-Black Tree** structure underneath. Keeps keys sorted in their natural order or via a custom `Comparator`. Operations take **$O(\log n)$** time.
* **Hashtable:** Obsolete, legacy, synchronized equivalent of HashMap. Does not allow `null` keys/values.

---

## 3. Set

Sets guarantee element uniqueness under the hood by wrapping corresponding Map implementations.

* **HashSet:** Uses a `HashMap` internally. Your element acts as the Map's *Key*, and a static dummy `Object` is reused as the *Value*.
* **LinkedHashSet:** Uses a `LinkedHashMap` internally to maintain **insertion order**.
* **TreeSet:** Uses a `TreeMap` underneath to maintain **natural or custom sorted order**.

---

## 4. Concurrent Iteration: Fail-Fast vs. Fail-Safe



### Fail-Fast (e.g., ArrayList, HashMap)
* **Behavior:** Throws `ConcurrentModificationException` instantly if the collection is modified structurally while an iterator loops over it.
* **Mechanism:** Tracks structural modifications using a `modCount` counter. If the iterator notices `modCount` changed mid-flight, it fails fast.
* **The Loophole:** It is safe to remove elements during iteration **only** if you use the iterator's own method: `iterator.remove()`.

### Fail-Safe / Non-Interfering (e.g., CopyOnWriteArrayList, ConcurrentHashMap)
* **Behavior:** Does **not** throw exceptions when structural modifications occur during iteration.
* **Mechanism:** Works on a immutable **snapshot clone** of the collection data state created at the moment the iterator was initialized.
* **Trade-off:** High memory usage footprint. The iterator will not reflect real-time updates made to the collection during the loop execution.

---

## `src/main/java/com/conduct/interview/_1_bases/exceptions/exceptions.md`

## Java Exceptions

In Java, exceptions are events that disrupt the normal flow of program
execution. They represent errors or unexpected conditions that occur during
runtime. Exceptions are categorized into two main types:

- **Checked Exceptions**: These are checked at compile time and must be either
  caught or declared in the method signature. Examples include
  `IOException` and `SQLException`.

- **Unchecked Exceptions**: These occur at runtime and include programming
  errors, such as `NullPointerException` and
  `ArrayIndexOutOfBoundsException`. They do not need to be declared or caught.

Java provides a robust exception handling mechanism using `try`, `catch`, and
`finally` blocks, allowing developers to manage errors gracefully and maintain
program stability.


# Exception Handling Best Practices

- **Do not swallow exceptions**: Always handle or log exceptions, never ignore them.

- **Catch specific exceptions**: Catch only the specific sub-classes of exceptions,
  not the general `Exception` or `Throwable` classes.

- **Wrap exceptions correctly**: Wrap exceptions in custom exceptions to preserve
  the stack trace.

- **Log or throw, never both**: Either log the exception or rethrow it, but don't do
  both in the same place.

- **Avoid throwing exceptions in `finally`**: Do not throw any exceptions from the
  `finally` block, as it may mask the original exception.

- **Handle only what you can**: Catch and handle only those exceptions that you can
  actually deal with.

- **Throw early, catch late**: Raise exceptions as soon as an error condition is
  detected and catch them at a higher, appropriate level in the code.

- **Terminate interrupted threads**: Ensure that threads are terminated when they are
  interrupted.

- **Document exceptions**: Always document the exceptions that your methods throw
  using Javadoc.


---

## `src/main/java/com/conduct/interview/_1_bases/generics/generics.md`

Java is invariant by default. There is a restriction in java related to having the same
type of reference type and object in generic object declaration:

it's not possible:
`List<Person> studentList = new ArrayList<Student>();`

If it were possible then we would not be able to rely on generics,
type consistency would be broken. We could then add person type to list of students.

`Covariance` - is an ability with help of `? extends <smthn>`
to a method parameter to allow to read type and it's parent. 
It's not possible to add anything to such a list. 
It's possible to assign just type and it's children.

`Contrvariance` - is an ability with help of `? super <smthn>`
to a method parameter to add type or it's children to such a list.
Can read just object. Can assign just the same type or subtypes.

`PECS` - Produces Extends Consumer Super

![img.png](img.png)

---

## `src/main/java/com/conduct/interview/_1_bases/immutables_classes/immutability.md`

# Immutable Class in Java

In Java, an **immutable class** is one whose instances cannot be modified
after they are created. This is especially useful in **concurrent programming**
or when dealing with shared resources. Immutable objects help avoid unintended
side effects.

## Key Characteristics of an Immutable Class

1. **All fields are `final`**: Once assigned, their values cannot be changed.
2. **No setter methods**: There are no methods to modify the fields after
   construction.
3. **Fields are private**: Direct access to the fields is restricted.
4. **The class is `final`**: This prevents subclassing, which could introduce
   mutability.
5. **Defensive copies for mutable fields**: If a field holds mutable data
   (e.g., arrays, lists), return copies rather than the original reference.

## Example of an Immutable Class

```java
public final class ImmutablePerson {
    private final String name;
    private final int age;
    
    // Constructor
    public ImmutablePerson(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    // Getters (No setters)
    public String getName() {
        return name;
    }
    
    public int getAge() {
        return age;
    }

    // Defensive copying for a mutable field
    private final List<String> attributes;

    public ImmutablePerson(String name, int age, List<String> attributes) {
        this.name = name;
        this.age = age;
        this.attributes = new ArrayList<>(attributes); // Copy for safety
    }

    public List<String> getAttributes() {
        return new ArrayList<>(attributes); // Return a copy
    }
}


---

## `src/main/java/com/conduct/interview/_1_bases/immutables_classes/why_string_is_immutable/string_immutable.md`

String is immutable because:
1. Security - should not be possible to change the same string (paths, 
passwords)
2. Multithreading.
3. Hashing.
4. String Pool.


---

## `src/main/java/com/conduct/interview/_1_bases/io_streams/io_streams.md`

# Java I/O Streams

## What are I/O Streams?
I/O (Input/Output) streams in Java allow for reading and writing data.

### Types of Streams
1. **Byte Streams**: Handle raw binary data (8-bit bytes).
    - Classes: `InputStream`, `OutputStream`.
2. **Character Streams**: Handle character data (16-bit Unicode).
    - Classes: `Reader`, `Writer`.

### Byte Streams
- **InputStream**: Reads byte data.
- **OutputStream**: Writes byte data.

#### Common Byte Stream Classes
- `FileInputStream`: Reads from a file.
- `FileOutputStream`: Writes to a file.
- `BufferedInputStream`: Buffers input for efficiency.
- `BufferedOutputStream`: Buffers output for efficiency.

### Character Streams
- **Reader**: Reads character data.
- **Writer**: Writes character data.

#### Common Character Stream Classes
- `FileReader`: Reads from a file.
- `FileWriter`: Writes to a file.
- `BufferedReader`: Buffers character input for efficiency.
- `BufferedWriter`: Buffers character output for efficiency.

# It's possible to use byte streams for text processing, but it's not good
# as you need to do encoding/decoding manually then.
## encoding defines how characters are mapped to sequences of bytes


### When You Need Encoding:
`File I/O`: Storing and reading text files.
`Web Development`: Displaying international characters on web pages.
`Data Transmission`: Sending/receiving text over networks (emails, APIs).
`Multilingual Support`: Handling various languages and special characters.
`Data Interchange Formats`: Using JSON, XML for cross-system communication.

Example: Encoding the String "A" in Different Encodings:
In ASCII:
"A" is represented as 65 in decimal, which is 01000001 in binary (7 bits).
In UTF-8:
"A" is also represented as a single byte: 01000001.
In UTF-16:
"A" is represented as 00000000 01000001 (2 bytes).

---

## `src/main/java/com/conduct/interview/_1_bases/io_streams/nio/nio.md`

# Java NIO vs Traditional I/O

## NIO Key Features
- **Non-blocking I/O**: Perform I/O without waiting for operations to finish.
- **Selectors**: Handle multiple connections with a single thread.
- **Buffers**: Data is stored in buffers, improving I/O efficiency.
- **Channels**: Bi-directional I/O streams, faster than traditional streams.
- **Memory-mapped files**: Efficiently handle large files.
- **Asynchronous operations**: Supports async file and socket I/O.
- **File locking**: Provides file locking for safe concurrent access.

## When to Use NIO
- **High-performance servers**: For handling many connections concurrently.
- **Large file processing**: Use memory-mapped files for better performance.
- **Asynchronous I/O**: When you need non-blocking operations.

## When to Use Traditional I/O
- **Simple I/O**: For small, basic read/write operations.
- **Blocking behavior is acceptable**: For single-user applications.


---

## `src/main/java/com/conduct/interview/_1_bases/java_versions/java8/streams/streams.md`

A Java Stream is a pipeline of operations used to process a sequence of data from a source, 
where intermediate operations are lazy and execution is triggered by a terminal operation.

---

## `src/main/java/com/conduct/interview/_1_bases/multithreading/1_threads.md`

A thread is a unit of execution within a process.
In Java, threads are created by the JVM and mapped to native OS threads.

Threads are lightweight compared to processes because they share the same heap memory,
but each thread has its own stack.

This shared memory model allows efficient communication between threads,
but also introduces concurrency issues such as:
- race conditions, 
- visibility problems,
- and deadlocks.

Threads are used to achieve concurrency and improve performance:
- better CPU utilization (parallel execution)
- non-blocking behavior (e.g., handling I/O)
- responsiveness (UI, APIs)

  Concurrency vs Parallelism
- Concurrency is about managing multiple tasks at once
- Parallelism is about executing tasks simultaneously (on multiple cores)


---

## `src/main/java/com/conduct/interview/_1_bases/multithreading/2_synced_blocking_or_not.md`

Synchronous vs asynchronous describes whether the caller waits for the result,
while blocking vs non-blocking describes what happens to the thread.

For example, Spring MVC uses a synchronous blocking model,
where each request is handled by a thread that waits during I/O.

In contrast, reactive systems use asynchronous non-blocking execution,
where threads are not blocked and can handle many requests.

**Sync/Async = waiting model
Blocking/Non-blocking = thread behavior**

---

## `src/main/java/com/conduct/interview/_1_bases/multithreading/common_issues/blocking_operations/try.md`

# Terminal 1
telnet localhost 8088

# Terminal 2
telnet localhost 8088

---

## `src/main/java/com/conduct/interview/_1_bases/multithreading/common_issues/deadlock/deadlock/deadlock.md`

Deadlock is the situation when 2 threads are waiting for the lock
to be open. One thread occupies lock on objA and tries to get the lock
on objB and another thread enters lock of objB and tries to get lock on
objA. 
Can be fixed with putting the locking into the same order.

---

## `src/main/java/com/conduct/interview/_1_bases/multithreading/common_issues/issues.md`

# Common Java Multithreading Issues

### 1. Race Conditions
- **Issue**: Threads access shared resources without synchronization.
- **Solution**: Use `synchronized`, locks, or atomic classes.

### 2. Deadlocks
- **Issue**: Threads wait on each other, causing a stuck state.
- **Solution**: Avoid nested locks or use `tryLock()`.

### 3. Livelock
- **Issue**: Threads respond to each other without making progress.
- **Solution**: Use retry strategies with increasing wait times.

### 4. Thread Starvation
- **Issue**: High-priority threads block lower-priority ones.
- **Solution**: Use fair locks and avoid priority-based threading.

### 5. Context Switching Overhead
- **Issue**: Frequent thread switching degrades performance.
- **Solution**: Minimize thread count and use thread pools.

### 6. Memory Consistency Errors
- **Issue**: Threads see inconsistent shared variable values.
- **Solution**: Use `volatile` or synchronized access.
Volatile solves visibility issue when single thread writes. If multiple thread write then
it's atomicity problem which needs to be solved by synchronized keyword or locking or
atomic types.

### 7. Blocking Operations
- **Issue**: Threads blocked by I/O hold resources too long.
- **Solution**: Use non-blocking I/O or async mechanisms.

### 8. Thread Leaks
- **Issue**: Threads created but not terminated cause resource leaks.
- **Solution**: Shut down thread pools and manage lifecycles.
We should never create threads with new Thread. Instead we should use
ThreadPools which guarantee proper lifecycle, queueing etc.
Just we should always shutdown executors to avoid leaking as well.

### 9. Improper Thread Pool Use
- **Issue**: Too few or too many threads in a pool impacts performance.
- **Solution**: Choose suitable thread pool sizes.
In case of CPU operations (heavy calculations etc.) thread pool size could be equal to
number CPU cores to gradually split load between cores. In case of IO operations (http calls, db call etc)
thread pools size should be calculated with taking into account waiting time, so would be 10 times bigger

### 10. Concurrency Bugs in Data Structures
- **Issue**: Concurrent data structure use causes issues.
- **Solution**: Use thread-safe collections like `ConcurrentHashMap`.

### 11. Fork/Join Pool Misuse
- **Issue**: Task imbalance leads to poor performance.
- **Solution**: Divide tasks evenly and follow Fork/Join patterns.
  Misusing the Fork/Join pool occurs when you execute blocking I/O operations or choose an inefficient task threshold, 
  which leads to thread starvation and excessive context-switching overhead.


---

## `src/main/java/com/conduct/interview/_1_bases/multithreading/java_concurrent_package/blocking_queue/blocking_queue.md`

It's a data structure which allows to implement produce-consumer paradigm.
Two threads can share the queue, one can add the message, another can take.
Can be Unbounded, bounded, priority (with the comparator), delayed queue etc.



---

## `src/main/java/com/conduct/interview/_1_bases/multithreading/java_concurrent_package/executor_service/completable_future/completable_future.md`

This is kind of wrapper around the Future object which is more 
powerful. The purpose is to execute asyc code effectively.
there is an ability to combine results of sever threads, to 
handle exceptions etc.

---

## `src/main/java/com/conduct/interview/_1_bases/multithreading/java_concurrent_package/executor_service/executors.md`

ExecutorService is a JDK API that simplifies running tasks in asynchronous mode. Generally speaking, 
ExecutorService automatically provides a pool of threads and an API for assigning tasks to it.

Execute method allows to run Runnables, submit and invoke like methods return 
the Future objects. Calling the get() method on the future object block the execution and 
returns actual result in case of Callable task and null in case of Runnable.

Future task can be checked and cancelled.

ExecutorService should be stopped:
- with `shutdown` method it will give ability to running threads to complete
- with `shutdownNow` method it will try to complete all running tasks but 
there is no guarantee

---

## `src/main/java/com/conduct/interview/_1_bases/multithreading/java_concurrent_package/fork_join/forkjoin.md`

Fork join pool is a feature which allows to fork task to many tasks, 
execute them in many cores (thread) and then join the result in the end.
It's a special implementation of the ExecutorService which uses recursion
and uses principle - divide and conquer.


---

## `src/main/java/com/conduct/interview/_1_bases/multithreading/java_concurrent_package/semaphore/semaphore.md`

Semaphore allows to restrict the number of threads which can get inside
the resource with help of permits value.

---

## `src/main/java/com/conduct/interview/_1_bases/multithreading/types_of_thread_executions/sync_async/sync_async.md`

Caller thread doesn't wait for result

---

## `src/main/java/com/conduct/interview/_1_bases/multithreading/types_of_work/cpu_heavy/cpu_heavy_work.md`

- massive math calculations
- reports generations
- encryption of data

Number of threads should be the same as number of cores in the CPU.


---

## `src/main/java/com/conduct/interview/_1_bases/multithreading/types_of_work/io_work/io_bound_tasks.md`

Threads are mostly waiting, wasting their time.

- HTTP calls
- database queries
- file reading/writing
- messaging (Rabbit, Kafka)



---

## `src/main/java/com/conduct/interview/_1_bases/multithreading/types_of_work/parallel_tasks/parallel_tasks.md`


- Fork-Join-Pool
- parallel streams


---

## `src/main/java/com/conduct/interview/_1_bases/multithreading/types_of_work/reactive__event_driven_tasks/reactive__event_driven_tasks.md`

Threads are reacting on some event. 

- event listeners
- message consumers
- ui events


---

## `src/main/java/com/conduct/interview/_1_bases/multithreading/types_of_work/scheduled_delayed_tasks/scheduled_delayed_tasks.md`

- cron jobs
- retry logic


---

## `src/main/java/com/conduct/interview/_1_bases/multithreading/way_to_thread_safety/atomic_objects/atomic_objects.md`

Atomic classes allow us to perform atomic operations, which are thread-safe, without using synchronization. 
An atomic operation is executed in one single machine-level operation.

---

## `src/main/java/com/conduct/interview/_1_bases/multithreading/way_to_thread_safety/concurrent_collections/concurrent_collections.md`

Unlike their synchronized counterparts, concurrent collections achieve thread-safety 
by dividing their data into segments. 
ConcurrentHashMap.
CopyOnWriteArrayList.
BlockingQueue.

🧩 What does "segmented" mean?
Segmented means:

🔓 Instead of locking the entire collection, only a small part (segment) of the data is locked or copied when accessed or modified.

🎯 Goal:
Avoid blocking the whole structure

Allow multiple threads to work in parallel on different parts

---

## `src/main/java/com/conduct/interview/_1_bases/multithreading/way_to_thread_safety/locks/locks.md`

Locks are more flexible. they allow for several thread to get inside
the critical section in case they just wanted to read. You can
`tryLock`, `lockInterrupidly`, critical section can start in one method
and end in another.

Reentrant lock allows the same thread to enter the critical section 
several times (with count usage). The fairness feature allows to build
a priority based on how much time threads are waiting for the lock.

Stamped locks are not reentrant, stamp long value is used to unlock
the lock in more safe way though should be careful not to escape the stamp.
Stamped locks provide the optimistic locking when reader can read regardless 
of writing operation is done or not. To be able to see current results.


---

## `src/main/java/com/conduct/interview/_1_bases/multithreading/way_to_thread_safety/synchronized_collections/syncCollections.md`

Wrapper method to make collection synchronized is used.
It's not that performant.

---

## `src/main/java/com/conduct/interview/_1_bases/multithreading/way_to_thread_safety/synchronized_keyword/synchronized.md`

Synchronized keyword (on method, static method or block of code) helps to avoid race condition.
Synchronization is achieved with the help of monitor - mechanism to achieve mutual exclusion (just 
one thread can execute in the critical part), conditional executions with notifications.
There is a lock object associated with every object or class called mutex (binary semaphore, intrinsic locking).
It's better to appUser external object for locking  for the security (attacker can cause a deadlock and Denial Of
Service in case of using 'this').
It's better to avoid Strings as mutex because of String pool. the same for Integer, Long pool.

There are two main problems in the multithreading:
- visibility (two threads can't predict what other thread can do)
- accessibility (two thread try to access the same resource at the same time)

---

## `src/main/java/com/conduct/interview/_1_bases/multithreading/way_to_thread_safety/volatile_keyword/volatile.md`

With the volatile keyword, we instruct the JVM and the compiler to store 
the counter variable in the main memory
Moreover, the use of a volatile variable ensures that all variables that are visible to a given thread 
will be read from the main memory as well

---

## `src/main/java/com/conduct/interview/_1_bases/reflection/reflection.md`

# Let's create a properly formatted Markdown file with 80 characters per line

md_content = """
# Java Reflection

Java Reflection is a powerful feature that allows a program to inspect and
manipulate its own structure and behavior at runtime. It is commonly used for:

- **Inspecting Classes**: Finding information about methods, fields, constructors,
  and annotations of a class.
- **Instantiating Objects**: Dynamically creating objects of a class during
  runtime.
- **Accessing/Modifying Fields and Methods**: Manipulating private and public
  fields or methods, bypassing normal access control.
- **Working with Annotations**: Retrieving metadata attached to classes, methods,
  and fields.

Can be useful in implementing frameworks, testing libraries, 
serialization/deserialization libraries, inspection tools, inspection tools, AOP etc.

## Key Classes in Java Reflection

1. **Class<?>**: Represents a class or interface and is the starting point for
   reflection.
   ```java
   Class<?> clazz = MyClass.class;  // Getting class info
   
2. Field field = clazz.getDeclaredField("name");
3. Method method = clazz.getMethod("doSomething");
4. Constructor<?> constructor = clazz.getConstructor();

Class<?> clazz = Class.forName("com.example.MyClass");
Constructor<?> constructor = clazz.getConstructor();
Object myObj = constructor.newInstance();



---

## `src/main/java/com/conduct/interview/_20_algorythms_and_data_structures/algorithms/search/search_algorythms.md`

Linear Search: just goes one by one till the end until finds.
Binary Search: on sorted array divides to 2 parts and search 
then in one of them.
Ternary Search: Similar to Binary but divides to 3 parts.
Fibonacci Search: Divides into none-equal parts.
Jump Search: Not to go one by one but to make some bigger steps

There 2 two types of complexity measures:
- time complexity - how much time it will take
- space complaxity - how much memory it could consume

Measured as: O(1), O(n), O(n2) etc.

Binary search is considered as the best searching algorithm. It 
implements `divide anc cocuir` principle.

---

## `src/main/java/com/conduct/interview/_20_algorythms_and_data_structures/algorithms/sorting/sorting_algorithms.md`

Bubble Sort.
We have 2 loops: i and j. The I increases as usual but j right bound
is decreasing every time with the destructing the i. 
In the internal j loop we start from start everytime and compare
the current value (arr(j)) with the next (arr(j) + 1) and swap - move
to the right until current value is bigger than next.

Insertion Sort.
We divide array from start on sorted part and unsorted part. 
Initially sorted part has just single the most left element. Then
we go to the next element, compare it with previous, swap, do it again
and again. And left array (sorted array) is getting bigger and bigger.

Merge Sort.
Array is divided into the set of single element arrays which then
are compared and merged into bigger arrays back.

Quick Sort.
Pivot is selected and all elements on the left side are compared to it.
Left side then is becoming ordered, pivot is put to the proper place
Effective on the large data sets.
IT IS THE FASTEST SORTING ALGORITHM!!!

Selection Sort.
Traversing the array and putting the smallest value on the left side.



---

## `src/main/java/com/conduct/interview/_20_algorythms_and_data_structures/big_O_notation/big_O_notation.md`

# Algorithm Complexity (Big O) Cheat Sheet

### 1. O(1) — Constant Time
**The Logic:** Time stays the same regardless of how much data you have.
* **Simple Example:** Accessing an array element by index or looking up a key in a Hash Map.
* **Code:** `return arr[0]`

### 2. O(log n) — Logarithmic Time
**The Logic:** The dataset is cut in half at every step. Very efficient for large data.
* **Simple Example:** Binary Search (finding a name in a sorted phone book by opening the middle).
* **Code:** A loop where the index is multiplied or divided by 2 each time.

### 3. O(n) — Linear Time
**The Logic:** Time grows exactly in proportion to the data. 10x more data = 10x more time.
* **Simple Example:** Finding a value in an unsorted list.
* **Code:** A single `for` loop.

### 4. O(n log n) — Linearithmic Time
**The Logic:** Usually involves sorting. It is basically performing a $O(\log n)$ operation $n$ times.
* **Simple Example:** The most efficient sorting algorithms like MergeSort or QuickSort.
* **Code:** `list.sort()` (Standard library sorting).

### 5. O(n²) — Quadratic Time
**The Logic:** Time grows exponentially. 10x more data = 100x more time.
* **Simple Example:** Comparing every item in a list with every other item.
* **Code:** Nested loops (a `for` loop inside another `for` loop).

---

## Quick Comparison Table

| Big O | Name | Speed | Memory Hook |
| :--- | :--- | :--- | :--- |
| **O(1)** | Constant | Instant | Direct access |
| **O(log n)** | Logarithmic | Very Fast | Halving the data |
| **O(n)** | Linear | Fair | One loop through data |
| **O(n log n)** | Linearithmic | Good | Efficient Sorting |
| **O(n²)** | Quadratic | Slow | Nested loops |

## Interview Rule of Thumb
- **Count the loops:** - 0 loops = $O(1)$
    - 1 loop = $O(n)$
    - 2 nested loops = $O(n^2)$
- **Searching sorted data:** Usually $O(\log n)$.
- **Sorting data:** Usually $O(n \log n)$.


# Search & Sort Algorithm Reference

## 🔍 Search Algorithms

### **Linear Search** | $O(n)$
* **How it works:** Checks every item one by one.
* **Best for:** Unsorted data.
* **Speed:** slow if the list is huge.

### **Binary Search** | $O(\log n)$
* **How it works:** Splits a **sorted** list in half repeatedly.
* **Best for:** Sorted data only.
* **Speed:** Extremely fast.

---

## 🧹 Sorting Algorithms

### **Quick Sort** | $O(n \log n)$
* **How it works:** Picks a "pivot" and moves smaller items left, larger items right.
* **Average:** $O(n \log n)$
* **Worst:** $O(n^2)$ (if pivot is bad).
* **Note:** Usually the fastest in practice.

### **Merge Sort** | $O(n \log n)$
* **How it works:** Splits the list into single items, then merges them back in order.
* **Complexity:** Always $O(n \log n)$ (very stable).
* **Note:** Uses more memory than Quick Sort.

### **Insertion Sort** | $O(n^2)$
* **How it works:** Builds the sorted list one item at a time (like sorting a hand of cards).
* **Complexity:** $O(n^2)$.
* **Note:** Very fast $O(n)$ if the data is **already almost sorted**.

### **Bubble Sort** | $O(n^2)$
* **How it works:** Swaps adjacent items repeatedly.
* **Note:** Highly inefficient. Only mentioned to show what *not* to use.

---

## 📊 Summary Comparison

| Algorithm | Type | Average Case | Worst Case |
| :--- | :--- | :--- | :--- |
| **Binary Search** | Search | **O(log n)** | O(log n) |
| **Linear Search** | Search | **O(n)** | O(n) |
| **Quick Sort** | Sort | **O(n log n)** | O(n^2) |
| **Merge Sort** | Sort | **O(n log n)** | O(n log n) |
| **Insertion Sort**| Sort | **O(n^2)** | O(n^2) |
| **Bubble Sort** | Sort | **O(n^2)** | O(n^2) |

## 💡 Interview Tips
1.  **Sorted Data?** Use Binary Search.
2.  **General Sorting?** Use Quick Sort or Merge Sort.
3.  **Space Constrained?** Quick Sort is better than Merge Sort (uses less RAM).
4.  **Almost Sorted?** Insertion Sort is actually very efficient.

---

## `src/main/java/com/conduct/interview/_20_algorythms_and_data_structures/data_structures.md`

A data structure is a storage that is used to store and organize data.
1. Linear data structures (elements are placed sequentially, elements
have connections to other elements):
- static (fixed memory size): array;
- dynamic (size can be changed at runtime): stack, queue, LinkedList.

2. None-linear data structures (it's not possible to traverse the 
structure in single run): 
- graph (nodes and edges which connect them) 
- tree
-- `binary tree` with at most 2 child nodes
-- `binary search tree` - left child has value smaller than parent node and
right child has bigger value
-- `ternary tree` with at most 3 child nodes
-- `ternary search tree`
-- `b-tree` - binary or whatever tree with more complex keys of nodes
-- `red-black tree` - with black top node, balancing is guaranteed by the
additional bit reserved for the color
- hashing data structure

---

## `src/main/java/com/conduct/interview/_23_redis_cache/redis.md`

# Redis: Key Concepts

Redis is an in-memory data store used as a database, LRUCache, and message broker.

## Main Data Structures
- **Strings**: Simple values, up to 512MB.
- **Lists**: Ordered collections, good for queues.
- **Sets**: Unique, unordered collections.
- **Sorted Sets**: Sets with scores, ideal for leaderboards.
- **Hashes**: Key-value pairs, great for storing objects.
- **Bitmaps & HyperLogLogs**: Used for analytics and counting unique elements.

## Core Features
- **Persistence**: Supports snapshots (RDB) and operation logs (AOF).
- **Replication & Clustering**: Master-slave replication, Sentinel for high
  availability, and Redis Cluster for sharding.
- **Transactions**: Atomic execution with `MULTI` and `EXEC`.
- **Lua Scripting**: Runs scripts atomically on the server.
- **Pub/Sub**: Messaging via channels for real-time updates.
- **Eviction Policies**: Manages memory with strategies like LRU.

## Common Uses
- **Caching**: Fast, in-memory storage.
- **Session Store**: Handles web session data.
- **Real-Time Analytics**: Data tracking with sets and sorted sets.
- **Message Queues**: Queuing with lists and Pub/Sub.

Redis combines speed with flexibility, making it ideal for low-latency,
real-time applications.


---

## `src/main/java/com/conduct/interview/_2_memory_and_reference_types/garbage_collectors/gcs.md`

# 🚀 Java Garbage Collectors — Interview Cheat Sheet

## 1. Core Trade-Offs (The "Big Four")

Every GC choice is a deliberate compromise between **Throughput** (processing
power) and **Latency** (pause times).

| Collector | Core Metric | How It Works | Best Use Case |
| :--- | :--- | :--- | :--- |
| **Serial GC** | **Resource Savings** | Single-threaded **Stop-The-World (STW)**. Uses one CPU core sequentially to clean the entire heap. | Tiny CLI tools, sidecars, or ultra-constrained containers (<2GB RAM). |
| **Parallel GC** | **Max Throughput** | Multi-threaded **STW**. Utilizes all CPU cores simultaneously to clean as fast as physically possible. | Background batch processing, calculations, offline pipelines. |
| **G1 GC** | **Balanced** | Divides the heap into dynamic **Regions**. Cleans and compacts areas with the most garbage first. Predictable pauses. | **Default choice** for standard enterprise APIs and web apps. |
| **ZGC** | **Ultra-Low Latency** | Performs cleaning and compaction **concurrently** while code runs. Pauses are **sub-millisecond (<1ms)**. | High-frequency trading, payment gateways, huge heaps (32GB+). |

---

## 2. Dynamic Memory Layout Evolution

Interviewers love asking how memory *physically* looks under the hood. It
changed dramatically with modern collectors:

* **Traditional Model (Serial / Parallel):** Strict, continuous, fixed-size
  physical blocks of memory for Eden, Survivor (S0/S1), and Old generation.
* **Modern Model (G1 / ZGC):** The heap is fragmented into thousands of
  independent, non-contiguous **Regions** (1MB to 32MB).
    * A region is assigned a logical role (**Eden, Survivor, Old**) dynamically.
    * Includes **Humongous Regions** to hold massive objects directly in the
      Old generation without copying them around and causing fragmentation.

---

## 3. The Interview Timeline (Version Changes)

If they ask *"Which GC was default when?"* or *"What happened to CMS?"*, give
them this clean progression:

* **Java 8:** Default was **Parallel GC**. Class metadata moved to **Metaspace** (Native Memory), completely replacing the old `PermGen` space.
* **Java 11:** Default changed to **G1 GC**. CMS was deprecated.
* **Java 17:** Default remains **G1 GC**. Old-school **CMS was completely
  removed**. ZGC became production-ready.
* **Java 21 / 25:** Default remains **G1 GC**. **Generational ZGC** is
  introduced (`-XX:+UseZGC -XX:+ZGenerational`), separating short-lived and
  long-lived objects within a concurrent architecture for higher throughput.

---

## 4. Why CMS was Executed (The G1 Kill Shot)

If an interviewer asks why **CMS (Concurrent Mark-Sweep)** was killed and
replaced by **G1**:

* ❌ **CMS had no Compaction:** It left memory full of holes (fragmentation),
  eventually causing massive, unpredictable **Full GC** spikes.
* ❌ **Concurrent Mode Failure:** If allocations happened faster than CMS could
  clean, the JVM would panic, freeze completely, and run a single-threaded
  fallback Full GC.
* ✅ **G1 compacts by default** because it evacuates entire regions into new
  ones, leaving zero fragmentation behind. It also lets you set a maximum
  target pause time (`-XX:MaxGCPauseMillis`).

---

## 💡 Quick Interview Blueprint Answers

* **Q: "What GC do you use in production on JDK 21?"**
    * *A:* "We use the default **G1 GC**. It divides the heap into dynamic
      regions rather than continuous blocks, providing an excellent balance of
      high throughput and predictable pause times for our microservices."
* **Q: "When would you switch away from the default?"**
    * *A:* "I would switch to **Parallel GC** if I am running a non-user-facing
      background batch job where raw throughput matters and pauses don't impact
      anyone. I would switch to **Generational ZGC** if I have an ultra-low
      latency requirement or a massive heap where stop-the-world pauses over 1ms
      cannot be tolerated."

---

## `src/main/java/com/conduct/interview/_2_memory_and_reference_types/jvm_tuning/jvm_tuning.md`

# JVM Tuning Overview

## Heap Memory Settings

- **Initial Heap Size (`-Xms`)**: Sets the initial size of the heap memory.
- **Maximum Heap Size (`-Xmx`)**: Sets the maximum size of the heap memory.

  ```bash
  java -Xms512m -Xmx4g -jar yourapp.jar

java -Xss1m -jar yourapp.jar

java -Xlog:gc* -jar yourapp.jar

java -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=512m -jar yourapp.jar

java -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/path/to/dump.hprof -jar yourapp.jar

java -Xloggc:/path/to/gc.log -jar yourapp.jar



---

## `src/main/java/com/conduct/interview/_2_memory_and_reference_types/memory_leaks/memory_leak.md`

Memory leaks in Java typically occur when objects are unintentionally held 
in memory longer than necessary. This can happen in various scenarios, 
such as improper handling of static references, incorrect usage of caches, 
or unintended object references.

---

## `src/main/java/com/conduct/interview/_2_memory_and_reference_types/memory.md`

## Java Memory Model (Execution Perspective)

The JVM divides runtime data memory into two fundamental execution areas:
**Stack memory** (thread execution) and **Heap memory** (shared data storage).

---

### 1. Stack Memory (Where Execution Happens)

Stack memory is strictly **thread-contained** and operates on a Last-In,
First-Out (LIFO) structure. It manages the active execution of methods.

* **Thread Isolation:** Each thread has its own private Stack. One thread cannot
  access or corrupt the Stack of another.
* **Stack Frames:** Every time a method is called, a new *Stack Frame* is pushed
  onto the stack. This frame stores:
  * **Local Variables:** Primitive types (`int`, `char`, etc.) and the
    **references (memory addresses)** pointing to actual objects on the Heap.
* **Automatic Lifecycle:** When a method finishes executing, its stack frame is
  automatically popped off and destroyed. Memory is reclaimed immediately
  without involving the Garbage Collector.

> 🔴 **Fatal Error:** If thread execution goes too deep (e.g., infinite
> recursion), the stack space limit is breached, throwing a
> **`StackOverflowError`**.

---

### 2. Heap Memory (Where Objects Live)

Heap memory is a unified runtime data area created on JVM startup. It is
**shared across all threads**.

* **Object Storage:** Stores all instances of classes, interfaces, and arrays.
* **Instance Fields:** Non-static fields of a class (both primitives and object
  references) live directly inside their parent object on the Heap.
* **Garbage Collection:** Memory is managed non-deterministically by the
  Garbage Collector (GC) only after an object becomes unreachable.

> 🔴 **Fatal Error:** If the heap runs out of allocatable space for new objects,
> the JVM throws an **`OutOfMemoryError: Java heap space`**.

---

### 🔀 Stack vs. Heap: The Reference Link

* **Local Variable Reference:** `User user = new User();`
  * The variable `user` is a reference (pointer) living on the **Stack**.
  * The actual `User` instance data lives on the **Heap**.
* **Object Property Reference:** If `User` has a field `Address address;`
  * The `address` reference variable lives inside the `User` object on the
    **Heap**.

---

### 3. Heap Structure & Generational GC

The JVM structures the Heap into distinct zones to optimize Garbage Collection
based on the rule that **most objects die young**.



#### A. Young Generation (Short-Lived Objects)
Designed for high-frequency, lightning-fast cleanup of short-lived objects.
* **Eden Space:** The initial landing zone where all new objects are allocated.
* **Survivor Spaces (S0 / S1):** Two equal spaces used to age objects before
  promoting them to the Old Generation.
  * *How they work:* During a Minor GC, surviving objects from Eden and the
    active Survivor space (`From`) are copied cleanly into the empty Survivor
    space (`To`).
  * The active space and Eden are then completely wiped. This alternation
    completely avoids memory fragmentation.

#### B. Old (Tenured) Generation (Long-Lived Objects)
* Holds long-surviving data, such as Spring beans, caches, or long-running tasks.
* Objects are moved ("promoted") here only after surviving a specific number
  of Minor GC cycles (the aging threshold).
* Cleaned via Major/Full GC cycles, which are significantly heavier and can
  cause application pauses.

#### C. Metaspace (Class Metadata)
* **Off-Heap Memory:** Located in native OS memory, separate from the Heap.
* **Purpose:** Stores runtime class definitions, method metadata, and
  annotations. It scales dynamically based on available system memory.

---

### 🚀 Advanced Optimizations (Great for Senior Interviews)

* **Escape Analysis:** If the Just-In-Time (JIT) compiler proves that an object
  does not escape the scope of the method where it was created, the JVM bypasses
  the Heap entirely. It breaks the object down and allocates it directly on the
  **Stack**, reducing GC overhead.
* **TLAB (Thread-Local Allocation Buffer):** To prevent threads from locking
  each other out while allocating memory on the shared Heap, the JVM gives each
  thread a tiny dedicated slot inside Eden called a TLAB. Threads allocate new
  objects inside their own TLAB safely and concurrently without synchronization
  overhead.

---

### 4. Static Members & Class Data

Static fields and static methods are bound directly to the class itself,
not to any specific instance of that class.

Where they live is split based on the type of data:

* **Class Metadata (Metaspace):** The architectural blueprint of the class
  (method bytecode, annotations, structural layout, and runtime constant pool)
  is stored off-heap in **Metaspace**.
* **Static Fields & References (Heap):** The actual data values of static fields,
  including reference variables pointing to other objects, are stored inside a
  special internal `java.lang.Class` object instance, which lives on the **Heap**.

---

### 🔀 Quick Memory Reference Map

| Data Type | Storage Location | Accessible By |
| :--- | :--- | :--- |
| **Local Variables** | Stack Frame | Owning Thread Only |
| **Instance Fields** | Heap (Inside Object) | All Threads |
| **Static Fields** | Heap (Inside Class Object) | All Threads |
| **Class Bytecode** | Metaspace (Native Memory) | JVM Engine |

This memory structure helps Java efficiently manage object lifecycle,  
garbage collection, and multithreading.

---

## `src/main/java/com/conduct/interview/_2_memory_and_reference_types/reference_types/reference_types.md`

# 🚀 Java Reference Types — Quick Interview Sheet

Reference types tell the Garbage Collector (GC) how aggressively it can reclaim
an object from memory.

| Reference Type | GC Behavior | Best Use Case |
| :--- | :--- | :--- |
| **Strong** | **Never collected** while active. Will trigger `OutOfMemoryError` instead. | Default for 99% of your normal code. |
| **Soft** | Collected **only during memory pressure** (when heap is almost full). | Memory-sensitive caches (e.g., heavy images/JSON trees). |
| **Weak** | Collected **immediately during the next GC cycle**, no matter what. | `WeakHashMap` or metadata tracking (avoids memory leaks). |
| **Phantom** | Object is already dead (`get()` always returns `null`). Used as a post-mortem notification. | Replacing `finalize()` for low-level native resource cleanup. |

---

## 1. Strong Reference — "The Default Anchor"
* **What it is:** Your everyday object declaration: `User u = new User();`
* **GC Rules:** As long as a strong reference points to an object, the GC will **never** touch it. To make it eligible for collection, you must explicitly break the bond by setting it to `null`.

## 2. SoftReference — "The Safety Net Cache"
* **GC Rules:** Safe during good times. But if the JVM runs out of heap space and is about to crash with an OOM, the GC will forcefully wipe out all Soft References to clear room.
* **Primitive Summary:** It stays alive until a **memory crisis** happens.



## 3. WeakReference — "The Short-Lived Guest"
* **GC Rules:** Completely ignored by the GC. The moment the Garbage Collector runs, if it sees an object held *only* by a Weak Reference, it destroys it instantly.
* **Primitive Summary:** Survives exactly until the **very next GC cycle**.

## 4. PhantomReference — "The Ghost Notification"
* **GC Rules:** You cannot interact with the object anymore. Calling `.get()` always returns `null`. It places a token into a `ReferenceQueue` *after* the object has been finalized.
* **Primitive Summary:** Used strictly as a **"goodbye email"** to clean up off-heap native memory (like file descriptors or direct byte buffers).

---

## 💡 10-Second Interview Blueprint

> * **Strong:** Never deleted.
> * **Soft:** Deleted only if memory is completely full.
> * **Weak:** Deleted during the next immediate GC run.
> * **Phantom:** Already dead; used for low-level post-mortem cleanup.

---

## `src/main/java/com/conduct/interview/_3_spring/aop/aop.md`

### Spring AOP (Aspect-Oriented Programming)

Spring AOP allows for the separation of cross-cutting concerns, like logging
or security, from business logic, making code modular and maintainable.

#### Key Concepts
- **Aspect**: A modularized concern, e.g., logging, transaction handling.
  (the aspect of application we need to apply in many places)
- **Join Point**: A specific point in execution, like a method call.
  (WHERE to execute - in method call)
- **Advice**: Code to execute at a join point (e.g., `@Before`, `@After`).
  (WHAT to do and WHEN - "do something @Before method call")
- **Pointcut**: Expression to select join points for advice application.
  (expression to filter methods by packages and classes for instance)
- **Weaving**: Linking aspects with objects to create an advised object.
  (compile time, load, runtime weaving)

#### Example
```java
@Aspect
@Component
public class LoggingAspect {

[//]: # This is Advice Type - ***@Before*** - when to do ( before the method )
    ***execution(* com.example.service.*.*(..))*** <<< pointcut
    @Before("execution(* com.example.service.*.*(..))")

[//]: # This is a Join point - where to do ( in this method )
    public void logBefore() {

[//]: # This is Advice - what to do ( body of method )
        System.out.println("Executing method...");
    }
}


---

## `src/main/java/com/conduct/interview/_3_spring/bean_lifecycle/postConstruct_annotation/post_construct.md`

`@PostConstruct` is used in Spring to mark a method that is executed after
dependency injection. The method must not have arguments, as Spring automatically
invokes it after the bean is fully constructed and all dependencies are injected.
This ensures the method relies only on the injected fields for initialization.
It is ideal for tasks like resource initialization, validation, or starting
background jobs. The method is executed once in the bean's lifecycle, ensuring
a consistent and predictable setup phase.


---

## `src/main/java/com/conduct/interview/_3_spring/bean_lifecycle/spring_bean_life_cycle.md`

The Spring Bean lifecycle includes several phases that every bean goes through from
its creation to its disposal. By interacting with these phases, it is possible to
set up dependencies, manage resources, and integrate external libraries into the
Spring framework. The lifecycle consists of the following steps:

- **Instantiation**: Bean is created by executing the constructor.
- **Populating properties**: Setter methods are called to populate bean properties.
- **Aware hooks**: Interfaces such as `BeanNameAware`, `BeanFactoryAware`, and
  `ApplicationContextAware` are invoked to pass context-related information.
- **BeanPostProcessor**: Includes two methods:
    - `postProcessBeforeInit`: Called before initialization.
    - `postProcessAfterInit`: Called after initialization.
- **PostConstruct annotation**: Executed after `postProcessBeforeInit` and before
  `postProcessAfterInit`.
- **InitializingBean**: `afterPropertiesSet` method is executed for custom
  initialization logic.
- **CustomInitMethod**: A user-defined initialization method can be called after
  bean properties are set.
- **PreDestroy annotation**: Invoked before the bean is destroyed to perform cleanup.
- **DisposableBean**: `destroy` method is executed for custom destruction logic.
- **Custom destroy method**: A user-defined destroy method is called during
  bean destruction.


---

## `src/main/java/com/conduct/interview/_3_spring/objectMapper/object_mapper.md`

ObjectMapper is the flagship class of the Jackson library. 
In a Spring Boot ecosystem, it acts as the primary "translator" 
between Java objects and JSON data, enabling seamless communication 
between your server and its clients.

Core Meaning & Concepts
Serialization (Writing): The process of converting a Java Object into a JSON string. 
This is what happens when your @RestController sends a response to a client.

Deserialization (Reading): The process of converting a JSON string back into a Java Object. 
This occurs when you receive data via @RequestBody.

Data Binding: The automatic mapping of JSON keys to Java fields. 
Jackson uses reflection to match "user_name" in JSON to userName in Java.

---

## `src/main/java/com/conduct/interview/_3_spring/security/spring_security.md`

Spring Security is a powerful and highly customizable authentication and access-control framework.

When security starter is added class `FilterChainProxy` calls additional filters to
handle request. There could be 3 scenarios:

1. Redirect to default login page. So it's just `GET` /login
In this scenario:
- UsernamePasswordAuthenticationFilter - checks the url contains `login` and if 
method of request is `POST`. Does nothing as method is `GET`
- DefaultLoginPageGeneratingFilter - checks if any authentication is done or not
and in case url path has `/login`, `/logout` or `/login/error` - generates the
login page
- AuthorizationFilter - This filter throws `AccessDeniedException` in case there is 
no authentication done.
- ExceptionTranslationFilter - catches `AccessDeniedException` and forces application
to redirect the browser to `/login` request

2. Display default login page.
- UsernamePasswordAuthenticationFilter - does nothing as it's `GET` request
- DefaultLoginPageGeneratingFilter - generates login page as url is `/login`

3. User enters credentials.
- UsernamePasswordAuthenticationFilter - this is `POST` with `/login` so
it extracts username and password and creates something like 
`UsernamePasswordAuthenticationToken` - Authentication object.
Method `authenticate` of `AuthenticationManager` is called then here. 
`ProviderManager` provides the implementation.
Authentication manager gets authentication object with credentials as an input
and returns the same authentication object but with principal.
If authenticated then `isAuthenticated` = true
ProviderManager has a list of AuthenticationProviders's with single method `authenticate`.
Example: `DaoAuthenticationProvider` which has `UserDetailsService`.
`UserDetailsService` with help of method `loadUserByUsername` fetches 
the user (f.e. from db) and compares his credentials with credentials 
from `Authentication` object. And returns principal in case of success.



To enable security we just need to add starter, create configuration file with
annotation `@EnableWebSecurity`, create some beans:
-  Security filter chain
- InMemoryUserDetailsManager
- PasswordEncoder

If we need method authorisation then we add annotation `EnableMethodSecurity`.




---

## `src/main/java/com/conduct/interview/_3_spring/spring_boot/spring_boot.md`

Spring Boot is the framework built on top of the Spring itself.
It consists of many configurations - auto configurations.
Spring boot has dependency - autoconfigure. 
Inside of it in META-INF folder there is a 
`spring.factories` file with the list of enabled auto configurations.

Those are configurations which provide beans related to 
particular third party library where beans are created based on some
conditions. Those conditions could be related to existence of some
property in application.properties or some class in class path etc.

Starter is a separate spring module which has it's own file
`spring-factories` with the path to it's Configuration (Auto Configuration).


---

## `src/main/java/com/conduct/interview/_3_spring/spring.md`

`POJO` - Plain Old Java Object - is the object which
doesn't depend on any framework. Just has fields and methods.

`JAVA Bean` - Object which should be Serializable,
have default constructor, private properties,
public getters and setters

`Spring Bean` - any object which managed by the Spring
IoC container.


Preparing for an interview on **Spring** and **Spring Boot**, including understanding **transactions**, 
is essential for showcasing your expertise as a Java developer, especially given your experience with Spring Boot, 
Hibernate, and microservices as noted in your LinkedIn profile. Below, 
I’ll provide a focused guide on Spring and Spring Boot, covering core concepts, 
common interview topics, and a simple explanation of transactions, tailored to your background. 
I’ll also include sample interview questions with concise answers, leveraging your experience with projects 
like the Spring Boot 2 to 3 upgrade, monolith-to-microservices transition, and Auto-QM service.


Why injecting bean via constructor is best approach:
1. Object will not be changed as it can be marked as final. Safe for multithreading
2. Object could not be created in half-ready state
3. Having many parameters inside constructor is a code-smell marker
4. It's good for Unit Tests. You don't need to up all the Spring Context but just can
   create object with one call

---

### Spring and Spring Boot Overview

**Spring** is a powerful Java framework for building enterprise applications, providing dependency injection, 
aspect-oriented programming, and modules like Spring MVC, Spring Data, and Spring Security. 
**Spring Boot** is an extension of Spring that simplifies development with auto-configuration, 
embedded servers, and a convention-over-configuration approach, making it ideal for microservices and rapid development.

#### Key Concepts to Prepare

1. **Core Spring Concepts**
    - **Dependency Injection (DI)**: Manages object creation and dependencies using `@Autowired`, `@Component`, or `@Bean`.
    - **Inversion of Control (IoC)**: Spring container (e.g., `ApplicationContext`) controls object lifecycle.
    - **Annotations**: `@Component`, `@Service`, `@Repository`, `@Controller`, `@RestController`.
    - **Your Experience**: You’ve used Spring Boot extensively in your microservices and Auto-QM projects, 
    - likely leveraging DI for services and repositories.

2. **Spring Boot Features**
    - **Auto-Configuration**: Automatically configures components like databases or web servers based on 
   dependencies (e.g., Spring Data JPA auto-configures Hibernate).
    - **Starters**: Pre-configured dependencies (e.g., `spring-boot-starter-web`, `spring-boot-starter-data-jpa`).
    - **Embedded Servers**: Tomcat, Jetty, or Undertow for standalone applications.
    - **Actuator**: Endpoints for monitoring (e.g., `/health`, `/metrics`).
    - **Your Experience**: You upgraded Spring Boot 2 to 3, handling changes like Jakarta EE and deprecated features, 
   and used starters for microservices.

3. **Spring MVC**
    - Handles HTTP requests using `@Controller` or `@RestController`.
    - Key annotations: `@GetMapping`, `@PostMapping`, `@RequestParam`, `@PathVariable`.
    - **Your Experience**: You built RESTful APIs for microservices, likely using `@RestController` and OpenAPI specs.

4. **Spring Data JPA**
    - Simplifies database access with repositories (e.g., `JpaRepository`).
    - Integrates with Hibernate for ORM, as you used in your Auto-QM service with PostgreSQL.
    - **Your Experience**: You managed data access in microservices, likely using Spring Data JPA for call evaluations.

5. **Spring Cloud**
    - Tools for microservices: Service Discovery (Eureka), API Gateway (Spring Cloud Gateway), Circuit Breakers (Resilience4j).
    - **Your Experience**: Your microservices project involved inter-service communication (Feign/WebClient), 
   suggesting familiarity with Spring Cloud concepts.

6. **Spring Security**
    - Handles authentication (e.g., OAuth2, JWT) and authorization.
    - **Your Experience**: You removed deprecated `WebSecurityConfigurerAdapter`
      during the Spring Boot 3 upgrade, indicating exposure to Spring Security.

---

### Transactions in Simple Words

A **transaction** in Spring (and databases) is like a single, reliable unit of work that ensures all database 
operations either complete successfully or are fully undone if something goes wrong. 
Think of it as a "promise" to keep your data consistent.

#### Simple Explanation
- Imagine you’re transferring $100 from Account A to Account B:
    - Step 1: Subtract $100 from Account A.
    - Step 2: Add $100 to Account B.
- A transaction ensures **both steps** happen together. If Step 1 works but Step 2 fails (e.g., due to a database error), 
the transaction "rolls back" Step 1, so Account A still has its $100, preventing inconsistent data.
- In Spring, you use `@Transactional` to wrap a method, telling Spring to manage this "all-or-nothing" behavior.

#### Key Points
- **ACID Properties**:
    - **Atomicity**: All operations succeed, or none do.
    - **Consistency**: Data remains valid (e.g., account balances don’t go negative).
    - **Isolation**: Transactions don’t interfere with each other.
    - **Durability**: Once committed, changes are saved permanently.
- **Spring’s Role**: Spring manages transactions via `@Transactional`, integrating with Hibernate or other data sources, 
handling commits and rollbacks automatically.
- **Your Experience**: You used `@Transactional` in your Auto-QM service to ensure consistent storage of evaluation 
- results in PostgreSQL, likely when saving data from RabbitMQ messages.

---

### Common Interview Questions and Answers

Below are sample questions on Spring, Spring Boot, and transactions, with answers tailored to your experience.

1. **What is the difference between Spring and Spring Boot?**
    - **Answer**: Spring is a framework for enterprise Java applications, providing dependency injection 
   and modules like Spring MVC and Spring Data. Spring Boot simplifies Spring development with auto-configuration, 
   embedded servers, and starters, reducing setup time. In my microservices project, 
   I used Spring Boot’s `spring-boot-starter-web` and `spring-boot-starter-data-jpa` to quickly build REST APIs 
   and integrate with PostgreSQL.

2. **How does Spring Boot’s auto-configuration work?**
    - **Answer**: Spring Boot auto-configures components based on classpath dependencies and properties. 
   For example, including `spring-boot-starter-data-jpa` auto-configures Hibernate and a data source if a 
   database driver is present. In my Auto-QM service, auto-configuration set up Hibernate for PostgreSQL, 
   letting me focus on writing repositories and services.

3. **What is a transaction, and how does Spring manage it?**
    - **Answer**: A transaction ensures multiple database operations are executed as a single unit, 
   either all succeeding or all rolling back to maintain data consistency. Spring manages transactions 
   with `@Transactional`, which wraps a method to handle commits and rollbacks. In my Auto-QM service, 
   I used `@Transactional` to ensure call evaluation results were consistently saved to PostgreSQL, 
   even when integrating with RabbitMQ.

4. **What happens if you don’t use @Transactional in a Spring application?**
    - **Answer**: Without `@Transactional`, database operations aren’t grouped as a single unit, 
   risking inconsistent data if one operation fails. For example, in my microservices project, 
   omitting `@Transactional` when saving related data could leave partial updates in PostgreSQL. 
   Using `@Transactional` ensured atomicity, like when storing evaluation scores and sub-scores together.

5. **How do you handle transaction propagation in Spring?**
    - **Answer**: Transaction propagation defines how transactions interact when one `@Transactional` method calls another. 
   Common settings include `REQUIRED` (default, joins existing transaction or starts a new one) and `NESTED` 
   (creates a sub-transaction). In my Auto-QM service, I used `REQUIRED` to ensure all database writes for call 
   evaluations were part of a single transaction, maintaining consistency.

6. **What challenges did you face during your Spring Boot 2 to 3 upgrade?**
    - **Answer**: During the Spring Boot 2 to 3 upgrade, I faced challenges like 
    - migrating from `javax.*` to `jakarta.*` packages for Hibernate and JPA, 
    - refactoring `@Transactional` annotations, and 
    - removing deprecated `WebSecurityConfigurerAdapter`. 
    - I also updated third-party libraries and fixed test failures due to Spring’s behavior changes, 
   ensuring my microservices and Auto-QM service ran smoothly.

7. **How do you secure a Spring Boot microservice?**
    - **Answer**: I secure Spring Boot microservices using Spring Security with OAuth2 or JWT for authentication and 
   role-based authorization. In my microservices project, I ensured secure inter-service communication with 
   Feign/WebClient, likely using JWT tokens. During the Spring Boot 3 upgrade, I updated security configurations 
   to align with new component-based approaches.

8. **How do you optimize Spring Boot performance?**
    - **Answer**: I optimize Spring Boot by using lazy loading for Hibernate relationships, 
   enabling second-level caching, and optimizing queries to avoid N+1 issues, as I did in my Auto-QM service. 
   I also used GraalVM Native Image in one project to reduce startup time and memory usage, ideal for microservices. 
   Actuator endpoints helped monitor performance in production.

---

### The N+1 Problem in Spring Context
Since you mentioned the **N+1 problem** in your previous query, it’s worth connecting it to Spring:
- **Context**: The N+1 problem often occurs in Spring Data JPA with Hibernate when fetching entities with relationships 
(e.g., `@OneToMany`). For example, querying a list of calls and their evaluation rules in your Auto-QM service 
could trigger N+1 queries if not optimized.
- **Solutions in Spring**:
    - Use `JOIN FETCH` in JPQL: `select c from Call c join fetch c.evaluationRules`.
    - Define an `@EntityGraph` in your Spring Data repository:
      ```java
      @EntityGraph(attributePaths = {"evaluationRules"})
      List<Call> findAll();
      ```
    - Use DTO projections to fetch only needed fields: `select new com.example.CallDTO(c.id, c.evaluationRules) from Call c`.
- **Your Experience**: You likely optimized queries in your Auto-QM service to fetch call data efficiently,
avoiding N+1 issues when integrating with PostgreSQL.

---

### Preparation Tips

1. **Review Your Experience**: Be ready to discuss how you used Spring Boot in your microservices 
(e.g., REST APIs with `@RestController`, OpenAPI specs) and Auto-QM service (e.g., `@Transactional` 
for data consistency, RabbitMQ integration).
2. **Practice Transactions**: Understand `@Transactional` attributes like `propagation`, `isolation`, and `rollbackOn`. 
Practice scenarios like:
   ```java
   @Transactional
   public void saveEvaluation(Call call, List<Rule> rules) {
       callRepository.save(call);
       ruleRepository.saveAll(rules);
   }
   ```
   Explain how this ensures atomicity for your Auto-QM service.
3. **Code Examples**: Be prepared for coding tasks, like writing a Spring Boot REST controller or a repository 
with a JPQL query. For example:
   ```java
   @RestController
   @RequestMapping("/calls")
   public class CallController {
       @Autowired
       private CallRepository callRepository;

       @GetMapping
       @Transactional(readOnly = true)
       public List<CallDTO> getAllCalls() {
           return callRepository.findAllWithRules(); // Uses JOIN FETCH or EntityGraph
       }
   }
   ```
4. **System Design**: If asked to design a system, explain how Spring Boot fits into a microservices architecture, 
using `@Transactional` for data consistency and Spring Cloud for service discovery or circuit breakers.
5. **N+1 Problem**: Practice explaining and solving N+1 issues in Spring Data JPA, as covered in the previous query. 
Be ready to write a query or repository method to fix it.
6. **Mock Interviews**: Use platforms like LeetCode or Pramp to practice Spring Boot-related coding or system design 
questions, focusing on REST APIs, transactions, and Hibernate integration.

---

### Additional Resources
- **Books**: “Spring in Action” by Craig Walls for Spring and Spring Boot fundamentals.
- **Online Courses**: Spring Boot and Spring Data JPA courses on Udemy or Pluralsight.
- **Practice**: Build a small Spring Boot project with a REST API, Hibernate entities, 
and `@Transactional` methods. Simulate an N+1 issue and fix it with `JOIN FETCH` or `@EntityGraph`.

If you need specific code examples (e.g., a Spring Boot controller with transactional logic or a repository fixing N+1)
or want to practice system design questions involving Spring, let me know! Good luck with your interview!




---

## `src/main/java/com/conduct/interview/_3_spring/transactions/transactions.md`

# Spring Boot Transactions

Spring Boot provides robust transaction management, simplifying database operations
with the `@Transactional` annotation.

## Key Concepts

- **Transaction Propagation**: Defines how transactions behave across method calls.
    - `REQUIRED`: Joins an existing transaction or creates a new one (default).
    - `REQUIRES_NEW`: Suspends any existing transaction, always creating a new one.
    - `SUPPORTS`: Runs within a transaction if one exists; doesn’t start a new one.
    - `NOT_SUPPORTED`: Executes without a transaction, suspending any existing one.
    - `MANDATORY`: Requires an existing transaction, otherwise throws an exception.
    - `NEVER`: Ensures no transaction exists, throws an error if one is present.
    - `NESTED`: Runs within a nested transaction, allowing partial rollbacks.

- **Isolation Levels**: Defines how transactions isolate data access.
    - `DEFAULT`: Uses the default database isolation level.
    - `READ_COMMITTED`: Prevents dirty reads.
    - `READ_UNCOMMITTED`: Allows dirty reads.
    - `REPEATABLE_READ`: Prevents non-repeatable reads.
    - `SERIALIZABLE`: Ensures full isolation across all transactions.

## Transactional Annotation

Use `@Transactional` to demarcate transaction boundaries. It can be applied at
either the class or method level.

```java
@Transactional
public void saveData(DataEntity entity) {
    repository.save(entity);
}


---

## `src/main/java/com/conduct/interview/_4_oop/oop.md`

# Object-Oriented Programming (OOP) Principles

## a) Abstraction

Abstraction is the process of simplifying complex real-world objects into a programming  
model. It focuses on **details hiding**, presenting only the essential features without  
exposing the implementation details.

- **Techniques**: Interfaces and abstract classes.
- **Goal**: To show only what is necessary to the user and hide the complexities of  
  implementation.

---

## b) Encapsulation

Encapsulation is about **data hiding** and **binding** together the code and the data  
it manipulates. It ensures that a class maintains its own state and provides controlled  
access to its internal data.

- **Concepts**:
  - **Private methods and fields**: Restrict access to the internal state.
  - **Public methods**: Provide controlled access to the data.
  - **Data Integrity**: The class ensures its own state remains consistent.

---

## c) Inheritance

Inheritance allows a class to **inherit data and behaviors** from another class, promoting  
**code reuse** and **extensibility**.

- **Visibility**: Only `public` and `protected` members of the superclass are inherited.
- **Types**:
  - **Class Inheritance**: A class inherits another class.
  - **Interface Implementation**: A class implements an interface.
  - **Interface Inheritance**: An interface inherits another interface.

---

## d) Polymorphism

Polymorphism means **"many forms"** and allows a reference to point to different kinds  
of objects at different times. The actual class of the object that a reference points to  
can be determined at runtime, allowing flexibility in method behavior.

- **Types of Polymorphism in Java**:
  1. **Static Polymorphism**: Achieved through method overloading (methods with the same  
     name but different parameters).
  2. **Dynamic Polymorphism**: Achieved through method overriding (subclass provides a  
     specific implementation of a method).

---

## e) Association

Association defines how objects are **related** and **interact** with each other in OOP.

- **Composition**: A strong "belongs-to" relationship where the contained object's  
  lifecycle depends on the container. It's a **"has-a"** relationship.
- **Aggregation**: A weaker "has-a" relationship where the contained object can exist  
  independently of the container.

---

These principles form the foundation of OOP, guiding the design and structure of software  
systems to enhance modularity, reusability, and maintainability.

---

### Key Differences between OOP and FP

- **OOP**: Focuses on modeling real-world entities with classes and objects, promoting  
  code reuse, encapsulation, and inheritance.
- **FP**: Emphasizes functions and immutability, using pure functions, lambda expressions,  
  and streams for declarative and concise data processing.

Functional Aspects:
Pure Functions: The filter and map operations are stateless and produce consistent outputs.
Immutability: Employee is immutable (final fields, no setters); List.of creates an immutable list.
Declarative: The stream pipeline declares what to do (filter, transform, collect) without loops or mutable state.
Higher-Order Functions: filter and map take lambda expressions as arguments.

---

## `src/main/java/com/conduct/interview/_5_solid/solid.md`

# SOLID Principles

**S - SRP:**  
One reason to change.  
Increases testability and cohesion.  
*Example:* `Book` and `PrintSomething` (printing)  
should be separate.

**O - OCP:**  
Open for extension, closed for modification.  

**L - LSP:**  
It should be possible to use subtypes whenever base type 
could be used.

Ostrich is a bird but it can't fly.

Whenever base Car has some speed restrictions it's child also
must have those restrictions.

Method in subtype should not declare more exception than
method in parent.

Subtypes should maintain the constraints of the base class.

Overriding method should not widen the method visibility to public.

Parameters in overriding method should not change (hierarchically).

Return type should not change (hierarchically).

Pre-condition (some checks before actual code in method) should not 
change.

Post-condition (at the end of method some reduce must happen).

**I - ISP:**  
Use small, specific interfaces instead of one broad  
interface.  
*Example:* Split a broad interface into smaller ones  
to avoid unused methods in unrelated classes.

**D - DIP:**  
Depend on abstractions, not concretions.  
*Example:* `Book` depends on `Printer` interface,  
not directly on `ConsolePrinter`.


---

## `src/main/java/com/conduct/interview/_6_message_brokers/comparison.md`



---

## `src/main/java/com/conduct/interview/_6_message_brokers/kafka/manual/kafka_manual.md`

cd /opt/bitnami/kafka/bin

/opt/bitnami/kafka/bin/kafka-topics.sh --bootstrap-server kafka:29092 --list


/opt/bitnami/kafka/bin/kafka-topics.sh \
--bootstrap-server kafka:29092 \
--create \
--topic person-topic \
--partitions 3 \
--replication-factor 1

✅ Partitions → divide work across consumers

✅ Replication → keeps copies for durability

✅ Group ID → decides if consumers share work or read independently

---

## `src/main/java/com/conduct/interview/_6_message_brokers/kafka.md`

# Apache Kafka: Architectural Overview

Apache Kafka is a **distributed event streaming platform** designed for high-volume,
real-time data feeds. Unlike traditional message brokers (like RabbitMQ) that push
messages to consumers and delete them upon acknowledgment, Kafka is architected as a
**distributed, replicated commit log** where consumers pull data at their own pace.

---

## 1. Core Architecture & Components

### Producer
An application that publishes (sends) events to Kafka topics. Producers decide
which partition to send data to, either using a round-robin approach, custom
business logic, or a hashing mechanism based on the event key.

### Consumer
An application that subscribes to topics and pulls (reads) events. Consumers track
their progress independently, meaning multiple different applications can read the
exact same data without interfering with one another.

### Event (Record / Message)
The fundamental unit of data in Kafka. It is a structured object consisting of:
*   **Key:** Used for routing data to specific partitions and ensuring ordering.
*   **Value:** The actual payload (e.g., JSON, Avro, Protobuf serialized as a byte
    array `byte[]`).
*   **Timestamp:** Appended automatically by the producer or the broker.
*   **Headers:** Optional key-value metadata for routing, tracing, or security tokens.

---

## 2. Storage & Scaling Concepts

### Topic
A category or logical feed name where records are stored.
*   **SQL Analogy:** Similar to a database table, but *without constraints*.
*   **Schema-agnostic:** The Kafka broker treats data as raw bytes; serialization
    and validation happen entirely on the application side.

### Partition
Topics are divided into multiple partitions to allow horizontal scaling.
*   **Structure:** A partition is an ordered, immutable, append-only sequence of
    records.
*   **Ordering Guarantee:** Kafka guarantees total strict order of messages *only
    within a single partition*, not across the whole topic.

### Offset
A unique, sequential 64-bit integer assigned to every message within a specific
partition.
*   **Position Tracking:** Offsets act like a bookmark, allowing consumers to
    precisely track their progress in the stream.

### Broker & Cluster
*   **Broker:** A single Kafka server.
*   **Cluster:** A group of brokers working together to share the processing load,
    manage scalability, and provide infrastructure backup.
*   **Controller:** One broker in the cluster elected by ZooKeeper to act as the
    manager — it assigns partition leaders and handles broker failures. With a single
    broker there is no election; it becomes the controller automatically. If the
    controller goes down, ZooKeeper elects a new one from the remaining brokers, and
    that broker already has all the data because it was replicating it continuously.

---

## 3. High Availability & Orchestration

### Replication & Redundancy
*   **Replication (The Mechanism):** The active process of synchronizing and
    copying log segments across multiple physical brokers.
*   **Redundancy (The Architecture Goal):** The state of having identical data
    copies stored on separate servers, ensuring no single point of failure.
*   **Leader/Follower:** Every partition has one **Leader** broker handling all
    reads and writes, and multiple **Follower** brokers replicating data in the
    background.
*   **Replication Factor:** The number of copies of a topic's data across brokers,
    not the number of brokers itself. `replication-factor=2` on a 3-broker cluster
    means only 2 of the 3 brokers hold that topic's data — the third may hold other
    topics. The replication factor cannot exceed the total number of brokers.

### Retention Policy
Defines how long Kafka preserves data before discarding it. Unlike AMQP brokers,
**data is not deleted when a consumer reads it**. Retention can be configured based on:
*   **Time:** Keep data for a duration (e.g., 7 days).
*   **Size:** Keep data until the partition log reaches a size ceiling (e.g., 50 GB).
*   **Compaction:** Retain only the latest value for each unique message key.

### Cluster Coordination: ZooKeeper vs. KRaft
Control plane services that manage cluster metadata, coordinate broker
registration, track active topics, and handle leader election.
*   **ZooKeeper:** Legacy external consensus ensemble.
*   **KRaft (Kafka Raft):** Modern internal consensus mechanism that embeds
    metadata management directly inside dedicated Kafka controllers, removing the
    external ZooKeeper dependency.

---

## 4. Consumer Groups & Parallelism

### Consumer Group
A pool of consumers cooperating to read data from a topic. Kafka coordinates the
group to ensure that **each partition is assigned to exactly one consumer member
within the group**, preventing duplicate processing.

### Parallelism Rule
The maximum number of parallel consumers in a single group is capped by the number
of partitions in the topic.
*   If a topic has **4 partitions**, a consumer group can scale up to **4 active
    consumers**.
*   Any additional consumers added to that group beyond the partition count will
    remain **idle**, serving as hot standbys.

### Rebalancing
The automatic cluster process of reassigning partition ownership across available
group members. It is triggered when:
*   A consumer joins or leaves the group.
*   A consumer fails its heartbeat check.
*   New partitions are added to the topic.

---

## 5. Performance Pillars (Why Kafka Scales)

### Sequential I/O
Kafka minimizes slow random disk access (Random I/O) by treating partitions as
**append-only commit logs**. Writing sequentially to modern storage layers provides
performance speeds comparable to writing directly to memory (RAM).

### Zero-copy Optimization
When sending records to network consumers, Kafka bypasses the Java application
layer entirely via `FileChannel.transferTo()`.
*   **Traditional:** `Disk` ➔ `Kernel Space` ➔ `User Space (JVM Heap)` ➔ `Socket 
    Buffer` ➔ `Network Card`.
*   **Zero-Copy:** Data moves directly from the `OS Page Cache` straight into the
    `Network Card Buffer`, freeing up CPU cycles and completely removing JVM
    Garbage Collection overhead for moving data.

### Architectural Core Strengths
*   **Persistence:** Every byte is stored directly to disk and replicated, ensuring
    high fault tolerance.
*   **Replayability:** Consumers can reset or "rewind" their offsets to any
    historical point within the retention window to re-process historical data.
*   **Decoupling:** Complete temporal and spatial isolation; producers and
    consumers operate independently with no awareness of each other's runtime states.

## Consumer Operational Patterns

### Scenario Summaries & Architectural Behaviors

| Scenario Setup | Execution State | Cluster Result | Interview Focus |
| :--- | :--- | :--- | :--- |
| **Different Group IDs** | Distinct string configurations | **Broadcast Pattern:** Every group processes a full copy of the stream. | Decoupled microservice architectures. |
| **Identical Group IDs** | Shared group strings | **Load-Balancing:** Partitions are divided evenly among instances. | Thread-safe concurrent worker scaling. |
| **Consumers > Partitions** | Group size outnumbers partitions | **Idle Standbys:** Extra instances get zero partition allocations. | Scaling limits match partition ceiling boundaries. |
| **New Group Configuration** | Fresh group string identification | **Offset Evaluation:** Evaluates `auto.offset.reset` parameters. | `earliest` reads history; `latest` awaits future log appends. |

---

## `src/main/java/com/conduct/interview/_6_message_brokers/rabbit.md`

# AMQP and RabbitMQ Overview

**AMQP** - Advanced Message Queuing Protocol. It standardizes messaging behavior
between producers, consumers, brokers, ensuring reliable communication across
distributed systems.

**RabbitMQ** - Open-source message broker which implements the AMQP protocol and
facilitates communication between systems acting as a middleman. Sender and
receiver applications don't know about each other, which leads to having loosely
coupled systems that are easy to scale and maintain.

RabbitMQ supports **AMQP**, **STOMP**, **HTTP** protocols, etc.

## Key Concepts

- **Producer** - An application that sends messages to the message broker
  (RabbitMQ).
- **Consumer** - An application that receives messages from the message broker.
- **Message** - The payload which is sent from the Producer to the Consumer.
- **Queue** - A buffer that stores the messages sent by the Producer until they
  are processed (consumed) by the Consumer.
- **Exchange** - Receives messages from the producer and routes them to queues.

## Types of Exchange

- **Direct Exchange** - Routes messages based on matching of message's routing
  key and queue's binding key.
- **Fanout Exchange** - Broadcasts messages to all bound queues, ignoring
  routing keys.
- **Topic Exchange** - Routes messages based on matching pattern between
  messages' routing keys and queues' binding keys.
- **Headers Exchange** - Routes messages based on headers rather than the
  routing key.

## Other Concepts

- **Binding** - Connection between an exchange and a queue.
- **Routing Key** - String used by exchanges to route messages to queues.

## Core Features of RabbitMQ

- **Message Durability** - Ensures messages are not lost.
- **Acknowledgment** - Confirms successful message processing.
- **Dead-letter Exchange** - Handles unroutable or expired messages.
- **Prefetch Count** - Controls the number of messages a consumer can process.
- **Priority Queues** - Allows prioritization of messages.


[//]: # (example is in practise/rabbit folder and `rabbit` profile)

---

## `src/main/java/com/conduct/interview/_6_message_brokers/rabbit/rabbit.md`

RabbitMQ Interview Guide
1. Core Architecture
   RabbitMQ is a Smart Broker. It manages message lifecycle and routing logic internally.

Producer: Sends messages to an Exchange.

Exchange: The routing engine (decides where data goes).

Binding: The link/rule between Exchange and Queue.

Queue: The buffer where messages sit.

Consumer: Grabs messages from the Queue.

2. Exchange Types (Routing)
   Direct: Exact match of Routing Key.

Use: Targeted tasks (e.g., logs.error).

Topic: Pattern match using wildcards.

*: Exactly one word.

#: Zero or more words.

Use: Geographic or categorized routing.

Fanout: Ignores keys; broadcasts to ALL bound queues.

Use: Pub/Sub, configuration updates.

Headers: Uses message header attributes instead of keys.

Use: Complex metadata routing.

3. Delivery Guarantees
   basicAck: Consumer says "I'm done, delete it."

basicNack: Consumer says "I failed."

requeue=true: Back to the queue.

requeue=false: Discard or send to DLX.

Prefetch (QoS): Controls how many unacked messages a worker can hold. basicQos(1) ensures "Fair Dispatch."

4. Reliability Features
   Durability: Queues/Exchanges survive a broker restart.

Persistence: Messages are written to disk.

DLX (Dead Letter Exchange):

Automatic "Trash Bin."

Handles: Rejected messages, Expired (TTL), or Queue length limit hit.

TTL: Time-To-Live. Messages die if not consumed in X ms.

---

## `src/main/java/com/conduct/interview/_7_patterns/behavioral/chain/chain.md`

The **Chain of Responsibility** is a behavioral design pattern that allows a
request to pass through a chain of handlers. Each handler can either process
the request or pass it to the next handler in the chain. This pattern provides
a way to decouple the sender of a request from its receiver and enables
multiple objects to potentially handle the request, allowing flexible and
dynamic request processing.


---

## `src/main/java/com/conduct/interview/_7_patterns/behavioral/command/command.md`

The Command pattern allows wrapping methods and parameters into objects to execute
them later. It also supports implementing undo operations and is an example of the
producer-consumer pattern.

Components:

Client: Configures commands.
Invoker: Executes commands.
Command: Defines an operation.
Receiver: Performs the work.
This decouples the object invoking operations from the one performing them.

---

## `src/main/java/com/conduct/interview/_7_patterns/behavioral/iterator/iterator.md`

The Iterator pattern is a behavioral design pattern that provides 
a standard way to access elements of a collection object sequentially 
without exposing the underlying details of its implementation. 
This pattern is particularly useful when you want to separate 
the collection's structure from the traversal logic, 
making the code more flexible and maintainable.

---

## `src/main/java/com/conduct/interview/_7_patterns/behavioral/mediator/mediator.md`

### Mediator Pattern

The Mediator Pattern defines an object that controls how a set of objects
interact. Instead of objects communicating directly with each other, they
use a mediator, reducing the dependencies between objects and promoting
loose coupling. This makes the system easier to maintain and modify.

#### Key Points:
- Centralizes communication between objects.
- Reduces direct dependencies between components.
- Encourages loose coupling in complex systems.


---

## `src/main/java/com/conduct/interview/_7_patterns/behavioral/memento/memento.md`

# Memento Design Pattern

The **Memento Pattern** is a behavioral design pattern that allows an
object's state to be saved and restored without exposing its internal
structure. It provides a way to capture and externalize the internal
state of an object so that it can be restored later. This pattern is
particularly useful when an object needs to revert to a previous state,
such as in undo or rollback operations.

## Key Participants

1. **Originator**:
    - The object whose state needs to be saved or restored.
    - It creates a `Memento` containing a snapshot of its current state.
    - It can also restore its state using a `Memento`.

2. **Memento**:
    - A simple object that stores the state of the `Originator`.
    - The state is private and cannot be accessed by external objects,
      ensuring encapsulation.
    - Typically contains the same fields as the `Originator`.

3. **Caretaker**:
    - Manages the saved `Memento` objects.
    - It keeps track of `Memento` instances but does not inspect or
      modify the stored states.
    - Responsible for deciding when to save and restore the
      `Originator`'s state.

## Structure

- The **Originator** generates a **Memento** that holds its internal
  state.
- The **Caretaker** stores the **Memento** without altering or
  accessing its contents.
- When needed, the **Caretaker** provides the **Memento** back to the
  **Originator**, which restores its state from the **Memento**.

## When to Use

- You want to provide the ability to restore an object to a previous
  state (e.g., undo/redo functionality).
- You want to maintain encapsulation and prevent other objects from
  accessing or modifying the internal state of the object directly.
- Saving and restoring the state of complex objects where you need
  control over when the object state is saved or restored.

## Example

A typical example of the **Memento Pattern** is a text editor with undo
functionality. The editor saves the state of the document (text, cursor
position, etc.) as a `Memento` object. When the user presses undo, the
`Caretaker` provides the saved `Memento`, and the editor restores the
previous state from it.

## Advantages

- **Encapsulation**: The internal state of the `Originator` is protected
  and cannot be accessed directly by other objects.
- **Undo/Redo functionality**: Easily implement undo/redo features by
  storing and restoring `Memento` objects.
- **Simplifies state management**: It allows an object to maintain its
  previous states without being responsible for managing them directly.

## Disadvantages

- **Memory overhead**: If the state of the `Originator` is large or
  complex, saving multiple `Memento` objects can consume a lot of
  memory.
- **Serialization complexity**: Managing state and serialization of
  large objects can introduce complexity in the implementation.


---

## `src/main/java/com/conduct/interview/_7_patterns/behavioral/observer/observer.md`

Observer pattern handles the situations when changes
in some object other objects should be aware of.

Interface Observable(methods subscribe, unsubscribe, notify),
Observer(method update)

Implementor of the Observable contains the list of
observer objects to be subscribed and notified when
event occurs.

---

## `src/main/java/com/conduct/interview/_7_patterns/behavioral/state/state.md`

# State Pattern

The **State Pattern** enables an object to change its behavior dynamically based  
on its internal state. Each state is encapsulated in its own class, and the  
context object delegates behavior to the current state. This pattern eliminates  
complex conditional logic and allows for cleaner transitions between states by  
letting each state manage its own transitions.

### Key Components:
- **State Interface**: Defines behavior common to all states.
- **Concrete States**: Implement specific behaviors for each state.
- **Context**: Holds the current state and switches between states dynamically.


---

## `src/main/java/com/conduct/interview/_7_patterns/behavioral/strategy/strategy.md`

The Strategy pattern allows defining a comunity of algorithms, encapsulating
each one, and making them interchangeable. The algorithm can vary independently
of clients using it.

Components:
Strategy: Common interface for all strategies.
Concrete Strategies: Implement variations of the algorithm.
Context: Uses a strategy and can switch between them.


---

## `src/main/java/com/conduct/interview/_7_patterns/behavioral/templatemethod/template_method.md`

The ***Template Method*** Pattern defines the skeleton of an algorithm 
in a base class, allowing subclasses to override specific steps 
without altering the algorithm’s overall structure. This promotes 
code reuse and consistency while enabling flexibility in implementation 
details where needed.

---

## `src/main/java/com/conduct/interview/_7_patterns/behavioral/visitor/visitor.md`

The **Visitor pattern** in Java allows adding operations to objects without
modifying their classes. It is particularly useful when you want to perform
different actions on a group of objects without altering their structure.
The pattern involves two key components:

1. **Visitor Interface**: Declares visit methods for each type of element.
2. **Concrete Visitor**: Implements the visit methods, defining specific
   operations for each element type.
3. **Element Interface**: Declares an `accept` method, which takes a Visitor.
4. **Concrete Element**: Implements the `accept` method and calls the appropriate
   visit method on the Visitor.

The Visitor pattern is ideal when the operations on an object hierarchy change
frequently but the structure remains the same. It promotes open/closed
principle by keeping the object classes closed for modification but open for
new behaviors via visitors.


---

## `src/main/java/com/conduct/interview/_7_patterns/creational/builder/builder.md`

Builder pattern separates out the instantiation process with 
help of additional object and instantiating step-by-step.

---

## `src/main/java/com/conduct/interview/_7_patterns/creational/factory/factory.md`

There could be creational method - just some method to create object in 
specific way.
Also could be static creational method - replacement for constructor like
in Singleton pattern.

Simple factory is just some static method which returns the
subclass object based on input parameter. 

Factory Method is a method of some Creator class children of which
(concrete creators) create some new object of some class hierarchi.
Like Shape>Square,Circle. It allows to create object and not even
knowing in the client which object type was created.

Abstract factory is used to create families of objects. Like 
create set of furniture (modern, classic etc.)

---

## `src/main/java/com/conduct/interview/_7_patterns/creational/prototype/prototype.md`

Prototype hides the complexity of the creating of the new objects
from the client. Cloning or some other method is used. Object is created
and just some properties could be changed if it's needed.

---

## `src/main/java/com/conduct/interview/_7_patterns/creational/singleton/singleton.md`

Pattern guarantees having just single instance of some class.

---

## `src/main/java/com/conduct/interview/_7_patterns/structural/adapter/adapter.md`

Adapter pattern solves incompatibility issues between
the client and external service with help of composition.


---

## `src/main/java/com/conduct/interview/_7_patterns/structural/bridge/bridge.md`

The purpose of the bridge pattern is to decouple abstraction from the implementation
so that the two can vary independently.

---

## `src/main/java/com/conduct/interview/_7_patterns/structural/composite/composite.md`

Composite pattern allows to group objects to the tree-like structure and 
handle them as a single object. Like directory/files.

---

## `src/main/java/com/conduct/interview/_7_patterns/structural/decorator/decorator.md`

Decorator pattern allows to add additional functionality
to the object

---

## `src/main/java/com/conduct/interview/_7_patterns/structural/facade/facade.md`

Facade provides simple interface to the complex set of objects hiding the
complexity.

---

## `src/main/java/com/conduct/interview/_7_patterns/structural/flyweight/flyweight.md`

Flyweight allows to save memory with creating and the using of the shared 
state between objects. Pre-constructed objects could be saved in the list, map.

---

## `src/main/java/com/conduct/interview/_7_patterns/structural/proxy/proxy.md`

Proxy pattern allows to add additional logic before or after call on original
object to check something or to use LRUCache based on some condition.

---

## `src/main/java/com/conduct/interview/_8_rest/http/http.md`

# Hypertext Transfer Protocol (HTTP)

**HTTP** (Hypertext Transfer Protocol) is a protocol, or a set of standards
and rules, that defines how data is transmitted over the web. It specifies
how **requests** sent by clients (e.g., browsers) are structured, and how
**responses** from servers are returned.

HTTP is **stateless**, meaning each request should contain all the
information required for the server to process it. The server does not
retain any state information about previous requests unless mechanisms
like cookies or sessions are used.

## HTTP Methods

Common HTTP methods include:

- **GET**  
  Used to retrieve data from the server. It is idempotent, meaning repeated
  GET requests have the same result. Often used for reading resources (e.g.,
  getting a webpage or data).

- **POST**  
  Used to send data to the server. Commonly used to submit form data or
  create new resources. Unlike GET, POST is not idempotent, meaning the
  result can change if the same request is repeated.

- **PUT**  
  Used to update or replace an existing resource on the server. PUT is also
  idempotent, meaning repeated requests with the same data will not change
  the result further.

- **DELETE**  
  Used to remove a resource from the server. Like PUT, DELETE is idempotent,
  meaning repeated DELETE requests for the same resource will result in the
  same outcome (the resource remains deleted).

## HTTP Status Codes

Servers respond with HTTP status codes to indicate the result of the
request:

- **200 OK**: The request was successful.
- **404 Not Found**: The requested resource could not be found.
- **500 Internal Server Error**: The server encountered an error.
- **302 Found**: The requested resource has been temporarily moved.

Request:
POST /submit-form HTTP/1.1
Host: www.example.com
User-Agent: Mozilla/5.0
Content-Type: application/x-www-form-urlencoded
Content-Length: 29

name=JohnDoe&age=30

Response:
HTTP/1.1 200 OK
Date: Mon, 16 Sep 2024 10:00:00 GMT
Content-Type: text/html
Content-Length: 1024

<html>
  <head><title>Example</title></head>
  <body><h1>Hello World!</h1></body>
</html>


---

## `src/main/java/com/conduct/interview/_8_rest/rest.md`

# REST (Representational State Transfer)

**REST** is an architectural style for designing network applications. It
relies on standard HTTP methods like **GET**, **POST**, **PUT**, and
**DELETE** to manage resources, which are identified by **URIs**.

### Key Features:
- **Stateless**: Each request contains all necessary information, with no
  server-side session.
- **Client-Server**: Separation of client (UI) and server (data handling).
- **Cacheable**: Responses can be marked as cacheable to improve performance.
- **Uniform Interface**: Consistent, predictable structure for requests and
  responses.


---

## `src/main/java/com/conduct/interview/auth/oauth2.md`

OAuth2 — це стандарт авторизації, який дозволяє одному додатку (наприклад, твоєму сайту) отримати обмежений доступ 
до ресурсів користувача на іншому сервісі (наприклад, Google, GitHub, Keycloak) без передачі пароля.

# OAuth2 / OIDC — Backend-controlled flow

## Учасники
- Frontend (Browser)
- Backend
- Authorization Server (Keycloak)
- Resource Server (API)
  Resource Server — це роль сервісу, який захищає ресурси і перевіряє токени.
  У більшості випадків наш backend є resource server для frontend.
  Але той самий backend може виступати як client, коли викликає інші сервіси.

---

## Конфігурація Backend
```
client_id
client_secret
redirect_uri = https://app.com/callback
```

---

## Flow

### 1. User → Frontend
```
GET /app
```

### 2. Frontend → Keycloak (redirect)
```
302 https://keycloak/auth
  ?client_id=app-client
  &redirect_uri=https://app.com/callback
  &response_type=code
  &scope=openid
```

### 3. User login на Keycloak

### 4. Keycloak → Frontend (redirect)
```
302 https://app.com/callback?code=abc123
```

### 5. Frontend → Backend
```
POST /auth/exchange
{
  "code": "abc123"
}
```

### 6. Backend → Keycloak (/token)
```
POST /token

grant_type=authorization_code
code=abc123
client_id=app-client
client_secret=SECRET
redirect_uri=https://app.com/callback
```

### 7. Keycloak → Backend
```
access_token
refresh_token
id_token
```

### 8. Backend storage
- session / Redis / DB

### 9. Backend → Frontend
```
Set-Cookie: SESSION=xyz; HttpOnly; Secure
```

---

## Виклики API

### Frontend → Backend
```
GET /api/data
Cookie: SESSION=xyz
```

---

## Перевірка JWT

### Отримання ключів
```
GET /realms/{realm}/protocol/openid-connect/certs
```

### Перевірка
- взяти `kid` з JWT header
- знайти ключ у JWKS
- перевірити підпис
- перевірити `exp`
- перевірити `iss`
- перевірити `aud`

---

## Refresh token

### Backend → Keycloak
```
POST /token

grant_type=refresh_token
refresh_token=XYZ
client_id=app-client
client_secret=SECRET
```

### Keycloak → Backend
```
new access_token
new refresh_token
```

---

## client_secret

Використовується:
- authorization_code → token
- refresh_token → token

Не використовується:
- для перевірки JWT
- для доступу до API
```

---

## `src/main/java/com/conduct/interview/big_tasks.md`

✅ 1. Spring Boot 2 → 3 + JDK 17 → 21 Upgrade
🛠️ What I did:
I upgraded the platform from Spring Boot 2 to 3, and JDK from 17 to 21, to improve performance, get long-term support, 
and adopt new features.

⚠️ Main issues I had to address:
✅ Spring Boot 3 requires Jakarta packages (javax.* → jakarta.*)

Refactoring annotations and imports like @Transactional, @PathParam, etc.

✅ Removed deprecated features (e.g., WebSecurityConfigurerAdapter)

✅ Library compatibility

Many third-party libraries had to be updated or replaced

✅ Test failures due to Spring behavior changes

Fixed tests that depended on internal boot lifecycle or mocks

✅ 2. Rewriting Monolith into Microservices (Kotlin, Spring Boot, Monorepo)
🛠️ What I did:
I helped break down a large monolithic Java application into Kotlin Spring Boot microservices
within a monorepo structure. APIs were defined using OpenAPI and shared across services.

⚙️ How I approached it:
✅ Identified bounded contexts and grouped related features into service domains

✅ Defined OpenAPI specs per service and generated API interfaces using OpenAPI Generator

Ensured strict contract between services

✅ Separated APIs into reusable Gradle modules

Shared DTOs, interfaces, and validation rules

✅ Used Feign/RestTemplate/WebClient for inter-service calls

✅ Handled distributed data

Via shared DB schemas (in early phase)


✅ 3. GraalVM Native Image
🛠️ What I did:
I built and optimized a native image using GraalVM, reducing startup time and memory usage.

📘 In simple words:
Normal Java apps run on the JVM and load classes at runtime

GraalVM native images compile everything ahead-of-time into a small standalone binary

Super-fast startup

Low memory usage

Great for microservices or CLI tools

⚠️ Challenges I handled:
✅ Reflections don’t work automatically

Had to configure reflection metadata (reflect-config.json or annotations)

✅ Third-party libraries (e.g., Jackson, Hibernate, SLF4J) often need native hints

✅ Spring Boot support added via spring-native or now built-in in Spring Boot 3.x

✅ Used native-maven-plugin to automate build

✅ 4. Implementation of Automated-QM Service
🛠️ What I did:
I designed and implemented the Automated Quality Management (Auto-QM) service
that evaluates calls automatically based on flexible rule conditions.

⚙️ Key functionality:
✅ Fetch calls periodically (e.g., every hour) from conversation-service

✅ Evaluate calls using rule sets:

Rule types: duration checks, label presence, speech tag scores, AI-based questions

✅ Apply subrules and scoring logic

Rule composition: ranges, multi-equals, yes/no scores

✅ Store results into DB (automated_reviews, attribute_sub_score, etc.)

✅ Track rule executions, retries, and errors (via executions, retry tables)

✅ Connected to AI system for subjective evaluation (e.g., "Did agent greet?")

✅ Used Spring + Kotlin with RabbitMQ and scheduler integration

---

## `src/main/java/com/conduct/interview/coding/arrays/find_max/find_max.md`

Ways to find max element in an array:

Iterative Way
Java 8 Stream
Sorting
Using Collections.max()

---

## `src/main/java/com/conduct/interview/coding/tasks.md`

Common Coding Interview Questions (Implementation-Heavy)
1️⃣ Cache / Storage Design

Implement LRU Cache (Least Recently Used).

Implement LFU Cache (Least Frequently Used).

Implement Cache with TTL / Expiry (entries expire after given time).

Implement Write-Through Cache (sync with backing store).

Implement Two-Level Cache (memory + disk).

2️⃣ Rate Limiting / Retry / Throttling

Sliding Window Rate Limiter – allow only N requests per time window. DONE

Token Bucket Rate Limiter – refill tokens over time.

Fixed Window Counter – count requests per interval, reset each window.

Exponential Backoff Retry – retry failed calls with increasing delay.

Circuit Breaker – open circuit after repeated failures, half-open after cooldown.

Request Debouncing / Throttling – merge or limit rapid calls.

3️⃣ Data Structure Design

Implement Priority Queue / Min-Max Heap from scratch. IN_PROGRESS

Implement Disjoint Set / Union-Find with path compression.

Implement Thread-Safe Bounded Queue (producer-consumer).

Implement Concurrent HashMap with segment locking or CAS.

Implement Circular Buffer / Ring Buffer with overwrites.

Implement Bloom Filter for probabilistic membership checks.


4️⃣ Scheduling / Timing

Task Scheduler – schedule tasks with cooldown periods.

Delayed Job Queue – tasks become available after a delay.

Retry Queue with Backoff – store failed jobs, retry later.

5️⃣ Streaming / Sliding Windows

Moving Average of Last N Items in a data stream.

Median of Streaming Data – maintain median dynamically.

Top-K Frequent Elements in Stream – track top k efficiently.

6️⃣ Classic System/Algo Patterns

Implement Consistent Hash Ring – for server load balancing.

Design URL Shortener – short ID generation, collision handling.

Implement Session Store – sessions with TTL, cleanup.

Implement In-Memory Key-Value Store with persistence/logging.

7️⃣ Locking / Concurrency

Implement Read-Write Lock.

Implement Semaphore.

Implement Thread Pool / Executor.

8️⃣ Other Interview-Favorites

Producer-Consumer Problem – thread-safe solution.

Dining Philosophers Problem – concurrency challenge.

Deadlock Detection – implement a simple graph-based checker.

9️⃣ Variants of Rate-Limiting (Your Specific Case)

Sliding window with timestamps – 500 calls/minute.

Leaky bucket – smooth out bursts.

Token bucket – refill at rate R, max capacity C.

---

## `src/main/java/com/conduct/interview/coding/trees/rbt/rbt.md`

1️⃣ Every node is red or black.
2️⃣ Root is always black.
3️⃣ No two reds in a row.
4️⃣ All paths from root → leaf have the same number of black nodes.

---

## `src/main/java/com/conduct/interview/docker.md`

### What is Docker?

**Docker** is a tool that makes it easy to create, deploy, and run applications by packaging them into 
lightweight, portable containers. Think of a container as a small, self-contained box that holds 
everything an application needs to run—code, libraries, dependencies, and settings—ensuring it works 
the same way on any system (e.g., your laptop, a server, or the cloud).

#### Simple Analogy
Imagine Docker as a shipping container for software. Just as a shipping container can hold 
different goods and be moved between ships, trucks, or trains without unpacking, a Docker container 
packages an app so it runs consistently across different environments.

#### Main Features of Docker
1. **Containers**:
    - Lightweight, isolated environments that run applications and their dependencies.
    - Unlike virtual machines, containers share the host operating system, making them faster 
   and more efficient.

2. **Portability**:
    - Containers work the same on any system (e.g., Windows, Linux, cloud), avoiding 
   "it works on my machine" issues.
    - **Your Experience**: You used Docker in your microservices and Kubernetes projects, 
   ensuring consistent deployments.

3. **Docker Images**:
    - A read-only template used to create containers, like a blueprint.
    - Built using a `Dockerfile`, which defines the app’s code, dependencies, and setup.
    - Example: You likely created images for your Spring Boot microservices.

4. **Docker Hub**:
    - A repository for sharing and storing Docker images (e.g., official images for Java, PostgreSQL).
    - You can pull pre-built images or push your own.

5. **Easy Scaling**:
    - Run multiple containers from the same image to handle increased load.
    - **Your Experience**: You used Docker with Kubernetes, which simplifies scaling microservices.

6. **Simplified Dependency Management**:
    - Containers include all dependencies (e.g., Java, libraries), avoiding conflicts between apps.
    - Example: Your Auto-QM service likely used Docker to bundle Spring Boot and RabbitMQ dependencies.

7. **Integration with CI/CD**:
    - Docker integrates with tools like Jenkins or Bamboo (as you used) to automate building, testing, 
   and deploying containers.
    - **Your Experience**: Your CI/CD pipelines likely built Docker images for deployment.

#### Why Use Docker?
- **Consistency**: Ensures apps run the same everywhere, reducing environment-related bugs.
- **Efficiency**: Containers are lightweight, using fewer resources than virtual machines.
- **Speed**: Fast to build, deploy, and start (e.g., your GraalVM optimization complements 
Docker’s efficiency).
- **Microservices**: Ideal for your microservices projects, as each service can run in its own container.

#### Your Context
In your projects, you used Docker with Kubernetes and Helm Charts to deploy Spring Boot microservices, 
ensuring consistent environments for your Auto-QM service and monolith-to-microservices transition. 
You likely created Docker images to package Java apps with dependencies like PostgreSQL and RabbitMQ, 
streamlining deployment and scaling.

If you need a deeper dive into Docker for your interview (e.g., explaining a `Dockerfile` 
or Docker-Kubernetes integration), let me know!

---

## `src/main/java/com/conduct/interview/hibernate.md`

Preparing for an interview on **Hibernate** and the **N+1 problem** is crucial for a Java developer, especially given 
your experience with Hibernate, JPA, and Spring Boot as listed in your LinkedIn profile. Below, I’ll provide a focused 
guide on Hibernate, including its core concepts, common interview topics, and a detailed explanation of the N+1 problem 
with solutions, tailored to your background. I’ll also include sample interview questions with concise answers to help 
you articulate your expertise, leveraging your experience with projects like the monolith-to-microservices transition 
and the Auto-QM service.

---

### Hibernate Overview and Key Concepts

**Hibernate** is an ORM (Object-Relational Mapping) framework for Java that simplifies database interactions by mapping 
Java objects to database tables. It implements the JPA (Java Persistence API) specification and provides additional 
features for efficient data handling.

#### Key Concepts to Prepare
1. **Core Components**
    - **Entity**: A Java class mapped to a database table (e.g., annotated with `@Entity`).
    - **SessionFactory**: A thread-safe, immutable factory for creating `Session` objects; expensive to create, 
    so typically one per application.
    - **Session**: A single-threaded, short-lived object for database operations (e.g., CRUD).
    - **Transaction**: Ensures atomicity for database operations (e.g., using `@Transactional`).
    - **Your Experience**: You’ve used Hibernate and JPA in your backend development, likely for mapping entities in 
    your microservices and Auto-QM service.

2. **Annotations**
    - `@Entity`, `@Table`, `@Id`, `@GeneratedValue`: Define entities and their primary keys.
    - `@OneToMany`, `@ManyToOne`, `@ManyToMany`: Define relationships between entities.
    - `@JoinColumn`, `@FetchType`: Control how relationships are fetched (eager vs. lazy).
    - **Your Experience**: You refactored annotations during the Spring Boot 2 to 3 upgrade (e.g., `@Transactional`), 
    indicating familiarity with Hibernate/JPA annotations.

3. **Fetching Strategies**
    - **Lazy Loading**: Loads related data only when accessed (default for `@OneToMany`, `@ManyToMany`).
    - **Eager Loading**: Loads related data immediately (default for `@ManyToOne`, `@OneToOne`).
    - **Your Experience**: Likely encountered fetching issues during your monolith-to-microservices or Auto-QM projects, 
    especially with distributed data.

4. **Caching**
    - **First-Level Cache**: Enabled by default within a `Session` to reduce database queries.
    - **Second-Level Cache**: Optional, application-wide LRUCache (e.g., using Ehcache or Redis).
    - **Query Cache**: Caches query results for repeated queries.
    - **Your Experience**: Your focus on performance (e.g., GraalVM optimization) suggests you’ve considered caching 
    for efficiency.

5. **HQL and Criteria API**
    - **HQL (Hibernate Query Language)**: SQL-like queries for entities (e.g., `from User where id = :id`).
    - **Criteria API**: Programmatic query building for dynamic queries.
    - **Your Experience**: Likely used HQL or JPA queries in your Auto-QM service to fetch and evaluate call data.

6. **Transaction Management**
    - Managed via `@Transactional` in Spring or `Session.beginTransaction()` in plain Hibernate.
    - Ensures data consistency, especially in distributed systems like microservices.
    - **Your Experience**: You handled distributed data in your microservices project, likely using transactions.

---

### The N+1 Problem in Hibernate

The **N+1 problem** is a common performance issue in Hibernate when fetching entities with relationships, 
resulting in excessive database queries.

#### What is the N+1 Problem?
- **Scenario**: When you fetch a list of entities (e.g., `N` parent entities) and their associated child entities, 
Hibernate may execute one query to fetch the parents (the "1") and `N` additional queries to fetch the children for 
each parent, leading to `N+1` total queries.
- **Example**: Fetching a list of `Order` entities, each with a collection of `OrderItem` entities. 
If lazy loading is used, Hibernate executes one query for `Orders` and one query per `Order` to fetch its `OrderItems`.

#### Why It Happens
- **Lazy Loading**: By default, `@OneToMany` relationships use lazy loading, so child entities are fetched only when 
accessed, triggering additional queries.
- **Poor Query Design**: Not optimizing queries to fetch related data in a single query.
- **Your Experience**: You likely encountered this during your microservices or Auto-QM projects when querying related 
data (e.g., call evaluations stored in PostgreSQL).

#### Solutions to N+1 Problem
1. **Eager Fetching**
    - Use `FetchType.EAGER` to load related data in the initial query.
    - **Drawback**: Can load unnecessary data, increasing memory usage.
    - **Example**: `@OneToMany(fetch = FetchType.EAGER)`.

2. **Join Fetch in HQL/JPQL**
    - Use `JOIN FETCH` in queries to fetch related data in one query.
    - **Example**: `select o from Order o join fetch o.orderItems`.
    - **Your Experience**: You could mention using JPQL in your Auto-QM service to optimize data retrieval.

3. **Entity Graph**
    - Define a fetch graph using `@NamedEntityGraph` or `EntityGraph` to specify which relationships to fetch.
    - **Example**:
      ```java
      @Entity
      @NamedEntityGraph(name = "orderWithItems", attributeNodes = @NamedAttributeNode("orderItems"))
      public class Order { ... }
      ```
      Use in repository: `entityManager.createQuery("select o from Order o", 
   Order.class).setHint("javax.persistence.fetchgraph", entityGraph).getResultList();`.

4. **Batch Fetching**
    - Configure Hibernate to fetch related entities in batches using `@BatchSize`.
    - **Example**: `@OneToMany @BatchSize(size = 10)` fetches 10 related collections at once.
    - **Drawback**: Still issues multiple queries but reduces the number.

5. **DTO Projections**
    - Use DTOs with JPQL or Spring Data JPA projections to fetch only required fields in a single query.
    - **Example**: `select new com.example.OrderDTO(o.id, o.orderItems) from Order o join o.orderItems`.

6. **Second-Level Cache**
    - Cache frequently accessed data to avoid repeated queries.
    - **Your Experience**: Your focus on performance suggests you might have explored caching.

#### Best Practices
- Analyze query performance using tools like `log4j` (enable `hibernate.show_sql`) or a database profiler.
- Use `JOIN FETCH` or Entity Graphs for predictable data access patterns.
- Avoid eager fetching for large datasets to prevent memory issues.
- Test queries under load to ensure scalability, especially in microservices.

---

### Sample Interview Questions and Answers

Below are common Hibernate and N+1-related interview questions with answers tailored to your experience.

1. **What is Hibernate, and how does it simplify database interactions?**
    - **Answer**: Hibernate is an ORM framework that maps Java objects to database tables, abstracting SQL queries 
     and reducing boilerplate code. It simplifies CRUD operations, manages transactions, and supports features like 
     caching and lazy loading. In my Auto-QM service, I used Hibernate with Spring Boot to map call evaluation entities 
     to PostgreSQL, streamlining data access and ensuring transactional consistency.

2. **What is the N+1 problem in Hibernate, and how do you identify it?**
    - **Answer**: The N+1 problem occurs when fetching `N` parent entities triggers `N` additional queries to load 
    - their related entities, often due to lazy loading. For example, querying a list of orders and accessing their 
    - items can lead to `N+1` queries. I identify it by enabling `hibernate.show_sql` or using a database profiler to 
    - spot multiple queries. In my microservices project, I noticed this when fetching related data and optimized it 
    - using `JOIN FETCH`.

3. **How would you solve the N+1 problem in a real-world scenario?**
    - **Answer**: I’d use `JOIN FETCH` in a JPQL query to fetch related data in one go, like 
    - `select o from Order o join fetch o.orderItems`. Alternatively, I’d define an `@NamedEntityGraph` 
    - to specify fetch paths or use DTO projections for specific fields. In my Auto-QM service, I optimized queries 
    - for call evaluations by using JPQL to fetch related rule sets, avoiding N+1 issues and improving performance.

4. **What are the differences between lazy and eager loading in Hibernate?**
    - **Answer**: Lazy loading fetches related data only when accessed, reducing initial query overhead but risking 
    - N+1 issues. Eager loading fetches related data immediately, avoiding additional queries but potentially 
    - loading unnecessary data. In my projects, I default to lazy loading for `@OneToMany` relationships and use 
    - `JOIN FETCH` or Entity Graphs when I need related data upfront to avoid N+1 problems.

5. **How do you handle transactions in Hibernate with Spring?**
    - **Answer**: In Spring, I use `@Transactional` to manage transactions, ensuring atomicity for database operations. 
    - Hibernate handles the underlying `Session` and transaction boundaries. In my Auto-QM service, 
    - I used `@Transactional` to ensure consistent storage of evaluation results in PostgreSQL, 
    - especially when integrating with RabbitMQ for asynchronous processing.

6. **How do you optimize Hibernate performance?**
    - **Answer**: I optimize Hibernate by using `JOIN FETCH` or Entity Graphs to avoid N+1 issues, 
    - enabling second-level caching for frequently accessed data, and using DTO projections for specific queries. 
    - In my GraalVM project, I optimized microservices for faster startup, and in my microservices work, 
    - I used batch fetching and query tuning to improve database performance.

7. **Can you describe a time you faced a Hibernate-related challenge?**
    - **Answer**: During my Spring Boot 2 to 3 upgrade, I encountered test failures due to changes in Hibernate’s 
    - behavior, particularly with lazy-loaded relationships. I identified an N+1 issue in a query fetching call data 
    - for the Auto-QM service. I resolved it by rewriting the query with `JOIN FETCH` and using an Entity Graph,
    - reducing database roundtrips and improving performance.

---

### Preparation Tips

1. **Review Your Experience**: Be ready to discuss how you used Hibernate/JPA in your microservices and Auto-QM 
projects. Mention specific entities (e.g., call evaluations) and how you mapped them to PostgreSQL.
2. **Practice N+1 Scenarios**: Write sample code to demonstrate N+1 issues and solutions. For example:
   ```java
   // Problematic query (N+1)
   List<Order> orders = entityManager.createQuery("select o from Order o", Order.class).getResultList();
   for (Order o : orders) {
       o.getOrderItems().size(); // Triggers N queries
   }

   // Solution with JOIN FETCH
   List<Order> orders = entityManager.createQuery("select o from Order o join fetch o.orderItems", Order.class).getResultList();
   ```
3. **Code Examples**: Be prepared for coding tasks, like writing a Spring Boot repository with JPQL or Criteria API queries. 
Practice creating entities with relationships and optimizing queries.
4. **System Design**: If asked to design a system, explain how you’d use Hibernate for data access in a microservices 
context, avoiding N+1 issues with proper fetch strategies.
5. **Debugging**: Be ready to explain how you’d debug Hibernate issues (e.g., enable `hibernate.show_sql`, 
use a profiler, or check transaction boundaries).
6. **Mock Interviews**: Practice with platforms like LeetCode or Pramp, focusing on Hibernate-related questions or 
Spring Boot data access scenarios.

---

### Additional Resources
- **Books**: “Java Persistence with Hibernate” by Christian Bauer for in-depth Hibernate knowledge.
- **Online Courses**: Spring Data JPA or Hibernate courses on Udemy or Pluralsight.
- **Practice**: Create a small Spring Boot project with Hibernate, defining entities with `@OneToMany` relationships, and experiment with `JOIN FETCH`, Entity Graphs, and batch fetching to solve N+1 issues.

If you need specific code examples (e.g., a Hibernate entity setup with Spring Boot or a query to fix N+1) or want to practice system design questions involving Hibernate, let me know! Good luck with your interview!

---

## `src/main/java/com/conduct/interview/interview/manager/questions.md`


Up-to-date overview of modern software development tools and techniques

Banking domain

bcm industry

https://career.luxoft.com/jobs/senior-java-developer-15269



🔹 Структура співбесіди (типова)


✅ 1. Tell me about yourself
My name is Dmytro. I’ve been working as a Java developer for over 10 years.
Most of that time I spent at the same company, where I went through different roles —
from working on a monolithic agent evaluation system to leading development of
microservices for importing call recordings.

I collaborated closely with the Product Owner and was part of the transition
from on-prem deployments to cloud infrastructure in AWS using Docker and Kubernetes.

I’m a responsible and well-organized person. I enjoy solving backend problems,
working with clean code, and being part of a focused and friendly team.

✅ 2. Why are you looking for a new job?
The main reason is the limitations of being an external developer — no paid vacations,
no sick days, and restricted access to tools, documentation, and repos compared to developer on-site.
Had to ask some actions to do for me.

Also, I’ve been with the same company for a long time and feel it’s time
to refresh my perspective, explore new technologies, and grow further
in a different environment.

✅ 3. Why Luxoft?
Luxoft is a stable company with strong engineering practices and interesting projects.
I see it as a place where I can apply my experience and keep growing.

✅ What were the key projects you worked on?
I was involved in several backend-focused projects during my time at the company.
One of the main ones was a monolithic application for agent evaluation, where I
eventually became a team lead.

Later, I moved to a distributed system built around importing call recordings
from external recording servers. This became my main area for several years.
I worked on a group of microservices responsible for importing, transforming,
and storing call data.

I collaborated closely with the Product Owner and took part in technical planning
and decision-making.

✅ What kind of architecture did you use?
We started with a legacy monolithic system deployed on-prem, then gradually moved
to a cloud-native setup.

The new services were designed as microservices running in AWS, orchestrated
with Kubernetes and Docker. We used a REST-based architecture and integrated
multiple third-party systems.


✅ What technologies did you use?
Mainly Java and Spring Boot for backend development, along with RabbitMQ for async
communication in some modules.

We also used PostgreSQL.

For security, we integrated with Keycloak. Everything was containerized with Docker
and deployed in Kubernetes on AWS. CI/CD was handled with Bamboo pipelines.

✅ What was your role? Did you influence architecture?
In the monolithic project, I eventually became a team lead and was involved
in shaping architecture and managing the team.

In the microservices part, I started as a developer and later became a lead there too.

I took part in defining how services should be split, what APIs to expose,
and how to handle cross-cutting concerns like authentication and session management.

I was also regularly involved in code reviews, mentoring, and helping newer team members.

✅ Tell me about a complex technical decision you made.
One of the challenges I solved was related to authentication and session handling.

We had several embedded React frontends running inside our monolithic app.
At first, they managed their own authentication flows and communicated
directly with Keycloak.

That led to session conflicts and inconsistent behavior across services.

I proposed a unified approach — the monolith would take full ownership
of authentication and session renewal.

Frontend apps only sent session prolongation requests to the backend,
which in turn updated its own session and handled refresh tokens in memory.

This simplified the flow, avoided race conditions, and made the system
more reliable and secure.

✅ Were there cases where you had to rewrite part of the project? Why?
Yes, there were several such cases.

For example, when external systems changed their APIs, we had to refactor
multiple services to support the updated structure.

In some cases, it wasn’t just about adapting — we had to redesign part of
the service logic to better match the new data model.

We also rewrote certain components to improve performance and maintainability
as the system evolved.


✅ 3. Процеси та співпраця (10–15 хв)
📅 Agile / Scrum
I’ve worked with both Scrum and Kanban.

Scrum was great for larger teams and projects with well-defined features and milestones.
We used regular sprints, retrospectives, and velocity tracking to improve predictability.

In other cases, especially for support or integration work, Kanban worked better.
It allowed for continuous delivery and quick reaction to changing priorities,
which was perfect for smaller tasks or bug fixing.

I’m comfortable using either depending on the nature of the team and the project.

👥 Командна робота
Скільки було людей у команді?

Як вирішували конфлікти?

Як ти допомагав менторити молодших?

Чи проводив code review?

✅ 4. Робота з клієнтом (10 хв)
Чи був прямий контакт з клієнтом?

Як ти пояснюєш складні технічні речі бізнес-замовнику?

Що робити, коли клієнт вимагає те, що технічно недоцільно?

Як ти дієш, коли клієнт змінює вимоги в останній момент?

✅ 5. Ситуаційні кейси (10 хв)
Це важливо, бо Luxoft часто працює з фінансовими клієнтами, де відповідальність і стресостійкість критичні.

Ситуація	Твої дії
🔥 Критичний баг в продакшені	"Я б перевірив логування, зібрав команду, пріоритезував фікс, повідомив PM"
📦 Новий реліз через 2 дні, а код нестабільний	"Я б запропонував відкласти реліз, щоб не наражати клієнта на ризики"
⚖️ Конфлікт з іншим сеньйором	"Обговорюю технічну суть, залучаю тімліда, шукаю компроміс"
📈 Потрібно оптимізувати запит, що вантажить базу	"Я аналізую індекси, explain plan, альтернативні структури"

🔹 Що важливо підкреслити?
Самостійність — вміння приймати рішення без постійного контролю.

Зрілість — ти не просто "кодиш", а розумієш бізнес-цілі.

Відповідальність — розумієш наслідки своїх рішень.

Комунікабельність — можеш говорити з бізнесом, а не лише з технічними колегами.

Гнучкість — не боїшся змін, працюєш у стресі.

❗ Поради:
Підготуй 2–3 сильні історії: технічне рішення, конфлікт у команді, критичний реліз.

1. Authentication with embedded FE
2. AutomatedQM implementation - how improved
3. Splitting of monolith to microservices
4. Conflict situation because of violating of KISS principle

Покажи, що ти можеш вести інших, брати ініціативу.

Дай зрозуміти, що можеш вільно говорити англійською (у Luxoft це часто критично).

Не бійся питати менеджера про проект, команду, плани — це показує твою залученість.


Що я зроблю поперше:
1. Курс по Пайтону
2. Курс по Google Cloud
3. Docker to install on Windows and use it
4. TestContainers
5. Okta
6. K8s and Docker
7. Terraform
8. Claude code

Де я покращив:
1. не чекати на запізнюваків
2. питати в паблік чатах
3. POC on every unknown

Питання до менеджера:
1. Scrum?
2. Windows or Mac is possible? 
3. Вдається дотримуватися плану на Спринти?
4. Це своя ОС або VDI, якщо працювати не можна на своєму, то обмеження?
5. How onboarding is looking like?
6. Is business well documented - confluence, java docs?
7. Performance reviews
8. Do you have a FE?
9. Theoretical possibility to start locally? to mock downstream services?
10. Any big stories are planned
11. ratio of bugfixing
12. Any Frontend?

//TODO:
1. UML - 2 types to know
2. Oauth2 to know
3. certs to know

---

## `src/main/java/com/conduct/interview/loose_coupling_and_cohesion/coupling_cohesion.md`

## 🔗 Loose Coupling

Loose coupling means that components or classes are independent and interact through 
well-defined interfaces. This makes the system more flexible, easier to maintain 
and allows changes without affecting other parts.

## 🧩 Cohesion

Cohesion refers to how closely related the functions within a single class or module are.  
High cohesion means a class is focused on one specific task, making it easier to understand 
and reuse.


---

## `src/main/java/com/conduct/interview/microservices.md`

Preparing for an interview focused on microservices requires a solid understanding of the concepts, 
architecture, best practices, and hands-on experience. Based on your LinkedIn profile and achievements, 
you already have relevant experience (e.g., decomposing a monolithic Java app into Kotlin Spring Boot 
microservices, using OpenAPI, and implementing inter-service communication). Below, I’ll outline 
key microservices topics to prepare, tailored to your background, and include sample interview 
questions with concise answers to help you articulate your expertise. I’ll also suggest practical 
ways to demonstrate your skills during the interview.

Key Microservices Topics to Prepare
Microservices Fundamentals
Definition: Microservices are small, independent services that work together to form an application, 
each responsible for a specific function, communicating via APIs.
Benefits: Scalability, independent deployment, technology diversity, fault isolation.
Challenges: Distributed system complexity, data consistency, inter-service communication, monitoring.
Your Experience: You’ve decomposed a monolithic Java app into microservices, so emphasize your understanding 
of bounded contexts and service separation.
Architecture and Design
Bounded Contexts: Dividing functionality into independent services based on domain-driven design (DDD).
API Design: Using RESTful APIs, OpenAPI specs, or gRPC for communication.
Patterns: API Gateway, Circuit Breaker, Service Discovery, Event-Driven Architecture.
Your Experience: You defined OpenAPI specs and used Feign/WebClient for inter-service calls, showcasing your 
ability to design clean APIs.
Inter-Service Communication
Synchronous: REST (via WebClient/Feign), gRPC.
Asynchronous: Messaging systems like RabbitMQ, Kafka.
Challenges: Latency, retries, idempotency, error handling.
Your Experience: You’ve used RabbitMQ for the Auto-QM service and Feign/WebClient for microservices communication.
Data Management
Each microservice typically owns its database to ensure loose coupling.
Challenges: Data consistency (e.g., eventual consistency, Saga pattern), distributed transactions.
Your Experience: You managed distributed data with shared DB schemas in the early phase of your microservices project.
DevOps and Deployment
Tools: Kubernetes, Docker, Helm Charts for container orchestration.
CI/CD: Automating pipelines with Jenkins, Bamboo, or Git.
Monitoring: Tools like Prometheus, Grafana, or ELK stack for observability.
Your Experience: You’ve worked with Kubernetes, Helm, and CI/CD pipelines, which are critical for microservices 
deployment.
Testing Microservices
Unit, integration, and contract testing (e.g., using Pact or Spring Cloud Contract).
End-to-end testing challenges in distributed systems.
Your Experience: You fixed test failures during the Spring Boot 3 upgrade, indicating familiarity with testing 
challenges.
Common Challenges and Solutions
Distributed tracing: Tools like Zipkin or Jaeger.
Fault tolerance: Circuit breakers (e.g., Resilience4j), retries, timeouts.
Security: OAuth2, JWT, or Keycloak for authentication/authorization.
Your Experience: Your Auto-QM service integrated with AI systems, likely requiring secure communication and fault 
handling.
Your Achievements in Context
Monolith to Microservices: Highlight how you identified bounded contexts, defined OpenAPI specs, and ensured strict 
contracts.
Auto-QM Service: Emphasize designing a scalable, rule-based service with RabbitMQ and AI integration.
GraalVM: Mention optimizing microservices for faster startup and lower memory usage.
Sample Interview Questions and Answers
Below are common microservices-related interview questions with concise answers tailored to your experience. 
Practice these to articulate your expertise clearly.

What are microservices, and how do they differ from a monolithic architecture?
Answer: Microservices are small, independent services that handle specific functions and communicate via APIs, 
enabling independent deployment and scalability. Unlike monoliths, where all functionality is tightly coupled in a 
single codebase, microservices allow modular development and technology diversity. For example, I decomposed a 
monolithic Java app into Kotlin Spring Boot microservices, defining bounded contexts and OpenAPI specs to ensure loose 
coupling and scalability.

How do you design a microservices architecture?
Answer: I start by identifying bounded contexts using domain-driven design to group related functionality into services. 
Each service gets its own database to ensure independence. I define clear APIs, often using OpenAPI specs, as I did 
when breaking down a monolith into microservices. I also implement an API Gateway for routing and use tools like 
Kubernetes for deployment. Inter-service communication is handled via REST (e.g., WebClient) 
or messaging (e.g., RabbitMQ), depending on the use case.

How do you handle data consistency in microservices?
Answer: Microservices often use eventual consistency due to separate databases. I’ve used shared DB schemas in 
early phases, as in my monolith-to-microservices project, transitioning to separate schemas later. 
For complex workflows, I’d use the Saga pattern to coordinate distributed transactions or event-driven messaging with 
RabbitMQ to ensure data propagation, as I did in the Auto-QM service.

What challenges have you faced in microservices development?
Answer: One challenge is managing inter-service communication. In my project, I used Feign and WebClient for 
synchronous calls and RabbitMQ for asynchronous messaging, addressing latency and retries with idempotent designs. 
Another challenge is testing; during my Spring Boot 3 upgrade, I fixed test failures caused by behavior changes, 
ensuring robust integration tests. Distributed tracing and monitoring are also critical—I’d use tools like Zipkin for 
tracing in production.

How do you ensure fault tolerance in microservices?
Answer: I implement circuit breakers (e.g., Resilience4j) to prevent cascading failures and retries with exponential 
backoff for transient errors. In my Auto-QM service, I used RabbitMQ with retry logic to handle message failures. 
Health checks, like those I configured in Kubernetes, ensure services report readiness only when dependencies like 
databases are available.

How do you test microservices?
Answer: I use unit tests for individual components, integration tests for service interactions, and contract tests to 
validate APIs. In my microservices project, I generated API interfaces with OpenAPI Generator and tested them with 
Spring Cloud Contract. For end-to-end testing, I simulate real-world scenarios, ensuring services like my Auto-QM 
service correctly process data through RabbitMQ and PostgreSQL.

How do you deploy microservices?
Answer: I deploy microservices using Kubernetes and Docker, with Helm Charts for configuration, as I did in my projects. 
CI/CD pipelines with Jenkins or Bamboo automate builds and deployments, ensuring zero-downtime updates. 
For optimization, I’ve used GraalVM Native Image to reduce startup time and memory usage, improving scalability for 
cloud-native environments.

Can you describe a microservices project you’ve worked on?
Answer: I transformed a monolithic Java application into Kotlin Spring Boot microservices within a monorepo. 
I identified bounded contexts, defined OpenAPI specs for APIs, and used Feign/WebClient for inter-service communication. 
Shared Gradle modules ensured reusable DTOs and validation rules. I also designed an Auto-QM service that automated call 
evaluations using Spring, Kotlin, and RabbitMQ, integrating AI for scoring and storing results in PostgreSQL.

Preparation Tips

Review Your Achievements: Be ready to discuss your monolith-to-microservices project and Auto-QM service in detail. 
Explain the "why" behind your decisions (e.g., choosing OpenAPI for API contracts, 
RabbitMQ for asynchronous processing).

Practice System Design: Be prepared for system design questions (e.g., “Design a microservices-based e-commerce system”). 
Sketch out components like API Gateway, service discovery (e.g., Eureka), and databases, referencing your Kubernetes 
and Helm experience.

Code Samples: If coding is part of the interview, expect tasks like implementing a REST API with Spring Boot or 
handling message queues. Practice a simple microservice (e.g., a Kotlin/Spring Boot service with a REST endpoint 
and RabbitMQ integration).

Behavioral Questions: Prepare to discuss challenges (e.g., test failures during Spring Boot upgrades) and how you 
solved them. Highlight teamwork, as you mentioned working in agile, cross-functional teams.

Know Related Tools: Be ready to talk about Kubernetes, Docker, Helm, RabbitMQ, and OpenAPI, as these are in your 
profile. If familiar with AWS, Azure, or monitoring tools (e.g., Prometheus), mention them.

Mock Interviews: Practice with a friend or use platforms like LeetCode or Pramp to simulate technical interviews, 
focusing on microservices scenarios.

Additional Resources
Books: “Building Microservices” by Sam Newman for architecture and patterns.
Online Courses: Spring Boot Microservices on Udemy or Pluralsight.
Practice: Build a small microservices project (e.g., two Spring Boot services communicating via REST and RabbitMQ) to 
solidify your hands-on skills.
If you want specific coding examples (e.g., a Spring Boot microservice with Kotlin and RabbitMQ) or mock system design 
questions tailored to your experience, let me know! Good luck with your interview!

---

## `src/main/java/com/conduct/interview/microservices/microservices.md`

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

---

## `src/main/java/com/conduct/interview/practise/spring/security/security-CSRF.md`

CSRF is an attack where a malicious website tricks a user’s browser (already authenticated with your site) into making 
an unwanted state-changing request (like POST, PUT, DELETE) to your application without the user’s knowledge.

Example:

You’re logged into your banking app.

Another site you visit silently sends a POST to your bank’s /transfer endpoint using your cookies.

Without CSRF protection, the transfer could go through.


How Spring Security protects

It generates a CSRF token per session and expects it on any state-changing request.

If the token is missing or invalid, the request is rejected.

This ensures a malicious site can’t forge a valid state-changing request.

---

## `src/main/java/com/conduct/interview/practise/spring/security/security_headers.md`

Content-Security-Policy (CSP)
What it does

CSP is a powerful browser-side security mechanism that:

Controls which scripts, styles, images, fonts etc. can load.

Blocks inline scripts/styles unless explicitly allowed.

Prevents XSS, clickjacking, and data injection attacks.

Impact

Very effective against XSS but can break your UI if not carefully configured.

Inline <script> or <style> tags or external resources from unlisted domains will be blocked.

You need to list all allowed sources (self, CDNs, etc.) in the policy.




HTTP Strict-Transport-Security (HSTS)
What it does

Tells browsers: always use HTTPS for this domain.

Prevents SSL stripping attacks.

Can include subdomains and preload the rule in browsers.

Impact

Once the browser caches it, users cannot access the site via HTTP anymore — only HTTPS.

If you misconfigure your HTTPS certificate and HSTS is set, users can’t “bypass” warnings easily.




X-Content-Type-Options
What it does

Prevents browsers from MIME-sniffing content types.

Reduces risk of some XSS attacks via content type confusion.

Impact

Very low risk — usually safe to enable.

Prevents browsers from interpreting non-JS files as JS, for example.



| Feature / Header / Concept | Purpose / What It Does | When It’s Critical | Side Effects if Misconfigured |
|----------------------------|------------------------|-------------------|------------------------------|
| **CSRF Protection** (Form Login) | Protects against cross-site request forgery when using cookies or sessions. | When using cookie-based sessions or form login. | Adds hidden tokens to forms / AJAX calls; requests without token are rejected. |
| **CSRF Protection** (Basic Auth) | Not usually needed, because credentials in Authorization header are not auto-sent cross-site. | Mostly unnecessary for APIs with Basic Auth. | None. |
| **CSRF Protection** (Token Auth) | Not usually needed if tokens are sent in Authorization headers. | Needed only if token is stored in cookies. | None unless stored improperly. |
| **Content-Security-Policy (CSP)** | Limits which scripts/styles/images can load; mitigates XSS and data injection. | Any web app serving HTML. | May block inline scripts or external resources until whitelisted. |
| **HTTP Strict-Transport-Security (HSTS)** | Forces browsers to always use HTTPS, preventing SSL stripping. | Public-facing apps with HTTPS configured. | Locks users to HTTPS; misconfig + expired cert can block access. |
| **X-Content-Type-Options (nosniff)** | Stops MIME type guessing by browsers; prevents executing malicious files as JS. | Any app serving files. | Virtually none; safe to enable. |
| **XSS Protection (general)** | Escape/encode user data, validate input, CSP headers. | Any dynamic content. | None; just prevents injection. |
| **MIME Sniffing (general)** | Browser feature that guesses MIME type of files. | Can be dangerous if not disabled. | Disabled with `nosniff`. |


---

## `src/main/java/com/conduct/interview/questions/junior/questions_junior.md`

# Java Junior Interview Questions

## Chapter 1: Java Basics
1. **What are JDK, JRE, and JVM? How do they interact?**
2. **Explain the process of compiling and running a Java program.**
3. **What types of comments are supported in Java?**
4. **What is platform independence in Java? Why is it considered cross-platform?**
5. **What key features make Java a popular programming language?**

## Chapter 2: Data Types and Operations
1. **What primitive data types exist in Java? Provide examples.**
2. **Explain the difference between primitive and reference data types.**
3. **What are autoboxing and unboxing in Java? Provide examples.**
4. **What are the basic arithmetic, logical, and bitwise operators in Java?**
5. **What are the rules for type conversion in Java?**

## Chapter 3: Control Flow Statements
1. **What control flow statements exist in Java (if, switch, for, while, etc.)?**
2. **How does the switch statement work in Java? What are its data type limits?**
3. **What are the break and continue statements? When should they be used?**
4. **Explain the difference between the for, while, and do-while loops.**
5. **What are the features of the return statement?**

## Chapter 4: Classes, Objects, and Methods
1. **What are classes and objects in Java? How do you declare and use them?**
2. **What is a constructor? What are its key properties?**
3. **Explain the concept of method overloading.**
4. **What is encapsulation, and how is it implemented in Java?**
5. **What are static fields and methods? When and why are they used?**

## Chapter 5: Advanced Data Types and Operations
1. **What are arrays in Java? How are they initialized and used?**
2. **Explain the difference between one-dimensional and multidimensional arrays.**
3. **What are strings in Java? What methods are used to work with strings?**
4. **How do StringBuilder and StringBuffer work? What is the difference?**
5. **How do generics work in Java? Provide an example.**

## Chapter 6: Advanced Methods and Classes
1. **What are method overloading and overriding? What’s the difference?**
2. **What are nested and inner classes? What are their advantages?**
3. **What are anonymous classes in Java? Provide an example of usage.**
4. **What are access modifiers (public, private, protected, default)?**
5. **Explain the final keyword for classes, methods, and variables.**

## Chapter 7: Inheritance
1. **What is inheritance in Java? What are the benefits of using it?**
2. **How does the super keyword work? Provide an example.**
3. **What are abstract classes and methods? How do they differ from interfaces?**
4. **Explain polymorphism in Java. How is it implemented?**
5. **What is an interface in Java? How is it used, and how does it differ from a class?**


---

## `src/main/java/com/conduct/interview/questions/senior/senior_questions.md`

---

# Advanced Java Interview Questions and Answers

## 1. Core Java (JVM, Classloading, Bytecode, Performance Optimization)

### Q1: What is Java language in your vision?
**Answer:** Java is a high-level, object-oriented, statically typed programming language designed for platform 
independence through its "Write Once, Run Anywhere" (WORA) capability. It compiles source code into bytecode, 
which is executed by the Java Virtual Machine (JVM), allowing it to run on any system with a JVM installed.

---

### Q2: What does "Object-Oriented" mean?
**Answer:** Object-Oriented Programming (OOP) is a paradigm that organizes code into objects, which are instances 
of classes. Objects encapsulate data (fields/attributes) and behavior (methods), promoting modularity, reusability, 
and maintainability.

---

### Q3: What does "Statically Typed" mean?
**Answer:** In a statically typed language like Java, variable types are checked at compile time. You must declare 
a variable's type before using it, and that type cannot change during runtime.

---

### Q4: Why is Java considered a strong programming language?
**Answer:**
- **Platform Independence**: Runs on any JVM-enabled system.
- **Robust and Secure**: Features like garbage collection, exception handling, and strong typing reduce errors.
- **Rich Ecosystem**: Frameworks like Spring Boot, Hibernate, and JavaFX enhance development.
- **Multithreading**: Built-in support for concurrency (e.g., Thread, ExecutorService).
- **Scalability**: Widely used in enterprise, microservices, and cloud solutions.
- **Community**: Extensive libraries and open-source support.

---

### Q5: What is the JVM, and how does it work?
**Answer:** The Java Virtual Machine (JVM) executes Java bytecode on any platform. It consists of:
- **ClassLoader**: Loads `.class` files into memory.
- **Runtime Memory Areas**: Heap (objects), Stack (method calls), Method Area (class metadata), etc.
- **Execution Engine**: Interprets bytecode or compiles it using the Just-In-Time (JIT) compiler.
- **Garbage Collector**: Frees unused memory.

**Example:**
```java
public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}
```
The JVM loads `HelloWorld.class`, verifies bytecode, allocates memory, and executes it.

---

### Q6: What is Java bytecode, and how does it ensure platform independence?
**Answer:** Bytecode is an intermediate representation of Java code (`.class` files) generated by the `javac` compiler. 
The JVM interprets or compiles it into native machine code, ensuring platform independence.  
**Example:**
```sh
javac HelloWorld.java  # Generates HelloWorld.class
java HelloWorld        # Executes on JVM
```

---

### Q7: What are the benefits of the Just-In-Time (JIT) compiler?
**Answer:**
- **Performance**: Converts hotspot bytecode to native code for faster execution.
- **Adaptive Optimization**: Focuses on frequently used methods.
- **Better than Interpretation**: Compiles once, reuses native code.
- **Code Inlining**: Eliminates method call overhead.
- **Loop Unrolling**: Reduces loop iterations for efficiency.

---

### Q8: How does the ClassLoader work in the JVM?
**Answer:** The ClassLoader loads `.class` files into memory using a delegation model:
- **Bootstrap ClassLoader**: Loads core Java classes (e.g., `java.lang`).
- **Extension ClassLoader**: Loads classes from the `jre/lib/ext` directory.
- **Application ClassLoader**: Loads classes from the classpath.  
  It ensures classes are loaded only once and follows a parent-first delegation hierarchy.

---

## 2. OOP, SOLID, and Design Patterns

### Q9: What are the four pillars of OOP? Provide examples.
**Answer:**
1. **Encapsulation**: Bundles data and methods, restricting access.  
   **Example:**
   ```java
   class Person {
       private String name;
       public String getName() { return name; }
       public void setName(String name) { this.name = name; }
   }
   ```
2. **Abstraction**: Hides complexity, exposing only essentials.  
   **Example:**
   ```java
   abstract class Shape {
       abstract double area();
   }
   ```
3. **Inheritance**: Reuses code via a parent-child relationship.  
   **Example:**
   ```java
   class Dog extends Animal {
       void bark() { System.out.println("Woof"); }
   }
   ```
4. **Polymorphism**: Allows objects to respond differently to the same method call.  
   **Example:**
   ```java
   class Cat extends Animal {
       void sound() { System.out.println("Meow"); }
   }
   ```

---

### Q10: What are Association, Aggregation, and Composition?
**Answer:**
- **Association**: A general relationship between objects (e.g., a teacher and a student).
- **Aggregation**: A "has-a" relationship with loose coupling (e.g., a car has wheels).
- **Composition**: A stronger "has-a" relationship where the child’s lifecycle depends on the parent (e.g., a house 
- has rooms).

**Example:**
```java
class Car { List<Wheel> wheels; }  // Aggregation
class House { List<Room> rooms; }  // Composition
```

---

### Q11: What are the SOLID principles? Provide an SRP violation example.
**Answer:**
- **S**: Single Responsibility Principle (SRP)
- **O**: Open-Closed Principle (OCP)
- **L**: Liskov Substitution Principle (LSP)
- **I**: Interface Segregation Principle (ISP)
- **D**: Dependency Inversion Principle (DIP)

**SRP Violation:**
```java
class Invoice {
    void calculateTotal() { /* Logic */ }
    void printInvoice() { /* Logic */ }  // Should be in a separate class
}
```
**Fix:**
```java
class InvoicePrinter {
    void printInvoice(Invoice invoice) { /* Logic */ }
}
```

---

### Q12: Explain the Dependency Inversion Principle (DIP) with an example.
**Answer:** High-level modules should depend on abstractions, not concrete implementations.  
**Example:**
```java
interface Database {
    void save(String data);
}
class MySQL implements Database {
    public void save(String data) { /* MySQL logic */ }
}
class App {
    private Database db;
    public App(Database db) { this.db = db; }  // Depends on abstraction
}
```

---

### Q13: How do primitive types and objects differ in parameter passing?
**Answer:**
- **Primitive Types** (e.g., `int`): Passed by value; changes don’t affect the caller.
- **Objects**: Reference is passed by value; changes to the object affect the original, but reassigning the reference 
- does not.

---

### Q14: What are Lambda Expressions, and how do they relate to Streams?
**Answer:** Lambda expressions (introduced in Java 8) provide a concise way to implement functional interfaces. 
They work with Streams for functional-style operations on collections.  
**Example:**
```java
List<Integer> numbers = Arrays.asList(1, 2, 3);
numbers.stream().filter(n -> n % 2 == 0).forEach(System.out::println);
```

---

### Q15: What are the most useful design patterns? Provide examples.
**Answer:**
- **Singleton**: Ensures one instance.
  ```java
  class Singleton {
      private static Singleton instance;
      private Singleton() {}
      public static Singleton getInstance() {
          if (instance == null) instance = new Singleton();
          return instance;
      }
  }
  ```
- **Factory Method**: Defers object creation.
  ```java
  interface Shape { void draw(); }
  class ShapeFactory {
      Shape getShape(String type) {
          if (type.equals("Circle")) return new Circle();
          return new Rectangle();
      }
  }
  ```
- **Observer**: Notifies objects of state changes.
  ```java
  class Subject {
      List<Observer> observers = new ArrayList<>();
      void notifyObservers() { observers.forEach(o -> o.update()); }
  }
  ```

---

### Q16: Explain the Command Pattern and its use cases.
**Answer:** Encapsulates a request as an object, decoupling sender and receiver.
- **Use Cases**: Undo/redo, queuing requests, logging.  
  **Example:**
```java
interface Command { void execute(); }
class LightOnCommand implements Command {
    private Light light;
    public LightOnCommand(Light light) { this.light = light; }
    public void execute() { light.turnOn(); }
}
```

---

## 3. Concurrency and Multithreading

### Q17: What are the ways to create a thread in Java?
**Answer:**
1. **Extending Thread**:
   ```java
   class MyThread extends Thread {
       public void run() { System.out.println("Thread running"); }
   }
   new MyThread().start();
   ```
2. **Implementing Runnable**:
   ```java
   class MyRunnable implements Runnable {
       public void run() { System.out.println("Runnable running"); }
   }
   new Thread(new MyRunnable()).start();
   ```
3. **Executor Framework**:
   ```java
   ExecutorService executor = Executors.newFixedThreadPool(5);
   executor.execute(() -> System.out.println("Thread running"));
   executor.shutdown();
   ```

---

### Q18: How do you prevent race conditions in Java?
**Answer:** Use synchronization mechanisms:
- **`synchronized`**:
  ```java
  class Counter {
      private int count = 0;
      public synchronized void increment() { count++; }
  }
  ```
- **`ReentrantLock`**:
  ```java
  class Counter {
      private int count = 0;
      private ReentrantLock lock = new ReentrantLock();
      public void increment() { lock.lock(); try { count++; } finally { lock.unlock(); } }
  }
  ```
- **Atomic Variables**:
  ```java
  class Counter {
      private AtomicInteger count = new AtomicInteger(0);
      public void increment() { count.incrementAndGet(); }
  }
  ```

---

### Q19: What is a deadlock, and how can it be prevented?
**Answer:** A deadlock occurs when two or more threads wait indefinitely for resources held by each other.
- **Prevention**:
   - Use consistent lock ordering.
   - Use `tryLock()` with timeouts.
   - Minimize lock usage with tools like `ConcurrentHashMap`.

---

## 4. Spring & Dependency Injection

### Q20: What are the types of Dependency Injection in Spring?
**Answer:**
- **Constructor Injection**: For mandatory dependencies.
  ```java
  @Component
  class UserService {
      private final UserRepository userRepository;
      @Autowired
      public UserService(UserRepository userRepository) { this.userRepository = userRepository; }
  }
  ```
- **Setter Injection**: For optional dependencies.
- **Field Injection**: Not recommended (hard to test).

---

### Q21: How does Spring’s Dependency Injection improve design?
**Answer:** It promotes loose coupling by injecting dependencies via interfaces rather than hardcoding implementations, 
enhancing testability and flexibility.

---

## 5. Hibernate (Caching, N+1, Performance Tuning)

### Q22: What is the N+1 problem in Hibernate, and how do you fix it?
**Answer:** Occurs when fetching each entity triggers separate queries for related entities.
- **Fix**: Use `JOIN FETCH` or `@BatchSize`.
  ```java
  @Query("SELECT u FROM User u JOIN FETCH u.orders")
  List<User> findAllUsersWithOrders();
  ```

---

### Q23: How does Hibernate caching work?
**Answer:**
- **First-Level Cache**: Session-specific, enabled by default.
- **Second-Level Cache**: Shared across sessions, requires configuration (e.g., Ehcache).

---

## 6. Security (Spring Security, Common Vulnerabilities)

### Q24: What are common web security threats, and how do you mitigate them?
**Answer:**
- **SQL Injection**: Use parameterized queries.
  ```java
  @Query("SELECT u FROM User u WHERE u.email = :email")
  User findByEmail(@Param("email") String email);
  ```
- **XSS**: Escape user input.
- **CSRF**: Use CSRF tokens in Spring Security.
- **Broken Authentication**: Enforce strong passwords and session management.

---

## 7. Java Exceptions

### Q25: What is an exception in Java?
**Answer:** An event that disrupts normal program flow, represented by a `Throwable` object.
- **Checked Exceptions**: Must be handled (e.g., `IOException`).
- **Unchecked Exceptions**: Runtime errors (e.g., `NullPointerException`).

---

## 8. Java Collections Hierarchy

### Q26: Explain the Java Collections Hierarchy.
**Answer:**
- **`Collection`**: Root interface.
   - **List**: Ordered, allows duplicates (e.g., `ArrayList`, `LinkedList`).
   - **Set**: No duplicates (e.g., `HashSet`, `TreeSet`).
   - **Queue**: Ordered for processing (e.g., `PriorityQueue`).
- **`Map`**: Key-value pairs (e.g., `HashMap`, `TreeMap`).

---

### Q27: Compare `ArrayList` and `LinkedList` performance.
**Answer:**
- **ArrayList**:
   - Access: `O(1)`
   - Insert/Delete: `O(n)`
   - Best for random access.
- **LinkedList**:
   - Access: `O(n)`
   - Insert/Delete: `O(1)`
   - Best for frequent modifications.

---

### Q28: How does `HashMap` work internally?
**Answer:**
- Uses an array of `Node<K, V>` (buckets).
- **Insertion**: `put(K key, V value)` computes `hash(key)` and places it at `index = (array.length - 1) & hash`.
- **Collision**: Handled via linked lists (pre-Java 8) or red-black trees (Java 8+).
- **Resizing**: Doubles size when load factor (0.75) is exceeded.


What is a Collision? 

A collision happens when:

Two distinct keys, say key1 and key2, are passed through the hash function.
The hash function returns the same index for both keys (e.g., hash(key1) == hash(key2)).
The hashmap must then resolve this conflict to store and retrieve both key-value pairs correctly.

---

## 9. Java Garbage Collection

### Q29: How does Garbage Collection work in Java?
**Answer:** The GC reclaims memory from unreachable objects.
- **Heap Divisions**:
   - **Young Generation**: Eden, Survivor Spaces (Minor GC).
   - **Old Generation**: Long-lived objects (Major GC).
   - **Metaspace**: Class metadata (post-JDK 8).
- **GC Roots**: Threads, static variables.

---

### Q30: What’s the difference between Heap and Stack memory?
**Answer:**
- **Heap**: Dynamic allocation for objects (`new`), managed by GC.
- **Stack**: Static allocation for local variables and method calls, popped off after execution.

---

## 10. Maven

### Q31: What are Maven’s default lifecycles and key phases?
**Answer:**
- **Lifecycles**: Clean, Default (Build), Site.
- **Key Phases**:
   - `compile`: Compiles code.
   - `test`: Runs unit tests.
   - `package`: Creates JAR/WAR.
   - `install`: Adds to local repository.

---

## 11. Best Practices

### Q32: What are high cohesion and loose coupling?
**Answer:**
- **High Cohesion**: A module has one focused responsibility.
- **Loose Coupling**: Modules interact via interfaces, not implementations.
- **Best Practices**: Use DI, follow SOLID, prefer abstractions.

---


---

## `src/main/kotlin/knowyourproject/kotlin/kotlin.md`

To answer this in an interview, you want to focus on modernity, safety, and developer productivity.
It’s not just about "liking" Kotlin; it’s about how Kotlin solves specific pain points that have existed
in Java for decades.Here is a concise, high-impact breakdown you can use:1. Null Safety
(The "Billion Dollar Mistake")In Java, any object can be null, leading to the dreaded
NullPointerException (NPE) at runtime. Kotlin's Fix: It distinguishes between nullable and non-nullable
types at the compiler level. Interview Tip: "Kotlin moves the 'null check' from runtime to compile-time.
# Kotlin vs. Java: Interview Guide

When explaining why Kotlin is often preferred over Java, focus on **Modernity**, **Safety**, and **Productivity**. 
Kotlin is designed to solve specific pain points that have existed in Java for decades.

---

## 1. Null Safety (The "Billion Dollar Mistake")
In Java, any object can be `null`, which frequently leads to `NullPointerException` (NPE) at runtime.

* **Kotlin's Fix:** It distinguishes between **nullable** and **non-nullable** types at the compiler level.
* **Interview Tip:** *"Kotlin moves null checks from runtime to compile-time. 
* If you try to assign a null value to a non-nullable variable, the code won't even compile. 
* This eliminates a massive category of common production crashes."*

## 2. Drastic Reduction in Boilerplate
Java is notoriously verbose, requiring a lot of code for simple structures.

* **Data Classes:** A single line `data class User(val name: String)` replaces dozens of lines of Java 
* (Getters, Setters, `equals()`, `hashCode()`, and `toString()`).
* **Type Inference:** You don't have to declare types explicitly every time; the compiler is smart enough to figure them out.

## 3. Coroutines vs. Threads
Traditional Java concurrency often involves complex thread management or "callback hell."

* **Kotlin's Fix:** **Coroutines** are "lightweight threads." You can run thousands of them on a single 
* OS thread without blocking it.
* **Interview Tip:** *"Coroutines allow us to write asynchronous code in a sequential style. 
* This makes the code much easier to read and maintain compared to Java's traditional threading or CompletableFuture."*

## 4. Smart Casts and Extension Functions
* **Smart Casts:** In Java, after checking `instanceof`, you still have to manually cast the object. 
* In Kotlin, once you check a type, the compiler treats it as that type automatically within that scope.
* **Extension Functions:** You can add new methods to existing classes 
* (even those in external libraries like `String` or `List`) without using inheritance.

## 5. 100% Interoperability
* **Interchangeability:** Kotlin and Java can coexist in the same project. You can call Java code from Kotlin 
* and vice versa seamlessly.
* **Interview Tip:** *"Interoperability makes Kotlin a low-risk choice. You don't have to rewrite your entire system; 
* you can migrate one class at a time while still using all your existing Java libraries."*

---

## Comparison Summary

| Feature | Java | Kotlin |
| :--- | :--- | :--- |
| **Null Safety** | Manual checks (`if != null`) | Built-in to the type system |
| **Boilerplate** | High (Lombok is a workaround) | Extremely low (Native support) |
| **Data Models** | Verbose (unless using Records) | Single-line `data class` |
| **Concurrency** | Threads (Heavyweight) | Coroutines (Lightweight) |
| **Functions** | Must belong to a class | Top-level & Extension functions |

---

## The "Elevator Pitch" Summary
> "Kotlin is a pragmatic evolution of Java. It retains the strengths of the JVM ecosystem while introducing modern features like null-safety and coroutines. This results in code that is safer, more concise, and easier to maintain, allowing developers to focus on business logic rather than language verbosity."

***Note on Java 21+:*** *If asked about newer Java versions, acknowledge that features like **Records** and **Virtual Threads (Project Loom)** are closing the gap, but emphasize that Kotlin's syntax remains more expressive and its null-safety is still more robust.*

Kotlin повністю працює на JVM і має повну сумісність із Java. Ми можемо використовувати Java SDK,
сторонні Java-бібліотеки, і навіть писати Java-код поруч із Kotlin-кодом в одному проєкті. При цьому Kotlin додає
сучасні можливості, такі як null-безпека, data-класи, лямбди, функції-розширення та інше — все це підвищує безпечність
і читабельність коду.

🟦 Основні переваги та фічі Kotlin

// ✅ 1. Нульова безпека (null safety)
// Kotlin не дозволяє null без явного зазначення `?`
val name: String = "Dmytro"
// val n: String = null // ❌ помилка

val nullableName: String? = null
val length = nullableName?.length ?: 0 // ?. — безпечний виклик, ?: — значення за замовчуванням
println("Довжина імені: $length") // 0



// ✅ 2. Лямбда-функції
val sum = { a: Int, b: Int -> a + b }
println("Сума: ${sum(3, 4)}") // 7

// Застосування у колекціях
val nums = listOf(1, 2, 3, 4)
val doubled = nums.map { it * 2 }
println(doubled) // [2, 4, 6, 8]



// ✅ 3. Extension-функції — додаємо методи до існуючих типів
fun String.capitalizeWords(): String =
split(" ").joinToString(" ") { it.replaceFirstChar(Char::uppercase) }

val text = "kotlin is awesome"
println(text.capitalizeWords()) // Kotlin Is Awesome

// ✅ 6. Smart casting — автоматичне приведення типу після перевірки
fun printLength(value: Any) {
if (value is String) {
println("Довжина: ${value.length}") // value автоматично стає String
}
}
printLength("Kotlin") // Довжина: 6

kotlin
Copy
Edit
val doubled = list.map { it * 2 }
✅ 3. Функції-розширення (extension functions)
Додають нову поведінку до існуючих класів

kotlin
Copy
Edit
fun String.capitalizeWords() = split(" ").joinToString(" ") { it.replaceFirstChar(Char::uppercase) }
✅ 4. data-класи
Автоматично генеруються toString(), equals(), hashCode(), copy()

kotlin
Copy
Edit
data class User(val name: String, val age: Int)
✅ 5. Sealed-класи
Гарантують, що всі підкласи відомі під час компіляції

kotlin
Copy
Edit
sealed class Result  
data class Success(val data: String): Result()  
data class Error(val msg: String): Result()
✅ 6. Smart casting
Після перевірки типу або null — автоматичне приведення типу

kotlin
Copy
Edit
if (x is String) println(x.length)
✅ 7. inline-функції
Вставляють тіло функції та лямбди напряму у виклик, економлять ресурси

kotlin
Copy
Edit
inline fun log(msg: String, block: () -> Unit) { println(msg); block() }
✅ 8. Делегація (by)
Делегування реалізації інтерфейсу іншому об’єкту

kotlin
Copy
Edit
class Service(logger: Logger) : Logger by logger
✅ 9. Колекції та функціональні API
map, filter, fold, groupBy — працюють з імутабельними колекціями

kotlin
Copy
Edit
val result = list.filter { it > 5 }.map { it * 2 }
✅ 10. when-вираз
Покращена альтернатива switch, підтримує всі типи

kotlin
Copy
Edit
when (x) {
is Int -> println("Int")
in 1..10 -> println("In range")
else -> println("Unknown")
}



# Kotlin

Kotlin is a modern, statically typed language developed by JetBrains. It
runs on the JVM, can be compiled to JavaScript or native code, and is
fully interoperable with Java.

## Key Features

1. **Java Interoperability**  
   Fully interoperable with Java, allowing seamless integration with
   existing Java libraries and frameworks.

2. **Concise Syntax**  
   Reduces boilerplate code with features like type inference, default
   parameters, and data classes.

3. **Null Safety**  
   Prevents `NullPointerException` with built-in null safety using `?` for
   nullable types.

4. **Functional Programming**  
   Supports lambdas, higher-order functions, and immutability.

5. **Extension Functions**  
   Extends existing classes with new functionality without modifying their
   source code.

6. **Coroutines**  
   Provides native support for asynchronous programming with lightweight
   coroutines.

7. **Data Classes**  
   Automatically generates useful methods (`toString()`, `equals()`,
   `hashCode()`, `copy()`) for classes designed to hold data.

8. **Multiplatform Support**  
   Allows code sharing across platforms (JVM, Android, JavaScript, Native).

9. **Android Development**  
   Preferred language for modern Android app development.

## Common Use Cases

- **Android App Development**: Preferred language for building Android apps.
- **Web Development**: Compiles to JavaScript for web applications.
- **Server-side Development**: Used with frameworks like Ktor and Spring.
- **Cross-Platform Development**: Kotlin Multiplatform for shared code.


Notes:
- no semicolon is required at the end of rows
- main method without args required
- **var** and **val**
- not required to set type of reference variable
- templates with $ instead of String concatenation
- smart cast (after checking type)
- default arguments and named parameters
- null safety (for nullable language enforces null checks)
- 

---

## `src/main/kotlin/knowyourproject/masking/masking.md`

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
annotation class MaskFields(val fields: Array<String> = [])


import org.springframework.stereotype.Component
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.memberProperties

@Component
class MaskingService {

    fun <T : Any?> maskFields(response: T, fieldsToMask: Array<String>): T {
        if (response == null || fieldsToMask.isEmpty()) return response
        val maskedPaths = fieldsToMask.toSet()
        maskObject(response, maskedPaths, "")
        @Suppress("UNCHECKED_CAST")
        return response
    }

    private fun maskObject(obj: Any, maskedPaths: Set<String>, currentPath: String) {
        val kClass = obj::class
        kClass.memberProperties.forEach { prop ->
            val propName = prop.name
            val newPath = if (currentPath.isEmpty()) propName else "$currentPath.$propName"

            if (maskedPaths.contains(newPath)) {
                if (prop is KMutableProperty<*>) {
                    try {
                        prop.setter.call(obj, "****")
                    } catch (e: Exception) {
                        throw RuntimeException("Failed to mask field: $newPath", e)
                    }
                }
            } else {
                val value = prop.getter.call(obj)
                if (value != null && !isPrimitiveOrString(value)) {
                    when (value) {
                        is Collection<*> -> value.forEach { item ->
                            if (item != null && !isPrimitiveOrString(item)) {
                                maskObject(item, maskedPaths, newPath)
                            }
                        }
                        is Map<*, *> -> value.values.forEach { item ->
                            if (item != null && !isPrimitiveOrString(item)) {
                                maskObject(item, maskedPaths, newPath)
                            }
                        }
                        else -> maskObject(value, maskedPaths, newPath)
                    }
                }
            }
        }
    }

    private fun isPrimitiveOrString(value: Any): Boolean {
        return value is String || value is Number || value is Boolean || value is Char
    }
}


Reflection: Modifies mutable (var) properties to "****", supporting nested fields via dot notation (e.g., address.street).
Collections: Handles Collection and Map for nested objects.
Error Handling: Throws RuntimeException for non-mutable or inaccessible fields.




import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.core.MethodParameter
import org.springframework.web.method.HandlerMethod
import org.springframework.web.reactive.HandlerResult
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.ServerWebExchangeDecorator
import org.springframework.web.server.ServerHttpResponse
import org.springframework.web.server.ServerHttpResponseDecorator
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class MaskingResponseDecorator(
delegate: ServerHttpResponse,
private val exchange: ServerWebExchange,
private val handlerResult: HandlerResult?,
private val maskingService: MaskingService,
private val objectMapper: ObjectMapper
) : ServerHttpResponseDecorator(delegate) {

    override fun writeWith(body: Flux<DataBuffer>): Mono<Void> {
        val handler = handlerResult?.handler
        val maskFields = if (handler is HandlerMethod) {
            handler.method.getAnnotation(MaskFields::class.java)
        } else null

        return if (maskFields != null) {
            // Handle reactive and non-reactive types
            val returnValue = handlerResult?.returnValue
            when (returnValue) {
                is Mono<*> -> returnValue
                    .map { maskingService.maskFields(it, maskFields.fields) }
                    .flatMap { masked ->
                        super.writeWith(Flux.just(objectMapper.writeValueAsBytes(masked)))
                    }
                is Flux<*> -> returnValue
                    .map { maskingService.maskFields(it, maskFields.fields) }
                    .collectList()
                    .flatMap { masked ->
                        super.writeWith(Flux.just(objectMapper.writeValueAsBytes(masked)))
                    }
                else -> {
                    val masked = maskingService.maskFields(returnValue, maskFields.fields)
                    super.writeWith(Flux.just(objectMapper.writeValueAsBytes(masked)))
                }
            }
        } else {
            super.writeWith(body)
        }
    }
}



Decorator: Wraps the ServerHttpResponse to intercept the response body.
Annotation Check: Retrieves the @MaskFields annotation from the handler method.
Masking: Uses MaskingService to mask fields, then serializes the result to bytes using ObjectMapper.
Reactive Support: Handles Mono, Flux, and non-reactive types.





import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import com.fasterxml.jackson.databind.ObjectMapper

@Component
class MaskingWebFilter(
private val maskingService: MaskingService,
private val objectMapper: ObjectMapper
) : WebFilter {

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        // Get the handler to access the method annotation
        return exchange.getAttribute<Mono<HandlerResult>>("org.springframework.web.reactive.DispatcherHandler.handlerResult")
            ?.flatMap { handlerResult ->
                val decoratedExchange = object : ServerWebExchangeDecorator(exchange) {
                    override fun getResponse(): ServerHttpResponse {
                        return MaskingResponseDecorator(
                            exchange.response,
                            exchange,
                            handlerResult,
                            maskingService,
                            objectMapper
                        )
                    }
                }
                chain.filter(decoratedExchange)
            } ?: chain.filter(exchange)
    }
}


WebFilter: Intercepts all requests, retrieves the HandlerResult from the exchange attributes, and wraps the response with MaskingResponseDecorator.
HandlerResult: Uses the internal attribute set by Spring WebFlux’s DispatcherHandler to access the handler and result.
Compatibility: Works with Spring Boot 2.7’s WebFlux auto-configuration.



---

## `src/main/kotlin/knowyourproject/migration_github.md`

план на міграцію з бітбакет на гітхаб:

1. Get githab account
2. Fix dry-run migration issues:
- long paths in `ui-mono-repo`
- to decrease number of branches to 100 (remove feature/bugfix/tests branches)

3. Do Migration
4. Replace bitbucket URL - `env.LS_BITBUCKET_URL`
5. Replace `omc-bitbucket-ssh` and `omc-bitbucket` credentials in Jenkinsfile where pushing of failed auto tests screenshots is happening.
   Questions:
   a) github provides ssh access?
   b) autotests don't push screenshots right now, functionality is not working, rihgt?
6. In buildinfra module in `settings.xml` we use user `omc_bitbucket`/`${bitbucket_password}` to connect to maven repository.
   Questions:
   a) will we have new user?
7. Create webhook in github so that build start automatically after changes
8. In projects in IDE to execute:
`git remote set-url origin https://github.com/mycompany/my-repo.git`

---

## `src/main/kotlin/knowyourproject/reactive/reactive.md`

doOnNext - executes for every stream item for side effects
next - returns first element from stream (returns Mono)
doOnComplete - executes after stream completion
doOnError - We intercept exception here, logging it and return as a response
onErrorContinue - continues processing of users, triggers doOnNext
onErrorResume - returns fallback for failed user, returns good users + this fallback user
onErrorReturn - stops handling of items in flux and just gives the fallback for the errored item
.log() - logs everything. can be adjusted by custom category, log level, place where to use logs etc.
.doOnEach() - just more flexible than log. Though required code writing for signal types
doOnSubscribe / doOnCancel
checkpoint() - helps to determine where error occurred. close to what operator
contextWrite + deferContextual

Mono.fromCallable (fromRunnable, fromSupplier etc.) is used to integrate blocking code inside reactive pipeline.
It's Cold subsciption (executed just when is subscribbed)
Mono.defer - not execution of Mono.fromCallable labda happens lazily but even creation of the publisher happens lazily,
could be used to be able to make choices, to avoid caching with repeatable subsciptions, to avoid sideeffects until 
actual execution

.map - transforms items. takes and returns elements, not publishers. Can be executed in different threads
though synchronously.

.flatmap - transforms element and returns publisher. Also does flatten to avoid having result like 
this - Flux<Mono<UserRest>>

.publishOn(Scheduler)   

.subscribeOn(Scheduler)

limitRate = upstream backpressure control, chunk by chunk

next to check:
- map vs flatmap. - map to use when you just transform value. from non-publisher to non-publisher. 
If map returns publisher then result for publisher chain will be Mono<Mono<String>>. We should flatten. 
so use flatMap
- cold vs hot subscription. - Cold subscription to use when we need execution of publisher per subscriber.
In case of hot subscription Producer executes once and pushes result to first coming subscriber
and subscribers are fetching it when they need, when they are ready etc. 
- streaming - is a multiple values over time. can be hot and cold (Flux.fromIterable(fileLines))
- sink - it's hot publishing with delay. with async execution involved. 
it's hot subscriber meaning execution happens just once and independently on subscribers. Sink "pushes"
something and subscribers get it when they are ready. Often used in callback scenarios


seq 1 200 | xargs -n1 -P 50 curl -s http://localhost:8081/users

curl -s http://localhost:8081/users

seq 1 100000 | xargs -n1 -P 2000 curl -s -X POST http://localhost:8081/users   -H "Content-Type: application/json"   -d '{"firstName":"testuser$1","lastName":"testuser$1","email":"testuser$1@example.com","password":"password123"}'   > /dev/null 2>&1


---

## `SystemDesign/HELP.md`

docker-compose up --build

curl http://localhost:8080/hello


[//]: # (    sudo apt-get install apache2-utils)
# 100 requests, concurrency 20
ab -n 100 -c 20 http://localhost:8080/hello


Change upstream spring_backend strategy to least_conn; or ip_hash; and restart NGINX:
docker-compose restart nginx

