package knowyourproject.reactive

import reactor.core.publisher.Mono

class ValidateRUserService {
    fun validateRUser(rUser: RUser): Boolean {
        Thread.sleep(1000)
        println("Validating user in thread - " + Thread.currentThread().name)
        return rUser != null
    }
    fun validateRUserReactive(rUser: RUser): Mono<Boolean> {
        Thread.sleep(1000)
        return Mono.just(rUser != null)
    }
}