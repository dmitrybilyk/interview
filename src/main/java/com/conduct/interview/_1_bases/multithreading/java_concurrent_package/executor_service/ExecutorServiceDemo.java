package com.conduct.interview._1_bases.multithreading.java_concurrent_package.executor_service;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;


public class ExecutorServiceDemo {
	public static void main(String[] args) {
		ExecutorService executor = Executors.newFixedThreadPool(10);
		executor.submit(new MyRunnable());
		executor.submit(new MyRunnable());
		executor.shutdown();
	}
}

class MyRunnable implements Runnable {
	public void run() {
		System.out.println("Running in executor service");
	}
}