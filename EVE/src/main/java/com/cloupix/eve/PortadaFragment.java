package com.cloupix.eve;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by AlonsoUSA on 16/11/13.
 */
public class PortadaFragment extends Fragment {



    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private PortadaFragmentCallbacks portadaCallbacks;

    private Button btnLogin, btnSigninup;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_portada, container, false);
        cargarElementos(rootView);
        return rootView;
    }

    private void cargarElementos(View rootView)
    {
        btnLogin = (Button) rootView.findViewById(R.id.btnLogin);
        btnSigninup = (Button) rootView.findViewById(R.id.btnSigninup);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (portadaCallbacks != null) {
                    portadaCallbacks.onPortadaFragmentLoginClicked();
                }
            }
        });
        btnSigninup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (portadaCallbacks != null) {
                    portadaCallbacks.onPortadaFragmentSigninupClicked();
                }
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            portadaCallbacks = (PortadaFragmentCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()+ " tiene que implementar PortadaFragmentCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        portadaCallbacks = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        // Construimos un menu especifico para este fragment
        inflater.inflate(R.menu.portada, menu);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle("");
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
        ((AuthenticatorActivity) getActivity()).onFragmentAttached(AuthenticatorActivity.PORTADA);
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface PortadaFragmentCallbacks {

        // Llamado cuando el boton de la actionBar sendLogin es pulsado
        void onPortadaFragmentLoginClicked();

        // Llamado cuando el boton de la actionBar sendLogin es pulsado
        void onPortadaFragmentSigninupClicked();
    }
}
