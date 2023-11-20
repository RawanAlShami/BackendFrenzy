package com.example.customersvertical.Services;
import java.io.Serializable;
import java.net.URI;
import java.util.List;

import com.example.customersvertical.Entities.Order;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ejb.Stateful;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import jakarta.enterprise.context.SessionScoped;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.Persistence;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import com.example.customersvertical.Entities.Customer;

@Stateful
@SessionScoped
@SuppressWarnings("unused")
public class customerService implements Serializable
{
    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("mysql");
    private final EntityManager entityManager = emf.createEntityManager();

    protected final String OrderBaseURL = "http://localhost:18072/customersVertical-1.0-SNAPSHOT/cvApi/order";
    protected final String ProductBaseURL = "http://localhost:18077/sellersVertical-1.0-SNAPSHOT/svApi/product";

    public boolean register(Customer customer)
    {
        try
        {
            entityManager.getTransaction().begin();
            entityManager.persist(customer);
            entityManager.getTransaction().commit();
            return true;
        }
        catch (Exception e)
        {
            entityManager.getTransaction().rollback();
            return false;
        }
    }

    public Customer login(Customer customer)
    {
        Customer customerEntry = entityManager.find(Customer.class, customer.getEmail());
        if(customerEntry != null && customerEntry.getPassword().equals(customer.getPassword()))
        {
            entityManager.getTransaction().begin();
            customerEntry.setLogged(true);
            entityManager.getTransaction().commit();
            return customerEntry;
        }
        return null;
    }

    public boolean logout(String username)
    {
        try
        {
            TypedQuery<Customer> query = entityManager.createQuery("SELECT c FROM Customer c WHERE c.username = :username", Customer.class);
            query.setParameter("username", username);

            entityManager.getTransaction().begin();
            query.getSingleResult().setLogged(false);
            entityManager.getTransaction().commit();
            return true;
        }
        catch(Exception e) {return false;}
    }

    //OUTBOUND
    public String placeOrder(List<Order> inCart, String username, String token)
    {
        try
        {
            ObjectMapper mapper = new ObjectMapper();
            String jsonOrders = mapper.writeValueAsString(inCart);
            String URL = OrderBaseURL + "/placeOrder/" + username;

            HttpResponse response = HttpClient.newHttpClient().send(HttpRequest.newBuilder()
                    .uri(URI.create(URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .timeout(java.time.Duration.ofMinutes(1))
                    .POST(HttpRequest.BodyPublishers.ofString(jsonOrders))
                    .build(), HttpResponse.BodyHandlers.ofString());
            return response.body().toString();
        }
        catch(Exception e) {return null;}
    }

    public String getOrdersByStatus(String username, String status, String token)
    {
        String URL = OrderBaseURL + "/getByStatus/customer/"+ username + "/" + status;
        try
        {
            HttpClient httpClient = HttpClient.newBuilder().build();
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(new URI(URL)).header("Authorization", "Bearer " + token)
                    .GET().build();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            return response.body();
        }
        catch(Exception e) {return null;}
    }

    //UTILITY
    public List<Customer> getAllCustomers()
    {
        return entityManager.createQuery("SELECT c FROM Customer c", Customer.class).getResultList();
    }
}