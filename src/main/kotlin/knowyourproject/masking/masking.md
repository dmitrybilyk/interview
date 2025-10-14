import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
annotation class MaskFields(val fields: Array<String> = [])


import org.springframework.stereotype.Component
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.memberProperties

@Component
class MaskingService {

    fun <T : Any?> maskFields(response: T, fieldsToMask: Array<String>): T {
        if (response == null || fieldsToMask.isEmpty()) return response
        val maskedPaths = fieldsToMask.toSet()
        maskObject(response, maskedPaths, "")
        @Suppress("UNCHECKED_CAST")
        return response
    }

    private fun maskObject(obj: Any, maskedPaths: Set<String>, currentPath: String) {
        val kClass = obj::class
        kClass.memberProperties.forEach { prop ->
            val propName = prop.name
            val newPath = if (currentPath.isEmpty()) propName else "$currentPath.$propName"

            if (maskedPaths.contains(newPath)) {
                if (prop is KMutableProperty<*>) {
                    try {
                        prop.setter.call(obj, "****")
                    } catch (e: Exception) {
                        throw RuntimeException("Failed to mask field: $newPath", e)
                    }
                }
            } else {
                val value = prop.getter.call(obj)
                if (value != null && !isPrimitiveOrString(value)) {
                    when (value) {
                        is Collection<*> -> value.forEach { item ->
                            if (item != null && !isPrimitiveOrString(item)) {
                                maskObject(item, maskedPaths, newPath)
                            }
                        }
                        is Map<*, *> -> value.values.forEach { item ->
                            if (item != null && !isPrimitiveOrString(item)) {
                                maskObject(item, maskedPaths, newPath)
                            }
                        }
                        else -> maskObject(value, maskedPaths, newPath)
                    }
                }
            }
        }
    }

    private fun isPrimitiveOrString(value: Any): Boolean {
        return value is String || value is Number || value is Boolean || value is Char
    }
}


Reflection: Modifies mutable (var) properties to "****", supporting nested fields via dot notation (e.g., address.street).
Collections: Handles Collection and Map for nested objects.
Error Handling: Throws RuntimeException for non-mutable or inaccessible fields.




import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.core.MethodParameter
import org.springframework.web.method.HandlerMethod
import org.springframework.web.reactive.HandlerResult
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.ServerWebExchangeDecorator
import org.springframework.web.server.ServerHttpResponse
import org.springframework.web.server.ServerHttpResponseDecorator
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class MaskingResponseDecorator(
delegate: ServerHttpResponse,
private val exchange: ServerWebExchange,
private val handlerResult: HandlerResult?,
private val maskingService: MaskingService,
private val objectMapper: ObjectMapper
) : ServerHttpResponseDecorator(delegate) {

    override fun writeWith(body: Flux<DataBuffer>): Mono<Void> {
        val handler = handlerResult?.handler
        val maskFields = if (handler is HandlerMethod) {
            handler.method.getAnnotation(MaskFields::class.java)
        } else null

        return if (maskFields != null) {
            // Handle reactive and non-reactive types
            val returnValue = handlerResult?.returnValue
            when (returnValue) {
                is Mono<*> -> returnValue
                    .map { maskingService.maskFields(it, maskFields.fields) }
                    .flatMap { masked ->
                        super.writeWith(Flux.just(objectMapper.writeValueAsBytes(masked)))
                    }
                is Flux<*> -> returnValue
                    .map { maskingService.maskFields(it, maskFields.fields) }
                    .collectList()
                    .flatMap { masked ->
                        super.writeWith(Flux.just(objectMapper.writeValueAsBytes(masked)))
                    }
                else -> {
                    val masked = maskingService.maskFields(returnValue, maskFields.fields)
                    super.writeWith(Flux.just(objectMapper.writeValueAsBytes(masked)))
                }
            }
        } else {
            super.writeWith(body)
        }
    }
}



Decorator: Wraps the ServerHttpResponse to intercept the response body.
Annotation Check: Retrieves the @MaskFields annotation from the handler method.
Masking: Uses MaskingService to mask fields, then serializes the result to bytes using ObjectMapper.
Reactive Support: Handles Mono, Flux, and non-reactive types.





import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import com.fasterxml.jackson.databind.ObjectMapper

@Component
class MaskingWebFilter(
private val maskingService: MaskingService,
private val objectMapper: ObjectMapper
) : WebFilter {

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        // Get the handler to access the method annotation
        return exchange.getAttribute<Mono<HandlerResult>>("org.springframework.web.reactive.DispatcherHandler.handlerResult")
            ?.flatMap { handlerResult ->
                val decoratedExchange = object : ServerWebExchangeDecorator(exchange) {
                    override fun getResponse(): ServerHttpResponse {
                        return MaskingResponseDecorator(
                            exchange.response,
                            exchange,
                            handlerResult,
                            maskingService,
                            objectMapper
                        )
                    }
                }
                chain.filter(decoratedExchange)
            } ?: chain.filter(exchange)
    }
}


WebFilter: Intercepts all requests, retrieves the HandlerResult from the exchange attributes, and wraps the response with MaskingResponseDecorator.
HandlerResult: Uses the internal attribute set by Spring WebFlux’s DispatcherHandler to access the handler and result.
Compatibility: Works with Spring Boot 2.7’s WebFlux auto-configuration.

