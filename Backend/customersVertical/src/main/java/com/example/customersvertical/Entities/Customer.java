package com.example.customersvertical.Entities;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateful;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;

@Entity
@Stateful
@LocalBean
@Table(name = "Customers")
@SuppressWarnings("unused")
public class Customer
{
    @Id
    private String email;
    private String password;
    private String username;
    private String address;
    private String zone;
    private boolean logged;

    //DEFAULT CONSTRUCTORS
    public Customer(){}

    //PARAMETERIZED CONSTRUCTOR
    public Customer(String email, String password, String username, String address, String zone)
    {
        this.email = email;
        this.password = password;
        this.username = username;
        this.address = address;
        this.zone = zone;
        this.logged = false;
    }

    //SETTERS AND GETTERS
    public void setEmail(String email) {this.email = email;}
    public String getEmail() {return email;}

    public void setPassword(String password) {this.password = password;}
    public String getPassword() {return password;}

    public void setUsername(String username) {this.username = username;}
    public String getUsername() {return username;}

    public void setAddress(String address) {this.address = address;}
    public String getAddress() {return address;}

    public void setZone(String zone) {this.zone = zone;}
    public String getZone() {return zone;}

    public void setLogged(boolean logged) {this.logged = logged;}
    public boolean isLogged() {return logged;}
}