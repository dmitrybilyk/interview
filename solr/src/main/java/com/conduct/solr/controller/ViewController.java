package com.conduct.solr.controller;

import com.conduct.solr.model.Item;
import com.conduct.solr.service.SolrSearchService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ViewController {

    private final SolrSearchService searchService;

    public ViewController(SolrSearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/")
    public String index() {
        return "search";
    }

    @GetMapping("/search")
    public String search(@RequestParam(defaultValue = "") String q,
                         @RequestParam(defaultValue = "10") int rows,
                         Model model) {
        List<Item> results;
        if (q.isEmpty()) {
            results = searchService.getAll();
        } else {
            results = searchService.search(q, rows);
        }
        model.addAttribute("query", q);
        model.addAttribute("results", results);
        return "search";
    }
}
