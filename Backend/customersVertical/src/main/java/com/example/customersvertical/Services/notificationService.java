package com.example.customersvertical.Services;
import java.util.List;
import jakarta.ejb.Stateless;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.Persistence;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import com.example.customersvertical.Entities.Notification;

@Stateless
@SuppressWarnings("unused")
public class notificationService
{
    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("mysql");
    private final EntityManager entityManager = emf.createEntityManager();

    public boolean persistNotification(Notification notification)
    {
        try
        {
            entityManager.getTransaction().begin();
            entityManager.persist(notification);
            entityManager.getTransaction().commit();
            return true;
        }
        catch (Exception e)
        {
            entityManager.getTransaction().rollback();
            return false;
        }
    }

    public List<Notification> getNotifications(String username)
    {
        TypedQuery<Notification> query = entityManager.createQuery("SELECT n FROM Notification n WHERE n.username=:username", Notification.class);
        query.setParameter("username", username);
        return query.getResultList();
    }
}
