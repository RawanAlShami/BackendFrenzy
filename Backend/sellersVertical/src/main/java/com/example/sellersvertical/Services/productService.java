package com.example.sellersvertical.Services;
import java.util.List;
import java.util.Random;
import jakarta.ejb.Stateless;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.Persistence;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import com.example.sellersvertical.Entities.Product;

@Stateless
@SuppressWarnings("unused")
public class productService
{
    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("mysql");
    private final EntityManager entityManager = emf.createEntityManager();

    //UTILITY
    public boolean addProduct(Product product)
    {
        try
        {
            Random random = new Random();
            product.setpId(random.nextInt(9000) + 1000);

            entityManager.getTransaction().begin();
            entityManager.persist(product);
            entityManager.getTransaction().commit();
            return true;
        }
        catch (Exception e)
        {
            entityManager.getTransaction().rollback();
            return false;
        }
    }

    public List<Product> getProductBySeller(String sellerName)
    {
        try
        {
            TypedQuery<Product> query = entityManager.createQuery("SELECT p FROM Product p WHERE p.sellerName=:sellerName", Product.class) ;
            query.setParameter("sellerName" , sellerName);
            return query.getResultList();
        }
        catch(Exception e) {return null;}
    }

    public List<Product> getAllProducts()
    {
        return entityManager.createQuery("SELECT p FROM Product p", Product.class).getResultList();
    }

    public boolean updateStock(int pID)
    {
        Product productEntry = entityManager.find(Product.class, pID);
        if(productEntry!= null && productEntry.getQuantity()!= 0)
        {
            productEntry.setQuantity(productEntry.getQuantity()-1);
            return true;
        }
        return false;
    }
}