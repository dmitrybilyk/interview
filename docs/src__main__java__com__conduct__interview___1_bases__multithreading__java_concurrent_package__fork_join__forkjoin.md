Fork join pool is a feature which allows to fork task to many tasks, 
execute them in many cores (thread) and then join the result in the end.
It's a special implementation of the ExecutorService which uses recursion
and uses principle - divide and conquer.
