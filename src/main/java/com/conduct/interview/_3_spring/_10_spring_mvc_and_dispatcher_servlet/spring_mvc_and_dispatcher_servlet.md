# Spring MVC and DispatcherServlet

Spring MVC is an implementation of the **Front Controller** pattern:
every HTTP request for the whole application goes through **one**
servlet, `DispatcherServlet`, which then delegates to the right piece of
your code, instead of each URL mapping to its own separate servlet.

`DispatcherServlet.doDispatch(request, response)`, simplified:

```
handler = handlerMapping.getHandler(request)         // "who handles this URL?"
result  = handlerAdapter.handle(request, response, handler)  // "invoke them"
if (result is a ModelAndView) viewResolver.resolveViewName(...).render(result, response)
```

- **`HandlerMapping`** (`RequestMappingHandlerMapping` for
  `@RequestMapping`/`@GetMapping`/etc.) - looks at every bean it knows
  about, once, at startup, finds the ones annotated `@Controller` (or
  with class-level `@RequestMapping`), and indexes their methods by
  URL+HTTP method pattern.
- **`HandlerAdapter`** (`RequestMappingHandlerAdapter`) - knows how to
  actually call a `@Controller` method: resolve each parameter
  (`@RequestParam`, `@RequestBody`, `@PathVariable`, ...) via a
  `HandlerMethodArgumentResolver`, invoke the method via reflection, then
  hand the return value to a `HandlerMethodReturnValueHandler`
  (`@ResponseBody` -> serialize with `ObjectMapper` and write to the
  response directly; a `String` -> treated as a view name instead).
- **`ViewResolver`**: only involved when the handler didn't already write
  the response itself (i.e. no `@ResponseBody`/`@RestController`) -
  resolves a view name (e.g. `"home"`) to an actual renderable `View`
  (e.g. a Thymeleaf template).

## Why this folder doesn't spin up a real server

Every other topic here runs via `ClassPathXmlApplicationContext` with no
web container involved - the same "no web application" constraint
applies here, but `DispatcherServlet` fundamentally needs a
`ServletContext` to register against. Rather than skip the "under the
hood" part, the test in
`src/test/java/.../_10_spring_mvc_and_dispatcher_servlet/` drives the
exact same `RequestMappingHandlerMapping`/`RequestMappingHandlerAdapter`
objects `DispatcherServlet` uses internally, directly, against a fake
`MockHttpServletRequest`/`MockHttpServletResponse` (from `spring-test`,
a test-only dependency - which is also why this one demo lives under
`src/test`, not `src/main` like the rest). It's the same two-line
algorithm above, with the servlet itself skipped.

For an actual running REST endpoint you can `curl`, see the `_8_rest` and
`webflux` packages elsewhere in this repo.
