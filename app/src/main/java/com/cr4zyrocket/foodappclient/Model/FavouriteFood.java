package com.cr4zyrocket.foodappclient.Model;

public class FavouriteFood {
    private String UserID,FoodID,FoodName,FoodImage,FoodDescription,FoodPrice,FoodDiscount,CategoryID;

    public FavouriteFood() {
    }

    public FavouriteFood(String userID, String foodID, String foodName, String foodImage, String foodDescription, String foodPrice, String foodDiscount, String categoryID) {
        UserID = userID;
        FoodID = foodID;
        FoodName = foodName;
        FoodImage = foodImage;
        FoodDescription = foodDescription;
        FoodPrice = foodPrice;
        FoodDiscount = foodDiscount;
        CategoryID = categoryID;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
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

    public String getFoodImage() {
        return FoodImage;
    }

    public void setFoodImage(String foodImage) {
        FoodImage = foodImage;
    }

    public String getFoodDescription() {
        return FoodDescription;
    }

    public void setFoodDescription(String foodDescription) {
        FoodDescription = foodDescription;
    }

    public String getFoodPrice() {
        return FoodPrice;
    }

    public void setFoodPrice(String foodPrice) {
        FoodPrice = foodPrice;
    }

    public String getFoodDiscount() {
        return FoodDiscount;
    }

    public void setFoodDiscount(String foodDiscount) {
        FoodDiscount = foodDiscount;
    }

    public String getCategoryID() {
        return CategoryID;
    }

    public void setCategoryID(String categoryID) {
        CategoryID = categoryID;
    }
}
