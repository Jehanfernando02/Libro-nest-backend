package com.bookstore.resource;

import com.bookstore.model.Author;
import com.bookstore.model.Book;
import com.bookstore.storage.InMemoryStorage;
import com.bookstore.exception.*;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Path("/authors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthorResource {
    private static final Logger LOGGER = Logger.getLogger(AuthorResource.class.getName());

    @POST
    public Response createAuthor(Author author) {
        LOGGER.info("Creating author: " + author.getFirstName() + " " + author.getLastName());
        validateAuthor(author);
        
        // Generate a unique ID if not provided by the client
        if (author.getId() == null || author.getId().isEmpty()) {
            author.setId(InMemoryStorage.generateId("author"));
        }

        InMemoryStorage.getAuthors().put(author.getId(), author);
        return Response.status(Response.Status.CREATED).entity(author).build();
    }

    @GET
    public List<Author> getAllAuthors() {
        LOGGER.info("Fetching all authors");
        return new ArrayList<>(InMemoryStorage.getAuthors().values());
    }

    @GET
    @Path("/{id}")
    public Author getAuthor(@PathParam("id") String id) {
        LOGGER.info("Fetching author with ID: " + id);
        Author author = InMemoryStorage.getAuthors().get(id);
        if (author == null) {
            throw new AuthorNotFoundException("Author with ID " + id + " does not exist.");
        }
        return author;
    }

    @PUT
    @Path("/{id}")
    public Author updateAuthor(@PathParam("id") String id, Author author) {
        LOGGER.info("Updating author with ID: " + id);
        if (!InMemoryStorage.getAuthors().containsKey(id)) {
            throw new AuthorNotFoundException("Author with ID " + id + " does not exist.");
        }
        validateAuthor(author);
        author.setId(id);
        InMemoryStorage.getAuthors().put(id, author);
        return author;
    }

    @DELETE
    @Path("/{id}")
    public Response deleteAuthor(@PathParam("id") String id) {
        LOGGER.info("Deleting author with ID: " + id);
        if (!InMemoryStorage.getAuthors().containsKey(id)) {
            throw new AuthorNotFoundException("Author with ID " + id + " does not exist.");
        }
        InMemoryStorage.getAuthors().remove(id);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @GET
    @Path("/{id}/books")
    public List<Book> getBooksByAuthor(@PathParam("id") String id) {
        // Fetch all books written by the specified author
        LOGGER.info("Fetching books for author with ID: " + id);
        if (!InMemoryStorage.getAuthors().containsKey(id)) {
            throw new AuthorNotFoundException("Author with ID " + id + " does not exist.");
        }
        return InMemoryStorage.getBooks().values().stream()
                .filter(book -> book.getAuthorId().equals(id))
                .collect(Collectors.toList());
    }

    private void validateAuthor(Author author) {
        // Ensure required fields are present and valid
        if (author.getFirstName() == null || author.getFirstName().isEmpty()) {
            throw new InvalidInputException("First name is required.");
        }
        if (author.getLastName() == null || author.getLastName().isEmpty()) {
            throw new InvalidInputException("Last name is required.");
        }
    }
}