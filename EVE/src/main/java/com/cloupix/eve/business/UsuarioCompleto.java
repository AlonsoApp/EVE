package com.cloupix.eve.business;

/**
 * Created by AlonsoApp on 17/01/14.
 */
public class UsuarioCompleto extends Usuario {

    private String userToken;
    private String userPassword;

    public UsuarioCompleto(){
        super();
        this.userToken="";
        this.userPassword="";
    }

    public UsuarioCompleto(String userToken, String userPassword){
        super();
        this.userToken = userToken;
        this.userPassword = userPassword;
    }

    public UsuarioCompleto(long userId, String userFullName, String userEmail, long userProfileImageId, String userToken, String userPassword){
        super(userId, userFullName, userEmail, userProfileImageId);
        this.userToken = userToken;
        this.userPassword = userPassword;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }
}
