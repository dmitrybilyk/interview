package com.another.boot;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TriggerController {

    private final BlockingCallService blockingCallService;

    public TriggerController(BlockingCallService blockingCallService) {
        this.blockingCallService = blockingCallService;
    }

    @GetMapping("/test/rest")
    public String testRest(@RequestParam(defaultValue = "10") int count) {
        blockingCallService.testWithRestTemplate(count);
        return "Triggered " + count + " blocking REST calls.";
    }

    @GetMapping("/test/webclient")
    public String testWebClient(@RequestParam(defaultValue = "10") int count) {
        blockingCallService.testWithWebClient(count);
        return "Triggered " + count + " non-blocking WebClient calls.";
    }
}
