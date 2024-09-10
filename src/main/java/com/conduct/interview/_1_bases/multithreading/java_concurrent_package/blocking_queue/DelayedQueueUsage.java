// import com.google.common.primitives.Ints;

// import java.util.concurrent.Delayed;
// import java.util.concurrent.TimeUnit;
// import java.util.concurrent.BlockingQueue;
// import java.util.concurrent.atomic.AtomicInteger;
// import java.util.UUID;
// import java.util.concurrent.BlockingQueue;
// import java.util.concurrent.BlockingQueue;
// import java.util.concurrent.DelayQueue;
// import java.util.concurrent.ExecutorService;
// import java.util.concurrent.Executors;
// import java.util.concurrent.TimeUnit;


// public class DelayedQueueUsage {
// 	public static void main(String[] args) {
// 		ExecutorService executor = Executors.newFixedThreadPool(2);
//         BlockingQueue<DelayObject> queue = new DelayQueue<>();
//         int numberOfElementsToProduce = 2;
//         int delayOfEachProducedMessageMilliseconds = 500;
//         DelayQueueConsumer consumer = new DelayQueueConsumer(queue, numberOfElementsToProduce);
//         DelayQueueProducer producer
//           = new DelayQueueProducer(queue, numberOfElementsToProduce, delayOfEachProducedMessageMilliseconds);

//         //when
//         executor.submit(producer);
//         executor.submit(consumer);

//         //then
//         executor.awaitTermination(5, TimeUnit.SECONDS);
//         executor.shutdown();

// 	}
// }

// class DelayObject implements Delayed {
//     private String data;
//     private long startTime;

//     DelayObject(String data, long delayInMilliseconds) {
//         this.data = data;
//         this.startTime = System.currentTimeMillis() + delayInMilliseconds;
//     }

//     @Override
//     public long getDelay(TimeUnit unit) {
//         long diff = startTime - System.currentTimeMillis();
//         return unit.convert(diff, TimeUnit.MILLISECONDS);
//     }

//     @Override
//     public int compareTo(Delayed o) {
//         return Ints.saturatedCast(this.startTime - ((DelayObject) o).startTime);
//     }

//     @Override
//     public String toString() {
//         return "{" + "data='" + data + '\'' + ", startTime=" + startTime + '}';
//     }
// }

// class DelayQueueConsumer implements Runnable {
//     private BlockingQueue<DelayObject> queue;
//     private final Integer numberOfElementsToTake;
//     final AtomicInteger numberOfConsumedElements = new AtomicInteger();

//     DelayQueueConsumer(BlockingQueue<DelayObject> queue, Integer numberOfElementsToTake) {
//         this.queue = queue;
//         this.numberOfElementsToTake = numberOfElementsToTake;
//     }


//     @Override
//     public void run() {
//         for (int i = 0; i < numberOfElementsToTake; i++) {
//             try {
//                 DelayObject object = queue.take();
//                 numberOfConsumedElements.incrementAndGet();
//                 System.out.println("Consumer take: " + object);
//             } catch (InterruptedException e) {
//                 e.printStackTrace();
//             }
//         }
//     }
// }

// class DelayQueueProducer implements Runnable {
//     private BlockingQueue<DelayObject> queue;
//     private final Integer numberOfElementsToProduce;
//     private final Integer delayOfEachProducedMessageMilliseconds;

//     DelayQueueProducer(BlockingQueue<DelayObject> queue,
//                        Integer numberOfElementsToProduce,
//                        Integer delayOfEachProducedMessageMilliseconds) {
//         this.queue = queue;
//         this.numberOfElementsToProduce = numberOfElementsToProduce;
//         this.delayOfEachProducedMessageMilliseconds = delayOfEachProducedMessageMilliseconds;
//     }

//     @Override
//     public void run() {
//         for (int i = 0; i < numberOfElementsToProduce; i++) {
//             DelayObject object
//                     = new DelayObject(UUID.randomUUID().toString(), delayOfEachProducedMessageMilliseconds);
//             System.out.println("Put object = " + object);
//             try {
//                 queue.put(object);
//                 Thread.sleep(500);
//             } catch (InterruptedException ie) {
//                 ie.printStackTrace();
//             }
//         }
//     }
// }