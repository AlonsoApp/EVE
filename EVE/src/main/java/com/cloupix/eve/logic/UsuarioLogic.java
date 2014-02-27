package com.cloupix.eve.logic;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cloupix.eve.R;
import com.cloupix.eve.authentication.Authenticator;
import com.cloupix.eve.business.Usuario;
import com.cloupix.eve.business.UsuarioCompleto;
import com.cloupix.eve.business.exceptions.EveHttpException;
import com.cloupix.eve.network.GestorComunicacionesTCP;

/**
 * Created by AlonsoUSA on 18/01/14.
 */
public class UsuarioLogic {

    public void fillNavigationHeaderInfo(final View textViewFullName, final View textViewEmail, final View imgViewProfile, final Context context){

        final SharedPreferencesManager spm = new SharedPreferencesManager(context);
        final long idUser = spm.getAccountUserId();
        if(idUser==-1L)
            return;

        new AsyncTask<Void, Void, Usuario>(){

            Bitmap profileImage;
            int statusCode;

            @Override
            protected Usuario doInBackground(Void... params) {
                Usuario usuario = null;
                GestorComunicacionesTCP gcTCP = null;
                try{
                    gcTCP= new GestorComunicacionesTCP(GestorComunicacionesTCP.SERVER_IP, GestorComunicacionesTCP.SERVER_PORT);
                    usuario = gcTCP.getUsuarioById(idUser);
                    if(usuario!=null)
                        if(usuario.getUserProfileImageId()!=-1){
                            profileImage = gcTCP.getImagenById(usuario.getUserProfileImageId());
                            // La redondeamos
                            ImageLogic imageLogic = new ImageLogic(context);
                            profileImage = imageLogic.getRoundedCornerBitmap(profileImage);
                        }
                }catch(EveHttpException e){
                    statusCode = e.getStatusCode();
                }catch(Exception e){
                    statusCode = 600;
                }finally {
                    try {
                        gcTCP.desconectar();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return usuario;
            }

            @Override
            protected void onPostExecute(Usuario usuario) {
                if(usuario!=null){
                    // FullName
                    if(textViewFullName instanceof TextView){
                        ((TextView) textViewFullName).setText(usuario.getUserFullName());
                    }else if(textViewFullName instanceof EditText){
                        ((EditText) textViewFullName).setText(usuario.getUserFullName());
                    }
                    spm.setAccountUserFullName(usuario.getUserFullName());
                    // Email
                    if(textViewEmail instanceof TextView){
                        ((TextView) textViewEmail).setText(usuario.getUserEmail());
                    }else if(textViewEmail instanceof EditText){
                        ((EditText) textViewEmail).setText(usuario.getUserEmail());
                    }
                    spm.setAccountUserName(usuario.getUserEmail());
                    // TODO: Aquí se debería cambiar el nombre de la cuenta de AccountManager.
                    // Como para ello habría que eliminar la cuenta y volver a logearse, lo dejamos
                    // para otro momento ya que no existe caso de uso alguno en el que el usuario cambie el nombre de su cuenta

                    // Imagen
                    if(profileImage!=null){
                        if(imgViewProfile instanceof ImageView){
                            ((ImageView)imgViewProfile).setImageBitmap(profileImage);
                        }else if(imgViewProfile instanceof ImageButton){
                            ((ImageButton)imgViewProfile).setImageBitmap(profileImage);
                        }
                    }

                }else{
                    switch(statusCode){

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
            }
        }.execute();
    }

    public void updateUsuarioInfo(final String userFullName, final String userEmail, final String userPassword, final Bitmap profileImage, final Context context){
        final SharedPreferencesManager spm = new SharedPreferencesManager(context);
        final long idUser = spm.getAccountUserId();
        final long idProfileImage = spm.getUserProfileImageId();
        String oldUserFullName = spm.getUserFullName();
        String oldUserEmail = spm.getAccountUserName();
        if(idUser==-1 || (oldUserFullName.equals(userFullName) && oldUserEmail.equals(userEmail) && TextUtils.isEmpty(userPassword) && profileImage==null))
            return;

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
                    UsuarioCompleto usuario = new UsuarioCompleto(idUser, userFullName, userEmail, idProfileImage, authToken, userPassword);
                    gcTCP.updateUsuarioInfoById(usuario);
                    if(profileImage!=null && idProfileImage!=-1L)
                        gcTCP.uploadBitmapById(profileImage, idProfileImage, userEmail, authToken, context);
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
                        spm.setAccountUserName(userEmail);
                        spm.setAccountUserFullName(userFullName);

                        Toast.makeText(context, context.getString(R.string.guardado_correctamente), Toast.LENGTH_SHORT).show();
                        break;
                    case 401:
                        Toast.makeText(context, context.getString(R.string.error_401), Toast.LENGTH_SHORT).show();
                        break;
                    case 402:
                        new AlertDialog.Builder(context)
                                .setTitle(R.string.cambios_no_realizados)
                                .setMessage(R.string.msg_ya_existe_email)
                                .setCancelable(false)
                                .setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();


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
