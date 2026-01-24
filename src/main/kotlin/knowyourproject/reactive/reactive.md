doOnNext - executes for every stream item for side effects
next - returns first element from stream (returns Mono)
doOnComplete - executes after stream completion
doOnError - We intercept exception here, logging it and return as a response
onErrorContinue - continues processing of users, triggers doOnNext
onErrorResume - returns fallback for failed user, returns good users + this fallback user
onErrorReturn - stops handling of items in flux and just gives the fallback for the errored item
.log() - logs everything. can be adjusted by custom category, log level, place where to use logs etc.
.doOnEach() - just more flexible than log. Though required code writing for signal types
doOnSubscribe / doOnCancel
checkpoint() - helps to determine where error occurred. close to what operator
contextWrite + deferContextual

Mono.fromCallable (fromRunnable, fromSupplier etc.) is used to integrate blocking code inside reactive pipeline.
It's Cold subsciption (executed just when is subscribbed)
Mono.defer - not execution of Mono.fromCallable labda happens lazily but even creation of the publisher happens lazily,
could be used to be able to make choices, to avoid caching with repeatable subsciptions, to avoid sideeffects until 
actual execution

.map - transforms items. takes and returns elements, not publishers. Can be executed in different threads
though synchronously.

.flatmap - transforms element and returns publisher. Also does flatten to avoid having result like 
this - Flux<Mono<UserRest>>

.publishOn(Scheduler)   

.subscribeOn(Scheduler)

limitRate = upstream backpressure control, chunk by chunk

next to check:
- map vs flatmap.
- cold vs hot subscription
- streaming
- sink


seq 1 200 | xargs -n1 -P 50 curl -s http://localhost:8081/users

curl -s http://localhost:8081/users

seq 1 100000 | xargs -n1 -P 2000 curl -s -X POST http://localhost:8081/users   -H "Content-Type: application/json"   -d '{"firstName":"testuser$1","lastName":"testuser$1","email":"testuser$1@example.com","password":"password123"}'   > /dev/null 2>&1
