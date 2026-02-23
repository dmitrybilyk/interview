package org.design.designurlshortenergenerator.generator.allocator.impl;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicLong;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.design.designurlshortenergenerator.generator.allocator.api.IdProvider;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Service
@Primary // Вказуємо Spring використовувати саме цю реалізацію зараз
public class ZooKeeperRangeAllocator implements IdProvider {
    private final DistributedAtomicLong dal;
    private final long blockSize = 1000;
    private final AtomicLong localCurrent = new AtomicLong(0);
    private final AtomicLong localEnd = new AtomicLong(-1);

    // Тепер ми впорскуємо клієнт, а не створюємо його самі!
    public ZooKeeperRangeAllocator(CuratorFramework client) {
        this.dal = new DistributedAtomicLong(
                client,
                "/global_id_counter",
                new ExponentialBackoffRetry(1000, 3)
        );
    }

    @Override
    public synchronized long nextId() {
        try {
            if (localCurrent.get() > localEnd.get()) {
                allocateBlock();
            }
            return localCurrent.getAndIncrement();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get next ID from ZK", e);
        }
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