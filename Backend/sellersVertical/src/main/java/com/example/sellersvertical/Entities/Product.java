package com.example.sellersvertical.Entities;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;

@Entity
@LocalBean
@Stateless
@Table(name = "Products")
@SuppressWarnings("unused")
public class Product
{
    @Id
    private int pId;
    private String name;
    private double price;
    private String description;
    private String sellerName;
    private String fileName;
    private int quantity;

    //DEFAULT CONSTRUCTOR
    public Product(){}

    //PARAMETERIZED CONSTRUCTOR
    public Product(int pId, String name, double price, String description, String sellerName , String fileName, int quantity)
    {
        this.pId = pId;
        this.name = name;
        this.price = price;
        this.description = description;
        this.sellerName = sellerName;
        this.fileName = fileName;
        this.quantity = quantity;
    }

    //SETTERS AND GETTERS
    public int getpId() {return pId;}
    public void setpId(int pId) {this.pId = pId;}

    public void setName(String name) {this.name = name;}
    public String getName() {return name;}

    public void setPrice(double price) {this.price = price;}
    public double getPrice() {return price;}

    public void setDescription(String description) {this.description = description;}
    public String getDescription() {return description;}

    public void setSellerName(String sellerName) {this.sellerName = sellerName;}
    public String getSellerName() {return sellerName;}

    public void setFileName(String fileName) {this.fileName = fileName;}
    public String getFileName() {return fileName;}

    public void setQuantity(int quantity) {this.quantity = quantity;}
    public int getQuantity() {return quantity;}
}
