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
