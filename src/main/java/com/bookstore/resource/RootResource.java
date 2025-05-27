
package com.bookstore.resource;

/**
 *
 * This class defines the root endpoint of the Bookstore API.
 * When a user accesses the base path (/api/), it returns a welcome message in JSON format.
 */


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class RootResource {
    @GET
    public Response getRoot() {
        
        // Return a welcome message for the API root endpoint
        Map<String, String> response = new HashMap<>();
        response.put("message", "Hello , Welcome to the Bookstore API.");
        return Response.ok(response).build();
    }
}