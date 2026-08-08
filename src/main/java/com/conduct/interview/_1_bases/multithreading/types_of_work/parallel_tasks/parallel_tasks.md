One big task split into independent pieces, run at the same time, then combined - not waiting
on I/O, just splitting CPU work (overlaps with cpu_heavy_work). Typical tools: parallel streams
(`ParallelTest` in `cpu_heavy/`) and `ForkJoinPool` (see `java_concurrent_package/fork_join/ForkJoinSumDemo`).
