package com.cloupix.eve.logic;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.cloupix.eve.R;
import com.cloupix.eve.authentication.Authenticator;
import com.cloupix.eve.business.Lista;
import com.cloupix.eve.business.exceptions.EveHttpException;
import com.cloupix.eve.network.GestorComunicacionesTCP;

/**
 * Created by AlonsoApp on 22/01/14.
 */
public class ListaLogic {


    public void crearLista(final Lista nuevaLista, final Context context){
        final SharedPreferencesManager spm = new SharedPreferencesManager(context);
        final String userEmail = spm.getAccountUserName();


        final AccountManager am = AccountManager.get(context);

        final Account[] arrayCuentas = am.getAccountsByType(Authenticator.ACCOUNT_TYPE);
        if(arrayCuentas.length<1)
            return;
        final String authToken = am.peekAuthToken(arrayCuentas[0], Authenticator.AUTH_TOKEN_TYPE);


        new AsyncTask<Void, Void, Void>(){

            int statusCode;

            @Override
            protected Void doInBackground(Void... params) {
                GestorComunicacionesTCP gcTCP = null;
                try{
                    gcTCP= new GestorComunicacionesTCP(GestorComunicacionesTCP.SERVER_IP, GestorComunicacionesTCP.SERVER_PORT);

                    gcTCP.crearLista(userEmail, authToken, nuevaLista);

                    statusCode = 200;
                }catch (EveHttpException e){
                    statusCode = e.getStatusCode();
                }catch (Exception e){
                    statusCode = 600;
                }finally {
                    try {
                        gcTCP.desconectar();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                switch (statusCode){
                    case 200:

                        Toast.makeText(context, context.getString(R.string.guardado_correctamente), Toast.LENGTH_SHORT).show();
                        break;
                    case 401:
                        Toast.makeText(context, context.getString(R.string.error_401), Toast.LENGTH_SHORT).show();
                        break;
                    case 500:
                        Toast.makeText(context, context.getString(R.string.error_500), Toast.LENGTH_SHORT).show();
                        break;
                    case 501:
                        Toast.makeText(context, context.getString(R.string.error_501), Toast.LENGTH_SHORT).show();
                        break;
                    case 600:
                        Toast.makeText(context, context.getString(R.string.error_600), Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        }.execute();
    }
}
