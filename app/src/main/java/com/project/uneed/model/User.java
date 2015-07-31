package com.project.uneed.model;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by Admin on 27/07/2015.
 */
public class User implements Serializable {

    private String fullName;
    private String loginService;
    private String accessToken;
    private String applicationId;
    private String userId;
    private String accountName;

    private Bitmap photo;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    public String getLoginService() {
        return loginService;
    }

    public void setLoginService(String loginService) {
        this.loginService = loginService;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }
}
