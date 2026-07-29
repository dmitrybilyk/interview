Spring Boot is the framework built on top of the Spring itself.
It consists of many configurations - auto configurations.
Spring boot has dependency - autoconfigure. 
Inside of it in META-INF folder there is a 
`spring.factories` file with the list of enabled auto configurations.

Those are configurations which provide beans related to 
particular third party library where beans are created based on some
conditions. Those conditions could be related to existence of some
property in application.properties or some class in class path etc.

Starter is a separate spring module which has it's own file
`spring-factories` with the path to it's Configuration (Auto Configuration).
