package com.conduct.interview

import knowyourproject.reactive.testing.ExternalClient
import knowyourproject.reactive.testing.User
import knowyourproject.reactive.testing.UserRepository
import knowyourproject.reactive.testing.UserService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.Duration

class UserServiceTest {

    private lateinit var repository: UserRepository
    private lateinit var externalClient: ExternalClient
    private lateinit var service: UserService

    @BeforeEach
    fun setUp() {
        repository = mock()
        externalClient = mock()
        service = UserService(repository, externalClient)
    }

    @Test
    fun `updateUserNameAsync should update asynchronously (real threads)`() {
        val user = User("1", "Alice", true)
        whenever(repository.findById("1")).thenReturn(Mono.just(user))
        whenever(repository.save(any())).thenAnswer { Mono.just(it.arguments[0] as User) }

        StepVerifier.create(service.updateUserNameAsync("1", "Alicia"))
            .expectNextMatches { it.name == "Alicia" }
            .verifyComplete()
    }

    @Test
    fun `updateUserNameAsync should update asynchronously`() {
        val user = User("1", "Alice", true)
        whenever(repository.findById("1")).thenReturn(Mono.just(user))
        whenever(repository.save(any())).thenAnswer { Mono.just(it.arguments[0] as User) }

        println("Updated user on thread: ${Thread.currentThread().name}")

        StepVerifier.withVirtualTime {
            service.updateUserNameAsync("1", "Alicia")
        }
            .thenAwait(Duration.ofMillis(300))
            .expectNextMatches { it.name == "Alicia" }
            .verifyComplete()

        verify(repository).save(check { assert(it.name == "Alicia") })
    }

    @Test
    fun `enrichUserWithScore should combine user and score`() {
        val user = User("1", "Bob", true)
        whenever(repository.findById("1")).thenReturn(Mono.just(user))
        whenever(externalClient.fetchScore("1")).thenReturn(Mono.just(85))

        StepVerifier.create(service.enrichUserWithScore("1"))
            .expectNextMatches { enriched ->
                enriched.user.name == "Bob" && enriched.score == 85
            }
            .verifyComplete()
    }

    @Test
    fun `deactivateAll should call deactivateUser for all`() {
        val ids = listOf("1", "2", "3")
        whenever(repository.findById(any())).thenReturn(Mono.just(User("x", "Temp", true)))
        whenever(repository.save(any())).thenAnswer { Mono.just(it.arguments[0] as User) }

        StepVerifier.create(service.deactivateAll(ids))
            .verifyComplete()

        verify(repository, times(ids.size)).save(any())
    }

    @Test
    fun `enrichUserWithScore should handle external error`() {
        val user = User("10", "Carol", true)
        whenever(repository.findById("10")).thenReturn(Mono.just(user))
        whenever(externalClient.fetchScore("10")).thenReturn(Mono.error(RuntimeException("API down")))

        StepVerifier.create(service.enrichUserWithScore("10"))
            .expectErrorMatches { it.message == "API down" }
            .verify()
    }

    @Test
    fun `updateUserNameAsync should handle missing user`() {
        whenever(repository.findById("404")).thenReturn(Mono.empty())

        StepVerifier.create(service.updateUserNameAsync("404", "Ghost"))
            .expectErrorMatches { it is RuntimeException }
            .verify()
    }
}
