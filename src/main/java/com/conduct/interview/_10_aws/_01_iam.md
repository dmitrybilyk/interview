# IAM — Identity & Access Management

## What it does
Controls **who** (authentication) can do **what** (authorization) on which AWS resources.
Global service — not region-specific.

## Core entities
| Entity | Description |
|---|---|
| **User** | Person or application with long-term credentials (access key + secret). Avoid for apps — use Roles instead. |
| **Group** | Collection of users. Attach policies to the group. |
| **Role** | Temporary credentials assumed by a service, EC2, Lambda, or another account. No long-term key. |
| **Policy** | JSON document defining permissions. Attached to user/group/role. |

## Policy structure
```json
{
  "Effect": "Allow",          // Allow | Deny
  "Action": "s3:GetObject",   // service:action, wildcards ok (s3:*)
  "Resource": "arn:aws:s3:::my-bucket/*"  // specific resource ARN
}
```
- **Explicit Deny** always wins over Allow.
- Identity-based policy (on principal) vs Resource-based policy (on resource, e.g. S3 bucket policy).

## Role assumption (STS)
```
App / Service → sts:AssumeRole → temporary credentials (15 min – 12 h)
```
- EC2 gets role via **Instance Profile** — SDK picks it up automatically from metadata endpoint.
- Lambda, ECS task, etc. each have their own execution role — no manual credential management.

## Least privilege principle
Grant the minimum permissions needed. Start with deny-all, add only what's required.

## Java SDK credential chain (in order)
1. Env vars `AWS_ACCESS_KEY_ID` / `AWS_SECRET_ACCESS_KEY`
2. System properties `aws.accessKeyId` / `aws.secretAccessKey`
3. `~/.aws/credentials` file
4. ECS container credentials
5. EC2 / Lambda instance metadata role ← what you use in production

## Interview points
- Never embed access keys in code or Docker images — use roles.
- `arn:aws:iam::123456789012:role/MyRole` — ARN uniquely identifies any AWS resource.
- Permission boundary: caps maximum permissions a role/user can ever have.
- Service control policies (SCPs) in AWS Organizations: org-wide guardrails.
