Most collections (`HashMap`, `ArrayList`, `HashSet`...) aren't thread-safe. Two threads writing
at once can corrupt internal state and silently lose entries - no exception, just a wrong result.

Fix: use a thread-safe collection, e.g. `ConcurrentHashMap` instead of `HashMap`.
