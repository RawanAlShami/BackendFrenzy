package com.example.sellersvertical.Services;
import java.net.URI;
import java.util.List;
import java.util.ArrayList;
import jakarta.ejb.Stateful;
import java.io.Serializable;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.Persistence;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.enterprise.context.SessionScoped;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.sellersvertical.Entities.Seller;
import com.example.sellersvertical.Entities.Product;

@Stateful
@SessionScoped
@SuppressWarnings("unused")
public class sellerService implements Serializable
{
    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("mysql");
    private final EntityManager entityManager = emf.createEntityManager();

    protected final String OrderBaseURL = "http://localhost:18072/customersVertical-1.0-SNAPSHOT/cvApi/order";
    protected final String ProductsBaseURL = "http://localhost:18077/sellersVertical-1.0-SNAPSHOT/svApi/product";

    public boolean register(Seller seller)
    {
        try
        {
            entityManager.getTransaction().begin();
            entityManager.persist(seller);
            entityManager.getTransaction().commit();
            return true;
        }
        catch (Exception e)
        {
            entityManager.getTransaction().rollback();
            return false;
        }
    }

    public Seller login(Seller seller)
    {
        Seller sellerEntry = entityManager.find(Seller.class, seller.getEmail());
        if(sellerEntry!=null && sellerEntry.getPassword().equals(seller.getPassword()))
        {
            entityManager.getTransaction().begin();
            sellerEntry.setLoggedIn(true);
            entityManager.getTransaction().commit();
            return sellerEntry;
        }
        return null;
    }

    public boolean logout(String username)
    {
        try
        {
            TypedQuery<Seller> query = entityManager.createQuery("SELECT s FROM Seller s WHERE s.username = :username", Seller.class);
            query.setParameter("username", username);

            entityManager.getTransaction().begin();
            query.getSingleResult().setLoggedIn(false);
            entityManager.getTransaction().commit();
            return true;
        }
        catch(Exception e) {return false;}
    }

    //OUTBOUND
    public boolean addProduct(Product product, String token)
    {
        try
        {
            ObjectMapper mapper = new ObjectMapper();
            String jsonProduct = mapper.writeValueAsString(product);
            String URL = ProductsBaseURL + "/addProduct";

            HttpClient.newHttpClient().send(HttpRequest.newBuilder()
                    .uri(URI.create(URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .timeout(java.time.Duration.ofMinutes(1))
                    .POST(HttpRequest.BodyPublishers.ofString(jsonProduct))
                    .build(), HttpResponse.BodyHandlers.ofString());
            return true;
        }
        catch(Exception e) {return false;}
    }

    public String viewOnSale(String username, String token)
    {
        String URL = ProductsBaseURL + "/getBySeller/" + username;
        try
        {
            HttpClient httpClient = HttpClient.newBuilder().build();
            HttpRequest httpRequest = HttpRequest.newBuilder().uri(new URI(URL))
                    .header("Authorization", "Bearer " + token).GET().build();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            return response.body();
        }
        catch(Exception e) {return null;}
    }

    public String getAllSold(String username, String token)
    {
        ArrayList<String> URLs = new ArrayList<>();
        URLs.add(OrderBaseURL + "/getByStatus/seller/" + username +"/shipped");
        URLs.add(OrderBaseURL + "/getByStatus/seller/" + username +"/inProgress");
        URLs.add(OrderBaseURL + "/getByStatus/seller/" + username +"/inQueue");
        try
        {
            String concatResponse = new String();
            for (String url:URLs)
            {
                HttpResponse<String> response = HttpClient.newHttpClient().send(HttpRequest.newBuilder()
                        .uri(URI.create(url)).header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + token)
                        .timeout(java.time.Duration.ofMinutes(1)).GET()
                        .build(), HttpResponse.BodyHandlers.ofString());
                concatResponse+=response.body().replaceAll("[\\[\\]]", "");
            }
            return concatResponse.replaceAll("\\}\\{", "},\n{");
        }
        catch(Exception e) {return null;}
    }

    //UTILITY
    public List<Seller> getAllSellers()
    {
        return entityManager.createQuery("SELECT s FROM Seller s", Seller.class).getResultList();
    }
}