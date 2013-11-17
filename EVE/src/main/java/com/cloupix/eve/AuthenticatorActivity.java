package com.cloupix.eve;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.cloupix.eve.authentication.Authenticator;
import com.cloupix.eve.business.exceptions.EVEHttpException;
import com.cloupix.eve.logic.AuthenticatorLogic;
import com.cloupix.eve.logic.SharedPreferencesManager;

/**
 * Created by AlonsoUSA on 16/11/13.
 */
public class AuthenticatorActivity extends AccountAuthenticatorActivity implements LoginFragment.LoginFragmentCallbacks, PortadaFragment.PortadaFragmentCallbacks, SigninupOneFragment.SigninupOneCallbacks, SigninupTwoFragment.SigninupTwoCallbacks
{
    // Estos atributos valen para especificar que fragment se tiene que abrir en el metodo openFragment
    public static final int PORTADA = 0;
    public static final int LOGIN = 1;
    public static final int SIGNINUP_ONE = 2;
    public static final int SIGNINUP_TWO = 3;

    private static final int SUBMIT_LOGIN = 0;
    private static final int SUBMIT_SIGNINUP = 1;


    public static String ARG_USER_PASS = "arg_user_pass";
    public static String ARG_IS_ADDING_NEW_ACCOUNT = "arg_is_adding_new_account";
    public static String COMES_FROM_MAIN_ACTIVITY = "comes_from_main_activity";
    public static String ARG_ACCOUNT_TYPE = "account_type";
    public static String ARG_AUTH_TYPE = "auth_type";
    private static String STATE_FRAGMENT_ATACHED = "fragment_atached";

