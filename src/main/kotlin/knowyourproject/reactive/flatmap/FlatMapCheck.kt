package knowyourproject.reactive.flatmap

import knowyourproject.ValidateRUserService
import knowyourproject.reactive.EnrichRUserService
import knowyourproject.reactive.RUserService
import reactor.core.scheduler.Schedulers
import java.lang.Thread.sleep

fun main() {
    val rUserService = RUserService()
    val enrichRUserService = EnrichRUserService()
    val start = System.currentTimeMillis()

    fun log(msg: String, item: Any? = "") =
        println("${System.currentTimeMillis() - start}ms | [${Thread.currentThread().name}] $msg $item")

    rUserService.getRUsers()
//        .mergeWith(rUserService.getPremiumRUsers())
        .doOnNext { log("FETCHED from Java server:", it) } // Track when the 1s wait ends
        .flatMap { user ->
            enrichRUserService.enrichUserReactive(user)
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSubscribe { log("STARTING enrichment for:", user.name) }
        }
        .doOnNext { log("FINISHED enrichment -", it) }
        .doOnComplete { log("TOTAL TIME:") }
        .subscribe()

    Thread.sleep(6000)
}