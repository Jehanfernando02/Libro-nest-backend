package com.bookstore.resource;

import com.bookstore.model.Book;
import com.bookstore.model.Cart;
import com.bookstore.storage.InMemoryStorage;
import com.bookstore.exception.*;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;
import java.util.logging.Logger;

@Path("/customers/{customerId}/cart")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CartResource {
    private static final Logger LOGGER = Logger.getLogger(CartResource.class.getName());

    @POST
    @Path("/items")
    public Response addItemToCart(@PathParam("customerId") String customerId, Map<String, Object> item) {
        LOGGER.info("Adding item to cart for customer: " + customerId);
        validateCustomer(customerId);

        // Extract and validate bookId and quantity from the input map
        Object bookIdObj = item.get("bookId");
        Object quantityObj = item.get("quantity");
        if (bookIdObj == null || !(bookIdObj instanceof String) || quantityObj == null || !(quantityObj instanceof Integer)) {
            throw new InvalidInputException("Book ID (string) and quantity (integer) are required.");
        }

        String bookId = (String) bookIdObj;
        Integer quantity = (Integer) quantityObj;
        if (quantity <= 0) {
            throw new InvalidInputException("Quantity must be positive.");
        }

        Book book = InMemoryStorage.getBooks().get(bookId);
        if (book == null) {
            throw new BookNotFoundException("Book with ID " + bookId + " does not exist.");
        }
        if (book.getStock() < quantity) {
            throw new OutOfStockException("Insufficient stock for book ID " + bookId);
        }

        // Create a new cart if none exists for the customer
        Cart cart = InMemoryStorage.getCarts().computeIfAbsent(customerId, k -> {
            Cart newCart = new Cart(customerId);
            newCart.setId(InMemoryStorage.generateId("cart"));
            return newCart;
        });
        cart.getItems().put(bookId, cart.getItems().getOrDefault(bookId, 0) + quantity);
        book.setStock(book.getStock() - quantity);
        return Response.status(Response.Status.CREATED).entity(cart).build();
    }

    @GET
    public Cart getCart(@PathParam("customerId") String customerId) {
        LOGGER.info("Fetching cart for customer: " + customerId);
        validateCustomer(customerId);
        Cart cart = InMemoryStorage.getCarts().get(customerId);
        if (cart == null) {
            throw new CartNotFoundException("Cart for customer ID " + customerId + " does not exist.");
        }
        return cart;
    }

    @PUT
    @Path("/items/{bookId}")
    public Response updateItemInCart(@PathParam("customerId") String customerId, @PathParam("bookId") String bookId, Map<String, Object> item) {
        LOGGER.info("Updating item in cart for customer: " + customerId);
        validateCustomer(customerId);

        Object quantityObj = item.get("quantity");
        if (quantityObj == null || !(quantityObj instanceof Integer)) {
            throw new InvalidInputException("Quantity (integer) is required.");
        }

        Integer quantity = (Integer) quantityObj;
        if (quantity < 0) {
            throw new InvalidInputException("Quantity cannot be negative.");
        }

        Cart cart = InMemoryStorage.getCarts().get(customerId);
        if (cart == null) {
            throw new CartNotFoundException("Cart for customer ID " + customerId + " does not exist.");
        }

        Book book = InMemoryStorage.getBooks().get(bookId);
        if (book == null) {
            throw new BookNotFoundException("Book with ID " + bookId + " does not exist.");
        }

        // Adjust stock based on the difference between new and current quantities
        int currentQuantity = cart.getItems().getOrDefault(bookId, 0);
        int stockAdjustment = quantity - currentQuantity;
        if (book.getStock() < stockAdjustment) {
            throw new OutOfStockException("Insufficient stock for book ID " + bookId);
        }

        if (quantity == 0) {
            cart.getItems().remove(bookId);
        } else {
            cart.getItems().put(bookId, quantity);
        }
        book.setStock(book.getStock() - stockAdjustment);
        return Response.ok(cart).build();
    }

    @DELETE
    @Path("/items/{bookId}")
    public Response removeItemFromCart(@PathParam("customerId") String customerId, @PathParam("bookId") String bookId) {
        LOGGER.info("Removing item from cart for customer: " + customerId);
        validateCustomer(customerId);
        Cart cart = InMemoryStorage.getCarts().get(customerId);
        if (cart == null) {
            throw new CartNotFoundException("Cart for customer ID " + customerId + " does not exist.");
        }
        if (!cart.getItems().containsKey(bookId)) {
            throw new BookNotFoundException("Book with ID " + bookId + " is not in the cart.");
        }
        int quantity = cart.getItems().get(bookId);
        cart.getItems().remove(bookId);
        Book book = InMemoryStorage.getBooks().get(bookId);
        book.setStock(book.getStock() + quantity);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    private void validateCustomer(String customerId) {
        if (!InMemoryStorage.getCustomers().containsKey(customerId)) {
            throw new CustomerNotFoundException("Customer with ID " + customerId + " does not exist.");
        }
    }
}