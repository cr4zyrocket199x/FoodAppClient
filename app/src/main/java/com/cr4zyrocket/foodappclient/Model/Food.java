package com.cr4zyrocket.foodappclient.Model;

public class Food{
    private String Name,Image,Description,Price,Discount,CategoryID;

    public Food() {
    }

    public Food(String name, String image, String description, String price, String discount, String categoryID) {
        Name = name;
        Image = image;
        Description = description;
        Price = price;
        Discount = discount;
        CategoryID = categoryID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }

    public String getCategoryID() {
        return CategoryID;
    }

    public void setCategoryID(String categoryID) {
        CategoryID = categoryID;
    }
}
