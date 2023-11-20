package com.example.couriersvertical.REST;
import java.util.List;
import jakarta.ws.rs.*;
import jakarta.ejb.EJB;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import com.example.couriersvertical.Entities.Courier;
import com.example.couriersvertical.Services.courierService;

@Path("/courier")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class courierREST
{
    @EJB
    courierService courierService;

    @POST
    @Path("/register")
    public boolean register(List<Courier> courierZones, @Context HttpServletRequest request)
    {
        if(request.getHeader("Authorization") != null)
            return courierService.register(courierZones);
        return false;
    }

    @PUT
    @Path("/login")
    public boolean login(Courier courier, @Context HttpServletRequest request)
    {
        Courier currentCourier = courierService.login(courier);
        if(currentCourier != null)
        {
            HttpSession session = request.getSession();
            session.setAttribute("courierUsername", currentCourier.getUsername());
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
            String loggedUsername = (String) session.getAttribute("courierUsername");
            session.invalidate();
            return courierService.logout(loggedUsername);
        }
        return false;
    }

    @GET
    @Path("/getOrdersByStatus/{status}")
    public String getOrdersByStatus(@PathParam("status") String status, @Context HttpServletRequest request)
    {
        HttpSession session = request.getSession();
        String loggedUsername = (String) session.getAttribute("courierUsername");
        if(loggedUsername != null)
            return courierService.getOrdersByStatus(loggedUsername, status, session.getId());
        return null;
    }

    @PUT
    @Path("/updateStatus/{orderId}/{newStatus}")
    public boolean updateStatus(@PathParam("orderId") int orderId, @PathParam("newStatus") String newStatus,  @Context HttpServletRequest request)
    {
        HttpSession session = request.getSession();
        String loggedUsername = (String) session.getAttribute("courierUsername");
        if(loggedUsername != null)
            return courierService.updateStatus(orderId, newStatus, session.getId());
        return false;
    }

    @GET
    @Path("/getAllCouriers")
    public String getAllCouriers(@Context HttpServletRequest request)
    {
        if(request.getHeader("Authorization") != null)
            return courierService.getAllCouriers();
        return null;
    }

    @GET
    @Path("/coversZone/{customersZone}/{customerUsername}/{orderID}")
    public String coversZone(@PathParam("customersZone") String customersZone,@PathParam("customerUsername") String customerUsername, @PathParam("orderID") int orderID, @Context HttpServletRequest request)
    {
        if(request.getHeader("Authorization") != null)
            return courierService.coversZone(customersZone, customerUsername, orderID, request.getSession().getId());
        return null;
    }

    @POST
    @Path("/sendToQueue")
    public void sendToQueue(String request)
    {
        courierService.sendToQueue(request);
    }
}
