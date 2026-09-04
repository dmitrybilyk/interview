Runs tasks after a delay, or repeatedly, without a manual `Timer`/`sleep` loop.

- `schedule(task, delay, unit)` - run once, after a delay
- `scheduleAtFixedRate(task, initialDelay, period, unit)` - period measured from each run's start
- `scheduleWithFixedDelay(task, initialDelay, delay, unit)` - delay measured from each run's end
  (use this if the task's duration can vary and runs shouldn't pile up)
