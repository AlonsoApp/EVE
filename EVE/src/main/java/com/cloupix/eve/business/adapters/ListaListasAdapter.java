package com.cloupix.eve.business.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cloupix.eve.R;
import com.cloupix.eve.business.Lista;

import java.util.ArrayList;

/**
 * Created by AlonsoApp on 23/01/14.
 */
public class ListaListasAdapter extends BaseAdapter
{
    private static ArrayList<Lista> listasArrayList;
    private LayoutInflater mInflater;

    public ListaListasAdapter(Context context, ArrayList<Lista> results)
    {
        listasArrayList = results;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return listasArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return listasArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public ArrayList<Lista> getArrayList() {
        return listasArrayList;
    }

    public void addItem(Lista lista){
        this.listasArrayList.add(lista);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null)
        {
            convertView = mInflater.inflate(R.layout.row_lista, null);
            holder = new ViewHolder();
            holder.textViewTitulo = (TextView) convertView.findViewById(R.id.textViewTitulo);
            holder.textViewSubTitulo = (TextView) convertView.findViewById(R.id.textViewSubtitulo);
            holder.textViewDescripcion = (TextView) convertView.findViewById(R.id.textViewDescripcion);
            convertView.setTag(holder);
        }else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        Lista lista = listasArrayList.get(position);

        holder.textViewTitulo.setText(lista.getTitulo());
        // Estos e suna guarrada como una catedral, no hay tiempo
        String strVisibilidad = "Pública";
        switch((int)lista.getIdVisibilidad()){
            case 1:strVisibilidad = "Pública";break;
            case 2:strVisibilidad = "Semi Pública";break;
            case 3:strVisibilidad = "Semi Privada";break;
            case 4:strVisibilidad = "Privada";break;
        }
        holder.textViewSubTitulo.setText(strVisibilidad);
        String strElementos = "";

        if(lista.getListaElementos().size()>0)
            strElementos = lista.getListaElementos().get(0).getContenido();

        for(int i =1; i<lista.getListaElementos().size(); i++)
            strElementos = strElementos + ", "+ lista.getListaElementos().get(i).getContenido();
        holder.textViewDescripcion.setText(strElementos);

        return convertView;
    }


    static class ViewHolder {
        TextView textViewTitulo, textViewSubTitulo, textViewDescripcion;
    }
}