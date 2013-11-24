package com.cloupix.eve.business;

/**
 * Created by AlonsoApp on 24/11/13.
 */
public class AuthResponse {
    private long userId;
    private String userFullName;
    private String userEmail;
    private String userToken;
    private long userProfileImageId;


    public AuthResponse(long userId, String userFullName, String userEmail, String userToken, long userProfileImageId) {
        this.userId = userId;
        this.userFullName = userFullName;
        this.userEmail = userEmail;
        this.userToken = userToken;
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

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    // Guarda en la tabla ususario de la BD toda la información
    public void save(){
        // TODO: Implementar esto
    }
}
