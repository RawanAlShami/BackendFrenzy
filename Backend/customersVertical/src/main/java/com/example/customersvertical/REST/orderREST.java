package com.example.customersvertical.REST;
import java.util.List;
import jakarta.ejb.EJB;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ejb.Singleton;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import com.example.customersvertical.Entities.Order;
import com.example.customersvertical.Services.orderService;

@Path("/order")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Singleton
public class orderREST
{
    @EJB
    orderService orderService;

    @POST
    @Path("/placeOrder/{customerUsername}")
    public boolean setOrder(@PathParam("customerUsername") String customerUsername, List<Order> inCart, @Context HttpServletRequest request)
    {
        if(request.getHeader("Authorization") != null)
            return orderService.getInstance().placeOrder(customerUsername, inCart, request.getSession().getId());
        return false;
    }

    @GET
    @Path("/getByStatus/{userType}/{username}/{status}")
    public List<Order> getOrdersByStatus (@PathParam("username") String username, @PathParam("userType") String userType, @PathParam("status") String status, @Context HttpServletRequest request)
    {
        if(request.getHeader("Authorization") != null)
            return orderService.getInstance().getOrdersByStatus( username, userType,status);
        return null;
    }

    @PUT
    @Path("/updateStatus/{orderId}/{newStatus}")
    public boolean updateStatus(@PathParam("orderId") int orderId, @PathParam("newStatus") String newStatus, @Context HttpServletRequest request)
    {
        if(request.getHeader("Authorization") != null)
            return orderService.getInstance().updateStatus(orderId,newStatus);
        return false;
    }
}