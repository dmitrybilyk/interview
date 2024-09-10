# JVM Tuning Overview

## Heap Memory Settings

- **Initial Heap Size (`-Xms`)**: Sets the initial size of the heap memory.
- **Maximum Heap Size (`-Xmx`)**: Sets the maximum size of the heap memory.

  ```bash
  java -Xms512m -Xmx4g -jar yourapp.jar

java -Xss1m -jar yourapp.jar

java -Xlog:gc* -jar yourapp.jar

java -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=512m -jar yourapp.jar

java -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/path/to/dump.hprof -jar yourapp.jar

java -Xloggc:/path/to/gc.log -jar yourapp.jar

