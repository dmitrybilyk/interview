# RDS & Aurora — Managed Relational Databases

## RDS — Relational Database Service
Managed service: AWS handles OS patching, backups, failover.
You still write SQL and manage schema.

**Supported engines:** MySQL, PostgreSQL, MariaDB, Oracle, SQL Server.

## Key RDS features
| Feature | What it does |
|---|---|
| **Multi-AZ** | Synchronous standby replica in another AZ. Auto-failover in ~1 min on primary failure. **HA, not read scaling** — standby can't serve reads. |
| **Read Replicas** | Asynchronous copies. Up to 5 per primary. Separate endpoint → scale reads. Can be in different regions (cross-region replica). |
| **Automated Backups** | Daily snapshot + transaction logs. Point-in-time restore up to 35 days. |
| **RDS Proxy** | Connection pool in front of RDS. Critical for Lambda — avoids opening thousands of DB connections on scale-out. |
| **Encryption** | At-rest (KMS) and in-transit (TLS). Enable at creation — can't add later without migration. |

## Aurora
AWS-built engine, MySQL and PostgreSQL compatible. Not a fork — rewritten storage layer.

**Why it's better than RDS MySQL/PostgreSQL:**
- 5× faster than MySQL, 3× faster than PostgreSQL (AWS claim).
- Storage auto-scales 10 GB → 128 TB in 10 GB increments — no provisioning needed.
- Up to 15 read replicas with < 10 ms replica lag.
- Failover to replica in < 30 seconds (vs ~1 min for RDS Multi-AZ).
- **Cluster volume** — 6 copies of data across 3 AZs built-in (2 copies per AZ).

**Aurora Serverless v2** — capacity scales by ACU (Aurora Capacity Unit) automatically.
Ideal for unpredictable workloads, dev/test. Can scale to 0 (v1 only; v2 scales to 0.5 ACU min).

**Aurora Global Database** — primary region + up to 5 read-only regions, < 1 s replication lag.

## Endpoints
| Endpoint | Use for |
|---|---|
| Cluster endpoint | Writes — always points to primary |
| Reader endpoint | Reads — load-balanced across all replicas |
| Instance endpoint | Direct to a specific instance (avoid in most cases) |

## Java integration
Standard JDBC — same driver as MySQL/PostgreSQL.
```java
// application.yml (Spring Boot)
spring:
  datasource:
    url: jdbc:postgresql://cluster.xxxx.eu-west-1.rds.amazonaws.com:5432/mydb
    username: ${DB_USER}
    password: ${DB_PASS}   # fetch from Secrets Manager via Spring Cloud AWS
  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
```

**IAM database authentication** — instead of password, generate short-lived auth token via SDK.
No password in config. Works for MySQL and PostgreSQL engines.

## Interview points
- Multi-AZ ≠ read scaling. Read replicas ≠ automatic failover (unless you promote manually or use Aurora).
- Aurora Global Database vs DynamoDB Global Tables: Aurora for complex SQL, DynamoDB for simple KV at scale.
- RDS Proxy is mandatory for Lambda → RDS (connection exhaustion otherwise).
- Choose Aurora over plain RDS for new projects — same SQL interface, better HA and performance.
