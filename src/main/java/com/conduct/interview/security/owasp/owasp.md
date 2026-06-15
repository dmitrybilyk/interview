# Brief Theory: OWASP & Top Security Issues

A high-level, scannable summary of the core concepts you need for your checklist.

---

## 1. What is OWASP?

**OWASP** (Open Worldwide Application Security Project) is a global non-profit
organization dedicated to improving software security.

* **The Mission:** They gather security professionals worldwide to track and
  analyze real-world security breaches.
* **The "Top 10":** Regularly updated, this document functions as the global
  standard for the most critical security vulnerabilities threatening modern web
  applications today.

---

## 2. Quick Overview of Top Vulnerabilities

Here is a brief breakdown of the most critical security issues you must protect
your application from:

* **A01:2021 – Broken Access Control:** Users can access resources or execute
  commands outside of their intended permissions (e.g., viewing another user's
  private data by modifying an ID in a URL).
* **A02:2021 – Cryptographic Failures:** Sensitive data (like user passwords or
  credit card numbers) is exposed because it is transmitted in plain text or
  stored using weak, outdated hashing algorithms.
* **A03:2021 – Injection:** Untrusted user input is mistakenly interpreted as
  executable system code.
    * *SQL Injection (SQLi):* Modifies backend database queries.
    * *Cross-Site Scripting (XSS):* Forces malicious JavaScript to execute inside
      a victim's browser.
* **A05:2021 – Security Misconfiguration:** Applications or servers are left vulnerable
  due to unhardened configurations (e.g., keeping default passwords active or
  showing raw backend error logs and stack traces to end-users).
* **A06:2021 – Vulnerable and Outdated Components:** Your application includes
  third-party open-source libraries (managed via tools like Gradle) that contain
  known, unpatched security flaws.

---
