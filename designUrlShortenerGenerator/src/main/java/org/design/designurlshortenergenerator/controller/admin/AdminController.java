package org.design.designurlshortenergenerator.controller.admin;

import lombok.RequiredArgsConstructor;
import org.design.designurlshortenergenerator.service.generator.impl.UrlGeneratorServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/system")
@RequiredArgsConstructor
public class AdminController {

    private final UrlGeneratorServiceImpl urlGeneratorService;

    /**
     * Toggles the maintenance mode of the URL generator.
     * Request body: {"enabled": true} or {"enabled": false}
     */
    @PostMapping("/maintenance")
    public ResponseEntity<Map<String, Object>> toggleMaintenance(@RequestBody Map<String, Boolean> request) {
        boolean enable = request.getOrDefault("enabled", false);
        
        urlGeneratorService.toggleMaintenanceMode(enable);
        
        String status = enable ? "Maintenance Mode Enabled" : "System Active";
        return ResponseEntity.ok(Map.of(
            "status", status,
            "currentState", urlGeneratorService.getCurrentState().getClass().getSimpleName()
        ));
    }

    /**
     * Checks the current state of the service.
     */
    @GetMapping("/status")
    public ResponseEntity<String> getStatus() {
        return ResponseEntity.ok("Current Service State: " + 
               urlGeneratorService.getCurrentState().getClass().getSimpleName());
    }

    @PostMapping("/chaos/mongo")
    public ResponseEntity<String> toggleMongo(@RequestParam boolean fail) {
        urlGeneratorService.toggleMongoFailure(fail);
        return ResponseEntity.ok("Mongo failure simulation: " + (fail ? "ON" : "OFF"));
    }
}