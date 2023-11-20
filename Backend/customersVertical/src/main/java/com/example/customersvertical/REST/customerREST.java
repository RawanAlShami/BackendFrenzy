package com.example.customersvertical.REST;
import java.util.List;
import jakarta.ws.rs.*;
import jakarta.ejb.EJB;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import com.example.customersvertical.Entities.Order;
import com.example.customersvertical.Entities.Customer;
import com.example.customersvertical.Services.customerService;

@Path("/customer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class customerREST
{
    @EJB
    customerService customerService;

    @GET
    @Path("/getUsername")
    public String getUsername(@Context HttpServletRequest request)
    {
        HttpSession session = request.getSession();
        String loggedUsername = (String) session.getAttribute("customerUsername");
        if(loggedUsername != null)
            return loggedUsername;
        return null;
    }

    @POST
    @Path("/register")
    public boolean register(Customer customer)
    {
        return customerService.register(customer);
    }

    @PUT
    @Path("/login")
    public boolean login(Customer customer, @Context HttpServletRequest request)
    {
        Customer currentCustomer = customerService.login(customer);
        if(currentCustomer != null)
        {
            HttpSession session = request.getSession();
            session.setAttribute("customerUsername", currentCustomer.getUsername());
            return true;
        }
        return false;
    }

    @PUT
    @Path("/logout")
    public boolean logout(@Context HttpServletRequest request)
    {
        HttpSession session = request.getSession(false);
        if(session != null)
        {
            String loggedUsername = (String) session.getAttribute("customerUsername");
            session.invalidate();
            return customerService.logout(loggedUsername);
        }
        return false;
    }

    @GET
    @Path("/getAllCustomers")
    public List<Customer> getAllCustomers(@Context HttpServletRequest request)
    {
        if(request.getHeader("Authorization") != null)
            return customerService.getAllCustomers();
        return null;
    }

    @POST
    @Path("/placeOrder")
    public String placeOrder(List<Order> inCart, @Context HttpServletRequest request)
    {
        HttpSession session = request.getSession();
        String loggedUsername = (String) session.getAttribute("customerUsername");
        if(loggedUsername != null)
            return customerService.placeOrder(inCart, loggedUsername, session.getId());
        return null;
    }

    @GET
    @Path("/getOrdersByStatus/{status}")
    public String getOrdersByStatus(@PathParam("status") String status, @Context HttpServletRequest request)
    {
        HttpSession session = request.getSession();
        String loggedUsername = (String) session.getAttribute("customerUsername");
        if(loggedUsername != null)
            return customerService.getOrdersByStatus(loggedUsername, status, session.getId());
        return null;
    }
}