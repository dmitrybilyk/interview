package org.design.designurlshortenerredirector.controller;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class RedirectorManualSendController {

    private final SimpMessagingTemplate messagingTemplate;
    private static final String TOPIC_RESPONSES = "/topic/responses";

    public RedirectorManualSendController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * REST endpoint для ручного надсилання повідомлення через WebSocket.
     * Викликає: http://localhost:8085/api/admin/send-manual?message=TestMessage
     */
    @GetMapping("/send-manual")
    public String sendManualResponseToGenerator(@RequestParam String message) {
        
        // Створюємо LinkResponse. Для ручного повідомлення requestSendTimeNs встановлюємо в 0.
        RedirectorWebSocketController.LinkResponse response = new RedirectorWebSocketController.LinkResponse(
            "MANUAL PUSH: " + message,
            0 // Немає початкового часу відправки, оскільки це не відповідь на запит
        );

        // Надсилаємо повідомлення безпосередньо в топік, на який підписаний Generator
        messagingTemplate.convertAndSend(TOPIC_RESPONSES, response);
        
        System.out.println("Redirector: Manually pushed WS message to " + TOPIC_RESPONSES + ": " + message);

        return "Successfully sent manual WebSocket message: " + message;
    }
}