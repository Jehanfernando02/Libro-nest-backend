package com.bookstore.model;

import java.util.HashMap;
import java.util.Map;

public class Cart {
    private String id;
    private String customerId;
    private Map<String, Integer> items;
    
    public Cart() {
    // Initialize items map to avoid null pointer issues
        this.items = new HashMap<>();
    }
    
    public Cart(String customerId) {
        this.customerId = customerId;
        this.items = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public Map<String, Integer> getItems() {
        return items;
    }

    public void setItems(Map<String, Integer> items) {
        this.items = items;
    }
}