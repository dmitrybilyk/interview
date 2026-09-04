Makes a fixed number of threads all wait for each other at a common point before any of them
continue - once everyone arrives, they're released together (optionally running one action
first). Unlike `CountDownLatch`, it's **reusable**: the count resets automatically for the next
round, which is why it's called "cyclic".
