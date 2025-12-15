package org.design.designurlshortenergenerator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicLong;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class RangeAllocator {

    private final DistributedAtomicLong dal;
    private final long blockSize = 1000;
    private final AtomicLong localCurrent = new AtomicLong(0);
    private final AtomicLong localEnd = new AtomicLong(-1);

    public RangeAllocator(@Value("${ZK_CONNECT:localhost:2181}") String connectString) throws Exception {
        System.out.println("Connecting to ZooKeeper: " + connectString);

        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(connectString)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .connectionTimeoutMs(5000)
                .sessionTimeoutMs(30000)
                .build();

        client.start();
        client.blockUntilConnected(10, java.util.concurrent.TimeUnit.SECONDS);

        if (!client.getZookeeperClient().isConnected()) {
            throw new IllegalStateException("Failed to connect to ZooKeeper at " + connectString);
        }

        System.out.println("ZooKeeper connected!");

        this.dal = new DistributedAtomicLong(
                client,
                "/global_id_counter",
                new ExponentialBackoffRetry(1000, 3)
        );
    }

    public synchronized long nextId() throws Exception {
        if (localCurrent.get() > localEnd.get()) {
            allocateBlock();
        }
        return localCurrent.getAndIncrement();
    }

    private void allocateBlock() throws Exception {
        AtomicValue<Long> result = dal.add(blockSize);
        if (!result.succeeded()) {
            throw new RuntimeException("Failed to increment counter: " + result.getStats());
        }
        long newEnd = result.postValue();
        long start = newEnd - blockSize + 1;
        localCurrent.set(start);
        localEnd.set(newEnd);
        System.out.println("Allocated block: " + start + " to " + newEnd);
    }
}
