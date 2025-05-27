package com.bookstore.resource;

import com.bookstore.model.Book;
import com.bookstore.storage.InMemoryStorage;
import com.bookstore.exception.*;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Path("/books")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BookResource {
    private static final Logger LOGGER = Logger.getLogger(BookResource.class.getName());

    @POST
    public Response createBook(Book book) {
        LOGGER.info("Creating book: " + book.getTitle());
        validateBook(book);

        // Auto-generate ID if not provided
        if (book.getId() == null || book.getId().isEmpty()) {
            book.setId(InMemoryStorage.generateId("book"));
        }

        InMemoryStorage.getBooks().put(book.getId(), book);
        return Response.status(Response.Status.CREATED).entity(book).build();
    }

    @GET
    public List<Book> getAllBooks() {
        LOGGER.info("Fetching all books");
        return new ArrayList<>(InMemoryStorage.getBooks().values());
    }

    @GET
    @Path("/{id}")
    public Book getBook(@PathParam("id") String id) {
        LOGGER.info("Fetching book with ID: " + id);
        Book book = InMemoryStorage.getBooks().get(id);
        if (book == null) {
            throw new BookNotFoundException("Book with ID " + id + " does not exist.");
        }
        return book;
    }

    @PUT
    @Path("/{id}")
    public Book updateBook(@PathParam("id") String id, Book book) {
        LOGGER.info("Updating book with ID: " + id);
        if (!InMemoryStorage.getBooks().containsKey(id)) {
            throw new BookNotFoundException("Book with ID " + id + " does not exist.");
        }
        validateBook(book);
        book.setId(id);
        InMemoryStorage.getBooks().put(id, book);
        return book;
    }

    @DELETE
    @Path("/{id}")
    public Response deleteBook(@PathParam("id") String id) {
        LOGGER.info("Deleting book with ID: " + id);
        if (!InMemoryStorage.getBooks().containsKey(id)) {
            throw new BookNotFoundException("Book with ID " + id + " does not exist.");
        }
        InMemoryStorage.getBooks().remove(id);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    private void validateBook(Book book) {
        // Validate all required fields and business rules
        if (book.getTitle() == null || book.getTitle().isEmpty()) {
            throw new InvalidInputException("Book title is required.");
        }
        if (book.getAuthorId() == null || !InMemoryStorage.getAuthors().containsKey(book.getAuthorId())) {
            throw new AuthorNotFoundException("Author with ID " + book.getAuthorId() + " does not exist.");
        }
        if (book.getIsbn() == null || book.getIsbn().isEmpty()) {
            throw new InvalidInputException("ISBN is required.");
        }
        if (book.getPublicationYear() > 2025) {
            throw new InvalidInputException("Publication year must not exceed the system's current year (2025).");
        }
        if (book.getPrice() <= 0) {
            throw new InvalidInputException("Price must be positive.");
        }
        if (book.getStock() < 0) {
            throw new InvalidInputException("Stock cannot be negative.");
        }
    }
}