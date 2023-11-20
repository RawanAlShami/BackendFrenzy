package com.example.adminsvertical.REST;
import jakarta.ejb.EJB;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import com.example.adminsvertical.Entities.Admin;
import com.example.adminsvertical.Services.adminService;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/admin")
public class adminREST
{
    @EJB
    adminService adminService;

    @POST
    @Path("/register")
    public boolean register(Admin admin)
    {
        return adminService.register(admin);
    }

    @PUT
    @Path("/login")
    public boolean login(Admin admin, @Context HttpServletRequest request)
    {
        Admin currentAdmin = adminService.login(admin);
        if(currentAdmin != null)
        {
            HttpSession session = request.getSession();
            session.setAttribute("adminUsername", currentAdmin.getUsername());
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
            String loggedUsername = (String) session.getAttribute("adminUsername");
            session.invalidate();
            return adminService.logout(loggedUsername);
        }
        return false;
    }

    @POST
    @Path("/generateSellerAccount/{sellerName}")
    public String generateSellerAccount(@PathParam("sellerName") String sellerName, @Context HttpServletRequest request)
    {
        HttpSession session = request.getSession();
        if(session.getAttribute("adminUsername") != null)
            return adminService.generateSellerAccount(sellerName, session.getId());
        return null;
    }

    @POST
    @Path("/generateCourierAccount/{courierName}/{binaryEncodedZones}")
    public String generateCourierAccount(@PathParam("courierName") String courierName , @PathParam("binaryEncodedZones") String binaryEncodedZones, @Context HttpServletRequest request)
    {
        HttpSession session = request.getSession();
        if(session.getAttribute("adminUsername") != null)
            return adminService.generateCourierAccount(courierName, binaryEncodedZones, session.getId());
        return null;
    }

    @GET
    @Path("/getAllUsers/{userType}")
    public String getAllUsers(@PathParam("userType") String userType, @Context HttpServletRequest request)
    {
        HttpSession session = request.getSession();
        if(session.getAttribute("adminUsername") != null)
            return adminService.getAllUsers(userType, session.getId());
        return null;
    }
}