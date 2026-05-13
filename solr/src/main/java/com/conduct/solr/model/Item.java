package com.conduct.solr.model;

import org.apache.solr.client.solrj.beans.Field;

public class Item {

    @Field
    private String id;

    @Field("title_t")
    private String title;

    @Field("description_t")
    private String description;

    @Field("category_s")
    private String category;

    @Field("price_d")
    private double price;

    public Item() {}

    public Item(String id, String title, String description, String category, double price) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.price = price;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    @Override
    public String toString() {
        return "Item{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", price=" + price +
                '}';
    }
}
