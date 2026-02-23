package org.design.designurlshortenergenerator.persistance.mongo.model;

import org.design.designurlshortenergenerator.persistance.mongo.model.repository.MongoUrlMappingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/perf")
public class PerformanceTestController {

    @Autowired
    private MongoUrlMappingRepository repo;

    @GetMapping("/by-id/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        long start = System.nanoTime();
        var result = repo.findById(id);
        long durationMs = (System.nanoTime() - start) / 1_000_000;

        return ResponseEntity.ok(
            Map.of(
                "durationMs", durationMs,
                "found", result.isPresent(),
                "data", result.orElse(null)
            )
        );
    }

    @GetMapping("/random/{count}")
    public ResponseEntity<?> getRandom(@PathVariable int count) {
        Random r = new Random();
        long start = System.nanoTime();

        List<MongoUrlMapping> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            long id = 1 + r.nextInt(100_000);
            repo.findById(id).ifPresent(list::add);
        }

        long durationMs = (System.nanoTime() - start) / 1_000_000;

        return ResponseEntity.ok(
            Map.of(
                "durationMs", durationMs,
                "size", list.size(),
                "sample", list.stream().limit(3).toList()
            )
        );
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAll() {
        long start = System.nanoTime();
        List<MongoUrlMapping> all = repo.findAll();
        long durationMs = (System.nanoTime() - start) / 1_000_000;

        return ResponseEntity.ok(
            Map.of(
                "durationMs", durationMs,
                "count", all.size()
            )
        );
    }
}
