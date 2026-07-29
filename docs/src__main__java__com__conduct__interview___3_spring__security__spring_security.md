Spring Security is a powerful and highly customizable authentication and access-control framework.

When security starter is added class `FilterChainProxy` calls additional filters to
handle request. There could be 3 scenarios:

1. Redirect to default login page. So it's just `GET` /login
In this scenario:
- UsernamePasswordAuthenticationFilter - checks the url contains `login` and if 
method of request is `POST`. Does nothing as method is `GET`
- DefaultLoginPageGeneratingFilter - checks if any authentication is done or not
and in case url path has `/login`, `/logout` or `/login/error` - generates the
login page
- AuthorizationFilter - This filter throws `AccessDeniedException` in case there is 
no authentication done.
- ExceptionTranslationFilter - catches `AccessDeniedException` and forces application
to redirect the browser to `/login` request

2. Display default login page.
- UsernamePasswordAuthenticationFilter - does nothing as it's `GET` request
- DefaultLoginPageGeneratingFilter - generates login page as url is `/login`

3. User enters credentials.
- UsernamePasswordAuthenticationFilter - this is `POST` with `/login` so
it extracts username and password and creates something like 
`UsernamePasswordAuthenticationToken` - Authentication object.
Method `authenticate` of `AuthenticationManager` is called then here. 
`ProviderManager` provides the implementation.
Authentication manager gets authentication object with credentials as an input
and returns the same authentication object but with principal.
If authenticated then `isAuthenticated` = true
ProviderManager has a list of AuthenticationProviders's with single method `authenticate`.
Example: `DaoAuthenticationProvider` which has `UserDetailsService`.
`UserDetailsService` with help of method `loadUserByUsername` fetches 
the user (f.e. from db) and compares his credentials with credentials 
from `Authentication` object. And returns principal in case of success.



To enable security we just need to add starter, create configuration file with
annotation `@EnableWebSecurity`, create some beans:
-  Security filter chain
- InMemoryUserDetailsManager
- PasswordEncoder

If we need method authorisation then we add annotation `EnableMethodSecurity`.


