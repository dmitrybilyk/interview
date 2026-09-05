# Spring Data JPA

You write an **interface** (`ProductRepository extends JpaRepository<Product, Long>`)
and never implement it. Two things make that work.

## 1. Query derivation

`findByNameContaining(String)` is parsed by `PartTree`, splitting the
method name into subject (`find`), predicate keywords (`By`,
`Containing`), and property (`Name`), and turned into a JPQL query -
purely from the method's name, no annotation needed. `@Query` lets you
write JPQL/native SQL directly when the name-derivation syntax can't
express what you need.

## 2. Under the hood: it's a JDK dynamic proxy

Since a repository is declared as a Java **interface**, Spring can only
give you back one thing at runtime: a **JDK dynamic proxy** implementing
that interface (see [_4_aop](../_4_aop) for the mechanism). Every method
call on it goes to `RepositoryFactorySupport`'s invocation handler,
which either:
- forwards it to `SimpleJpaRepository` for the standard CRUD methods
  (`save`, `findById`, `findAll`, `deleteById`, ...), or
- runs a `PartTree`/`@Query`-derived JPQL query for anything else.

`<jpa:repositories base-package="..."/>` (or `@EnableJpaRepositories`)
is what scans for repository interfaces and registers a proxy factory
bean for each one - this is the XML/annotation equivalent of Spring Boot
auto-configuring the same thing for you (see
[_11_spring_boot_autoconfiguration](../_11_spring_boot_autoconfiguration)).

## Run it

This demo wires `DataSource` (embedded H2), `EntityManagerFactory`
(Hibernate as the JPA provider), `JpaTransactionManager`, and
`ProductRepository` entirely from XML - no Spring Boot autoconfiguration
involved, so every piece Boot normally hides for you is visible in
`spring-data-jpa-context.xml`. `SpringDataJpaDemo` saves a couple of
`Product` rows, then calls a derived-name query and prints
`repository.getClass()` to confirm it really is a JDK proxy.
