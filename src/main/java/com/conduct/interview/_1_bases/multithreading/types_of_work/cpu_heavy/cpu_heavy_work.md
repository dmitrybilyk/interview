Work that keeps a CPU core busy the whole time - no waiting involved (math, encryption, report
generation). Thread pool size should match the number of CPU cores - more threads than cores just
adds context-switching overhead without speeding anything up.

Run `SequentialTest` vs `ParallelTest` (parallel stream, uses Fork/Join under the hood) to see
the difference on a multi-core machine.
