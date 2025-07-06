Unlike their synchronized counterparts, concurrent collections achieve thread-safety 
by dividing their data into segments. 
ConcurrentHashMap.
CopyOnWriteArrayList.
BlockingQueue.

🧩 What does "segmented" mean?
Segmented means:

🔓 Instead of locking the entire collection, only a small part (segment) of the data is locked or copied when accessed or modified.

🎯 Goal:
Avoid blocking the whole structure

Allow multiple threads to work in parallel on different parts