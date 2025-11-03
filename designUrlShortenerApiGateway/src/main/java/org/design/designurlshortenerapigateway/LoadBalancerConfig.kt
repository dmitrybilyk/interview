// src/main/kotlin/com/yourcompany/gateway/config/LoadBalancerConfig.kt
package org.design.designurlshortenerapigateway

import org.springframework.cloud.client.ServiceInstance
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Flux
import java.net.URI

@Configuration
class LoadBalancerConfig {

    @Bean
    fun generatorServiceInstanceListSupplier(): ServiceInstanceListSupplier {
        return FixedServiceInstanceListSupplier(
            "generator",
            listOf("generator1:8082")  // Add generator2 later
        )
    }

    @Bean
    fun redirectorServiceInstanceListSupplier(): ServiceInstanceListSupplier {
        return FixedServiceInstanceListSupplier(
            "redirector",
            listOf("redirector1:8085", "redirector2:8086")
        )
    }
}

class FixedServiceInstanceListSupplier(
    private val serviceId: String,
    private val instances: List<String>
) : ServiceInstanceListSupplier {

    override fun getServiceId(): String = serviceId

    override fun get(): Flux<List<ServiceInstance>> {
        val serviceInstances = instances.map { hostPort ->
            val (host, port) = hostPort.split(":")
            object : ServiceInstance {
                override fun getServiceId() = "gatewayService"
                override fun getInstanceId() = "$host:$port"
                override fun getHost() = host
                override fun getPort() = port.toInt()
                override fun isSecure() = false
                override fun getMetadata() = emptyMap<String, String>()
                override fun getUri() = URI("http://$host:$port")
                override fun getScheme() = "http"
            }
        }
        return Flux.just(serviceInstances)
    }
}