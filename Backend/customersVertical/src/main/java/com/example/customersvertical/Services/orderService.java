package com.example.customersvertical.Services;
import java.net.URI;
import java.util.List;
import java.util.Random;
import java.sql.Timestamp;
import jakarta.ejb.Singleton;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.Persistence;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import com.example.customersvertical.Entities.Order;
import com.example.customersvertical.Entities.Customer;

@Singleton
@SuppressWarnings("unused")
public class orderService
{
    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("mysql");
    private final EntityManager entityManager = emf.createEntityManager();

    protected final String CourierBaseURL = "http://localhost:8080/couriersVertical-1.0-SNAPSHOT/crvApi/courier";
    protected final String ProductBaseURL = "http://localhost:18077/sellersVertical-1.0-SNAPSHOT/svApi/product";

    private static orderService instance =null;

    public static orderService getInstance()
    {
        if(instance ==null)
        {
            instance = new orderService();
            System.out.println("instance created");
        }
        System.out.println("instance already exists");
        return instance;
    }

    //OUTBOUND
    public String coversZone(String URL, String token)
    {
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

    public Boolean updateStock(String URL, String token)
    {
        try
        {
            HttpClient httpClient = HttpClient.newBuilder().build();
            HttpRequest httpRequest = HttpRequest.newBuilder().uri(new URI(URL))
                    .header("Authorization", "Bearer " + token).GET().build();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            return Boolean.parseBoolean(response.body());
        }
        catch(Exception e) {return null;}
    }

    public void sendToQueue(String requestBody)
    {
        try
        {
            String URL = CourierBaseURL + "/sendToQueue";

            HttpClient.newHttpClient().send(HttpRequest.newBuilder()
                    .uri(URI.create(URL))
                    .header("Content-Type", "application/json")
                    .timeout(java.time.Duration.ofMinutes(1))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build(), HttpResponse.BodyHandlers.ofString());
        }
        catch (Exception e) {e.printStackTrace();}
    }

    //UTILITY
    public boolean placeOrder(String customerUsername, List<Order> inCart, String token)
    {
        try
        {
            TypedQuery<Customer> query = entityManager.createQuery("SELECT c FROM Customer c WHERE c.username=:customerUsername", Customer.class);
            query.setParameter("customerUsername", customerUsername);
            String customersZone = query.getSingleResult().getZone();

            Random random = new Random();
            int orderId = random.nextInt(9000) + 1000;

            String URL =  CourierBaseURL + "/coversZone/" + customersZone + "/" + customerUsername + "/" + orderId;
            String assignedCourier = coversZone(URL, token);

            if(assignedCourier != null)
            {
                for (Order order : inCart)
                {
                    String ProductURL = ProductBaseURL + "/" + order.getProductId() + "/updateStock";
                    boolean stockUpdated = updateStock(ProductURL, token);
                    if(stockUpdated)
                    {
                        order.setOrderId(orderId);
                        order.setStatus("inQueue");
                        order.setCourierName(assignedCourier);
                        order.setCustomerUsername(customerUsername);

                        entityManager.getTransaction().begin();
                        entityManager.persist(order);
                        entityManager.getTransaction().commit();
                    }
                    else
                        return false;
                }
                return true;
            }
            return false;
        }
        catch(Exception e)
        {
            entityManager.getTransaction().rollback();
            return false;
        }
    }

    public Boolean updateStatus(int orderId, String newStatus)
    {
        if(newStatus.equals("inProgress") | newStatus.equals("shipped"))
        {
            try
            {
                TypedQuery<Order> query = entityManager.createQuery("SELECT o FROM Order o WHERE o.orderId=:orderId", Order.class);
                query.setParameter("orderId", orderId);

                entityManager.getTransaction().begin();
                for(Order order : query.getResultList())
                    order.setStatus(newStatus);
                entityManager.getTransaction().commit();

                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                String username = getOrdersCustomer(orderId);

                String message = "New Status Update: " + newStatus;
                String requestBody = "{\"orderID\":\"" + orderId + "\",\"username\":\"" + username + "\",\"message\":\"" + message + "\",\"timestamp\":\"" + timestamp + "\"}";
                sendToQueue(requestBody);
                return true;
            }
            catch(Exception e)
            {
                entityManager.getTransaction().rollback();
                return false;
            }
        }
        return false;
    }

    public List<Order> getOrdersByStatus (String username, String userType, String status)
    {
        String queryString = generateQueryString(userType, status);
        if(queryString != null)
        {
            try
            {
                TypedQuery<Order> query = entityManager.createQuery(queryString, Order.class);
                query.setParameter("status", status);
                query.setParameter("username", username);
                return query.getResultList();
            }
            catch(Exception e) {return null;}
        }
        return null;
    }

    public String generateQueryString(String userType, String status)
    {
        if(status.equals("inQueue") | status.equals("inProgress") | status.equals("shipped"))
        {
            String query;
            switch (userType)
            {
                case "seller" -> query = "SELECT o FROM Order o WHERE o.status=:status AND o.sellerName=:username";
                case "courier" -> query = "SELECT o FROM Order o WHERE o.status=:status AND o.courierName=:username";
                case "customer" -> query = "SELECT o FROM Order o WHERE o.status=:status AND o.customerUsername=:username";
                default -> query = null;
            }
            return query;
        }
        return null;
    }

    public String getOrdersCustomer(int orderID)
    {
        TypedQuery<Order> query = entityManager.createQuery("SELECT o FROM Order o WHERE o.orderId=:orderID", Order.class);
        query.setParameter("orderID", orderID);
        return query.getResultList().get(0).getCustomerUsername();
    }
}