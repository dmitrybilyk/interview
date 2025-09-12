CSRF is an attack where a malicious website tricks a user’s browser (already authenticated with your site) into making 
an unwanted state-changing request (like POST, PUT, DELETE) to your application without the user’s knowledge.

Example:

You’re logged into your banking app.

Another site you visit silently sends a POST to your bank’s /transfer endpoint using your cookies.

Without CSRF protection, the transfer could go through.


How Spring Security protects

It generates a CSRF token per session and expects it on any state-changing request.

If the token is missing or invalid, the request is rejected.

This ensures a malicious site can’t forge a valid state-changing request.