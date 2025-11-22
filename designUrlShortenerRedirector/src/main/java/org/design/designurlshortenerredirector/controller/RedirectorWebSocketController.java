package org.design.designurlshortenerredirector.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.concurrent.TimeUnit;

@Controller
public class RedirectorWebSocketController {

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

    @MessageMapping("/register") // Приймає повідомлення
    @SendTo("/topic/responses")  // Надсилає відповідь
    public LinkResponse handleLinkRegistration(LinkRequest request) throws Exception {
        
        // --- Імітація роботи ---
        TimeUnit.MILLISECONDS.sleep(10); // 10 мс затримка
        // -------------------------

        System.out.println("Redirector: Received WS request for link: " + request.getLink());

        // Повертаємо час відправки, щоб Generator міг вирахувати загальну затримку
        return new LinkResponse(
            "WS Success! Processed link: " + request.getLink(),
            request.getSendTimeNs()
        );
    }
}