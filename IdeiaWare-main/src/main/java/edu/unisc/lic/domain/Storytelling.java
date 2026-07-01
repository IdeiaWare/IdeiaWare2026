package edu.unisc.lic.domain;

import edu.unisc.lic.classes.Data;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author viniciussdsilva
 */
@SuppressWarnings("serial")
@Entity
public class Storytelling extends GenericDomain {

    // Chaves estrangeiras
    @OneToOne
    @JoinColumn(nullable = false)
    private Usuario usuario;

    @OneToOne
    @JoinColumn(nullable = false)
    private Ideia ideia;

    // Atributos
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date dtCriacao;
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date dtFinalizacao;

    @Column(length = 2, nullable = false)
    private String status;

    @Column(columnDefinition = "longtext")
    private String caminhoFinalizado;

    // Construtores
    public Storytelling() {
        usuario = new Usuario();
        ideia = new Ideia();
    }

    public Storytelling(Usuario usuario, Ideia ideia, Date dtCriacao, String status) {
        this.usuario = usuario;
        this.ideia = ideia;
        this.dtCriacao = dtCriacao;
        this.status = status;
    }
    
    // Métodos

    @Override
    public String toString() {
        return "Storytelling{" + "usuario=" + usuario + ", ideia=" + ideia + ", dtCriacao=" + dtCriacao + ", dtFinalizacao=" + dtFinalizacao + ", status=" + status + ", caminhoFinalizado=" + caminhoFinalizado + '}';
    }

    // Getters e Setters
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

    public Date getDtCriacao() {
        return dtCriacao;
    }

    public void setDtCriacao(Date dtCriacao) {
        this.dtCriacao = dtCriacao;
    }

    public void setDtCriacao() {
        this.dtCriacao = Data.horaAtual();
    }

    public Date getDtFinalizacao() {
        return dtFinalizacao;
    }

    public void setDtFinalizacao(Date dtFinalizacao) {
        this.dtFinalizacao = dtFinalizacao;
    }

    public void setDtFinalizacao() {
        this.dtFinalizacao = Data.horaAtual();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCaminhoFinalizado() {
        return caminhoFinalizado;
    }

    public void setCaminhoFinalizado(String caminhoFinalizado) {
        this.caminhoFinalizado = caminhoFinalizado;
    }

}
