Different kinds of work need different threading strategies - more threads doesn't always help:

- `cpu_heavy` - crunching, no waiting -> threads ~= CPU cores
- `io_work` - mostly waiting (HTTP, DB, files) -> can use way more threads, or virtual threads
- `parallel_tasks` - one task split into pieces and run at once (overlaps with cpu_heavy)
- `reactive__event_driven_tasks` - threads react to events instead of blocking/waiting
- `scheduled_delayed_tasks` - work that runs later or repeatedly, not immediately
