package com.cloupix.eve.logic;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

import com.cloupix.eve.authentication.Authenticator;
import com.cloupix.eve.business.UsuarioCompleto;
import com.cloupix.eve.business.exceptions.EveHttpException;
import com.cloupix.eve.network.GestorComunicacionesTCP;

/**
 * Created by AlonsoApp on 16/11/13.
 */
public class AuthenticatorLogic
{

    public static String STATUS_CODE = "status_code";
    public static String ERROR_MESSAGE = "error_message";

    public static String TOKEN = "token";

    private AccountManager accountManager;
    private Context context;

    //Ponemos private el constructor sin parametros para que sea obligatorio instanciar la clase con conext
    private AuthenticatorLogic()
    {
    }

    public AuthenticatorLogic(Context context)
    {
        this.context = context;
        accountManager = AccountManager.get(context);
    }

    // Hace un login del usuario userName y userPass devoliendo (si el login es correcto) el token que le corresponde
    public UsuarioCompleto userLogin(String userName, String userPass, String mAuthTokenType) throws EveHttpException, Exception
    {
        /*// Sustituido por GestorComunicacionesTCP
        GestorComunicacionesREST gcREST = new GestorComunicacionesREST(GestorComunicacionesREST.SERVER_IP, GestorComunicacionesREST.SERVER_PORT_SSL, context);
        return gcREST.userLogin(userName, userPass, mAuthTokenType);
        */
        UsuarioCompleto usuario;
        GestorComunicacionesTCP gcTCP = new GestorComunicacionesTCP(GestorComunicacionesTCP.SERVER_IP, GestorComunicacionesTCP.SERVER_PORT);
        try{
            usuario = gcTCP.userLogin(userName, userPass, mAuthTokenType);
        }catch(EveHttpException e){
            throw e;
        }catch(Exception e){
            throw e;
        }finally {
            gcTCP.desconectar();
        }
        return usuario;
    }
    /*
     * Hace un siginup del usuario userName y userPass devoliendo (si el login es correcto) el token que le corresponde
     * Trata de registrar al usuario con userName, userPass y userEmail introducidos
     * Si ya existe una cuenta asociada a ese email saltará una EveHttpException con codigo 412 que se tratará más arriba
     * Si el registro sale bien, actual igual que el login, devuelve el token entregado por el servidor asociado a la cuenta
     */
    public UsuarioCompleto userSignInUp(String userFullName, String userPass, String userEmail) throws EveHttpException, Exception
    {
        /*// Sustituido por GestorComunicacionesTCP
        GestorComunicacionesREST gcREST = new GestorComunicacionesREST(GestorComunicacionesREST.SERVER_IP, GestorComunicacionesREST.SERVER_PORT_SSL, context);
        return gcREST.userSignInUp(userName, userPass, userEmail);
        */
        UsuarioCompleto usuario;
        GestorComunicacionesTCP gcTCP = new GestorComunicacionesTCP(GestorComunicacionesTCP.SERVER_IP, GestorComunicacionesTCP.SERVER_PORT);
        try{
            usuario = gcTCP.userSignInUp(userFullName, userPass, userEmail);
        }catch(EveHttpException e){
            throw e;
        }catch(Exception e){
            throw e;
        }finally {
            gcTCP.desconectar();
        }
        return usuario;
    }

    public Account getAccountByUserName(String userName)
    {
        Account[] arrayCuentas = this.accountManager.getAccountsByType(Authenticator.ACCOUNT_TYPE);
        for(int i = 0; i<arrayCuentas.length; i++){
            if(arrayCuentas[i].name.equals(userName))
                return arrayCuentas[i];
        }
        return null;
    }

    public String getAuthTokenByUserName(String userName) {
        return accountManager.peekAuthToken(getAccountByUserName(userName), Authenticator.AUTH_TOKEN_TYPE);
    }

    public void invalidateAuthToken(String token){
        accountManager.invalidateAuthToken(Authenticator.ACCOUNT_TYPE, token);
    }

}
