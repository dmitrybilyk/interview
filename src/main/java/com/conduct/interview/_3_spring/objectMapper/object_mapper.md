ObjectMapper is the flagship class of the Jackson library. 
In a Spring Boot ecosystem, it acts as the primary "translator" 
between Java objects and JSON data, enabling seamless communication 
between your server and its clients.

Core Meaning & Concepts
Serialization (Writing): The process of converting a Java Object into a JSON string. 
This is what happens when your @RestController sends a response to a client.

Deserialization (Reading): The process of converting a JSON string back into a Java Object. 
This occurs when you receive data via @RequestBody.

Data Binding: The automatic mapping of JSON keys to Java fields. 
Jackson uses reflection to match "user_name" in JSON to userName in Java.