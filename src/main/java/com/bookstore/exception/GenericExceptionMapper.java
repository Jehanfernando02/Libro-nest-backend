package com.bookstore.exception;

import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


@Provider
public class GenericExceptionMapper implements ExceptionMapper<RuntimeException> {
    @Override
    public Response toResponse(RuntimeException exception) {
        Map<String, String> error = new HashMap<>();
        int status;

        // Handling not-found exceptions with 404 status code
        
        if (exception instanceof BookNotFoundException || 
            exception instanceof AuthorNotFoundException || 
            exception instanceof CustomerNotFoundException || 
            exception instanceof CartNotFoundException) {
            status = 404;
            error.put("error", exception.getClass().getSimpleName());
        } 
        
          // Handling client input issues with 400 status codes
        
        else if (exception instanceof InvalidInputException || 
                   exception instanceof OutOfStockException) {
            status = 400;
            error.put("error", exception.getClass().getSimpleName());
            
        } 
        
        // Assuming all other exceptions are internal server errors (500)
        
        else {
            status = 500;
            error.put("error", "InternalServerError");
        }
        error.put("message", exception.getMessage());
        return Response.status(status).entity(error).build();
    }
}