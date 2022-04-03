package com.cr4zyrocket.foodappclient.Model;

public class FacebookUser {
    private String FacebookID,Name,Phone,Address,ImageURI;

    public FacebookUser() {
    }

    public FacebookUser(String facebookID, String name, String phone, String address, String imageURI) {
        FacebookID = facebookID;
        Name = name;
        Phone = phone;
        Address = address;
        ImageURI = imageURI;
    }

    public String getFacebookID() {
        return FacebookID;
    }

    public void setFacebookID(String facebookID) {
        FacebookID = facebookID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getImageURI() {
        return ImageURI;
    }

    public void setImageURI(String imageURI) {
        ImageURI = imageURI;
    }
}
