package com.cloupix.eve.business;

/**
 * Created by AlonsoApp on 18/11/13.
 */
public class NavigationDrawerSection {

    private String nombre;
    private int resIcon;

    public NavigationDrawerSection(String nombre, int resIcon){
        this.nombre = nombre;
        this.resIcon = resIcon;
    }


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public int getResIcon() {
        return resIcon;
    }

    public void setResIcon(int resIcon) {
        this.resIcon = resIcon;
    }
}
