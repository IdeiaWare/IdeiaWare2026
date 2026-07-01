package edu.unisc.lic.domain;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author viniciussdsilva
 */

@SuppressWarnings("serial")
@Entity
public class LogColaboracao extends GenericDomain {
    // Chaves estrangeiras
    @ManyToOne
    @JoinColumn(nullable = false)
    private Ideia ideia;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Usuario usuario;
    
    // Atributos
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date dtModificacao;
    
    @Column(nullable = false, length = 1500)
    private String descricao;

    // Métodos construtores
    public LogColaboracao() {
        ideia = new Ideia();
        usuario = new Usuario();
    }

    public LogColaboracao(Ideia ideia, Usuario usuario, Date dtModificacao, String descricao) {
        this.ideia = ideia;
        this.usuario = usuario;
        this.dtModificacao = dtModificacao;
        this.descricao = descricao;
    }

    // Getters and Setters
    public Ideia getIdeia() {
        return ideia;
    }

    public void setIdeia(Ideia ideia) {
        this.ideia = ideia;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Date getDtModificacao() {
        return dtModificacao;
    }

    public void setDtModificacao(Date dtModificacao) {
        this.dtModificacao = dtModificacao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return "LogColaboracao{" + "ideia=" + ideia + ", usuario=" + usuario + ", dtModificacao=" + dtModificacao + ", descricao=" + descricao + '}';
    }
}
