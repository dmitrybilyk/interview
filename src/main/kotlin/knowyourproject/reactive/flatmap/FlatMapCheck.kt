package knowyourproject.reactive.flatmap

import knowyourproject.reactive.ValidateRUserService
import knowyourproject.reactive.EnrichRUserService
import knowyourproject.reactive.RUserService
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.lang.Thread.sleep

fun main() {
    val rUserService = RUserService()
    val enrichRUserService = EnrichRUserService()
    val validateRUserService = ValidateRUserService()
    val start = System.currentTimeMillis()

    fun log(msg: String, item: Any? = "") =
        println("${System.currentTimeMillis() - start}ms | [${Thread.currentThread().name}] $msg $item")

    rUserService.getRUsers()
        .mergeWith(rUserService.getPremiumRUsers())
        .doOnNext { log("FETCHED from Java server:", it) } // Track when the 1s wait ends
        .flatMap { user ->
            enrichRUserService.enrichUserReactive(user)
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSubscribe { log("STARTING enrichment for:", user.name) }
                .flatMap {
//                    Mono.fromCallable { validateRUserService.validateRUser(it) }
//                        .subscribeOn(Schedulers.boundedElastic())
                    Mono.defer { validateRUserService.validateRUserReactive(it) }
                        .subscribeOn(Schedulers.boundedElastic())
                }
        }
        .doOnNext { log("FINISHED processing -", it) }
        .doOnComplete { log("TOTAL TIME:") }
        .subscribe()

    sleep(6000)
}