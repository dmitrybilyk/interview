package knowyourproject.reactive.testing

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.time.Duration

data class User(
    val id: String,
    val name: String,
    var active: Boolean
)

interface UserRepository {
    fun findById(id: String): Mono<User>
    fun findAll(): Flux<User>
    fun save(user: User): Mono<User>
}

interface ExternalClient {
    fun fetchScore(id: String): Mono<Int>
}

data class EnrichedUser(val user: User, val score: Int)

class UserService(
    private val repository: UserRepository,
    private val externalClient: ExternalClient
) {

    fun findUserById(id: String): Mono<User> =
        repository.findById(id)
            .switchIfEmpty(Mono.error(RuntimeException("User not found")))

    fun findAllUsers(): Flux<User> =
        repository.findAll()
            .filter { it.active }

    fun deactivateUser(id: String): Mono<Void> =
        repository.findById(id)
            .flatMap {
                it.active = false
                repository.save(it)
            }
            .then()

    // --- Async update in background thread ---
    fun updateUserNameAsync(id: String, newName: String): Mono<User> =
        repository.findById(id)
            .publishOn(Schedulers.boundedElastic())
            .delayElement(Duration.ofMillis(200)) // simulate async I/O
            .flatMap { user ->
                user.copy(name = newName).let(repository::save)
            }
            .doOnNext { println("Updated user on thread: ${Thread.currentThread().name}") }

    // --- Combine local + external sources ---
    fun enrichUserWithScore(id: String): Mono<EnrichedUser> =
        repository.findById(id)
            .zipWith(externalClient.fetchScore(id))
            .map { tuple -> EnrichedUser(tuple.t1, tuple.t2) }

    // --- Parallel deactivation of multiple users ---
    fun deactivateAll(users: List<String>): Mono<Void> =
        Flux.fromIterable(users)
            .flatMap { id ->
                deactivateUser(id)
                    .subscribeOn(Schedulers.parallel())
            }
            .then()
}
