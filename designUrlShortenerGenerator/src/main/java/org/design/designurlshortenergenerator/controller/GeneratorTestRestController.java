package org.design.designurlshortenergenerator.controller;

import org.design.designurlshortenergenerator.service.WebSocketClientService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@RequestMapping("/api/shorten")
public class GeneratorTestRestController {

    private final WebClient webClient;
    private final WebSocketClientService webSocketClientService;
    // Порт Redirector для REST
    private static final String REDIRECTOR_URL = "http://localhost:8085";

    public GeneratorTestRestController(WebClient.Builder webClientBuilder, WebSocketClientService webSocketClientService) {
        this.webClient = webClientBuilder.baseUrl(REDIRECTOR_URL).build();
        this.webSocketClientService = webSocketClientService;
    }

    /**
     * Endpoint для тестування REST комунікації (HTTP).
     */
    @GetMapping("/trigger-rest")
    public String triggerRestRequest() {
        // --- START TIME ---
        long startTime = System.nanoTime();

        System.out.println("Generator: Triggering REST request to Redirector...");

        String response = webClient.get()
                .uri("/redirect/status") // Шлях у Redirector Controller
                .retrieve()
                .bodyToMono(String.class)
                .block(); // Чекаємо на відповідь

        // --- END TIME ---
        long endTime = System.nanoTime();
        long durationNs = endTime - startTime;
        double durationMs = durationNs / 1_000_000.0;

        System.out.printf("Generator: REST response received. Duration: %.3f ms.\n", durationMs);

        return String.format(
                "REST Communication successful! Redirector said: [%s]. Total time: %.3f ms.",
                response,
                durationMs
        );
    }

    /**
     * Endpoint для тестування WS комунікації.
     * Викликає надсилання повідомлення через WebSocket.
     */
    @GetMapping("/trigger-ws")
    public String triggerWsRequest() {
        String testLink = "http://localhost:8085/link-ws-" + System.currentTimeMillis();

        // Надсилаємо запит, вимірювання часу відбудеться асинхронно у WebSocketClientService
        webSocketClientService.sendLinkRequest(testLink);

        return "WebSocket request sent for link: " + testLink +
                ". Check **Generator Console** for the asynchronous response time (latency).";
    }
}