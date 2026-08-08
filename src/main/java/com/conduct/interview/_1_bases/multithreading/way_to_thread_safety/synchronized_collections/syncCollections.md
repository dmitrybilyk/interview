`Collections.synchronizedXxx(collection)` wraps every method call with a lock on one shared
object - correct, but the whole collection is locked for every access, so only one thread can
use it at a time. `ConcurrentHashMap`/`CopyOnWriteArrayList` scale better because they don't
lock everything at once (see `concurrent_collections`).