    private String signinupUserName;
    private int fragmentAttached = PORTADA;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autheticator);
        //Si teniamos un fragmento abierto se habrira de nuevo el mismo
        if(savedInstanceState!=null){
            openFragment(savedInstanceState.getInt(STATE_FRAGMENT_ATACHED, PORTADA), true);
        }else{

            openFragment(PORTADA, true);
        }
    }

    @Override
    public void onLoginFragmentSubmit(String userName, String userPass) {
        submit(userName, userPass, "", SUBMIT_LOGIN);
    }

    @Override
    public void onPortadaFragmentLoginClicked() {
        openFragment(LOGIN, false);
    }

    @Override
    public void onPortadaFragmentSigninupClicked() {
        openFragment(SIGNINUP_ONE, false);
    }

    @Override
    public void onImageButtonClicked() {
        //TODO: Abrir un picker de imagen y almacenar el bitmap en un atributo
    }

    @Override
    public void onNextButtonClicked(String userName) {
        signinupUserName = userName;
        openFragment(SIGNINUP_TWO, false);
    }

    @Override
    public void onDoneButtonClicked(String email, String password) {
        submit(signinupUserName, password, email, SUBMIT_SIGNINUP);
    }

    private void openFragment(int fragmentId, boolean isFirstFragment){
        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment;
        FragmentTransaction fragmentTransaction;
        switch (fragmentId){
            case PORTADA:
                PortadaFragment portadaFragment;
                fragment = fragmentManager.findFragmentByTag(getResources().getString(R.string.tag_fragment_portada));
                // Comprobamos que la clase que hemos sacado es de tipo LoginFragment, si no lo es
                // es que se trata de otro fragmento que se encontraba en el authenticatorContainer
                if (fragment instanceof PortadaFragment){
                    portadaFragment = (PortadaFragment) fragment;
                }else{
                    portadaFragment = new PortadaFragment();
                }

                // Creamos la transaccion que sustituira el actual fragmento por el nueo y enviara el viejo a la BackStack
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.autheticatorContainer, portadaFragment, getString(R.string.tag_fragment_portada));
                // Cuando es el primer fragment que se abre, si mandas el estado anterio al backStrack queda un estado extra en blanco
                // que aparece al pulsar el boton back al final de la Stack
                if (!isFirstFragment)
                    fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            case LOGIN:
                LoginFragment loginFragment;
                fragment = fragmentManager.findFragmentByTag(getResources().getString(R.string.tag_fragment_login));
                // Comprobamos que la clase que hemos sacado es de tipo LoginFragment, si no lo es
                // es que se trata de otro fragmento que se encontraba en el authenticatorContainer
                if (fragment instanceof LoginFragment){
                    loginFragment = (LoginFragment) fragment;
                }else{
                    loginFragment = new LoginFragment();
                }

                // Creamos la transaccion que sustituira el actual fragmento por el nueo y enviara el viejo a la BackStack
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.autheticatorContainer, loginFragment, getString(R.string.tag_fragment_login));
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            case SIGNINUP_ONE:
                SigninupOneFragment signinupOneFragment;
                fragment = fragmentManager.findFragmentByTag(getResources().getString(R.string.tag_fragment_signinup_one));
                // Comprobamos que la clase que hemos sacado es de tipo LoginFragment, si no lo es
                // es que se trata de otro fragmento que se encontraba en el authenticatorContainer
                if (fragment instanceof SigninupOneFragment){
                    signinupOneFragment = (SigninupOneFragment) fragment;
                }else{
                    signinupOneFragment = new SigninupOneFragment();
                }

                // Creamos la transaccion que sustituira el actual fragmento por el nueo y enviara el viejo a la BackStack
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.autheticatorContainer, signinupOneFragment, getString(R.string.tag_fragment_signinup_one));
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            case SIGNINUP_TWO:
                SigninupTwoFragment signinupTwoFragment;
                fragment = fragmentManager.findFragmentByTag(getResources().getString(R.string.tag_fragment_signinup_two));
                // Comprobamos que la clase que hemos sacado es de tipo LoginFragment, si no lo es
                // es que se trata de otro fragmento que se encontraba en el authenticatorContainer
                if (fragment instanceof SigninupTwoFragment){
                    signinupTwoFragment = (SigninupTwoFragment) fragment;
                }else{
                    signinupTwoFragment = new SigninupTwoFragment();
                }

                // Creamos la transaccion que sustituira el actual fragmento por el nueo y enviara el viejo a la BackStack
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.autheticatorContainer, signinupTwoFragment, getString(R.string.tag_fragment_signinup_two));
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Guardamos el idFragment que estaba abierto por ultima vez al llamarse a onSaveInstanceState()
        outState.putInt(STATE_FRAGMENT_ATACHED, fragmentAttached);
    }

    // Metodo para que los fragments en onResume espeficiquen en que fragment se encuentra la activity
    public void onFragmentAttached(int fragmentId){
        this.fragmentAttached = fragmentId;
    }


    // Este metodo vale tanto para login como para Signinup, solamente hay que especificar el tipo de submit que s equiere hacer
    public void submit(final String userName, final String userPass, final String userEmail, final int submitType) {

        new AsyncTask<Void, Void, Intent>() {

            private final ProgressDialog dialog = new ProgressDialog(AuthenticatorActivity.this);
            private int statusCode = 0;
            private boolean errorOcurred = false;

            @Override
            protected void onPreExecute() {
                this.dialog.setMessage(getApplicationContext().getString(R.string.msg_login_en_curso));
                this.dialog.setIndeterminate(true);
                this.dialog.show();
                super.onPreExecute();
            }

            @Override
            protected Intent doInBackground(Void... params) {

                AuthenticatorLogic authLogic = new AuthenticatorLogic(getApplicationContext());
                String authtoken="";
                try {
                    // Dependiendo del submitType hacemos una cosa u otra
                    if(submitType == SUBMIT_LOGIN){
                        authtoken = authLogic.userLogin(userName, userPass, Authenticator.AUTH_TOKEN_TYPE);
                    }else{
                        authtoken = authLogic.userSignInUp(userName, userPass, userEmail, Authenticator.AUTH_TOKEN_TYPE);
                    }
                    if(!TextUtils.isEmpty(authtoken))
                    {
                        final Intent res = new Intent();
                        res.putExtra(AccountManager.KEY_ACCOUNT_NAME, userName);
                        res.putExtra(AccountManager.KEY_ACCOUNT_TYPE, Authenticator.ACCOUNT_TYPE);
                        res.putExtra(AccountManager.KEY_AUTHTOKEN, authtoken);
                        res.putExtra(ARG_USER_PASS, userPass);
                        return res;
                    }
                } catch (EVEHttpException ex) {
                    statusCode = ex.getStatusCode();
                    errorOcurred=true;
                    ex.printStackTrace();
                } catch (Exception ex) {
                    statusCode = 600;
                    errorOcurred=true;
                    ex.printStackTrace();
                }
                // Si el token esta vacio ha tenido que haber un error
                errorOcurred=true;
                return null;
            }

            @Override
            protected void onPostExecute(Intent intent) {
                this.dialog.cancel();
                if(!errorOcurred)
                {
                    finishLogin(intent);
                    // TODO: Enviar la imagen (si la hubiera) en caso de ser submitType == SUBMIT_SIGNINUP
                }else if(statusCode==401){
                    Toast.makeText(getApplicationContext(), getString(R.string.error_401), Toast.LENGTH_SHORT).show();
                }else if(statusCode==412){
                    Toast.makeText(getApplicationContext(), getString(R.string.error_412), Toast.LENGTH_LONG).show();
                }else{
                    //TODO: swith de los errores
                }
            }

        }.execute();
    }

    private void finishLogin(Intent intent) {
        AccountManager mAccountManager = AccountManager.get(this);
        String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String accountPassword = intent.getStringExtra(ARG_USER_PASS);
        final Account account = new Account(accountName, intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));

        if (getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false))
        {
            String authtoken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
            String authtokenType = Authenticator.AUTH_TOKEN_TYPE;
            // Creating the account on the device and setting the auth token we got
            // (Not setting the auth token will cause another call to the server to authenticate the user)
            try{

                mAccountManager.addAccountExplicitly(account, accountPassword, null);
                mAccountManager.setAuthToken(account, authtokenType, authtoken);

            }catch(Exception e)
            {
                e.printStackTrace();
            }
        } else {
            mAccountManager.setPassword(account, accountPassword);
        }
        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        if(getIntent().getBooleanExtra(COMES_FROM_MAIN_ACTIVITY, false)){
            Intent intentMainActivity = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intentMainActivity);
        }
        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(getApplicationContext());
        sharedPreferencesManager.setAccountUserName(accountName);
        finish();
    }

}

