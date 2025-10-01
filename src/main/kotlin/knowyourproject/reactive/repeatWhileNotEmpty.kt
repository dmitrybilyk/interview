package knowyourproject.reactive

import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.CountDownLatch
import reactor.core.publisher.Mono
import reactor.core.publisher.Flux

// Your repeatWhileNotEmpty function adapted for Mono
fun <T> Mono<T>.repeatWhileNotEmpty(): Flux<T> {
    val empty = AtomicBoolean(false)
    val monoToRepeat = this.doOnSuccess { value ->
        if (value == null || value.toString().isEmpty()) {
            empty.set(true)
        }
    }
    return monoToRepeat.repeat { !empty.get() }
}

fun main() {
    println("=== Simple Repeat While Not Empty Demo ===\n")
    
    // Use CountDownLatch to wait for async operations instead of block()
    val latch = CountDownLatch(3)
    
    // Example 1: Polling until empty result
    println("1. Polling numbers until empty:")
    var counter = 3
    
    fun getNextNumber(): Mono<String> = Mono.fromCallable {
        if (counter > 0) {
            val number = "Number-$counter"
            counter--
            number
        } else {
            "" // Empty string means stop
        }
    }
    
    getNextNumber()
        .repeatWhileNotEmpty()
        .doOnNext { value -> println("  Received: $value") }
        .doOnComplete { 
            println("  Polling completed!")
            latch.countDown()
        }
        .subscribe()
    
    // Example 2: Processing items from a list
    println("\n2. Processing items from list:")
    val items = mutableListOf("Apple", "Banana", "Cherry")
    
    fun getNextItem(): Mono<String> = Mono.fromCallable {
        if (items.isNotEmpty()) {
            items.removeAt(0)
        } else {
            null // null means stop
        }
    }
    
    getNextItem()
        .repeatWhileNotEmpty()
        .doOnNext { item -> println("  Processing: $item") }
        .doOnComplete { 
            println("  All items processed!")
            latch.countDown()
        }
        .subscribe()
    
    // Example 3: Simulating API polling
    println("\n3. Simulating API polling:")
    var apiCallCount = 0
    
    fun mockApiCall(): Mono<String> = Mono.fromCallable {
        apiCallCount++
        if (apiCallCount <= 3) {
            "API Response #$apiCallCount"
        } else {
            "" // Empty response means stop polling
        }
    }
    
    mockApiCall()
        .repeatWhileNotEmpty()
        .delayElements(java.time.Duration.ofSeconds(1)) // Poll every second
        .doOnNext { response -> println("  API Response: $response") }
        .doOnComplete { 
            println("  API polling finished!")
            latch.countDown()
        }
        .subscribe()
    
    // Wait for all async operations to complete
    println("\n=== Waiting for operations to complete ===")
    latch.await(10, java.util.concurrent.TimeUnit.SECONDS)
    println("=== All done! ===")
}