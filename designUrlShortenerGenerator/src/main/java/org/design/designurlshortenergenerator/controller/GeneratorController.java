package org.design.designurlshortenergenerator.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.design.designurlshortenergenerator.controller.dto.CreateReq;
import org.design.designurlshortenergenerator.service.api.UrlGeneratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.PostConstruct;
import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GeneratorController {

    private final UrlGeneratorService urlService;

    @PostConstruct
    public void init() {
        log.info("Curator Version: {}",
                org.apache.curator.framework.CuratorFramework.class.getPackage().getImplementationVersion());
    }

    @GetMapping("/get")
    public ResponseEntity<String> get() {
        return ResponseEntity.ok("Got something");
    }

    @GetMapping("/codes/{code}")
    public ResponseEntity<String> getOriginalUrl(@PathVariable("code") String code) {
        log.info("Received request to resolve code: {}", code);

        String originalUrl = urlService.getOriginalUrlByCode(code);

        if (originalUrl != null) {
            return ResponseEntity.ok(originalUrl);
        } else {
            log.warn("Code not found: {}", code);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/shorten")
    public ResponseEntity<String> shorten(@RequestBody @Valid CreateReq req) {
        String code = urlService.shortenUrl(req.url());
        return ResponseEntity.created(URI.create("/" + code)).body(code);
    }

    @DeleteMapping("/short/{code}")
    public ResponseEntity<Void> delete(@PathVariable("code") String code) {
        urlService.deleteByCode(code);
        return ResponseEntity.noContent().build();
    }
}