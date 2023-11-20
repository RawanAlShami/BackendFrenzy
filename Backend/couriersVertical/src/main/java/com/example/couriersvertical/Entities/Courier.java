package com.example.couriersvertical.Entities;
import jakarta.ejb.Stateful;
import jakarta.ejb.LocalBean;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;

@Entity
@Stateful
@LocalBean
@Table(name = "Couriers")
@SuppressWarnings("unused")
public class Courier
{
    @Id
    private String email;
    private String password;
    private String username;
    @Id
    private String zone;
    private boolean logged;

    //DEFAULT CONSTRUCTOR
    public Courier(){}

    //PARAMETERIZED CONSTRUCTOR
    public Courier(String email, String password, String username, boolean logged, String zone)
    {
        this.email = email;
        this.password = password;
        this.username = username;
        this.logged = logged;
        this.zone = zone;
    }

    //SETTERS AND GETTERS
    public void setEmail(String email) {this.email = email;}
    public String getEmail() {return email;}

    public void setPassword(String password) {this.password = password;}
    public String getPassword() {return password;}

    public void setUsername(String username) {this.username = username;}
    public String getUsername() {return username;}

    public void setZone(String zone) {this.zone = zone;}
    public String getZone() {return zone;}

    public void setLogged(boolean logged) {this.logged = logged;}
    public boolean isLogged() {return logged;}
}