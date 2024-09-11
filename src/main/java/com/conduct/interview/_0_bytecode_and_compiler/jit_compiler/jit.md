Just-In-Time (JIT) Compiler: Overview
In Java, the Just-In-Time (JIT) compiler is an integral part of the Java Virtual Machine (JVM). 
Java code is compiled twice:

Compile-Time (javac): When you write Java code and compile it using javac, 
it is first converted into bytecode. This bytecode is platform-independent and can run on any machine that has a JVM.

Run-Time (JIT): When the Java application runs, the JIT compiler kicks in. Instead of interpreting 
the bytecode instruction-by-instruction (which is slower), the JVM converts sections of bytecode into native machine code (specific to your system’s architecture) as needed. This process happens "just in time" when the code is about to be executed, hence the name.

The JIT compiler optimizes frequently executed code paths (hot spots) by compiling them into machine 
code, allowing the JVM to run the program more efficiently. If certain code is run repeatedly, 
the JVM compiles and stores the machine code for reuse, which significantly boosts performance.


Additional Notes:
JVM Options: You can pass various JVM options to observe JIT behavior more clearly. For example, use -Xcomp to force compilation or -Xint to run entirely in interpreted mode (without JIT):

Force full JIT compilation:

bash
Copy code
java -Xcomp JITExample
Force interpretation (disable JIT):

bash
Copy code
java -Xint JITExample
You can compare the performance difference between these modes to observe the effect of JIT compilation.

JIT Optimizations:
The JIT compiler performs several optimizations:

Method Inlining: Replaces the method call with the method body to reduce overhead.
Loop Unrolling: Repeats the loop body multiple times to decrease branching.
Dead Code Elimination: Removes code that is never used or executed.
Constant Folding: Pre-computes constant expressions at compile-time.
JIT also has different levels of optimization, such as C1 (client) and C2 (server) compilers, 
depending on the JVM configuration and environment.


cd /home/dmytro/dev/projects/interview/src/main/java

javac com/learn/interview/interviews/_0_bytecode_and_compiler/jit_compiler/JITExample.java

java -Xcomp com.conduct.interview._0_bytecode_and_compiler.jit_compiler.JITExample

java -Xint com.conduct.interview._0_bytecode_and_compiler.jit_compiler.JITExample