package org.design.designurlshortenerredirector.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.design.designurlshortenerredirector.service.rest.api.RedirectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RedirectController {

    private final RedirectService service;

//    @GetMapping("/redirect/{code}")
//    public ResponseEntity<Void> redirect(@PathVariable("code") String code,
//                                         HttpServletResponse response) throws IOException {
//
//        log.info("Resovling big url");
//        String target = service.resolve(code);
//
//        // Optional: record analytics (clicks, IP, UA) here
//        // ...
//
//        response.sendRedirect(target);               // 302
//        return ResponseEntity.status(302).build();   // explicit for metrics
//    }

    @GetMapping("/redirect/{code}")
    public Mono<ResponseEntity<Void>> redirect(@PathVariable("code") String code) {

        log.info("Resolving big url for code: {}", code);

        return service.resolve(code)
                .flatMap(target -> {
                    // Success Path: Returns ResponseEntity<Void> (302)
                    return Mono.just(ResponseEntity
                            .<Void>status(HttpStatus.FOUND) // Explicitly type the builder as <Void>
                            .header("Location", target)
                            .build());
                });
//                .onErrorResume(NotFoundException.class, e -> {
//                    // Error Path: Returns ResponseEntity<Void> (404)
//                    // We explicitly specify the type parameter on Mono.just()
//                    return Mono.<ResponseEntity<Void>>just(ResponseEntity.notFound().build());
//                });
    }
}