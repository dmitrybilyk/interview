Three ways to run code on a thread:

- **extends Thread** - simplest, but burns your only superclass slot (Java has no multiple
  inheritance) and mixes "what to run" together with "how to run it"
- **implements Runnable** - preferred: no result, but the class stays free to extend something
  else; pass it to a `Thread` or an `ExecutorService`
- **implements Callable\<V\>** - like `Runnable` but returns a result and can throw checked
  exceptions; only usable via `ExecutorService.submit()`, which hands back a `Future<V>`
