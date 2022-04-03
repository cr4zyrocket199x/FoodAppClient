package com.cr4zyrocket.foodappclient.Model;

public class Rating {
    private String userID,userName,foodID,rateValue,comment,userID_foodID;

    public Rating() {
    }

    public Rating(String userID, String userName, String foodID, String rateValue, String comment, String userID_foodID) {
        this.userID = userID;
        this.userName = userName;
        this.foodID = foodID;
        this.rateValue = rateValue;
        this.comment = comment;
        this.userID_foodID = userID_foodID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFoodID() {
        return foodID;
    }

    public void setFoodID(String foodID) {
        this.foodID = foodID;
    }

    public String getRateValue() {
        return rateValue;
    }

    public void setRateValue(String rateValue) {
        this.rateValue = rateValue;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUserID_foodID() {
        return userID_foodID;
    }

    public void setUserID_foodID(String userID_foodID) {
        this.userID_foodID = userID_foodID;
    }
}
