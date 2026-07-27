package com.conduct.solr.controller;

import com.conduct.solr.model.Item;
import com.conduct.solr.service.SolrSearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SolrSearchService searchService;

    public SearchController(SolrSearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping
    public ResponseEntity<List<Item>> search(
            @RequestParam String q,
            @RequestParam(required = false, defaultValue = "10") int rows) {
        List<Item> results = searchService.search(q, rows);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Item>> getAll() {
        return ResponseEntity.ok(searchService.getAll());
    }

    @PostMapping("/index")
    public ResponseEntity<String> index(@RequestBody Item item) {
        searchService.index(item);
        return ResponseEntity.ok("Indexed: " + item.getId());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable String id) {
        searchService.delete(id);
        return ResponseEntity.ok("Deleted: " + id);
    }
}
