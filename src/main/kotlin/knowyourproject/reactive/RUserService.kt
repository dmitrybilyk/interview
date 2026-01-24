package knowyourproject.reactive

import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux

class RUserService {

    // Ensure the port matches your Java app (you used 8081 in previous examples)
    private val webClient = WebClient.create("http://localhost:8080")

    fun getRUsers(): Flux<RUser> = webClient.get()
        .uri("/users")
        .retrieve()
        // If the server sends an array, bodyToFlux is the right choice
        .bodyToFlux(RUser::class.java)

    fun getPremiumRUsers(): Flux<RUser> = webClient.get()
        .uri("/users/premium")
        .retrieve()
        .bodyToFlux(RUser::class.java)
}