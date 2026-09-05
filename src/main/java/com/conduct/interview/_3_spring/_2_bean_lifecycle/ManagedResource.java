package com.conduct.interview._3_spring._2_bean_lifecycle;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

/**
 * A realistic reason to touch the lifecycle: acquire an expensive resource
 * once the bean is fully wired, release it before the container discards
 * the bean.
 *
 * Normal example: a bean that owns its own ExecutorService for background
 * jobs. @PostConstruct creates the thread pool. @PreDestroy calls
 * executorService.shutdown() - skip it, and on every app restart you leak
 * a thread pool: non-daemon threads can even stop the JVM from exiting,
 * and any job still running gets abandoned mid-work instead of finishing
 * or failing cleanly.
 */
public class ManagedResource {

    private boolean connectionOpen;

    @PostConstruct
    public void openConnection() {
        connectionOpen = true;
        System.out.println("ManagedResource: connection opened");
    }

    public void doWork() {
        if (!connectionOpen) {
            throw new IllegalStateException("used before @PostConstruct ran - shouldn't be possible");
        }
        System.out.println("ManagedResource: doing work over the open connection");
    }

    @PreDestroy
    public void closeConnection() {
        connectionOpen = false;
        System.out.println("ManagedResource: connection closed");
    }
}
