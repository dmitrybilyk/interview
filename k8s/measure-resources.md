
java \
-Xms1g \
-XX:NativeMemoryTracking=summary \
-XX:+UseContainerSupport \
-Xlog:os+container=info \
-jar k8s-0.0.1-SNAPSHOT.jar



ps aux | grep k8s-0.0.1-SNAPSHOT.jar | grep -v grep

ps -o pid,rss,cmd -p 7764
cat /proc/7764/status | egrep "VmRSS|VmHWM"

top -p 7764

K8s can use a bit more because of no swaps like on vm's and a bit of
container runtime overhead. 



ps -o pid,rss,vsz,cmd -p 6587
cat /proc/6587/status | grep "VmRSS|VmHWM"
rss - current memory
vmhwm - maximum memory usage

On k8s there are no swaps. Memory limit is hard wall. 

memory_limit =
peak_RSS (VmHWM)
+ 20–30% safety buffer

RES = 209,084 KB ✅ (THIS is real RAM)

to see real JVM breakdown:
java -jar -XX:NativeMemoryTracking=summary k8s-0.0.1-SNAPSHOT.jar
jcmd 6124 VM.native_memory summary

curl http://localhost:8080/hello

ab -n 10000 -c 50 http://localhost:8080/hello
jstat -gc 96125 1000
jstat -gc 96125 


ps -p 96125 -o pid,comm,rss,vsz


Quick heap overview:

jcmd 96125 GC.heap_info


java \
-Xms512m \
-Xmx2g \
-Xss1m \
-jar k8s-0.0.1-SNAPSHOT.jar


| Flag   | Meaning               |
| ------ | --------------------- |
| `-Xms` | Initial heap          |
| `-Xmx` | Max heap              |
| `-Xss` | Stack size per thread |
