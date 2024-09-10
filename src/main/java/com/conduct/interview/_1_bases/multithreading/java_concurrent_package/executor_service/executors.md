ExecutorService is a JDK API that simplifies running tasks in asynchronous mode. Generally speaking, 
ExecutorService automatically provides a pool of threads and an API for assigning tasks to it.

Execute method allows to run Runnables, submit and invoke like methods return 
the Future objects. Calling the get() method on the future object block the execution and 
returns actual result in case of Callable task and null in case of Runnable.

Future task can be checked and cancelled.

ExecutorService should be stopped:
- with `shutdown` method it will give ability to running threads to complete
- with `shutdownNow` method it will try to complete all running tasks but 
there is no guarantee