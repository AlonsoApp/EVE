package com.cloupix.eve;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cloupix.eve.business.ElementoLista;
import com.cloupix.eve.business.Lista;
import com.cloupix.eve.business.adapters.NuevaListaAdapter;
import com.cloupix.eve.logic.ListaLogic;

import java.util.ArrayList;

/**
 * Created by AlonsoApp on 20/01/14.
 */
public class NuevaListaActivity extends Activity implements View.OnClickListener{

    private Spinner spinnerColaboracion, spinnerPrivacidad;
    private EditText editTextTitulo, editTextMaxVotos, editTextNumColavoraciones;
    private CheckBox checkBoxVotoPrivado;
    private ListView listElementos;
    private NuevaListaAdapter nuevaListaAdapter;

    private int idPrivacidad = 0;
    private int idColaboracion = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nueva_lista);

        cargarActionBar();
        cargarElementos();
    }

    private void cargarActionBar(){
        final LayoutInflater inflater = (LayoutInflater) getActionBar().getThemedContext().getSystemService(LAYOUT_INFLATER_SERVICE);

        final View customActionBarView = inflater.inflate(R.layout.actionbar_custom_view_done, null);


        View textViewDone = customActionBarView.findViewById(R.id.textViewDone);
        if(textViewDone instanceof TextView)
            ((TextView) textViewDone).setText(R.string.crear);

        // Show the custom action bar view and hide the normal Home icon and title.
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM, ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setCustomView(customActionBarView);
    }

    private void cargarElementos(){
        editTextTitulo = (EditText) findViewById(R.id.editTextTitulo);
        editTextMaxVotos = (EditText) findViewById(R.id.editTextMaxVotos);
        editTextNumColavoraciones = (EditText) findViewById(R.id.editTextNumColavoraciones);
        checkBoxVotoPrivado = (CheckBox) findViewById(R.id.checkBoxVotoPrivado);
        spinnerPrivacidad = (Spinner) findViewById(R.id.spinnerPrivacidad);
        spinnerColaboracion = (Spinner) findViewById(R.id.spinnerColaboracion);
        listElementos = (ListView) findViewById(R.id.listElementos);


        //SpinnerPrivacidad
        // TODO: Ahora mismo la lista está harcodeada, en el futuro tendrá que conseguirse una lista de todos los
        // TODO: tipos de privacidad y colaboraciones
        ArrayAdapter<CharSequence> adapterPrivacidad = ArrayAdapter.createFromResource(this, R.array.array_privacidad, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapterPrivacidad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerPrivacidad.setAdapter(adapterPrivacidad);
        spinnerPrivacidad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //nuevaOfertaAdmin.setTipo(position+1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //SpinnerColaboracion
        ArrayAdapter<CharSequence> adapterColaboracion = ArrayAdapter.createFromResource(this, R.array.array_colaboracion, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapterColaboracion.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerColaboracion.setAdapter(adapterColaboracion);
        spinnerColaboracion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //nuevaOfertaAdmin.setTipo(position+1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        editTextMaxVotos.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus && TextUtils.isEmpty(editTextMaxVotos.getText().toString()))
                    editTextMaxVotos.setText(R.string.default_num_colaboraciones);
            }
        });

        editTextNumColavoraciones.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus && TextUtils.isEmpty(editTextNumColavoraciones.getText().toString()))
                    editTextNumColavoraciones.setText(R.string.default_max_votos);
            }
        });



        //Lista
        LayoutInflater inflater = LayoutInflater.from(this);
        listElementos.addFooterView(inflater.inflate(R.layout.footer_add_element, null));
        nuevaListaAdapter = new NuevaListaAdapter(getApplicationContext(), new ArrayList<ElementoLista>());
        listElementos.setAdapter(nuevaListaAdapter);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.actionbar_done:
                crearLista();
                break;
            case R.id.layoutNewElement:
                // Set up the input
                final EditText input = new EditText(this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

                new AlertDialog.Builder(this)
                        .setTitle(R.string.nuevo_elemento)
                        .setCancelable(false)
                        .setView(input)
                        .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                crearElemento(input.getText().toString());
                                dialog.dismiss();
                            }
                        })
                        .show();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nueva_lista, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_invitados:
                //crearPreview();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void crearLista(){
        String titulo = editTextTitulo.getText().toString();
        if(TextUtils.isEmpty(titulo)){
            Toast.makeText(getApplicationContext(), R.string.no_titulo, Toast.LENGTH_SHORT).show();
            return;
        }
        Lista nuevaLista = new Lista();
        nuevaLista.setTitulo(titulo);
        nuevaLista.setIdVisibilidad(idPrivacidad + 1);
        nuevaLista.setIdModoColaboracion(idColaboracion + 1);
        nuevaLista.setColaboracionesUsuario(Integer.parseInt(editTextNumColavoraciones.getText().toString()));
        nuevaLista.setMaxVotos(Integer.parseInt(editTextMaxVotos.getText().toString()));
        //Ahora mismo el ususario no puede especificar categoría. Se pone a desconocida
        nuevaLista.setIdCategoria(1);
        // Privado = 2 Público = 1
        if(checkBoxVotoPrivado.isChecked()){
            nuevaLista.setIdModoVoto(2);
        }else{
            nuevaLista.setIdModoVoto(1);
        }

        nuevaLista.setListaElementos(nuevaListaAdapter.getArrayList());

        ListaLogic listaLogic = new ListaLogic();
        listaLogic.crearLista(nuevaLista, NuevaListaActivity.this);
        finish();
    }

    private void crearElemento(String strElemento){
        ElementoLista elementoLista = new ElementoLista();
        elementoLista.setContenido(strElemento);
        // No le asignamos el id del perfil del creador porque todavía no existe tal relación. Cuando el servidor cree el perfil en la BD
        // asignará a cada uno d elos elementos el id del perfil de su creador
        nuevaListaAdapter.addItem(elementoLista);
        nuevaListaAdapter.notifyDataSetChanged();
    }
}
