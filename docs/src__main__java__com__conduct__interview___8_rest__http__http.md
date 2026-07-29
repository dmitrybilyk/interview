# Hypertext Transfer Protocol (HTTP)

**HTTP** (Hypertext Transfer Protocol) is a protocol, or a set of standards
and rules, that defines how data is transmitted over the web. It specifies
how **requests** sent by clients (e.g., browsers) are structured, and how
**responses** from servers are returned.

HTTP is **stateless**, meaning each request should contain all the
information required for the server to process it. The server does not
retain any state information about previous requests unless mechanisms
like cookies or sessions are used.

## HTTP Methods

Common HTTP methods include:

- **GET**  
  Used to retrieve data from the server. It is idempotent, meaning repeated
  GET requests have the same result. Often used for reading resources (e.g.,
  getting a webpage or data).

- **POST**  
  Used to send data to the server. Commonly used to submit form data or
  create new resources. Unlike GET, POST is not idempotent, meaning the
  result can change if the same request is repeated.

- **PUT**  
  Used to update or replace an existing resource on the server. PUT is also
  idempotent, meaning repeated requests with the same data will not change
  the result further.

- **DELETE**  
  Used to remove a resource from the server. Like PUT, DELETE is idempotent,
  meaning repeated DELETE requests for the same resource will result in the
  same outcome (the resource remains deleted).

## HTTP Status Codes

Servers respond with HTTP status codes to indicate the result of the
request:

- **200 OK**: The request was successful.
- **404 Not Found**: The requested resource could not be found.
- **500 Internal Server Error**: The server encountered an error.
- **302 Found**: The requested resource has been temporarily moved.

Request:
POST /submit-form HTTP/1.1
Host: www.example.com
User-Agent: Mozilla/5.0
Content-Type: application/x-www-form-urlencoded
Content-Length: 29

name=JohnDoe&age=30

Response:
HTTP/1.1 200 OK
Date: Mon, 16 Sep 2024 10:00:00 GMT
Content-Type: text/html
Content-Length: 1024

<html>
  <head><title>Example</title></head>
  <body><h1>Hello World!</h1></body>
</html>
