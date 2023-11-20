package com.example.sellersvertical.REST;
import java.util.List;
import jakarta.ejb.EJB;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import com.example.sellersvertical.Entities.Product;
import com.example.sellersvertical.Services.productService;

@Path("/product")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class productREST
{
    @EJB
    productService productService;

    @POST
    @Path("/addProduct")
    public boolean addProduct(Product product, @Context HttpServletRequest request)
    {
        if(request.getHeader("Authorization") != null)
            return productService.addProduct(product);
        return false;
    }

    @GET
    @Path("/getBySeller/{sellerName}")
    public List<Product> getProductBySeller(@PathParam("sellerName") String sellerName, @Context HttpServletRequest request)
    {
        if(request.getHeader("Authorization") != null)
            return productService.getProductBySeller(sellerName);
        return null;
    }

    @GET
    @Path("/getAllProducts")
    public List<Product> getAllProducts(@Context HttpServletRequest request)
    {
        return productService.getAllProducts();
    }

    @GET
    @Path("/{pID}/updateStock")
    public Boolean updateStock(@PathParam("pID") int pID, @Context HttpServletRequest request)
    {
        if(request.getHeader("Authorization") != null)
            return productService.updateStock(pID);
        return null;
    }
}