package com.conduct.solr.runner;

import com.conduct.solr.model.Item;
import com.conduct.solr.service.SolrSearchService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final SolrSearchService searchService;

    public DataInitializer(SolrSearchService searchService) {
        this.searchService = searchService;
    }

    @Override
    public void run(String... args) throws InterruptedException {
        List<Item> items = List.of(
                new Item("1", "Apache Solr Search Platform",
                        "Solr is a popular, blazing-fast, open source enterprise search platform built on Apache Lucene.",
                        "technology", 0.00),
                new Item("2", "Spring Boot with SolrJ",
                        "SolrJ is the Java client library for interacting with Apache Solr.",
                        "development", 29.99),
                new Item("3", "Distributed Search at Scale",
                        "Learn how to scale Solr across multiple nodes for high availability.",
                        "architecture", 49.99),
                new Item("4", "Full-Text Search Techniques",
                        "Master full-text search with Solr: tokenization, stemming, and relevance scoring.",
                        "technology", 39.99),
                new Item("5", "Solr Cloud: SolrCloud Mode",
                        "Deploy and manage Solr in cloud-native mode with ZooKeeper.",
                        "devops", 59.99),
                new Item("6", "Faceted Search with Solr",
                        "Implement faceted navigation to let users filter search results by category and price.",
                        "development", 34.99),
                new Item("7", "Solr Query Syntax",
                        "Deep dive into Solr query syntax: standard, dismax, edismax, and boost queries.",
                        "technology", 24.99),
                new Item("8", "Indexing PDF and Documents",
                        "Use Solr Cell to index PDF, Word, and other binary documents.",
                        "devops", 44.99)
        );

        for (int i = 0; i < 10; i++) {
            try {
                if (searchService.getAll().isEmpty()) {
                    searchService.indexAll(items);
                    System.out.println("Indexed " + items.size() + " sample documents into Solr.");
                } else {
                    System.out.println("Solr already has data, skipping initialization.");
                }
                return;
            } catch (Exception e) {
                System.out.println("Waiting for Solr... (" + (i + 1) + "/10)");
                Thread.sleep(3000);
            }
        }
        System.out.println("Could not connect to Solr after 10 retries.");
    }
}
