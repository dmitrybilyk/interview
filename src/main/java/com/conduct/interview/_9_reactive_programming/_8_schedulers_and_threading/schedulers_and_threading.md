# publishOn vs subscribeOn

Both switch which thread later operators run on, but at different points
in the chain:

- **`subscribeOn(scheduler)`**: affects where the *subscription itself*
  happens - i.e. where the source starts emitting. Placement in the chain
  doesn't matter, and only the first `subscribeOn` in a chain has any
  effect (it applies to the whole chain, from the source).
- **`publishOn(scheduler)`**: affects where everything *after it* in the
  chain runs, from that point downstream. You can have several, each
  switching the thread again for the rest of the pipeline.

Rule of thumb: `subscribeOn` picks the thread for the source (e.g. "run
this blocking JDBC call on the boundedElastic scheduler");
`publishOn` picks the thread for everything downstream of a specific
point (e.g. "switch back to a worker thread after that blocking call").
