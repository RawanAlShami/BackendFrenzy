package com.example.adminsvertical.Services;
import java.net.URI;
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
import com.example.adminsvertical.Entities.Admin;
import com.example.adminsvertical.EmailPasswordGenerator;

@Stateful
@SessionScoped
@SuppressWarnings("unused")
public class adminService implements Serializable
{
    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("mysql");
    private final EntityManager entityManager = emf.createEntityManager();

    protected final String SellersBaseURL = "http://localhost:18077/sellersVertical-1.0-SNAPSHOT/svApi/seller";
    protected final String CouriersBaseURL = "http://localhost:8080/couriersVertical-1.0-SNAPSHOT/crvApi/courier";
    protected final String CustomersBaseURL = "http://localhost:18072/customersVertical-1.0-SNAPSHOT/cvApi/customer";

    public boolean register(Admin admin)
    {
        try
        {
            entityManager.getTransaction().begin();
            entityManager.persist(admin);
            entityManager.getTransaction().commit();
            return true;
        }
        catch (Exception e)
        {
            entityManager.getTransaction().rollback();
            return false;
        }
    }

    public Admin login(Admin admin)
    {
        Admin adminEntry = entityManager.find(Admin.class, admin.getEmail());
        if(adminEntry != null && adminEntry.getPassword().equals(admin.getPassword()))
        {
            entityManager.getTransaction().begin();
            adminEntry.setLogged(true);
            entityManager.getTransaction().commit();
            return adminEntry;
        }
        return null;
    }

    public boolean logout(String username)
    {
        try
        {
            TypedQuery<Admin> query = entityManager.createQuery("SELECT a FROM Admin a WHERE a.username =: username", Admin.class);
            query.setParameter("username", username);

            entityManager.getTransaction().begin();
            query.getSingleResult().setLogged(false);
            entityManager.getTransaction().commit();
            return true;
        }
        catch(Exception e) {return false;}
    }

    //OUTBOUND
    public String generateSellerAccount(String sellerName, String token)
    {
        try
        {
            String generatedEmail = EmailPasswordGenerator.generateEmail();
            String generatedPassword = EmailPasswordGenerator.generatePassword();

            String requestBody = "{\"email\":\"" + generatedEmail + "\",\"password\":\"" + generatedPassword + "\",\"username\":\"" + sellerName + "\"}";

            String URL = SellersBaseURL + "/register";
            HttpClient.newHttpClient().send(HttpRequest.newBuilder()
                    .uri(URI.create(URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .timeout(java.time.Duration.ofMinutes(1))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build(), HttpResponse.BodyHandlers.ofString());
            return generatedEmail + ":" + generatedPassword;
        }
        catch(Exception e) {return null;}
    }

    public String generateCourierAccount( String courierName, String binaryEncodedZones, String token)
    {
        try
        {
            ArrayList<String> credentials = generateCouriersRequest(courierName, binaryEncodedZones);
            String requestBody = credentials.get(2);

            String URL = CouriersBaseURL + "/register";
            HttpClient.newHttpClient().send(HttpRequest.newBuilder()
                    .uri(URI.create(URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .timeout(java.time.Duration.ofMinutes(1))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build(), HttpResponse.BodyHandlers.ofString());
            return credentials.get(0) + ":" + credentials.get(1);
        }
        catch(Exception e) {return null;}
    }

    public String getAllUsers(String userType, String token)
    {
        String URL;
        switch (userType)
        {
            case "customers" -> URL = CustomersBaseURL + "/getAllCustomers";
            case "couriers" -> URL = CouriersBaseURL + "/getAllCouriers";
            case "sellers" -> URL = SellersBaseURL + "/getAllSellers";
            default -> {return null;}
        }
        try
        {
            HttpResponse<String> response = HttpClient.newHttpClient().send(HttpRequest.newBuilder()
                    .uri(URI.create(URL)).GET()
                    .header("Authorization", "Bearer " + token)
                    .build(), HttpResponse.BodyHandlers.ofString());
            return response.body();
        }
        catch(Exception e) {return null;}
    }

    //UTILITY
    public ArrayList<String> generateCouriersRequest(String courierName, String binaryEncodedZones)
    {
        int[] binaryArray = new int[binaryEncodedZones.length()];
        for (int i = 0; i < binaryEncodedZones.length(); i++)
            binaryArray[i] = Integer.parseInt(String.valueOf(binaryEncodedZones.charAt(i)));

        String[] zones = {"Sheikh_Zayed", "Dokki", "Fifth_Settlement", "Haram"};
        StringBuilder requestBody = new StringBuilder();
        for (int i = 0; i < binaryEncodedZones.length(); i++)
        {
            if(binaryArray[i] == 1 && (requestBody.length() == 0))
                requestBody.append("[{\"zone\":\"").append(zones[i]).append("\"}");
            else if(binaryArray[i] == 1 && (requestBody.length() > 0))
                requestBody.append(",{\"zone\":\"").append(zones[i]).append("\"}");
        }
        if(requestBody.length() > 0)
        {
            ArrayList<String> credentials = new ArrayList<>();

            String generatedEmail = EmailPasswordGenerator.generateEmail();
            String generatedPassword = EmailPasswordGenerator.generatePassword();

            credentials.add(generatedEmail);
            credentials.add(generatedPassword);
            requestBody.append(",{\"email\":\"").append(generatedEmail).append("\",\"password\":\"").append(generatedPassword).append("\",\"username\":\"").append(courierName).append("\"}");
            requestBody.append("]");

            credentials.add(requestBody.toString());
            return credentials;
        }
        return null;
    }
}