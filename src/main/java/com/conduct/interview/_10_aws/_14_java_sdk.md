# AWS SDK for Java v2 & Spring Cloud AWS

---

## AWS SDK v2 (software.amazon.awssdk)
Current SDK. Rewritten from scratch: non-blocking async, immutable requests, builder pattern throughout.

### Dependency (Maven)
```xml
<dependencyManagement>
  <dependency>
    <groupId>software.amazon.awssdk</groupId>
    <artifactId>bom</artifactId>
    <version>2.26.0</version>
    <type>pom</type>
    <scope>import</scope>
  </dependency>
</dependencyManagement>
<dependency><groupId>software.amazon.awssdk</groupId><artifactId>s3</artifactId></dependency>
<dependency><groupId>software.amazon.awssdk</groupId><artifactId>sqs</artifactId></dependency>
<dependency><groupId>software.amazon.awssdk</groupId><artifactId>dynamodb-enhanced</artifactId></dependency>
```

### Client creation
```java
// Sync client (blocking) — fine for most Spring Boot apps
S3Client s3 = S3Client.builder()
    .region(Region.EU_WEST_1)
    // credentials auto-discovered: env → system props → ~/.aws → container → EC2 metadata role
    .build();

// Async client (non-blocking, returns CompletableFuture)
S3AsyncClient s3Async = S3AsyncClient.builder().region(Region.EU_WEST_1).build();
CompletableFuture<GetObjectResponse> future = s3Async.getObject(..., AsyncResponseTransformer.toBytes());
```

### Credential provider chain (auto-discovery order)
1. `AWS_ACCESS_KEY_ID` / `AWS_SECRET_ACCESS_KEY` env vars
2. Java system properties `aws.accessKeyId` / `aws.secretAccessKey`
3. `~/.aws/credentials` file (profiles)
4. ECS container credentials (via `AWS_CONTAINER_CREDENTIALS_RELATIVE_URI`)
5. EC2 / ECS / Lambda instance metadata role ← production

**Never hardcode credentials.** Role-based auth is zero config on EC2/Lambda/ECS.

### Common clients quick reference
```java
SqsClient sqs = SqsClient.create();                          // SQS
DynamoDbClient ddb = DynamoDbClient.create();                // DynamoDB low-level
DynamoDbEnhancedClient ddbE = DynamoDbEnhancedClient.builder().dynamoDbClient(ddb).build(); // Enhanced
SecretsManagerClient sm = SecretsManagerClient.create();     // Secrets Manager
SsmClient ssm = SsmClient.create();                          // Parameter Store
KmsClient kms = KmsClient.create();                          // KMS
CloudWatchClient cw = CloudWatchClient.create();             // CloudWatch
SnsClient sns = SnsClient.create();                          // SNS
LambdaClient lambda = LambdaClient.create();                 // invoke Lambda
```
All clients are thread-safe and expensive to create — make them beans (`@Bean`) and reuse.

---

## Spring Cloud AWS (io.awspring.cloud)
Spring Boot auto-configuration for AWS services. Eliminates boilerplate.

```xml
<dependencyManagement>
  <dependency>
    <groupId>io.awspring.cloud</groupId>
    <artifactId>spring-cloud-aws-dependencies</artifactId>
    <version>3.2.0</version>
    <type>pom</type>
    <scope>import</scope>
  </dependency>
</dependencyManagement>
```

### SQS listener
```java
@SqsListener("my-queue-name")               // auto polls, deserializes JSON, acks on success
public void onMessage(OrderPlaced event) {
    // process
}
// Partial batch failure:
@SqsListener(value = "my-queue", acknowledgementMode = SqsListenerAcknowledgementMode.MANUAL)
public void onBatch(List<Message<OrderPlaced>> messages, Acknowledgement ack) { ... }
```

### S3 with ResourceLoader
```java
@Value("s3://my-bucket/data.json")
private Resource s3Resource;

String content = StreamUtils.copyToString(s3Resource.getInputStream(), StandardCharsets.UTF_8);
```

### Secrets & config
```yaml
spring:
  config:
    import:
      - aws-secretsmanager:/prod/myapp/db     # injects username, password as properties
      - aws-parameterstore:/config/myapp/      # injects all params under prefix
```
```java
@Value("${username}") private String dbUser;   // from Secrets Manager
@Value("${feature.flag}") private boolean flag; // from Parameter Store
```

---

## Lambda Java — patterns

### Cold start minimization
```java
// Static init runs once (warm between invocations):
private static final DynamoDbClient DDB = DynamoDbClient.create();
private static final ObjectMapper MAPPER = new ObjectMapper();

public class MyHandler implements RequestHandler<SQSEvent, Void> {
    public Void handleRequest(SQSEvent event, Context ctx) {
        for (SQSMessage msg : event.getRecords()) { process(msg); }
        return null;
    }
}
```
- Use `aws-lambda-java-events` for typed event classes (SQSEvent, S3Event, APIGatewayProxyRequestEvent...).
- Minimize classpath: no Spring Boot unless using SnapStart or Quarkus/Micronaut native.
- **SnapStart**: in `template.yaml` set `SnapStart: ApplyOn: PublishedVersions`. Deploy a version → Lambda snapshots.

### GraalVM native (Quarkus / Micronaut)
Compile to native binary — near-zero cold start (10–50 ms). Ideal for Lambda.
Trade-off: longer build time, reflection config required for AWS SDK.

---

## Interview points
- SDK v1 (`com.amazonaws`) vs v2 (`software.amazon.awssdk`): v2 is current. v1 is maintenance-mode — don't start new projects with it.
- Clients are thread-safe — one instance per service, shared across threads.
- SDK automatically retries transient errors (throttling, 5xx) with exponential backoff. Don't implement your own.
- `SdkClientException` = networking/SDK problem. `AwsServiceException` = service returned an error. Both are unchecked.
- Spring Cloud AWS 3.x uses SDK v2 under the hood and Spring Boot 3 / Spring 6.
