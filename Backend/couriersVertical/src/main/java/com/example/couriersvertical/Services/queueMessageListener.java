package com.example.couriersvertical.Services;
import java.net.URI;
import jakarta.jms.Message;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import jakarta.ejb.MessageDriven;
import jakarta.jms.MessageListener;

@MessageDriven(
        activationConfig = {
                @jakarta.ejb.ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Queue"),
                @jakarta.ejb.ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/jms/queue/MsgQueue")
        },
        mappedName = "java:/jms/queue/MsgQueue", name = "queueMessageListener"
)

@SuppressWarnings("unused")
public class queueMessageListener implements MessageListener
{
    String NotificationBaseURL = "http://localhost:18072/customersVertical-1.0-SNAPSHOT/cvApi/notification";

    @Override
    public void onMessage(Message message)
    {
        try
        {
            String requestBody = message.getBody(String.class);
            String URL = NotificationBaseURL + "/persistNotification";

            HttpClient.newHttpClient().send(HttpRequest.newBuilder()
                    .uri(URI.create(URL))
                    .header("Content-Type", "application/json")
                    .timeout(java.time.Duration.ofMinutes(1))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build(), HttpResponse.BodyHandlers.ofString());
        }
        catch (Exception e) {e.printStackTrace();}
    }
}