A rendezvous point for exactly two threads to swap objects. Both call `exchange(value)`; each
blocks until the other arrives, then each gets back what the other one passed in. Niche - mostly
seen in producer/consumer buffer-swapping scenarios.
