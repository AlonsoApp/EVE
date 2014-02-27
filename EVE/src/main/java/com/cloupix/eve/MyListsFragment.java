package com.cloupix.eve;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cloupix.eve.authentication.Authenticator;
import com.cloupix.eve.business.Lista;
import com.cloupix.eve.business.adapters.ListaListasAdapter;
import com.cloupix.eve.business.exceptions.EveHttpException;
import com.cloupix.eve.logic.ListaLogic;
import com.cloupix.eve.logic.SharedPreferencesManager;
import com.cloupix.eve.network.GestorComunicacionesTCP;

import java.util.ArrayList;

/**
 * Created by AlonsoApp on 16/11/13.
 */
public class MyListsFragment extends ListFragment {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public static final String ARG_SECTION_NUMBER = "section_number";

    private ProgressBar progressBar;
    private LinearLayout layoutNoListas;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_my_lists, container, false);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBarListas);
        layoutNoListas = (LinearLayout) rootView.findViewById(R.id.layoutNoListas);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        cargarListas();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
    }

    @Override
    public void onResume() {
        super.onResume();
        //cargarListas();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

    }

    private void cargarListas(){
        final Context context = getActivity().getApplicationContext();

        final SharedPreferencesManager spm = new SharedPreferencesManager(context);
        final String userEmail = spm.getAccountUserName();


        final AccountManager am = AccountManager.get(context);

        final Account[] arrayCuentas = am.getAccountsByType(Authenticator.ACCOUNT_TYPE);
        if(arrayCuentas.length<1)
            return;
        final String authToken = am.peekAuthToken(arrayCuentas[0], Authenticator.AUTH_TOKEN_TYPE);


        new AsyncTask<Void, Void, ArrayList<Lista>>(){

            private int statusCode;

            @Override
            protected ArrayList<Lista> doInBackground(Void... params) {
                ArrayList<Lista> listaListas = null;


                GestorComunicacionesTCP gcTCP = null;
                try{
                    gcTCP= new GestorComunicacionesTCP(GestorComunicacionesTCP.SERVER_IP, GestorComunicacionesTCP.SERVER_PORT);

                    listaListas = gcTCP.getListasByUserEmailToken(userEmail, authToken);
                    //listaListas = new ArrayList<Lista>();

                    statusCode = 200;
                /*}catch (EveHttpException e){
                    statusCode = e.getStatusCode();*/
                }catch (Exception e){
                    statusCode = 600;
                }finally {
                    try {
                        gcTCP.desconectar();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


                return listaListas;
            }

            @Override
            protected void onPostExecute(ArrayList<Lista> listaListas) {
                if(listaListas!=null){
                    if(listaListas.size()>0){
                        setListAdapter(new ListaListasAdapter(context, listaListas));
                        getListView().setVisibility(View.VISIBLE);
                    }else{
                        layoutNoListas.setVisibility(View.VISIBLE);
                    }
                }else{
                    switch (statusCode){
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
                progressBar.setVisibility(View.GONE);
            }
        }.execute();
    }
}
