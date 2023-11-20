package com.example.couriersvertical.Services;
import java.util.*;
import java.net.URI;
import jakarta.jms.*;
import jakarta.jms.Queue;
import java.sql.Timestamp;
import javax.naming.Context;
import jakarta.ejb.Stateful;
import java.io.Serializable;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import javax.naming.InitialContext;
import jakarta.annotation.Resource;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.Persistence;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.enterprise.context.SessionScoped;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.couriersvertical.Entities.Courier;

@Stateful
@SessionScoped
@SuppressWarnings("unused")
public class courierService implements Serializable
{
    @Resource(mappedName = "java:/jms/queue/MsgQueue")
    private Queue queue;

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("mysql");
    private final EntityManager entityManager = emf.createEntityManager();

    protected final String OrderBaseURL = "http://localhost:18072/customersVertical-1.0-SNAPSHOT/cvApi/order";

    public boolean register(List<Courier> courierZones)
    {
        try
        {
            Courier courierCredentials = courierZones.get(courierZones.size()-1);
            for (int i = 0; i < courierZones.size()-1; i++)
            {
                Courier current = courierZones.get(i);
                current.setEmail(courierCredentials.getEmail());
                current.setPassword(courierCredentials.getPassword());
                current.setUsername(courierCredentials.getUsername());

                entityManager.getTransaction().begin();
                entityManager.persist(current);
                entityManager.getTransaction().commit();
            }
            return true;
        }
        catch(Exception e) {return false;}
    }

    public Courier login(Courier courier)
    {
        TypedQuery<Courier> query = entityManager.createQuery("SELECT c FROM Courier c WHERE c.email=:courierEmail", Courier.class);
        query.setParameter("courierEmail",courier.getEmail());
        List<Courier> couriersEntries = query.getResultList();

        if (!couriersEntries.isEmpty() && couriersEntries.get(0).getPassword().equals(courier.getPassword()))
        {
            entityManager.getTransaction().begin();
            for (Courier c:couriersEntries)
                c.setLogged(true);
            entityManager.getTransaction().commit();
            return couriersEntries.get(0);
        }
        return null;
    }

    public boolean logout(String username)
    {
        try
        {
            TypedQuery<Courier> query = entityManager.createQuery("SELECT c FROM Courier c WHERE c.username = :username", Courier.class);
            query.setParameter("username", username);
            List<Courier> couriersEntries = query.getResultList();

            if(couriersEntries != null)
            {
                entityManager.getTransaction().begin();
                for (Courier c:couriersEntries)
                    c.setLogged(false);
                entityManager.getTransaction().commit();
                return true;
            }
            return false;
        }
        catch (Exception e) {return false;}
    }

    //OUTBOUND
    public String getOrdersByStatus(String courierName, String status, String token)
    {
        if(status.equals("inQueue") | status.equals("inProgress") | status.equals("shipped"))
        {
            String url = OrderBaseURL + "/getByStatus/courier/" + courierName + "/" + status;
            try
            {
                HttpResponse<String> response = HttpClient.newHttpClient().send(HttpRequest.newBuilder()
                        .uri(URI.create(url)).header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + token)
                        .timeout(java.time.Duration.ofMinutes(1)).GET()
                        .build(), HttpResponse.BodyHandlers.ofString());
                return response.body();
            }
            catch(Exception e) {return null;}
        }
        return null;
    }

    public boolean updateStatus(int orderId, String newStatus, String token)
    {
        try
        {
            String url = OrderBaseURL + "/updateStatus/" + orderId + "/" + newStatus;
            HttpResponse<String> response = HttpClient.newHttpClient().send(HttpRequest.newBuilder()
                    .uri(URI.create(url)).header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .timeout(java.time.Duration.ofMinutes(1))
                    .PUT(HttpRequest.BodyPublishers.noBody())
                    .build(), HttpResponse.BodyHandlers.ofString());
            return true;
        }
        catch(Exception e) {return false;}
    }

    public void sendToQueue(String request)
    {
        try
        {
            Context context = new InitialContext();
            ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup("java:/ConnectionFactory");
            Connection connection = connectionFactory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer(this.queue);
            ObjectMessage message = session.createObjectMessage();
            message.setObject(request);
            producer.send(message);
            session.close();
            connection.close();
        }
        catch (Exception e) {e.printStackTrace();}
    }

    //UTILITY
    public String getAllCouriers()
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            List<Map<String, Object>> responseMap = new ArrayList<>();

            TypedQuery<String> query = entityManager.createQuery("SELECT DISTINCT c.username FROM Courier c", String.class);
            List<String> courierNames = query.getResultList();

            if(!courierNames.isEmpty())
            {
                for (String name : courierNames)
                {
                    Map<String, Object> company = new HashMap<>();
                    company.put("name", name);
                    company.put("zones", getZones(String.valueOf(name)));
                    responseMap.add(company);
                }
                return objectMapper.writeValueAsString(responseMap);
            }
            return null;
        }
        catch (Exception e){return null;}
    }

    public List<Courier> getZones(String username)
    {
        TypedQuery<Courier> query = entityManager.createQuery("SELECT c.zone FROM Courier c WHERE c.username=:username",Courier.class);
        query.setParameter("username", username);
        return query.getResultList();
    }

    public String coversZone(String customersZone, String customerUsername, int orderID, String token)
    {
        TypedQuery<Courier> query = entityManager.createQuery("SELECT DISTINCT c FROM Courier c WHERE c.zone =: zone", Courier.class);
        query.setParameter("zone", customersZone);

        String message;
        String result;
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        if(!query.getResultList().isEmpty())
        {
            int randomIndex = new Random().nextInt(query.getResultList().size());
            String randomCourier = query.getResultList().get(randomIndex).getUsername();
            message = "Order Received Successfully! " + randomCourier + " Will Process Your Order Soon";
            result = randomCourier;
        }
        else
        {
            message = "Failed To Process Your Order :( No Courier Covers Your Zone";
            result = null;
        }
        String requestBody = "{\"orderID\":\"" + orderID + "\",\"username\":\"" + customerUsername + "\",\"message\":\"" + message + "\",\"timestamp\":\"" + timestamp + "\"}";
        sendToQueue(requestBody);
        return result;
    }
}