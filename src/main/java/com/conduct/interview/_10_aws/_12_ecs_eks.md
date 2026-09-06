# ECS, EKS & ECR — Containers on AWS

---

## ECR — Elastic Container Registry
Private Docker image registry in AWS.
```bash
aws ecr get-login-password | docker login --username AWS --password-stdin <account>.dkr.ecr.<region>.amazonaws.com
docker build -t my-app .
docker tag my-app:latest <account>.dkr.ecr.<region>.amazonaws.com/my-app:latest
docker push <account>.dkr.ecr.<region>.amazonaws.com/my-app:latest
```
Supports image scanning (CVE detection), lifecycle policies (auto-delete old images), cross-region replication.

---

## ECS — Elastic Container Service
AWS-native container orchestration. Simpler than Kubernetes, tightly integrated with AWS services.

### Key concepts
| Concept | What it is |
|---|---|
| **Task Definition** | Blueprint — Docker image, CPU/memory, env vars, port mappings, IAM role, log config. Versioned. |
| **Task** | One running instance of a Task Definition (= a container or group of containers). |
| **Service** | Keeps N tasks running. Integrates with ALB for traffic distribution. Replaces failed tasks. |
| **Cluster** | Logical group of tasks/services. Can contain EC2 instances or use Fargate. |

### Launch types
| | EC2 | Fargate |
|---|---|---|
| Hosts | You manage EC2 instances in cluster | AWS manages all infra |
| Cost | Cheaper for constant high load | Cheaper for variable/low load (pay per task CPU/memory) |
| Control | Full OS access, GPU support | No host access |
| Use for | Large steady workloads, GPU | Most services — simpler ops |

**Fargate** = serverless containers. Just define task CPU/memory and image — AWS handles everything else.

### Task Definition snippet (Java app)
```json
{
  "family": "checkout-service",
  "cpu": "512",
  "memory": "1024",
  "networkMode": "awsvpc",
  "requiresCompatibilities": ["FARGATE"],
  "executionRoleArn": "arn:aws:iam::...:role/ecsTaskExecutionRole",
  "taskRoleArn":      "arn:aws:iam::...:role/checkoutTaskRole",
  "containerDefinitions": [{
    "name": "checkout",
    "image": "<account>.dkr.ecr.eu-west-1.amazonaws.com/checkout:latest",
    "portMappings": [{"containerPort": 8080}],
    "environment": [{"name": "SPRING_PROFILES_ACTIVE", "value": "prod"}],
    "secrets": [{"name": "DB_PASS", "valueFrom": "arn:aws:secretsmanager:..."}],
    "logConfiguration": {
      "logDriver": "awslogs",
      "options": {"awslogs-group": "/ecs/checkout", "awslogs-region": "eu-west-1", "awslogs-stream-prefix": "ecs"}
    }
  }]
}
```

### ECS + ALB (standard pattern)
```
Internet → ALB → Target Group (ECS Service) → Fargate Tasks (port 8080)
```
Service auto-registers/deregisters tasks with ALB target group. Health checks control traffic.

### Auto Scaling
- Service auto scaling on CloudWatch metrics (CPU, memory, custom).
- Scale in/out by adjusting desired task count.

---

## EKS — Elastic Kubernetes Service
Managed Kubernetes control plane. You manage worker nodes (or use Fargate for EKS for serverless nodes).

**When to choose EKS over ECS:**
- Team already knows Kubernetes.
- Need Kubernetes ecosystem (Helm, Istio, ArgoCD, KEDA, etc.).
- Multi-cloud portability.
- Complex scheduling requirements.

**When ECS is fine:** AWS-only, team is small, ops simplicity matters.

### EKS Fargate
Define `FargateProfile` → pods matching selector run on serverless nodes. No EC2 node groups to manage.

---

## Interview points
- ECS Task role (app permissions) ≠ Execution role (ECS agent permissions to pull image, write logs).
- Secrets in ECS: reference Secrets Manager or Parameter Store ARN in `secrets` — value injected as env var at runtime. Never bake secrets into image.
- `awsvpc` network mode = each task gets its own ENI + Security Group. Required for Fargate.
- ECS Service rolling update: `minimumHealthyPercent=100, maximumPercent=200` = blue/green style zero-downtime.
- EKS control plane cost: ~$0.10/hour per cluster even with zero nodes.
