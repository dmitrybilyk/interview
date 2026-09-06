# Secrets Manager & KMS — Secrets and Encryption

---

## Secrets Manager
Stores and rotates secrets (DB passwords, API keys, OAuth tokens).
Never put secrets in env vars, code, or config files in version control.

### How it works
```
App → SDK → Secrets Manager → returns JSON secret value
```
Secret is cached in memory by the SDK (avoid calling on every request). TTL-based cache.

### Java
```java
SecretsManagerClient sm = SecretsManagerClient.create();
String secretJson = sm.getSecretValue(r -> r.secretId("prod/myapp/db")).secretString();
// parse JSON: {"username":"admin","password":"s3cr3t"}

// Spring Cloud AWS — auto-inject into @Value or application.properties:
# bootstrap.yml
spring.config.import: aws-secretsmanager:/prod/myapp/db
# Then @Value("${username}") works
```

### Automatic rotation
- Lambda function rotates the secret on schedule (every 30/90 days).
- AWS provides managed rotation Lambdas for RDS (MySQL, PostgreSQL, Oracle).
- After rotation, apps using cached SDK get new value on next cache expiry.

### Pricing
$0.40/secret/month + $0.05 per 10 000 API calls.

---

## Parameter Store (SSM)
Lighter alternative to Secrets Manager. Good for non-sensitive config + some secrets.

| | Secrets Manager | Parameter Store |
|---|---|---|
| Cost | $0.40/secret/month | Free (standard tier), $0.05/advanced param |
| Secret rotation | Built-in | Manual Lambda |
| Encryption | Always KMS-encrypted | Optional (SecureString = KMS) |
| Use for | Passwords, API keys | Feature flags, config values, some secrets |

```java
// Spring Cloud AWS
spring.config.import: aws-parameterstore:/config/myapp/
# parameters named /config/myapp/db.url → @Value("${db.url}")
```

---

## KMS — Key Management Service
Manages encryption keys. You never see the raw key material — you call KMS to encrypt/decrypt.

### Key concepts
| Concept | Meaning |
|---|---|
| **CMK** (Customer Managed Key) | Your key, you control policy, rotation, deletion. $1/month + $0.03 per 10K API calls. |
| **AWS Managed Key** | AWS creates and rotates it for you (e.g. `aws/s3`, `aws/rds`). Free. Less control. |
| **Envelope encryption** | KMS generates a **data key** → you encrypt your data with the data key → store encrypted data + encrypted data key. KMS never sees the plaintext data. |
| **Key policy** | JSON doc controlling who can use/manage the key. |

### Envelope encryption flow (SDK)
```java
KmsClient kms = KmsClient.create();

// Encrypt
GenerateDataKeyResponse dk = kms.generateDataKey(r -> r.keyId("alias/mykey").keySpec(DataKeySpec.AES_256));
byte[] plaintextKey = dk.plaintext().asByteArray();
byte[] encryptedKey = dk.ciphertextBlob().asByteArray();
// use plaintextKey to encrypt data locally, then discard it
// store encryptedKey + ciphertext

// Decrypt
byte[] plaintextKey2 = kms.decrypt(r -> r.ciphertextBlob(SdkBytes.fromByteArray(encryptedKey)))
    .plaintext().asByteArray();
// use to decrypt data, then discard
```

In practice: AWS services (S3, RDS, EBS, SQS, DynamoDB, Secrets Manager) call KMS transparently — just enable encryption and specify key.

### Key rotation
Enable automatic rotation: AWS rotates key material annually. Existing data stays decryptable (old material kept).

---

## Cognito — User Identity
Managed user directory + OAuth2/OIDC provider.
- **User Pools** — sign up/in, MFA, social federation (Google, Facebook, Apple). Issues JWT (id_token, access_token).
- **Identity Pools** — exchange any identity (Cognito User Pool, social, SAML) for temporary AWS credentials (STS). Then call AWS services directly from the app.

```
Mobile App → Cognito User Pool → JWT → API Gateway (validates JWT) → Lambda
Mobile App → Cognito Identity Pool → AWS creds → call S3/DynamoDB directly
```

---

## Interview points
- Secrets Manager vs env vars: env vars are plaintext in task definitions, logs, debug output — secrets aren't.
- KMS key deletion: 7–30 day waiting period — cannot be undone, all data encrypted with it becomes unrecoverable.
- Cognito User Pool ≠ Identity Pool: User Pool = who you are (authentication); Identity Pool = what AWS resources you can access (authorization via STS).
- Spring Cloud AWS SecretsManager: populates `Environment` at startup — zero boilerplate, secrets refresh on restart.
