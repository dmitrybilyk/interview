package knowyourproject

import knowyourproject.reactive.RUser
import reactor.core.publisher.Mono

class ValidateRUserService {
    fun validateRUser(rUser: RUser): Mono<Boolean> {
        return Mono.just(rUser != null)
    }
}