package edu.unisc.lic.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


import edu.unisc.lic.classes.Data;

/**
 *
 * @author viniciussdsilva
 */
@SuppressWarnings("serial")
@Entity
public class IdeiaUsuario extends GenericDomain {

    // Chaves estrangeiras
    @ManyToOne
    @JoinColumn(nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Ideia ideia;

    @Column(length = 1)
    private String flLider;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date dtInscricao;

    // Métodos construtores
    public IdeiaUsuario() {
        this.usuario = new Usuario();
        this.ideia = new Ideia();
    }

    public IdeiaUsuario(Usuario usuario, Ideia ideia, String flLider) {
        if (usuario == null) {
            this.usuario = new Usuario();
        } else {
            this.usuario = usuario;
        }

        if (ideia == null) {
            this.ideia = new Ideia();
        } else {
            this.ideia = ideia;
        }

        this.flLider = flLider;
    }

    // Métodos
    @Override
    public String toString() {
        return "IdeiaUsuario{" + "usuario=" + usuario + ", ideia=" + ideia + ", flLider=" + flLider + '}';
    }

    // Getters and Setters
    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Ideia getIdeia() {
        return ideia;
    }

    public void setIdeia(Ideia ideia) {
        this.ideia = ideia;
    }

    public String getFlLider() {
        return flLider;
    }

    public void setFlLider(String flLider) {
        this.flLider = flLider;
    }

    public Date getDtInscricao() {
        return dtInscricao;
    }

    public void setDtInscricao() {
        this.dtInscricao = Data.horaAtual();
    }

}
