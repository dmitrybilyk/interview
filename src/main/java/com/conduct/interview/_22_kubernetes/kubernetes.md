# Kubernetes

## What is it?

Container orchestration platform. Automates deployment, scaling, self-healing, and networking of containers.
You describe the desired state; Kubernetes continuously reconciles actual state toward it.

```
kubectl apply -f deployment.yaml  →  "I want 3 replicas of this image"
Kubernetes:  creates pods, restarts crashed ones, replaces unhealthy ones, rolls out updates
```

---

## Core Objects

### Pod
Smallest deployable unit. One or more containers sharing network + storage.
Pods are ephemeral — don't rely on a pod's IP or existence. Use Services.

### Deployment
Manages a set of identical pods. Handles rolling updates and rollbacks.

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: order-service
spec:
  replicas: 3
  selector:
    matchLabels: { app: order-service }
  template:
    metadata:
      labels: { app: order-service }
    spec:
      containers:
        - name: order-service
          image: myregistry/order-service:abc123   # always use SHA, not latest
          ports: [{ containerPort: 8080 }]
          resources:
            requests: { cpu: "250m", memory: "256Mi" }  # scheduler uses this
            limits:   { cpu: "500m", memory: "512Mi" }  # enforced at runtime
          livenessProbe:
            httpGet: { path: /actuator/health/liveness, port: 8080 }
          readinessProbe:
            httpGet: { path: /actuator/health/readiness, port: 8080 }
```

### Service
Stable DNS name + IP for a set of pods (selected by label). Pods behind it can change.

| Type | Use |
|---|---|
| `ClusterIP` (default) | Internal only — service-to-service |
| `NodePort` | Expose on node's port — dev/testing |
| `LoadBalancer` | Cloud LB — expose to internet |

```yaml
kind: Service
spec:
  selector: { app: order-service }
  ports: [{ port: 80, targetPort: 8080 }]
```

### Ingress
HTTP/HTTPS routing at Layer 7. Routes by host/path to Services. Replaces many LoadBalancers.

```yaml
kind: Ingress
spec:
  rules:
    - host: api.myapp.com
      http:
        paths:
          - path: /orders
            backend:
              service: { name: order-service, port: { number: 80 } }
```

### ConfigMap & Secret
Externalise config from the image. ConfigMap = plain text. Secret = base64 (use Vault/KMS in prod).

```yaml
envFrom:
  - configMapRef: { name: app-config }
  - secretRef:    { name: app-secrets }
```

### HPA — Horizontal Pod Autoscaler
Scale replicas automatically based on CPU/memory or custom metrics.

```yaml
kind: HorizontalPodAutoscaler
spec:
  scaleTargetRef: { kind: Deployment, name: order-service }
  minReplicas: 2
  maxReplicas: 10
  metrics:
    - type: Resource
      resource: { name: cpu, target: { type: Utilization, averageUtilization: 70 } }
```

---

## Resource Requests vs Limits

**Requests** — what the pod is guaranteed (scheduler places pod on node with enough free).
**Limits** — max it can use (CPU throttled, memory → OOMKilled if exceeded).

Always set both. Without requests: pod lands on overloaded node. Without limits: one pod starves others.

---

## Liveness vs Readiness vs Startup Probes

| Probe | Failure action | Use |
|---|---|---|
| **Liveness** | Restart the container | App is deadlocked, unrecoverable |
| **Readiness** | Remove from Service endpoints | App booting, or temporarily overloaded |
| **Startup** | Keep checking before liveness kicks in | Slow-starting apps (don't kill too early) |

**Critical**: never put DB health in liveness. One DB blip → all pods restart simultaneously → outage.

---

## Rolling Update Strategy

```yaml
strategy:
  type: RollingUpdate
  rollingUpdate:
    maxSurge: 1        # create 1 extra pod before terminating old
    maxUnavailable: 0  # never go below desired replica count
```

Zero-downtime only if: readiness probe works correctly, DB migrations are backward-compatible.

---

## Namespaces

Logical isolation within a cluster. `default`, `kube-system`, or your own (`dev`, `staging`, `prod`).
RBAC, NetworkPolicies, ResourceQuotas applied per namespace.

---

## kubectl Cheatsheet

```bash
kubectl get pods -n prod                          # list pods
kubectl describe pod <name>                       # events, state, probes
kubectl logs <pod> -f --previous                  # logs, follow, previous crashed container
kubectl exec -it <pod> -- /bin/sh                 # shell into container
kubectl rollout status deployment/order-service   # watch rollout
kubectl rollout undo deployment/order-service     # rollback
kubectl top pods                                  # CPU/memory usage (needs metrics-server)
kubectl scale deployment order-service --replicas=5
```

---

## Interview Points

- Deployment ≠ Pod. Deployment manages pods declaratively. Never create pods directly in prod.
- `ClusterIP` for internal traffic. `Ingress` for external HTTP. `LoadBalancer` creates a cloud LB per service — expensive.
- Requests/limits are mandatory in production — without them HPA doesn't work and pods get evicted.
- Readiness probe failing = pod stays up but gets no traffic. Liveness failing = pod restarts.
- ConfigMaps for config, Secrets for credentials — but Secrets are only base64, not encrypted. Use external secrets operator + Vault/AWS Secrets Manager for real security.
- StatefulSets for stateful apps (DBs, Kafka) — stable network identity, ordered start/stop, persistent volumes.
- PodDisruptionBudget: guarantees minimum available pods during voluntary disruptions (node drain, rolling update).
