# Testing Spring Applications

Three levels, ordered fastest/most-isolated to slowest/most-realistic -
default to the fastest one that can actually catch the bug you care about:

1. **Plain unit test, no Spring at all**: `new OrderService(mockRepository)`
   with a Mockito mock, then assert on `OrderService`'s own logic. No
   context, no reflection, runs in milliseconds. This should be the vast
   majority of your tests - a class's own logic rarely needs a container
   to verify.
2. **A hand-picked slice of context**: `@ExtendWith(SpringExtension.class)`
   + `@ContextConfiguration(locations = "classpath:...xml")` (or
   `classes = {...}` for JavaConfig) loads *only* the beans you list -
   real DI wiring, real XML parsing, but none of Spring Boot's
   autoconfiguration, no embedded server, no real database unless you
   wire one yourself (as in [_5_transactions](../_5_transactions)'s H2
   setup). Good for "does my wiring/config actually work," without
   paying for the whole app.
3. **`@SpringBootTest`**: boots the *entire* application context,
   exactly like `SpringApplication.run` would - all autoconfiguration,
   all your `@Component`s, optionally a real embedded server
   (`webEnvironment = RANDOM_PORT`). Slowest, most realistic - reserve it
   for a handful of true end-to-end checks, not routine unit testing.

## Run it

`SliceContextTest` (under `src/test/.../_15_testing_spring_apps/`) uses
level 2: `testing-context.xml` wires a real `OrderService` against an
`InMemoryOrderRepository` bean - no mocks, but also no full Boot
context. `PlainUnitTestExample` next to it uses level 1: no Spring
import anywhere except the class under test itself.
