package org.design.designurlshortenerapigateway

import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.stereotype.Component
import java.util.UUID

// CustomHeaderFilter.kt
@Component
class CustomHeaderFilter : AbstractGatewayFilterFactory<Any>(Any::class.java) {
    override fun apply(config: Any): GatewayFilter {
        return GatewayFilter { exchange, chain ->
            val request = exchange.request.mutate()
                .header("X-Request-ID", UUID.randomUUID().toString())
                .build()
            chain.filter(exchange.mutate().request(request).build())
        }
    }
}