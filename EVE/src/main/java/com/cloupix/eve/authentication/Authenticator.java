package com.cloupix.eve.authentication;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.cloupix.eve.AuthenticatorActivity;
import com.cloupix.eve.logic.AuthenticatorLogic;

/**
 * Created by AlonsoUSA on 16/11/13.
 */
public class Authenticator extends AbstractAccountAuthenticator
{
    public static String ACCOUNT_TYPE = "com.cloupix.eve";
    public static String AUTH_TOKEN_TYPE = "1";

    private Context context;

    public Authenticator(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options)throws NetworkErrorException
    {
        //Llamado cuando el susuario quiere logearse o anyadir una cuenta nueva al telefono
        final Intent intent = new Intent(this.context, AuthenticatorActivity.class);
        intent.putExtra(AuthenticatorActivity.ARG_ACCOUNT_TYPE, accountType);
        intent.putExtra(AuthenticatorActivity.ARG_AUTH_TYPE, authTokenType);
        intent.putExtra(AuthenticatorActivity.ARG_IS_ADDING_NEW_ACCOUNT, true);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) throws NetworkErrorException
    {
        return null;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException
    {
        /**IMPORTANTE: Si el Token que devuelve no vale, es necesario invalidarlo con AccountManager.invalidateAuthToken()
         * Esto lo haremos en la parte del programa "cliente" cuando el servidor no devuelva un msg de token invalido*/

        //El Account que le viene por parametros (que esta compuesto por la info que esta en el AccountManager) mas el tipo de Token que solicitamos,
        //son utilizados para obtener el Token que tengamos almacenado (y asignado a esta cuenta) en el AccountManager
        final AccountManager am = AccountManager.get(this.context);

        String authToken = am.peekAuthToken(account, authTokenType);

        // Si el Token está vacío, solicitamos al servidor un token nuevo con el user y la password que estaban almacenados en el AccountManager
        if (TextUtils.isEmpty(authToken)) {
            final String password = am.getPassword(account);
            if (password != null) {
                AuthenticatorLogic authLogic = new AuthenticatorLogic(this.context);
                try {
                    authToken = authLogic.userLogin(account.name, password, authTokenType);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        // Si el token que obtenemos no está vacío devolvemos un Bundle con el nombre de la cuenta (que ya teniamos),
        //el tipo de cuenta (que ya teniamos) y el valor del token que hemos obtenido
        if (!TextUtils.isEmpty(authToken)) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
            return result;
        }

        //Si llegamos hasta aqui es que o el ususario o la contraseña no han sido los adecuados para solicitar un token
        //Por lo tanto, devolvemos un Intent de nuestra AuthenticatorActivity para que el usuario introduzca personalmente
        //un usuario y una contraseña
        final Intent intent = new Intent(this.context, AuthenticatorActivity.class);
        intent.putExtra(AuthenticatorActivity.ARG_ACCOUNT_TYPE, account.type);
        intent.putExtra(AuthenticatorActivity.ARG_AUTH_TYPE, authTokenType);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException
    {
        // TODO Auto-generated method stub
        return null;
    }

}

