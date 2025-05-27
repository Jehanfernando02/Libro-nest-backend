package com.bookstore.resource;

import com.bookstore.model.Book;
import com.bookstore.model.Cart;
import com.bookstore.model.Order;
import com.bookstore.storage.InMemoryStorage;
import com.bookstore.exception.*;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Path("/customers/{customerId}/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource {
    private static final Logger LOGGER = Logger.getLogger(OrderResource.class.getName());

    @POST
    public Response createOrder(@PathParam("customerId") String customerId) {
        LOGGER.info("Creating order for customer: " + customerId);
        validateCustomer(customerId);
        Cart cart = InMemoryStorage.getCarts().get(customerId);
        if (cart == null || cart.getItems().isEmpty()) {
            throw new InvalidInputException("Cart is empty or does not exist.");
        }
        
        // Calculate total price and verify stock for all items
        double totalPrice = 0;
        Map<String, Integer> orderItems = new HashMap<>(cart.getItems());
        for (Map.Entry<String, Integer> entry : orderItems.entrySet()) {
            String bookId = entry.getKey();
            int quantity = entry.getValue();
            Book book = InMemoryStorage.getBooks().get(bookId);
            if (book == null) {
                throw new BookNotFoundException("Book with ID " + bookId + " does not exist.");
            }
            if (book.getStock() < quantity) {
                throw new OutOfStockException("Insufficient stock for book ID " + bookId);
            }
            totalPrice += book.getPrice() * quantity;
        }

        // Create order and update stock
        String orderId = InMemoryStorage.generateId("order");
        Order order = new Order(orderId, customerId, orderItems, totalPrice);
        InMemoryStorage.getOrders().put(orderId, order);

        for (Map.Entry<String, Integer> entry : orderItems.entrySet()) {
            String bookId = entry.getKey();
            int quantity = entry.getValue();
            Book book = InMemoryStorage.getBooks().get(bookId);
            book.setStock(book.getStock() - quantity);
        }
        InMemoryStorage.getCarts().remove(customerId);

        return Response.status(Response.Status.CREATED).entity(order).build();
    }

    @GET
    public List<Order> getOrders(@PathParam("customerId") String customerId) {
        LOGGER.info("Fetching orders for customer: " + customerId);
        validateCustomer(customerId);
        return InMemoryStorage.getOrders().values().stream()
                .filter(order -> order.getCustomerId().equals(customerId))
                .collect(Collectors.toList());
    }

    @GET
    @Path("/{orderId}")
    public Order getOrder(@PathParam("customerId") String customerId, @PathParam("orderId") String orderId) {
        LOGGER.info("Fetching order with ID: " + orderId);
        validateCustomer(customerId);
        Order order = InMemoryStorage.getOrders().get(orderId);
        if (order == null || !order.getCustomerId().equals(customerId)) {
            throw new InvalidInputException("Order with ID " + orderId + " does not exist for this customer.");
        }
        return order;
    }

    private void validateCustomer(String customerId) {
        if (!InMemoryStorage.getCustomers().containsKey(customerId)) {
            throw new CustomerNotFoundException("Customer with ID " + customerId + " does not exist.");
        }
    }
}