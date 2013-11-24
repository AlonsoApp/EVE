package com.cloupix.eve.business.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cloupix.eve.R;
import com.cloupix.eve.business.NavigationDrawerSection;

import java.util.ArrayList;

/**
 * Created by AlonsoApp on 18/11/13.
 */
public class NavigationDrawerAdapter extends BaseAdapter
{
    private static ArrayList<NavigationDrawerSection> sectionsArrayList;
    private LayoutInflater mInflater;

    public NavigationDrawerAdapter(Context context, ArrayList<NavigationDrawerSection> results)
    {
        sectionsArrayList = results;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return sectionsArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return sectionsArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null)
        {
            convertView = mInflater.inflate(R.layout.row_navigation_drawer_section, null);
            holder = new ViewHolder();
            holder.icono = (ImageView) convertView.findViewById(R.id.navigationDrawerSectionIcon);
            holder.txtNombre = (TextView) convertView.findViewById(R.id.navigationDrawerSectionTitle);
            convertView.setTag(holder);
        }else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        NavigationDrawerSection navigationDrawerSection = sectionsArrayList.get(position);
        holder.txtNombre.setText(navigationDrawerSection.getNombre());
        holder.icono.setImageResource(navigationDrawerSection.getResIcon());
        return convertView;
    }

    static class ViewHolder {
        TextView txtNombre;
        ImageView icono;
    }

}
