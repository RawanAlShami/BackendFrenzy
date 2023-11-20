package com.example.sellersvertical.REST;
import java.util.List;

import com.example.sellersvertical.Entities.Product;
import jakarta.ejb.EJB;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import com.example.sellersvertical.Entities.Seller;
import com.example.sellersvertical.Services.sellerService;

@Path("/seller")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class sellerREST
{
    @EJB
    sellerService sellerService;

    @GET
    @Path("/getUsername")
    public String getUsername(@Context HttpServletRequest request)
    {
        HttpSession session = request.getSession();
        String loggedUsername = (String) session.getAttribute("sellerUsername");
        if(loggedUsername != null)
            return loggedUsername;
        return null;
    }

    @POST
    @Path("/register")
    public boolean register(Seller seller, @Context HttpServletRequest request)
    {
        if(request.getHeader("Authorization") != null)
            return sellerService.register(seller);
        return false;
    }

    @PUT
    @Path("/login")
    public boolean login(Seller seller, @Context HttpServletRequest request)
    {
        Seller currentSeller = sellerService.login(seller);
        if(currentSeller != null)
        {
            HttpSession session = request.getSession();
            session.setAttribute("sellerUsername", currentSeller.getUsername());
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
            String loggedUsername = (String) session.getAttribute("sellerUsername");
            session.invalidate();
            return sellerService.logout(loggedUsername);
        }
        return false;
    }

    @GET
    @Path("/getAllSellers")
    public List<Seller> getAllSellers(@Context HttpServletRequest request)
    {
        if(request.getHeader("Authorization") != null)
            return sellerService.getAllSellers();
        return null;
    }

    @POST
    @Path("/addProduct")
    public boolean addProduct(Product product, @Context HttpServletRequest request)
    {
        HttpSession session = request.getSession();
        String loggedUsername = (String) session.getAttribute("sellerUsername");
        if(loggedUsername != null)
            return sellerService.addProduct(product, session.getId());
        return false;
    }

    @GET
    @Path("/viewOnSale")
    public String viewOnSale(@Context HttpServletRequest request)
    {
        HttpSession session = request.getSession();
        String loggedUsername = (String) session.getAttribute("sellerUsername");
        if(loggedUsername != null)
            return sellerService.viewOnSale(loggedUsername, session.getId());
        return null;
    }

    @GET
    @Path("/getAllSold")
    public String getAllSold(@Context HttpServletRequest request)
    {
        HttpSession session = request.getSession();
        String loggedUsername = (String) session.getAttribute("sellerUsername");
        if(loggedUsername != null)
            return sellerService.getAllSold(loggedUsername, session.getId());
        return null;
    }
}