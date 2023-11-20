package com.example.sellersvertical.Entities;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateful;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;

@Entity
@Stateful
@LocalBean
@Table(name = "Sellers")
@SuppressWarnings("unused")
public class Seller
{
    @Id
    private String email;
    private String password;
    private String username;
    private boolean isLogged;

    //DEFAULT CONSTRUCTOR
    public Seller() {}

    //PARAMETERIZED CONSTRUCTOR
    public Seller(String email, String password, String username)
    {
        this.email = email;
        this.password = password;
        this.username = username;
        this.isLogged = false;
    }

    //SETTERS AND GETTERS
    public void setEmail(String email) {this.email = email;}
    public String getEmail() {return email;}

    public void setPassword(String password) {this.password = password;}
    public String getPassword() {return password;}

    public void setUsername(String username) {this.username = username;}
    public String getUsername() {return username;}

    public void setLoggedIn(boolean loggedIn) {this.isLogged = loggedIn;}
    public boolean isLoggedIn() {return isLogged;}
}
