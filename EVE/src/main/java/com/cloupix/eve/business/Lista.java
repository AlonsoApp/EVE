package com.cloupix.eve.business;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by AlonsoApp on 20/01/14.
 */
public class Lista implements Serializable {

    private static final long serialVersionUID = 1L;

    private long idLista;
    private String titulo;
    private int colaboracionesUsuario;
    private int maxVotos;
    private long fechaCreacion;
    private long fechaFinalizacion;
    private long idCategoria;
    private long idVisibilidad;
    private long idModoVoto;
    private long idModoColaboracion;
    private ArrayList<ElementoLista> listaElementos;
    private ArrayList<UsuarioPerfilLista> listaPerfiles;

    public Lista() {
        this.idLista = -1L;
        this.titulo = "";
        this.colaboracionesUsuario = -1;
        this.maxVotos = -1;
        this.fechaCreacion = -1L;
        this.fechaFinalizacion = -1L;
        this.idCategoria = -1;
        this.idVisibilidad = -1;
        this.idModoVoto = -1;
        this.idModoColaboracion = -1;
        this.listaElementos = new ArrayList<ElementoLista>();
        this.listaPerfiles = new ArrayList<UsuarioPerfilLista>();
    }

    public Lista(long idLista, String titulo, int colaboracionesUsuario, int maxVotos, long fechaCreacion, long fechaFinalizacion, long idCategoria, long idVisibilidad, long idModoVoto, long idModoColaboracion, ArrayList<ElementoLista> listaElementos, ArrayList<UsuarioPerfilLista> listaPerfiles) {
        this.idLista = idLista;
        this.titulo = titulo;
        this.colaboracionesUsuario = colaboracionesUsuario;
        this.maxVotos = maxVotos;
        this.fechaCreacion = fechaCreacion;
        this.fechaFinalizacion = fechaFinalizacion;
        this.idCategoria = idCategoria;
        this.idVisibilidad = idVisibilidad;
        this.idModoVoto = idModoVoto;
        this.idModoColaboracion = idModoColaboracion;
        this.listaElementos = listaElementos;
        this.listaPerfiles = listaPerfiles;
    }

    public long getIdLista() {
        return idLista;
    }

    public void setIdLista(long idLista) {
        this.idLista = idLista;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getColaboracionesUsuario() {
        return colaboracionesUsuario;
    }

    public void setColaboracionesUsuario(int colaboracionesUsuario) {
        this.colaboracionesUsuario = colaboracionesUsuario;
    }

    public int getMaxVotos() {
        return maxVotos;
    }

    public void setMaxVotos(int maxVotos) {
        this.maxVotos = maxVotos;
    }

    public long getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(long fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public long getFechaFinalizacion() {
        return fechaFinalizacion;
    }

    public void setFechaFinalizacion(long fechaFinalizacion) {
        this.fechaFinalizacion = fechaFinalizacion;
    }

    public long getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(long idCategoria) {
        this.idCategoria = idCategoria;
    }

    public long getIdVisibilidad() {
        return idVisibilidad;
    }

    public void setIdVisibilidad(long idVisibilidad) {
        this.idVisibilidad = idVisibilidad;
    }

    public long getIdModoVoto() {
        return idModoVoto;
    }

    public void setIdModoVoto(long idModoVoto) {
        this.idModoVoto = idModoVoto;
    }

    public long getIdModoColaboracion() {
        return idModoColaboracion;
    }

    public void setIdModoColaboracion(long idModoColaboracion) {
        this.idModoColaboracion = idModoColaboracion;
    }

    public ArrayList<ElementoLista> getListaElementos() {
        return listaElementos;
    }

    public void setListaElementos(ArrayList<ElementoLista> listaElementos) {
        this.listaElementos = listaElementos;
    }

    public void addElemento(ElementoLista elemento){
        this.listaElementos.add(elemento);
    }

    public ArrayList<UsuarioPerfilLista> getListaPerfiles() {
        return listaPerfiles;
    }

    public void setListaPerfiles(ArrayList<UsuarioPerfilLista> listaPerfiles) {
        this.listaPerfiles = listaPerfiles;
    }

    public void addPerfil(UsuarioPerfilLista usuarioPerfilLista){
        this.listaPerfiles.add(usuarioPerfilLista);
    }
}
