# Spring Security

Spring Security is an authentication (who are you?) and authorization
(what are you allowed to do?) framework.

## Under the hood: Chain of Responsibility, not a proxy

Unlike AOP/`@Transactional`/Spring Data ([_4_aop](../_4_aop),
[_5_transactions](../_5_transactions), [_12_spring_data_jpa](../_12_spring_data_jpa)),
security enforcement for the web layer is **not** a proxy around your
beans - it's a **Chain of Responsibility** (`FilterChainProxy`) sitting
in front of `DispatcherServlet` at the servlet-filter level, entirely
outside the container's bean-proxying machinery. Each filter in the
chain gets a chance to act (or short-circuit) before the request
reaches your controller at all:

- `UsernamePasswordAuthenticationFilter` - only acts on `POST /login`;
  extracts the credentials and builds a `UsernamePasswordAuthenticationToken`.
- `DefaultLoginPageGeneratingFilter` - serves Spring's built-in login
  page on `GET /login` if you haven't supplied your own.
- `AuthorizationFilter` - throws `AccessDeniedException` if the request
  isn't authenticated/authorized for the target URL.
- `ExceptionTranslationFilter` - catches that exception and redirects to
  `/login` instead of letting a raw 403/500 through.

Walking through the three request scenarios that matter most:

1. **`GET /login`, nobody logged in**: `UsernamePasswordAuthenticationFilter`
   does nothing (wrong method); `DefaultLoginPageGeneratingFilter`
   renders the login form and stops the chain there.
2. **A protected URL, nobody logged in**: `AuthorizationFilter` throws
   `AccessDeniedException`; `ExceptionTranslationFilter` catches it and
   redirects to `/login`.
3. **`POST /login` with credentials**: `UsernamePasswordAuthenticationFilter`
   builds an `Authentication` and hands it to
   `AuthenticationManager.authenticate(...)` - this is where the
   mechanism below takes over.

## The authentication mechanism itself (this is what's runnable, no web needed)

```
UsernamePasswordAuthenticationToken (username + raw password, unauthenticated)
        |
        v
ProviderManager (the standard AuthenticationManager)
        |  tries each configured AuthenticationProvider in turn
        v
DaoAuthenticationProvider
        |  loadUserByUsername(username) --> UserDetailsService
        |  passwordEncoder.matches(rawPassword, userDetails.getPassword())
        v
UsernamePasswordAuthenticationToken (now authenticated, principal attached)
```

`InMemoryUserDetailsManager` is the simplest `UserDetailsService` -
looks a username up in a plain in-memory map. Swap it for one backed by
a database and nothing above it changes.

This whole chain is just plain beans calling each other's public
methods - no proxy involved - which is why `AuthenticationDemo` can
exercise the entire mechanism with a `main()` method and zero HTTP.

## Enabling it for real (for context, not run here)

A real app adds `spring-boot-starter-security`, then a `@Configuration`
class with a `SecurityFilterChain` bean (`HttpSecurity` builder),
`UserDetailsService`, and `PasswordEncoder` beans, plus
`@EnableMethodSecurity` if you also want method-level
`@PreAuthorize`/`@PostAuthorize` checks (which - unlike the filter chain
above - **are** implemented as an AOP proxy/advice, since they guard a
bean method call rather than an HTTP request).

Note: this repo's `application.properties` explicitly excludes Spring
Security's autoconfiguration for the actual running app (see the
comment there) - `spring-boot-starter-security` was added to the
classpath purely so this topic has real Spring Security classes to
build the standalone demo below with, without turning on a login wall
for every other example in the repo.
