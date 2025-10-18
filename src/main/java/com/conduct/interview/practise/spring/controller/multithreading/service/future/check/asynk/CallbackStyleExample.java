package com.conduct.interview.practise.spring.controller.multithreading.service.future.check.asynk;

public class CallbackStyleExample {

    // Step 1️⃣: Define a callback interface
    interface RequestCallback {
        void onSuccess(String response);
        void onError(Exception e);
    }

    // Step 2️⃣: Define an async API that takes a callback
    static class AsyncRequestService {
        public void fetchData(String url, RequestCallback callback) {
            new Thread(() -> {
                try {
                    System.out.println("Fetching data from " + url + " on thread " + Thread.currentThread().getName());
                    Thread.sleep(2000); // Simulate slow network request

                    if (url.contains("bad")) {
                        throw new RuntimeException("404 Not Found");
                    }

                    // Notify success
                    callback.onSuccess("Response from " + url);
                } catch (Exception e) {
                    // Notify failure
                    callback.onError(e);
                }
            }).start(); // Starts a background thread
        }
    }

    public static void main(String[] args) {
        AsyncRequestService service = new AsyncRequestService();

        // Step 3️⃣: Start async call — program continues immediately
        System.out.println("Sending async request...");
        service.fetchData("http://example.com/api/data", new RequestCallback() {
            @Override
            public void onSuccess(String response) {
                System.out.println("✅ Got response: " + response);
            }

            @Override
            public void onError(Exception e) {
                System.out.println("❌ Failed: " + e.getMessage());
            }
        });

        // Do something else in parallel
        System.out.println("Main thread doing something else...");

        // Keep program alive long enough to see callback output
        try { Thread.sleep(3000); } catch (InterruptedException ignored) {}
        System.out.println("Main thread done.");
    }
}
