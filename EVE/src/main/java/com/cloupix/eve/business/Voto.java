package com.cloupix.eve.business;

import java.io.Serializable;

/**
 * Created by AlonsoApp on 20/01/14.
 */
public class Voto implements Serializable{

    private static final long serialVersionUID = 1L;

    private long idVoto;
    private long idTipoVoto;
    private long idPerfilVotante;

    public Voto() {
        idVoto=-1L;
        idTipoVoto = -1;
        idPerfilVotante = -1L;
    }

    public Voto(long idVoto, long idTipoVoto, long idPerfilVotante) {
        this.idVoto = idVoto;
        this.idTipoVoto = idTipoVoto;
        this.idPerfilVotante = idPerfilVotante;
    }

    public long getIdVoto() {
        return idVoto;
    }

    public void setIdVoto(long idVoto) {
        this.idVoto = idVoto;
    }

    public long getIdTipoVoto() {
        return idTipoVoto;
    }

    public void setIdTipoVoto(long idTipoVoto) {
        this.idTipoVoto = idTipoVoto;
    }

    public long getIdPerfilVotante() {
        return idPerfilVotante;
    }

    public void setIdPerfilVotante(long idPerfilVotante) {
        this.idPerfilVotante = idPerfilVotante;
    }
}
