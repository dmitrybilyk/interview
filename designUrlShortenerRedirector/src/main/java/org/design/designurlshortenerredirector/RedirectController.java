package org.design.designurlshortenerredirector;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RedirectController {

    private final RedirectService service;

    @GetMapping("/redirect/{code}")
    public ResponseEntity<Void> redirect(@PathVariable("code") String code,
                                         HttpServletResponse response) throws IOException {

        log.info("Resovling big url");
        String target = service.resolve(code);

        // Optional: record analytics (clicks, IP, UA) here
        // ...

        response.sendRedirect(target);               // 302
        return ResponseEntity.status(302).build();   // explicit for metrics
    }
}