import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.netty.http.client.HttpClient
import io.netty.handler.ssl.SslContextBuilder
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.TrustManagerFactory
import java.io.FileInputStream
import java.security.KeyStore

fun createWebClient(): WebClient {
    // Load client keystore
    val clientKeyStore = KeyStore.getInstance("PKCS12").apply {
        load(FileInputStream("src/main/resources/client.p12"), "password".toCharArray())
    }

    val kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm()).apply {
        init(clientKeyStore, "password".toCharArray())
    }

    // Load truststore (trust server)
    val trustStore = KeyStore.getInstance("PKCS12").apply {
        load(FileInputStream("src/main/resources/client-truststore.p12"), "password".toCharArray())
    }

    val tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm()).apply {
        init(trustStore)
    }

    // Create Reactor Netty HttpClient with SSL
    val httpClient = HttpClient.create()
        .secure { spec ->
            spec.sslContext(
                SslContextBuilder.forClient()
                    .keyManager(kmf)
                    .trustManager(tmf)
            )
        }

    return WebClient.builder()
        .baseUrl("https://localhost:8443")
        .clientConnector(ReactorClientHttpConnector(httpClient))
        .build()
}

fun main() {
    val client = createWebClient()
    val response: Mono<String> = client.get().uri("/interview").retrieve().bodyToMono(String::class.java)
    response.subscribe { println("Response: $it") }

    // Keep JVM alive for demo purposes
    Thread.sleep(5000)
}
