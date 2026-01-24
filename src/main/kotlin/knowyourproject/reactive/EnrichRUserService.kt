package knowyourproject.reactive

import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono

class EnrichRUserService {
    private val webClient = WebClient.create("http://localhost:8080")

    // This fails if the server returns [ {user} ]
    fun enrichUserReactive(user: RUser): Mono<RUser> = webClient.get()
        .uri("/enrich/${user.name}")
        .retrieve()
        .bodyToMono(RUser::class.java) // <--- ERROR HERE
}