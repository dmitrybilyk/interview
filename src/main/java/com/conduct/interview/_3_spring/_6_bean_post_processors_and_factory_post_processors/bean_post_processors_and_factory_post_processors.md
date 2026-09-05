# BeanPostProcessor vs BeanFactoryPostProcessor

Both let you hook into bean creation and change things. The difference
is just *timing*:

- **`BeanFactoryPostProcessor`** runs once, before **any** bean is
  actually created. It only sees the *blueprints* (bean definitions -
  class name, constructor args, property values as plain text) and can
  edit them.
- **`BeanPostProcessor`** runs once per **real bean object**, right when
  that bean is created (see [_2_bean_lifecycle](../_2_bean_lifecycle)
  for exactly where). It can inspect the real object - or even swap it
  out for a different object entirely.

## Why should I care? I'm never going to write one of these

Probably true - but these two things are the reason a bunch of Spring
features that look like magic actually work:

- **`@Autowired` fields getting filled in automatically** - a
  `BeanPostProcessor` does that. When your bean is created, it scans it
  for `@Autowired` fields and sets them via reflection.
- **`@Transactional` (or any AOP) silently wrapping your class in a
  proxy** - a `BeanPostProcessor` swaps your real object for a proxy
  version right after it's built.
- **`${app.greeting}` in a config value turning into real text from a
  `.properties` file** - a `BeanFactoryPostProcessor` does that, editing
  the blueprint before your object even exists.

So the point isn't "go write one of these" - it's "now you know what's
actually running when Spring does something you never wrote code for."

## Run it

`BeanDefinitionCountingPostProcessor` (a `BeanFactoryPostProcessor`)
runs first and prints every bean **name** it knows about - before
anything is actually built.

`BeanCreationTimingPostProcessor` (a `BeanPostProcessor`) times how long
each real bean takes to initialize.

`PropertySourcesPlaceholderConfigurer` is Spring's own built-in
`BeanFactoryPostProcessor` - it resolves `${app.greeting}` from
`app.properties` into the `Greeter` bean's constructor argument, proving
this happens before `Greeter`'s constructor ever runs.
