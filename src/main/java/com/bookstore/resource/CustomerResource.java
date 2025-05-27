package com.bookstore.resource;

import com.bookstore.model.Customer;
import com.bookstore.storage.InMemoryStorage;
import com.bookstore.exception.*;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Path("/customers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerResource {
    private static final Logger LOGGER = Logger.getLogger(CustomerResource.class.getName());

    @POST
    public Response createCustomer(Customer customer) {
        LOGGER.info("Creating customer: " + customer.getEmail());
        validateCustomer(customer);
        if (customer.getId() == null || customer.getId().isEmpty()) {
            customer.setId(InMemoryStorage.generateId("customer"));
        }
        InMemoryStorage.getCustomers().put(customer.getId(), customer);
        return Response.status(Response.Status.CREATED).entity(customer).build();
    }

    @GET
    public List<Customer> getAllCustomers() {
        LOGGER.info("Fetching all customers");
        return new ArrayList<>(InMemoryStorage.getCustomers().values());
    }

    @GET
    @Path("/{id}")
    public Customer getCustomer(@PathParam("id") String id) {
        LOGGER.info("Fetching customer with ID: " + id);
        Customer customer = InMemoryStorage.getCustomers().get(id);
        if (customer == null) {
            throw new CustomerNotFoundException("Customer with ID " + id + " does not exist.");
        }
        return customer;
    }

    @PUT
    @Path("/{id}")
    public Customer updateCustomer(@PathParam("id") String id, Customer customer) {
        LOGGER.info("Updating customer with ID: " + id);
        if (!InMemoryStorage.getCustomers().containsKey(id)) {
            throw new CustomerNotFoundException("Customer with ID " + id + " does not exist.");
        }
        validateCustomer(customer);
        customer.setId(id);
        InMemoryStorage.getCustomers().put(id, customer);
        return customer;
    }

    @DELETE
    @Path("/{id}")
    public Response deleteCustomer(@PathParam("id") String id) {
        LOGGER.info("Deleting customer with ID: " + id);
        if (!InMemoryStorage.getCustomers().containsKey(id)) {
            throw new CustomerNotFoundException("Customer with ID " + id + " does not exist.");
        }
        InMemoryStorage.getCustomers().remove(id);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    private void validateCustomer(Customer customer) {
        if (customer.getFirstName() == null || customer.getFirstName().isEmpty()) {
            throw new InvalidInputException("First name is required.");
        }
        if (customer.getLastName() == null || customer.getLastName().isEmpty()) {
            throw new InvalidInputException("Last name is required.");
        }
        if (customer.getEmail() == null || !customer.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new InvalidInputException("Valid email is required.");
        }
        if (customer.getPassword() == null || customer.getPassword().length() < 8) {
            throw new InvalidInputException("Password must be at least 8 characters long.");
        }
    }
}