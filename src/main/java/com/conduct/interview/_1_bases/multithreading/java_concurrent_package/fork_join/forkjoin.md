A special `ExecutorService` for divide-and-conquer work: split a big task into smaller ones
recursively (`fork`), run them in parallel across CPU cores, then combine the results (`join`).
Use `RecursiveTask<V>` if the task returns a result, `RecursiveAction` if it doesn't.
