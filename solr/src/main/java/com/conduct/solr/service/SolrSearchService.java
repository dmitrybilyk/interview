package com.conduct.solr.service;

import com.conduct.solr.model.Item;
import com.conduct.solr.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SolrSearchService {

    private final ItemRepository itemRepository;

    public SolrSearchService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public List<Item> search(String q) {
        try {
            return itemRepository.search(q);
        } catch (Exception e) {
            throw new RuntimeException("Search failed: " + e.getMessage(), e);
        }
    }

    public List<Item> search(String q, int rows) {
        try {
            return itemRepository.search(q, rows);
        } catch (Exception e) {
            throw new RuntimeException("Search failed: " + e.getMessage(), e);
        }
    }

    public List<Item> getAll() {
        try {
            return itemRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch all items: " + e.getMessage(), e);
        }
    }

    public void index(Item item) {
        try {
            itemRepository.save(item);
        } catch (Exception e) {
            throw new RuntimeException("Indexing failed: " + e.getMessage(), e);
        }
    }

    public void indexAll(List<Item> items) {
        try {
            itemRepository.saveAll(items);
        } catch (Exception e) {
            throw new RuntimeException("Bulk indexing failed: " + e.getMessage(), e);
        }
    }

    public void delete(String id) {
        try {
            itemRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Delete failed: " + e.getMessage(), e);
        }
    }
}
