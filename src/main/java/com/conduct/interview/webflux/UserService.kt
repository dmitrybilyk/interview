package com.conduct.interview.webflux

import reactor.core.publisher.Mono
import reactor.core.publisher.Flux

// Simulated UserService class that returns reactive types
class UserService {
    // Returns a Mono with a user ID after a simulated delay
    fun getUserId(username: String): Mono<Int> {
        return Mono.just(if (username == "alice") 1 else 2)
            .delayElement(java.time.Duration.ofMillis(500)) // Simulate async delay
    }

    // Returns a Flux of user roles
    fun getUserRoles(userId: Int): Flux<String> {
        return when (userId) {
            1 -> Flux.just("admin", "user")
            2 -> Flux.just("guest")
            else -> Flux.empty()
        }.delayElements(java.time.Duration.ofMillis(300)) // Simulate async streaming
    }
}

// Simulated EmailService class that returns a Mono
class EmailService {
    // Sends an email and returns a confirmation Mono
    fun sendEmail(userId: Int, message: String): Mono<String> {
        return Mono.just("Email.kt sent to user $userId: $message")
            .delayElement(java.time.Duration.ofMillis(800)) // Simulate async operation
    }
}

fun main() {
    val userService = UserService()
    val emailService = EmailService()

    // Example 1: Basic map - Transform a value synchronously
    Mono.just("alice")
        .map { username -> username.uppercase() } // Transform to uppercase
        .subscribe { result -> println("Mapped username: $result") }

    // Example 2: flatMap - Chain async operations (Mono to Mono)
    Mono.just("alice")
        .flatMap { username -> userService.getUserId(username) } // Get user ID async
        .flatMap { userId -> emailService.sendEmail(userId, "Welcome!") } // Then send email async
        .subscribe(
            { result -> println("FlatMap chain result: $result") },
            { error -> println("Error in flatMap chain: ${error.message}") }
        )

    // Example 3: Flux with flatMap - Flatten and chain streaming operations
    Flux.just("alice", "bob")
        .flatMap { username -> userService.getUserId(username) } // Get IDs as Flux
        .flatMap { userId -> userService.getUserRoles(userId) } // Then get roles for each ID
        .subscribe(
            { role -> println("Role from Flux flatMap: $role") },
            { error -> println("Error in Flux flatMap: ${error.message}") }
        )

    // Example 4: zip - Combine two Monos in parallel
    val userIdMono = userService.getUserId("alice")
    val rolesFlux = userService.getUserRoles(1).collectList() // Convert Flux to Mono<List>
    Mono.zip(userIdMono, rolesFlux)
        .map { tuple -> "User ID: ${tuple.t1}, Roles: ${tuple.t2}" }
        .subscribe { result -> println("Zipped result: $result") }

    // Example 5: Error handling with flatMap
    Mono.just("unknown")
        .flatMap { username -> userService.getUserId(username) } // Will return ID 2
        .flatMap { userId -> if (userId > 1) Mono.error(RuntimeException("Invalid user")) else Mono.just(userId) }
//        .onErrorResume { error -> Mono.just("Handled error: ${error.message}") }
        .subscribe { result -> println("Error handling result: $result") }

    // Block the main thread briefly to see async results (for demo purposes only; avoid in real apps)
    Thread.sleep(3000)
}