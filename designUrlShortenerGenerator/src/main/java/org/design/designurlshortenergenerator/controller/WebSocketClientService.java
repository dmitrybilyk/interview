package org.design.designurlshortenergenerator.controller;

import jakarta.annotation.PostConstruct;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.concurrent.ExecutionException;

@Service
public class WebSocketClientService {

    private StompSession stompSession;
    // URL для підключення через Gateway, який працює на 8080
    private static final String WS_URL = "ws://localhost:8080/ws-link-register"; 
    private static final String TOPIC_RESPONSES = "/topic/responses";
    private static final String APP_REGISTER = "/app/register";

    @PostConstruct
    public void connect() {
        WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        try {
            stompSession = stompClient.connectAsync(WS_URL, new StompSessionHandlerAdapter() {
                @Override
                public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                    System.out.println("Generator: WebSocket connected. Session ID: " + session.getSessionId());
                    
                    // Підписка на відповіді
                    session.subscribe(TOPIC_RESPONSES, new StompFrameHandler() {
                        @Override
                        public Type getPayloadType(StompHeaders headers) {
                            return LinkResponse.class;
                        }

                        @Override
                        public void handleFrame(StompHeaders headers, Object payload) {
                            LinkResponse response = (LinkResponse) payload;
                            
                            // --- ВИМІРЮВАННЯ ЧАСУ ---
                            long endTime = System.nanoTime();
                            // Загальний час = Час кінця - Час відправки, що був переданий у відповіді
                            long totalDurationNs = endTime - response.getRequestSendTimeNs(); 
                            double totalDurationMs = totalDurationNs / 1_000_000.0;
                            // -------------------------

                            System.out.printf(
                                "Generator: ⚡ WS response received. Message: [%s]. Total time (Latency): %.3f ms.\n", 
                                response.getMessage(), 
                                totalDurationMs
                            );
                        }
                    });
                }

                @Override
                public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
                    System.err.println("WS Error: " + exception.getMessage());
                }

                @Override
                public void handleTransportError(StompSession session, Throwable exception) {
                    System.err.println("WS Transport Error: " + exception.getMessage());
                }
            }).get(); 
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Failed to connect to WebSocket: " + e.getMessage());
        }
    }

    public void sendLinkRequest(String link) {
        if (stompSession == null || !stompSession.isConnected()) {
            System.err.println("WebSocket not connected. Cannot send message.");
            return;
        }

        LinkRequest request = new LinkRequest();
        request.setLink(link);
        request.setSendTimeNs(System.nanoTime()); // Фіксуємо час відправки

        // Надсилаємо повідомлення
        stompSession.send(APP_REGISTER, request);
        System.out.println("Generator: Sent WS request for link: " + link);
    }

    // Класи DTO (перенесені в Redirector, або в спільний модуль)
    public static class LinkRequest {
        private String link;
        private long sendTimeNs; // Час відправки від Generator (в наносекундах)
        // ... (геттери/сеттери)
        public LinkRequest() {}
        public String getLink() { return link; }
        public void setLink(String link) { this.link = link; }
        public long getSendTimeNs() { return sendTimeNs; }
        public void setSendTimeNs(long sendTimeNs) { this.sendTimeNs = sendTimeNs; }
    }

    public static class LinkResponse {
        private String message;
        private long requestSendTimeNs; // Повертаємо час відправки
        // ... (геттери/сеттери)
        public LinkResponse(String message, long requestSendTimeNs) {
            this.message = message;
            this.requestSendTimeNs = requestSendTimeNs;
        }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public long getRequestSendTimeNs() { return requestSendTimeNs; }
        public void setRequestSendTimeNs(long requestSendTimeNs) { this.requestSendTimeNs = requestSendTimeNs; }
    }
}