package com.serviceapps.transport.models;

import android.graphics.Bitmap;

public class ProfileDataClass {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String  profileImage;

    public ProfileDataClass(){

    }

    public ProfileDataClass(String firstName, String lastName, String phoneNumber, String profileImage) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.profileImage = profileImage;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
