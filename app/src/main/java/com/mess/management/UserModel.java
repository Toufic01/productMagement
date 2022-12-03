package com.mess.management;

public class UserModel {
    String name,address,email,password,gender;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public UserModel(String name, String address, String email, String password, String gender) {
        this.name = name;
        this.address = address;
        this.email = email;
        this.password = password;
        this.gender = gender;
    }

    public UserModel() {
    }
}
