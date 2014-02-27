package com.cloupix.eve.business;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by AlonsoApp on 20/01/14.
 */
public class ElementoLista implements Serializable{

    private static final long serialVersionUID = 1L;

    private long idElemento;
    private String contenido;
    private long fechaCreacion;
    private long idPerfilCreador;
    private ArrayList<Voto> listaVotos;

    public ElementoLista() {
    }

    public ElementoLista(long idElemento, String contenido, long fechaCreacion, long idPerfilCreador, ArrayList<Voto> listaVotos) {
        this.idElemento = idElemento;
        this.contenido = contenido;
        this.fechaCreacion = fechaCreacion;
        this.listaVotos = listaVotos;
    }

    public long getIdElemento() {
        return idElemento;
    }

    public void setIdElemento(long idElemento) {
        this.idElemento = idElemento;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public long getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(long fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public ArrayList<Voto> getListaVotos() {
        return listaVotos;
    }

    public void setListaVotos(ArrayList<Voto> listaVotos) {
        this.listaVotos = listaVotos;
    }

    public void addVoto(Voto voto){
        listaVotos.add(voto);
    }

    public int getNumVotos(){
        return listaVotos.size();
    }

    public long getIdPerfilCreador() {
        return idPerfilCreador;
    }

    public void setIdPerfilCreador(long idPerfilCreador) {
        this.idPerfilCreador = idPerfilCreador;
    }
}
