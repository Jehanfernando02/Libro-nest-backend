package com.bookstore.storage;

import com.bookstore.model.Author;
import com.bookstore.model.Book;
import com.bookstore.model.Cart;
import com.bookstore.model.Customer;
import com.bookstore.model.Order;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory storage for bookstore entities with synchronized sequential ID generation.
 */
public class InMemoryStorage {

    private static final Map<String, Book> books = new ConcurrentHashMap<>();
    private static final Map<String, Author> authors = new ConcurrentHashMap<>();
    private static final Map<String, Customer> customers = new ConcurrentHashMap<>();
    private static final Map<String, Cart> carts = new ConcurrentHashMap<>();
    private static final Map<String, Order> orders = new ConcurrentHashMap<>();

    private static long authorCounter = 0;
    private static long bookCounter = 0;
    private static long customerCounter = 0;
    private static long cartCounter = 0;
    private static long orderCounter = 0;

    public static Map<String, Book> getBooks() {
        return books;
    }

    public static Map<String, Author> getAuthors() {
        return authors;
    }

    public static Map<String, Customer> getCustomers() {
        return customers;
    }

    public static Map<String, Cart> getCarts() {
        return carts;
    }

    public static Map<String, Order> getOrders() {
        return orders;
    }

    public static synchronized String generateId(String entityType) {
        
        // Generate a unique ID for the given entity type
        switch (entityType.toLowerCase()) {
            case "author":
                authorCounter = recalculateCounter(authors, "author");
                return "author" + (++authorCounter);
            case "book":
                bookCounter = recalculateCounter(books, "book");
                return "book" + (++bookCounter);
            case "customer":
                customerCounter = recalculateCounter(customers, "customer");
                return "customer" + (++customerCounter);
            case "cart":
                cartCounter = recalculateCounter(carts, "cart");
                return "cart" + (++cartCounter);
            case "order":
                orderCounter = recalculateCounter(orders, "order");
                return "order" + (++orderCounter);
            default:
                throw new IllegalArgumentException("Unknown entity type: " + entityType);
        }
    }

    // Generic counter recalculation based on existing map keys
    private static long recalculateCounter(Map<String, ?> map, String prefix) {
        // Recalculate the counter based on the highest existing ID
        if (map.isEmpty()) {
            return 0;
        }
        return map.keySet().stream()
                .map(id -> id.replace(prefix, ""))
                .filter(num -> num.matches("\\d+"))
                .mapToLong(Long::parseLong)
                .max()
                .orElse(0);
    }
}
