package edu.unisc.lic.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;


import edu.unisc.lic.classes.Data;

@SuppressWarnings("serial")
@Entity
// K.8 #2: unique(usuario_codigo, ideia_codigo) trava no BANCO que o mesmo usuario entre 2x na ideia.
@Table(uniqueConstraints = @UniqueConstraint(name = "uk_ideiausuario_par", columnNames = {"usuario_codigo", "ideia_codigo"}))
public class IdeiaUsuario extends GenericDomain {

    @ManyToOne
    @JoinColumn(name = "usuario_codigo", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "ideia_codigo", nullable = false)
    private Ideia ideia;

    @Column(length = 1)
    private String flLider;

    // M.2: status na "lista de espera" -- P(endente)/A(provado)/R(ejeitado); NULL = vinculo legado (aprovado).
    @Column(length = 1)
    private String flStatusVinculo;

    // M.3: motivo do lider ao REJEITAR a entrada de alguem.
    @Column(length = 200)
    private String motivoRejeicaoMembro;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date dtInscricao;

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

    @Override
    public String toString() {
        return "IdeiaUsuario{" + "usuario=" + usuario + ", ideia=" + ideia + ", flLider=" + flLider + '}';
    }

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

    public String getFlStatusVinculo() {
        return flStatusVinculo;
    }

    public void setFlStatusVinculo(String flStatusVinculo) {
        this.flStatusVinculo = flStatusVinculo;
    }

    public String getMotivoRejeicaoMembro() {
        return motivoRejeicaoMembro;
    }

    public void setMotivoRejeicaoMembro(String motivoRejeicaoMembro) {
        this.motivoRejeicaoMembro = motivoRejeicaoMembro;
    }

    public Date getDtInscricao() {
        return dtInscricao;
    }

    public void setDtInscricao() {
        this.dtInscricao = Data.horaAtual();
    }

}
