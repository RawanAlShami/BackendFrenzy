package com.example.customersvertical.Entities;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;

@Entity
@Stateless
@LocalBean
@Table(name = "Notifications")
@SuppressWarnings("unused")
public class Notification
{
    @Id
    int orderID;
    String timestamp;
    @Id
    String username;
    @Id
    String message;

    //DEFAULT CONSTRUCTOR
    public Notification(){}

    //PARAMETERIZED CONSTRUCTOR
    public Notification(int orderID, String timestamp, String username, String message)
    {
        this.orderID = orderID;
        this.timestamp = timestamp;
        this.username = username;
        this.message = message;
    }

    //SETTERS AND GETTERS
    public void setOrderID(int orderID) {this.orderID = orderID;}
    public int getOrderID() {return orderID;}

    public void setTimestamp(String timestamp) {this.timestamp = timestamp;}
    public String getTimestamp() {return timestamp;}

    public void setUsername(String username) {this.username = username;}
    public String getUsername() {return username;}

    public void setMessage(String message) {this.message = message;}
    public String getMessage() {return message;}
}