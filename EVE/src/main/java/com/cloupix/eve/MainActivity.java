package com.cloupix.eve;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.widget.Toast;

import com.cloupix.eve.authentication.Authenticator;
import com.cloupix.eve.logic.SharedPreferencesManager;

/**
 * Created by AlonsoApp on 12/11/13.
 */
public class MainActivity extends Activity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    // Estos atributos valen para especificar que fragment se tiene que abrir cuando salte el evento {@link #onNavigationDrawerItemSelected()}
    public static final int PROFILE = 0;
    public static final int STREAM = 1;
    public static final int MIS_LISTAS = 2;
    public static final int ADMINISTRAR = 3;
    public static final int BUSCAR = 4;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private SharedPreferencesManager sharedPreferencesManager;
    private String authToken;
    private LruCache<String, Bitmap> mMemoryCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpCache();
        cargarPreferencias();
        manageAccounts();


        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mMemoryCache);
    }

    //Este metodo es el que se ejecuta cuando se clica un elemento
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        switch(position){
            case STREAM:
                // Stream
                fragmentManager.beginTransaction().replace(R.id.mainContainer, PlaceholderFragment.newInstance(position)).commit();
                break;


            case MIS_LISTAS:
                // Mis Listas
                MyListsFragment myListFragment;
                Fragment fragment = fragmentManager.findFragmentByTag(getResources().getString(R.string.tag_fragment_my_ists));
                // Comprobamos que la clase que hemos sacado es de tipo MyListFragment, si no lo es
                // es que se trata de otro fragmento que se encontraba en el mainContainer
                if (fragment instanceof MyListsFragment){
                    myListFragment = (MyListsFragment) fragment;
                }else{
                    myListFragment = new MyListsFragment();

                    // Ponemos los args extra que quramos enviar al Fragment
                    Bundle args = new Bundle();
                    // Enviamos la posicion del menu para que cuando el fragemnt haga atach cambie el titulo de la actionBar
                    args.putInt(MyListsFragment.ARG_SECTION_NUMBER, position);
                    myListFragment.setArguments(args);
                }

                // Creamos la transaccion que sustituira el actual fragmento por el nueo y enviara el viejo a la BackStack
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.mainContainer, myListFragment, getString(R.string.tag_fragment_my_ists));
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;

            case ADMINISTRAR:
                //Administrar
                fragmentManager.beginTransaction().replace(R.id.mainContainer, PlaceholderFragment.newInstance(position)).commit();
                break;

            case BUSCAR:
                //Buscar
                fragmentManager.beginTransaction().replace(R.id.mainContainer, PlaceholderFragment.newInstance(position)).commit();
                break;
            default:
                fragmentManager.beginTransaction().replace(R.id.mainContainer, PlaceholderFragment.newInstance(position)).commit();
                break;
        }

    }



    //Este metodo se llama desde los framents para que cuando los fragments se "atach" ellos puedan cambiar el nombre de la action bar
    public void onSectionAttached(int number) {
        switch (number) {
            case STREAM:
                mTitle = getString(R.string.title_section1);
                break;
            case MIS_LISTAS:
                mTitle = getString(R.string.title_section2);
                break;
            case ADMINISTRAR:
                mTitle = getString(R.string.title_section3);
                break;
            case BUSCAR:
                mTitle = getString(R.string.title_section4);
                break;
        }
    }

    public void setUpCache()
    {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return (bitmap.getRowBytes() * bitmap.getHeight()) / 1024;
            }
        };
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId())
        {
            case R.id.action_settings:
                return true;
            case R.id.action_nuevo:
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.action_nuevo), Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_notificaciones:
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.action_notificaciones), Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void cargarPreferencias() {
        this.sharedPreferencesManager = new SharedPreferencesManager(getApplicationContext());

    }

    /**Se encarga de mirar las cuentas de tipo com.cloupix.eve existentes
     * 1)Si no hay ninguna lanza directamente la activity de login
     * 2)Si hay una solicita el token de authentication de ésta
     * * * Si no hay token lanza la activity de login
     * * * Si hay token lo guarda para utilizarlo en futuras consultas (de esta activity)
     * 3)Si hay varias las muestra en un dialog y una vez seeccionado pasa al paso (2)*/
    private void manageAccounts()
    {
        final AccountManager mAccountManager = AccountManager.get(this);

        final Account[] arrayCuentas = mAccountManager.getAccountsByType(Authenticator.ACCOUNT_TYPE);
        switch (arrayCuentas.length) {
            case 0:
                Intent intent = new Intent(getApplicationContext(), AuthenticatorActivity.class);
                intent.putExtra(AuthenticatorActivity.ARG_IS_ADDING_NEW_ACCOUNT, true);
                intent.putExtra(AuthenticatorActivity.COMES_FROM_MAIN_ACTIVITY, true);
                startActivity(intent);
                finish();
                break;
            case 1:
                authToken = mAccountManager.peekAuthToken(arrayCuentas[0], Authenticator.AUTH_TOKEN_TYPE);
                if(!TextUtils.isEmpty(authToken))
                    this.sharedPreferencesManager.setAccountUserName(arrayCuentas[0].name);
                break;

            default:
                String[] nombresCuentas = new String[arrayCuentas.length];
                for(int i = 0; i<arrayCuentas.length; i++){
                    nombresCuentas[i] = arrayCuentas[i].name;
                }
                new AlertDialog.Builder(this)
                        .setSingleChoiceItems(nombresCuentas, 0, null)
                        .setTitle(R.string.selecciona_cuenta)
                        .setCancelable(false)
                        .setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                                int selectedPosition = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
                                authToken = mAccountManager.peekAuthToken(arrayCuentas[selectedPosition], Authenticator.AUTH_TOKEN_TYPE);
                                if(!TextUtils.isEmpty(authToken))
                                    sharedPreferencesManager.setAccountUserName(arrayCuentas[selectedPosition].name);
                            }
                        })
                        .show();
                break;
        }

    }


}
