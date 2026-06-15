# A10:2021 – Server-Side Request Forgery (SSRF)

---

## The moment it happens

Your app has a "link preview" feature. A user pastes a URL and the server fetches it
to show a thumbnail and title. The code:

```java
@PostMapping("/preview")
public String preview(@RequestBody String url) {
    return httpClient.get(url).body();  // server fetches whatever URL the user sends
}
```

A normal user sends `https://example.com/article` and gets a preview. Fine.

An attacker sends this instead:
```json
"http://169.254.169.254/latest/meta-data/iam/security-credentials/prod-role"
```

`169.254.169.254` is the AWS Instance Metadata Service — a special IP that every EC2
instance can reach, but nobody on the internet can reach directly.

Your server makes the request. From your server's perspective it's just an HTTP call.
AWS responds with:

```json
{
  "AccessKeyId": "ASIAXXX",
  "SecretAccessKey": "wJalrXUtnFEMI...",
  "Token": "AQoDYXdzEJr..."
}
```

Your server returns this to the attacker as a "preview."

The attacker now has valid AWS credentials. They use them to list your S3 buckets,
read your RDS snapshots, access your secrets from Parameter Store.

**The moment**: when your server made the HTTP request on behalf of the attacker.
The attacker couldn't reach `169.254.169.254` from their laptop — it's an internal
AWS address. But your server can. You became their proxy.

---

## Why blocklisting doesn't work

A naive fix blocks `169.254.169.254`. The attacker tries:
```
http://169.254.169.254.evil.com/  → resolves to 169.254.169.254 (DNS rebinding)
http://0251.0376.0251.0376/       → octal representation of the same IP
http://2852039166/                → decimal representation of the same IP
http://[::ffff:169.254.169.254]/  → IPv6 mapped address
```

Each of these bypasses a hostname check. You cannot enumerate all tricks.

---

## Fix: allowlist, not blocklist

Only allow URLs that you explicitly approve:

```java
private static final Set<String> ALLOWED_HOSTS = Set.of(
    "ogp.example-cdn.com",
    "api.trusted-partner.com"
);

public void validateUrl(String rawUrl) {
    URI uri = URI.create(rawUrl);

    if (!ALLOWED_HOSTS.contains(uri.getHost())) {
        throw new SecurityException("Host not allowed");
    }
    if (!"https".equals(uri.getScheme())) {
        throw new SecurityException("Only HTTPS allowed");
    }
}
```

If a URL isn't on your explicit list of known-good hosts, the server refuses to fetch it.
An attacker can't bypass this by encoding the IP differently — `169.254.169.254` is
simply not in `ALLOWED_HOSTS`, in any representation.

---

## Second fix: validate the resolved IP too

Even an allowed hostname could theoretically be hijacked via DNS to resolve to an internal IP.
After DNS resolution, check what IP you got:

```java
InetAddress address = InetAddress.getByName(uri.getHost());
if (address.isLoopbackAddress()    // 127.x.x.x
 || address.isSiteLocalAddress()   // 10.x, 172.16-31.x, 192.168.x
 || address.isLinkLocalAddress()   // 169.254.x.x
 || address.isAnyLocalAddress()) {
    throw new SecurityException("Resolved to a private IP — request blocked");
}
```

Even if DNS returned a private IP for a trusted hostname, the request never goes out.

---

## On AWS: enforce IMDSv2

IMDSv2 requires a PUT request to get a session token before any metadata can be read.
Your vulnerable app only makes GET requests — so even if SSRF reaches the metadata IP,
it gets nothing.

```bash
aws ec2 modify-instance-metadata-options \
  --instance-id i-xxxx \
  --http-tokens required
```

Defence in depth: even if your URL validation has a gap, the AWS layer blocks the credential theft.
