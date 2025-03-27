// src/main/java/com/example/gmh_app/User.java
package com.example.gmh_app.Classes;

public class User {
    public String username, email, cellphone, age, location, city, country, businessName, businessDescription, gender, education, hasBusinessName, isNameDisplayed, province;

    public User(String username, String email, String cellphone, String age, String location, String city, String country,
                String businessName, String businessDescription, String gender, String education, String hasBusinessName, String isNameDisplayed) {
        this.username = username;
        this.email = email;
        this.cellphone = cellphone;
        this.age = age;
        this.location = location;
        this.city = city;
        this.country = country;
        this.businessName = businessName;
        this.businessDescription = businessDescription;
        this.gender = gender;
        this.education = education;
        this.hasBusinessName = hasBusinessName;
        this.isNameDisplayed = isNameDisplayed;
        this.province = province;
    }
}
