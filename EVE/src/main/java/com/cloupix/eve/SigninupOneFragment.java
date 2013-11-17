package com.cloupix.eve;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

/**
 * Created by AlonsoUSA on 16/11/13.
 */
public class SigninupOneFragment extends Fragment {

    //Recordamos el nombre de ususario que se introdujo cuando se pauso el fragment.
    private static final String STATE_USER_NAME = "saved_user_name";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private SigninupOneCallbacks signinupOneCallbacks;

    private String userName;
    private boolean mFromSavedInstanceState;

    private EditText editTextSigninupUserName;
    private ImageButton imgBtnProfileImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            userName = savedInstanceState.getString(STATE_USER_NAME);
            mFromSavedInstanceState = true;
        }else{
            userName = getOwnerFullName();
        }

        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    private String getOwnerFullName(){
        String result = "";
        Cursor c = getActivity().getContentResolver().query(ContactsContract.Profile.CONTENT_URI, null, null, null, null);
        int count = c.getCount();
        String[] columnNames = c.getColumnNames();
        boolean b = c.moveToFirst();
        int position = c.getPosition();
        if (count == 1 && position == 0) {

            // Para recorrer todos los campos del contacto
            /*
            for (int j = 0; j < columnNames.length; j++) {
                String columnName = columnNames[j];
                String columnValue = c.getString(c.getColumnIndex(columnName));
                int i = 0;
                //Use the values
            }*/
            result = c.getString(c.getColumnIndex("display_name"));
        }
        c.close();
        return result;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_signinup_step_one, container, false);
        cargarElementos(rootView);
        return rootView;
    }

    private void cargarElementos(View rootView)
    {
        editTextSigninupUserName = (EditText) rootView.findViewById(R.id.editTextSigninupUserName);
        imgBtnProfileImage = (ImageButton) rootView.findViewById(R.id.imgBtnProfileImage);

        // Si el ususario ya introdujo un nombre de ususario anteriormente lo volvemos a poner

        if(!TextUtils.isEmpty(userName))
            editTextSigninupUserName.setText(userName);


        imgBtnProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signinupOneCallbacks.onImageButtonClicked();
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            signinupOneCallbacks = (SigninupOneCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()+ " tiene que implementar SigninupOneCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        signinupOneCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (editTextSigninupUserName != null)
            outState.putString(STATE_USER_NAME, editTextSigninupUserName.getText().toString());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_next:
                // Si se pulsa el boton de la actionBar NEXT se lanza un evento a la activity con el user y la img
                if (signinupOneCallbacks != null && editTextSigninupUserName!=null) {
                    if(TextUtils.isEmpty(editTextSigninupUserName.getText().toString())){
                        Toast.makeText(getActivity().getApplicationContext(), getString(R.string.msg_username_no_introducido), Toast.LENGTH_SHORT).show();
                    }else{
                        signinupOneCallbacks.onNextButtonClicked(editTextSigninupUserName.getText().toString());
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
        inflater.inflate(R.menu.signinup_one, menu);
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
        ((AuthenticatorActivity) getActivity()).onFragmentAttached(AuthenticatorActivity.SIGNINUP_ONE);
    }


    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface SigninupOneCallbacks {

        // Llamado cuando el boton de seleccion de imagen de ususario es pulsado
        void onImageButtonClicked();

        // Llamado cuando el boton next del action bar se ha clicado
        void onNextButtonClicked(String userName);
    }

}
