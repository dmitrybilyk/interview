package com.conduct.interview._1_bases.multithreading.java_concurrent_package.executor_service.future;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class FutureDemo {

    public static void main(String... args) throws InterruptedException, ExecutionException {
		ExecutorService executorService = Executors.newFixedThreadPool(5);
		Future runnableFuture = executorService.submit(new RunnableTask());
		Future<String> callableFuture = executorService.submit(new CallableTask());
		List<Future<String>> futureList = executorService.invokeAll(Arrays.asList(new CallableTask(),
				new CallableTask(), new CallableTask()));
		futureList.forEach(stringFuture -> {
			try {
				System.out.println(stringFuture.get());
			} catch (InterruptedException | ExecutionException e) {
				throw new RuntimeException(e);
			}
        });
		System.out.println(callableFuture.get());
		System.out.println("After get");
		executorService.shutdown();
    }

}

class CallableTask implements Callable<String> {
    @Override
    public String call() throws Exception {
		Thread.sleep(5000);
		System.out.println("Something is happening in callable task");
        return "Returning for future";
    }
}

class RunnableTask implements Runnable {

	@Override
	public void run() {
		System.out.println("Running something");
	}
}

