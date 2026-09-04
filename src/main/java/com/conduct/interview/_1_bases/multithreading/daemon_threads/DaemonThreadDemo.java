package com.conduct.interview._1_bases.multithreading.daemon_threads;

/**
 * A daemon thread doesn't keep the JVM alive - the process exits as soon as all
 * non-daemon threads finish, killing any daemon threads mid-loop, no cleanup.
 */
public class DaemonThreadDemo {

    public static void main(String[] args) throws InterruptedException {
        Thread daemon = new Thread(() -> {
            while (true) {
                System.out.println("Daemon thread still running...");
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    return;
                }
            }
        });

        daemon.setDaemon(true); // must be called BEFORE start()
        daemon.start();

        Thread.sleep(1000);
        System.out.println("Main thread finishing - JVM exits now, daemon gets killed mid-loop");
        // No daemon.join() here on purpose: the JVM does not wait for daemon threads.
    }
}
