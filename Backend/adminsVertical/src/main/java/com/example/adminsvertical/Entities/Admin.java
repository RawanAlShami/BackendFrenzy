package com.example.adminsvertical.Entities;
import jakarta.ejb.Stateful;
import jakarta.ejb.LocalBean;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;

@Entity
@Stateful
@LocalBean
@Table(name = "Admins")
@SuppressWarnings("unused")
public class Admin
{
    @Id
    private String email;
    private String password;
    private String username;
    private boolean isLogged;

    //DEFAULT CONSTRUCTOR
    public Admin() {}

    //PARAMETERIZED CONSTRUCTOR
    public Admin(String email, String password, String username)
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

    public void setLogged(boolean logged) {isLogged = logged;}
    public boolean isLogged() {return isLogged;}
}