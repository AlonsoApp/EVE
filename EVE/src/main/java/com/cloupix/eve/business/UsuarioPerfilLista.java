package com.cloupix.eve.business;

import java.io.Serializable;

/**
 * Created by AlonsoApp on 20/01/14.
 */
public class UsuarioPerfilLista extends Usuario implements Serializable{

    private static final long serialVersionUID = 1L;

    private long idUsuarioPerfilLista;
    private boolean activo;
    private long fechaIncorporacion;
    private long idTipoPerfil;

    public UsuarioPerfilLista(long idUsuarioPerfilLista, boolean activo, long fechaIncorporacion, long idTipoPerfil) {
        this.idUsuarioPerfilLista = idUsuarioPerfilLista;
        this.activo = activo;
        this.fechaIncorporacion = fechaIncorporacion;
        this.idTipoPerfil = idTipoPerfil;
    }

    public UsuarioPerfilLista(long userId, String userFullName, String userEmail, long userProfileImageId, long idUsuarioPerfilLista, boolean activo, long fechaIncorporacion, long idTipoPerfil) {
        super(userId, userFullName, userEmail, userProfileImageId);
        this.idUsuarioPerfilLista = idUsuarioPerfilLista;
        this.activo = activo;
        this.fechaIncorporacion = fechaIncorporacion;
        this.idTipoPerfil = idTipoPerfil;
    }

    public long getIdUsuarioPerfilLista() {
        return idUsuarioPerfilLista;
    }

    public void setIdUsuarioPerfilLista(long idUsuarioPerfilLista) {
        this.idUsuarioPerfilLista = idUsuarioPerfilLista;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public long getFechaIncorporacion() {
        return fechaIncorporacion;
    }

    public void setFechaIncorporacion(long fechaIncorporacion) {
        this.fechaIncorporacion = fechaIncorporacion;
    }

    public long getIdTipoPerfil() {
        return idTipoPerfil;
    }

    public void setIdTipoPerfil(long idTipoPerfil) {
        this.idTipoPerfil = idTipoPerfil;
    }
}
