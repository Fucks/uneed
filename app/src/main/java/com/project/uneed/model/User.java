package com.project.uneed.model;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by Admin on 27/07/2015.
 */
public class User implements Serializable {

    private String firstName;
    private String lastName;

    private Bitmap photo;

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

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

}
