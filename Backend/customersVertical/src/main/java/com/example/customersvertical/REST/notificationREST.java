package com.example.customersvertical.REST;
import java.util.List;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import com.example.customersvertical.Services.notificationService;
import com.example.customersvertical.Entities.Notification;

@Path("/notification")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class notificationREST
{
    @EJB
    notificationService notificationService;

    @POST
    @Path("/persistNotification")
    public boolean persistNotification(Notification notification)
    {
        return notificationService.persistNotification(notification);
    }

    @GET
    @Path("/{username}/getNotifications")
    public List<Notification> getNotifications(@PathParam("username") String username)
    {
        return notificationService.getNotifications(username);
    }
}