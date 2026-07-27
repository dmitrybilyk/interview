package com.conduct.solr.repository;

import com.conduct.solr.model.Item;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ItemRepository {

    private final SolrClient solrClient;
    private final String core;

    public ItemRepository(SolrClient solrClient, @Value("${solr.core}") String core) {
        this.solrClient = solrClient;
        this.core = core;
    }

    public void save(Item item) throws Exception {
        SolrInputDocument doc = new SolrInputDocument();
        doc.setField("id", item.getId());
        doc.setField("title_t", item.getTitle());
        doc.setField("description_t", item.getDescription());
        doc.setField("category_s", item.getCategory());
        doc.setField("price_d", item.getPrice());
        solrClient.add(core, doc);
        solrClient.commit(core);
    }

    public void saveAll(List<Item> items) throws Exception {
        for (Item item : items) {
            SolrInputDocument doc = new SolrInputDocument();
            doc.setField("id", item.getId());
            doc.setField("title_t", item.getTitle());
            doc.setField("description_t", item.getDescription());
            doc.setField("category_s", item.getCategory());
            doc.setField("price_d", item.getPrice());
            solrClient.add(core, doc);
        }
        solrClient.commit(core);
    }

    public List<Item> search(String queryStr) throws Exception {
        SolrQuery query = new SolrQuery(queryStr);
        query.set("defType", "edismax");
        query.set("qf", "title_t description_t");
        query.setFields("id", "title_t", "description_t", "category_s", "price_d");
        QueryResponse response = solrClient.query(core, query);
        return response.getBeans(Item.class);
    }

    public List<Item> search(String queryStr, int rows) throws Exception {
        SolrQuery query = new SolrQuery(queryStr);
        query.set("defType", "edismax");
        query.set("qf", "title_t description_t");
        query.setRows(rows);
        query.setFields("id", "title_t", "description_t", "category_s", "price_d");
        QueryResponse response = solrClient.query(core, query);
        return response.getBeans(Item.class);
    }

    public List<Item> findAll() throws Exception {
        SolrQuery query = new SolrQuery("*:*");
        query.setRows(100);
        QueryResponse response = solrClient.query(core, query);
        return response.getBeans(Item.class);
    }

    public void deleteById(String id) throws Exception {
        solrClient.deleteById(core, id);
        solrClient.commit(core);
    }

    public void deleteAll() throws Exception {
        solrClient.deleteByQuery(core, "*:*");
        solrClient.commit(core);
    }
}
