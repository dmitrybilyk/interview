A method/class with no fields (or only `static final` constants) is automatically thread-safe:
the result depends only on its arguments, and nothing is shared between calls for two threads to
race over. Prefer this over immutability when you don't even need an object - just static methods.
