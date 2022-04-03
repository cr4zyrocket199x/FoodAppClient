package com.cr4zyrocket.foodappclient.Model;

public class Order {
    private String FoodID,FoodName,Image,Quantity,Price,Discount;

    public Order() {
    }

    public Order(String foodID, String foodName, String image, String quantity, String price, String discount) {
        FoodID = foodID;
        FoodName = foodName;
        Image = image;
        Quantity = quantity;
        Price = price;
        Discount = discount;
    }

    public String getFoodID() {
        return FoodID;
    }

    public void setFoodID(String foodID) {
        FoodID = foodID;
    }

    public String getFoodName() {
        return FoodName;
    }

    public void setFoodName(String foodName) {
        FoodName = foodName;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
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
}
