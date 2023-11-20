package com.example.customersvertical.Entities;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Singleton;
import jakarta.persistence.*;
import jakarta.persistence.Id;

@Entity
@LocalBean
@Singleton
@Table(name = "Orders")
@SuppressWarnings("unused")
public class Order
{
    @Id
    private int orderId;
    @Id
    private int productId;
    @Id
    private String customerUsername;
    private String courierName;
    private String sellerName;
    private String status;

    //DEFAULT CONSTRUCTOR
    public Order(){}

    //PARAMETERIZED CONSTRUCTOR
    public Order(int orderId, int productId, String customerUsername, String courierName, String sellerName, String status)
    {
        this.orderId = orderId;
        this.productId = productId;
        this.customerUsername = customerUsername;
        this.courierName = courierName;
        this.sellerName = sellerName;
        this.status = status;
    }

    //SETTERS AND GETTERS
    public void setOrderId(int orderId) {this.orderId = orderId;}
    public int getOrderId() {return orderId;}

    public void setProductId(int productId) {this.productId = productId;}
    public int getProductId() {return productId;}

    public void setCustomerUsername(String customerUsername) {this.customerUsername = customerUsername;}
    public String getCustomerUsername() {return customerUsername;}

    public void setCourierName(String courierName) {this.courierName = courierName;}
    public String getCourierName() {return courierName;}

    public void setSellerName(String sellerName) {this.sellerName = sellerName;}
    public String getSellerName() {return sellerName;}

    public void setStatus(String status) {this.status = status;}
    public String getStatus() {return status;}
}