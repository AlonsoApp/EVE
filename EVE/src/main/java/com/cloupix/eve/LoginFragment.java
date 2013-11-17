package com.cloupix.eve;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
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
public class LoginFragment extends Fragment{


    //Recordamos el nombre de ususario que se introdujo cuando se pauso el login.
    private static final String STATE_USER_NAME = "saved_user_name";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private LoginFragmentCallbacks loginCallbacks;

    private String userName;
    private boolean mFromSavedInstanceState;

    private EditText editTextLoginUsername, editTextLoginPassword;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            userName = savedInstanceState.getString(STATE_USER_NAME);
            mFromSavedInstanceState = true;
        }

        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        cargarElementos(rootView);
        return rootView;
    }

    private void cargarElementos(View rootView)
    {
        editTextLoginUsername = (EditText) rootView.findViewById(R.id.editTextLoginUsername);
        editTextLoginPassword = (EditText) rootView.findViewById(R.id.editTextLoginPassword);

        // Si el ususario ya introdujo un nombre de ususario anteriormente lo volvemos a poner
        if(mFromSavedInstanceState && !TextUtils.isEmpty(userName))
            editTextLoginUsername.setText(userName);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            loginCallbacks = (LoginFragmentCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()+ " tiene que implementar LoginFragmentCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        loginCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (editTextLoginUsername != null)
            outState.putString(STATE_USER_NAME, editTextLoginUsername.getText().toString());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_send_login:
                // Si se pulsa el boton de la actionBar sendLogin se lanza un evento a la activity con el user y la pass
                if (loginCallbacks != null && editTextLoginUsername!=null && editTextLoginPassword!=null) {
                    if(TextUtils.isEmpty(editTextLoginUsername.getText().toString())){
                        Toast.makeText(getActivity().getApplicationContext(), getString(R.string.msg_username_no_introducido), Toast.LENGTH_SHORT).show();
                    }else if (TextUtils.isEmpty(editTextLoginPassword.getText().toString())){
                        Toast.makeText(getActivity().getApplicationContext(), getString(R.string.msg_password_no_introducida), Toast.LENGTH_SHORT).show();
                    }else{
                        loginCallbacks.onLoginFragmentSubmit(editTextLoginUsername.getText().toString(), editTextLoginPassword.getText().toString());
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
        inflater.inflate(R.menu.login, menu);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(getString(R.string.title_login));
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
        ((AuthenticatorActivity) getActivity()).onFragmentAttached(AuthenticatorActivity.LOGIN);
    }


    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface LoginFragmentCallbacks {

        // Llamado cuando el boton de la actionBar sendLogin es pulsado
        void onLoginFragmentSubmit(String userName, String userPass);
    }
}
