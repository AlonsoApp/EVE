package com.cloupix.eve;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by AlonsoUSA on 16/11/13.
 */
public class SigninupTwoFragment extends Fragment {

    //Recordamos el email que se introdujo cuando se pauso el fragment.
    private static final String STATE_EMAIL = "saved_email";
    private static final int NUM_MIN_CHAR_PASS = 8;

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private SigninupTwoCallbacks signinupTwoCallbacks;

    private String userEmail;
    private boolean mFromSavedInstanceState;

    private EditText editTextSigninupEmail, editTextSigninupPassword;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            userEmail = savedInstanceState.getString(STATE_EMAIL);
            mFromSavedInstanceState = true;
        }else{
            userEmail = getOwnerEmail(getActivity());
        }

        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    // Sacamos el nombre de la cuenta de Google del dispositivo
    static String getOwnerEmail(Context context) {
        AccountManager accountManager = AccountManager.get(context);
        Account account = getAccount(accountManager);

        if (account == null) {
            return null;
        } else {
            return account.name;
        }
    }

    private static Account getAccount(AccountManager accountManager) {
        Account[] accounts = accountManager.getAccountsByType("com.google");
        Account account;
        if (accounts.length > 0) {
            account = accounts[0];
        } else {
            account = null;
        }
        return account;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_signinup_step_two, container, false);
        cargarElementos(rootView);
        return rootView;
    }

    private void cargarElementos(View rootView)
    {
        editTextSigninupEmail = (EditText) rootView.findViewById(R.id.editTextSigninupEmail);
        editTextSigninupPassword = (EditText) rootView.findViewById(R.id.editTextSigninupPassword);

        // Si el ususario ya introdujo un nombre de ususario anteriormente lo volvemos a poner
        if(!TextUtils.isEmpty(userEmail))
            editTextSigninupEmail.setText(userEmail);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            signinupTwoCallbacks = (SigninupTwoCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()+ " tiene que implementar SigninupTwoCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        signinupTwoCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (editTextSigninupEmail != null)
            outState.putString(STATE_EMAIL, editTextSigninupEmail.getText().toString());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_done:
                // Si se pulsa el boton de la actionBar NEXT se lanza un evento a la activity con el user y la img
                if (signinupTwoCallbacks != null && editTextSigninupEmail!=null && editTextSigninupPassword!=null) {
                    if(TextUtils.isEmpty(editTextSigninupEmail.getText().toString())){
                        Toast.makeText(getActivity().getApplicationContext(), getString(R.string.msg_email_no_introducido), Toast.LENGTH_SHORT).show();
                    }else if (TextUtils.isEmpty(editTextSigninupPassword.getText().toString())){
                        Toast.makeText(getActivity().getApplicationContext(), getString(R.string.msg_password_no_introducida), Toast.LENGTH_SHORT).show();
                    }else if (editTextSigninupPassword.getText().toString().length()<NUM_MIN_CHAR_PASS){
                        Toast.makeText(getActivity().getApplicationContext(), getString(R.string.msg_password_pequenya), Toast.LENGTH_SHORT).show();
                    }else{
                        signinupTwoCallbacks.onDoneButtonClicked(editTextSigninupEmail.getText().toString(), editTextSigninupPassword.getText().toString());
                    }

                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        // Construimos un menu especifico para este fragment
        inflater.inflate(R.menu.signinup_two, menu);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(getString(R.string.title_signin));
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setHomeButtonEnabled(false);

        super.onCreateOptionsMenu(menu, inflater);
    }

    private ActionBar getActionBar() {
        return getActivity().getActionBar();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AuthenticatorActivity) getActivity()).onFragmentAttached(AuthenticatorActivity.SIGNINUP_TWO);
    }


    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface SigninupTwoCallbacks {

        // Llamado cuando el boton done del action bar se ha clicado
        void onDoneButtonClicked(String email, String password);
    }
}
