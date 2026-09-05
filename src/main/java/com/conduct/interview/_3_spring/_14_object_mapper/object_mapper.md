# ObjectMapper (Jackson)

`ObjectMapper` is Jackson's core class - the translator between Java
objects and JSON. It's a Jackson concept, not a Spring one; Spring Boot
just auto-configures a shared instance and wires it into
`MappingJackson2HttpMessageConverter` so `@RequestBody`/`@ResponseBody`
work without you touching Jackson directly.

- **Serialization**: Java object -> JSON string (`writeValueAsString`).
- **Deserialization**: JSON string -> Java object (`readValue`).
- **Data binding**: Jackson matches JSON keys to Java fields/getters by
  naming convention (`userName` <-> `"userName"`), using reflection to
  find the accessors - a `PropertyNamingStrategy` (e.g. `SNAKE_CASE`)
  changes the naming convention it matches against, without touching
  your Java field names at all.

## Run it

`ObjectMapperDemo` wires an `ObjectMapper` as a plain bean via XML with
`SNAKE_CASE` naming, then serializes/deserializes a `User` record whose
Java fields are camelCase while the JSON uses `snake_case`.
