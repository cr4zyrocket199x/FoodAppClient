package com.cr4zyrocket.foodappclient.Model;

public class User {
    private String Name,Password,Phone,Address;

    public User() {
    }

    public User(String name, String password, String phone, String address) {
        Name = name;
        Password = password;
        Phone = phone;
        Address = address;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
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
}
