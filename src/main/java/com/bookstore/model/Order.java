
package com.bookstore.model;

import java.util.Map;

/**
 *
 * @author jehanfernando
 */

// Created this class order to support OrderResource class
public class Order {
    
    private String id;
    private String customerId;
    private Map<String, Integer> items; // To store the book IDs and their quantities in the order (Book ID -> Quantity)
    private double totalPrice; // Total cost of the order
    

    public Order() {
    
    }

    // Constructor to initialize all order details
    
    public Order(String id, String customerId, Map<String, Integer> items, double totalPrice) {
        this.id = id;
        this.customerId = customerId;
        this.items = items;
        this.totalPrice = totalPrice;
    }
    
    // Getters and Setters

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

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
    
    

  
}
