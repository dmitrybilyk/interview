# VPC — Virtual Private Cloud

## What it is
Your private, isolated network inside AWS. You define the IP space, subnets, routing, and firewall rules.
Every account gets a default VPC per region (ready to use, but create a custom one for production).

## Core building blocks
```
VPC (10.0.0.0/16)
├── Public Subnet (10.0.1.0/24)  — has route to Internet Gateway
│   └── EC2, ALB, NAT Gateway
└── Private Subnet (10.0.2.0/24) — no direct internet
    └── RDS, ElastiCache, App servers
```

| Resource | Role |
|---|---|
| **CIDR block** | IP range for VPC (e.g. `10.0.0.0/16` = 65 536 IPs). Plan carefully — can't shrink. |
| **Subnet** | Subdivision of VPC, tied to one AZ. Public or private. |
| **Internet Gateway (IGW)** | Connects VPC to the public internet. One per VPC. Required for public subnets. |
| **NAT Gateway** | Lets private subnet instances reach internet outbound (OS updates, external APIs). Managed, costs per GB. |
| **Route Table** | Rules mapping destination CIDR → target (IGW, NAT GW, peering). Each subnet has one. |
| **Security Group (SG)** | Stateful firewall at the **instance** level. Rules are "allow" only — no deny. Return traffic auto-allowed. |
| **NACL** | Stateless firewall at the **subnet** level. Explicit allow AND deny. Rules processed in order by number. |

## Security Group vs NACL
| | Security Group | NACL |
|---|---|---|
| Level | Instance / ENI | Subnet |
| State | Stateful (return traffic auto-allowed) | Stateless (must allow inbound AND outbound explicitly) |
| Rules | Allow only | Allow and Deny |
| Default | Deny all in, allow all out | Allow all in and out |
| Use for | Fine-grained per-instance control | Broad subnet-level guardrails |

## VPC Connectivity
- **VPC Peering** — connect two VPCs (same or different account/region). No transitive routing.
- **Transit Gateway** — hub-and-spoke model connecting many VPCs and on-prem. Solves VPC peering at scale.
- **VPC Endpoints** — access AWS services (S3, DynamoDB, SQS) privately without leaving the AWS network.
  - **Gateway endpoint** (S3, DynamoDB) — free, added to route table.
  - **Interface endpoint** (everything else) — ENI in your subnet, costs per hour + per GB.
- **Direct Connect** — dedicated private fiber from on-prem to AWS (not internet).
- **Site-to-Site VPN** — encrypted tunnel over internet from on-prem to VPC.

## Subnet sizing tip
AWS reserves 5 IPs per subnet (first 4 + last 1). `/24` = 256 IPs, 251 usable.

## DNS
- `enableDnsHostnames` + `enableDnsSupport` — enable so EC2 instances get public DNS names.
- Route 53 private hosted zone — internal DNS resolution within VPC.

## Java developer relevance
- RDS, ElastiCache must be in a private subnet — Lambda or ECS in the same VPC accesses them.
- Lambda in VPC: needs a subnet with NAT Gateway to reach the internet or other AWS services (unless using VPC endpoints).
- Spring Boot → RDS: SG rule must allow TCP 5432 (PostgreSQL) from the app's SG.

## Interview points
- 0.0.0.0/0 in route table pointing to IGW = public subnet. Without that route = private subnet.
- NAT Gateway is per-AZ — deploy one per AZ for high availability.
- Security Groups are stateful: if you allow inbound 443, the response traffic on ephemeral port is automatically allowed out.
- NACLs are the last line of defence — use SGs for day-to-day control.
