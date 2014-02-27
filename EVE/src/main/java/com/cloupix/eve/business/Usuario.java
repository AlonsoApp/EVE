package com.cloupix.eve.business;

import java.io.Serializable;

/**
 * Created by AlonsoApp on 24/11/13.
 */
public class Usuario implements Serializable{

    private static final long serialVersionUID = 1L;

    private long userId;
    private String userFullName;
    private String userEmail;
    private long userProfileImageId;

    public Usuario() {
        this.userId = -1L;
        this.userFullName = "";
        this.userEmail = "";
        this.userProfileImageId = -1L;
    }

    public Usuario(long userId, String userFullName, String userEmail, long userProfileImageId) {
        this.userId = userId;
        this.userFullName = userFullName;
        this.userEmail = userEmail;
        this.userProfileImageId = userProfileImageId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public long getUserProfileImageId() {
        return userProfileImageId;
    }

    public void setUserProfileImageId(long userProfileImageId) {
        this.userProfileImageId = userProfileImageId;
    }

    // Guarda en la tabla ususario de la BD toda la información
    public void save(){
        // TODO: Implementar esto
    }
}
