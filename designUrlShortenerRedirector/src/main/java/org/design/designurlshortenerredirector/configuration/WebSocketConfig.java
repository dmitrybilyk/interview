package org.design.designurlshortenerredirector.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // Увімкнення STOMP WebSocket-брокера
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Реєструє ендпоінт, до якого клієнти можуть підключатися.
     * Це "ручка" (URL) для handshake WebSocket.
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Клієнт буде підключатися до ws://localhost:8085/ws-stomp
        registry.addEndpoint("/ws-stomp").setAllowedOriginPatterns("*");
        
        // Додаємо варіант з SockJS для підтримки старих браузерів (опціонально)
        registry.addEndpoint("/ws-stomp").setAllowedOriginPatterns("*").withSockJS();
    }

    /**
     * Налаштовує брокер повідомлень.
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Префікс для адрес, на які брокер надсилає повідомлення (відповіді)
        // Наприклад, /topic/responses
        config.enableSimpleBroker("/topic"); 

        // Префікс для адрес, які обробляються контролерами (@MessageMapping)
        // Клієнт надсилає повідомлення на /app/register
        config.setApplicationDestinationPrefixes("/app");
    }
}