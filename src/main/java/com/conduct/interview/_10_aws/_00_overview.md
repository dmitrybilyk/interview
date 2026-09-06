# AWS Overview

## What is AWS
Amazon Web Services — cloud platform offering 200+ services (compute, storage, networking, AI, etc.)
on a pay-as-you-go model. You rent infrastructure instead of owning it.

## Global Infrastructure
| Concept | What it is |
|---|---|
| **Region** | Geographic cluster of data centers (e.g. `eu-west-1` = Ireland). Services are scoped to a region. Choose based on latency + compliance. |
| **Availability Zone (AZ)** | One or more isolated data centers inside a region. Each region has ≥ 2 AZs. Spread your resources across AZs for high availability. |
| **Edge Location** | CloudFront CDN point-of-presence — caches content close to users. 400+ worldwide, more than regions. |

## Shared Responsibility Model
- **AWS** secures *of* the cloud: physical hardware, networking, hypervisor, managed service software.
- **You** secure *in* the cloud: OS patches, app code, IAM config, data encryption, firewall rules.

## Key Service Categories
| Category | Key services |
|---|---|
| Compute | EC2, Lambda, ECS, EKS, Elastic Beanstalk |
| Storage | S3, EBS, EFS, Glacier |
| Database | RDS, Aurora, DynamoDB, ElastiCache, Redshift |
| Messaging | SQS, SNS, EventBridge, Kinesis |
| Networking | VPC, Route 53, CloudFront, ALB/NLB, API Gateway |
| Security | IAM, Secrets Manager, KMS, Cognito, WAF |
| Observability | CloudWatch, X-Ray, CloudTrail |
| DevOps | CodePipeline, CodeBuild, CodeDeploy, CloudFormation, CDK |

## Pricing
- Pay-as-you-go: charged by second/hour/request/GB.
- **On-demand** — full price, no commitment.
- **Reserved** — 1 or 3 year commitment, up to 75% cheaper.
- **Spot** — spare capacity, up to 90% cheaper, can be interrupted.
- **Savings Plans** — flexible commitment to $/hour usage level.

## Interview one-liners
- AZ ≠ data center, but one AZ can contain multiple data centers physically close together.
- Every AWS API call is signed with SigV4 (HMAC-SHA256) — no plaintext credentials on the wire.
- Default limit: most services have soft limits (request increase via Support).
