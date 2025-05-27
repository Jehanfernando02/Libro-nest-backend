package com.bookstore;

import com.bookstore.resource.*;
import com.bookstore.exception.GenericExceptionMapper;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("/api")
public class BookstoreApplication extends ResourceConfig {
    public BookstoreApplication() {
        packages("com.bookstore.resource"); // Optional, if you're not manually adding each class
        register(RootResource.class);
        register(BookResource.class);
        register(AuthorResource.class);
        register(CustomerResource.class);
        register(CartResource.class);
        register(OrderResource.class);
        register(GenericExceptionMapper.class);
        register(CorsFilter.class); // This will now work more reliably
        register(JacksonFeature.class);
        register(RolesAllowedDynamicFeature.class);
    }
}
