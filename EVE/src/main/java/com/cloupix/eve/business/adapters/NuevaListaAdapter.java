package com.cloupix.eve.business.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cloupix.eve.R;
import com.cloupix.eve.business.ElementoLista;

import java.util.ArrayList;

/**
 * Created by AlonsoUSA on 21/01/14.
 */
public class NuevaListaAdapter extends BaseAdapter
{
    private static ArrayList<ElementoLista> elementosArrayList;
    private LayoutInflater mInflater;

    public NuevaListaAdapter(Context context, ArrayList<ElementoLista> results)
    {
        elementosArrayList = results;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return elementosArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return elementosArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public ArrayList<ElementoLista> getArrayList() {
        return elementosArrayList;
    }

    public void addItem(ElementoLista elemento){
        this.elementosArrayList.add(elemento);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null)
        {
            convertView = mInflater.inflate(R.layout.row_elemento_nueva_lista, null);
            holder = new ViewHolder();
            holder.textViewElemento = (TextView) convertView.findViewById(R.id.textViewElemento);
            holder.imgBtnDeleteElemento = (ImageButton) convertView.findViewById(R.id.imgBtnDeleteElemento);
            convertView.setTag(holder);
        }else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        ElementoLista elementoLista = elementosArrayList.get(position);


        holder.textViewElemento.setText(elementoLista.getContenido());
        holder.imgBtnDeleteElemento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                elementosArrayList.remove(position);
                notifyDataSetChanged();
            }
        });
        return convertView;
    }


    static class ViewHolder {
        TextView textViewElemento;
        ImageButton imgBtnDeleteElemento;
    }
}
