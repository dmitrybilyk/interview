package org.design.designshortenerapigateway;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class RedirectFallbackController {

    @GetMapping("/redirect-fallback")
    public Mono<ResponseEntity<String>> fallback() {
        return Mono.just(ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Redirect service unavailable. Try again later."));
    }
}
