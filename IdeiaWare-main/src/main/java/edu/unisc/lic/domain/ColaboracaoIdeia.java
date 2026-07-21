package edu.unisc.lic.domain;

import edu.unisc.lic.classes.Data;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@SuppressWarnings("serial")
@Entity
public class ColaboracaoIdeia extends GenericDomain {

    @ManyToOne
    @JoinColumn(nullable = false)
    private Ideia ideia;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Usuario usuario;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date dtModificacao;

    @Column(length = 1500)
    private String descricaoIdeiaAnterior;

    @Column(length = 1500, nullable = false)
    private String descricaoIdeiaAtual;

    @Column(length = 1500)
    private String descricaoIdeiaLider;

    @Column(length = 2)
    private String flEditadoLider;
    
    @Column(length = 2)
    private String flSalvado;

    public ColaboracaoIdeia() {
        this.ideia = new Ideia();
        this.usuario = new Usuario();
    }

    public ColaboracaoIdeia(Ideia ideia, Usuario usuario, Date dtModificacao, String descricaoIdeiaAtual) {
        this.ideia = ideia;
        this.usuario = usuario;
        this.dtModificacao = dtModificacao;
        this.descricaoIdeiaAtual = descricaoIdeiaAtual;
    }

    @Override
    public String toString() {
        return "ColaboracaoIdeia{" + "ideia=" + ideia + ", usuario=" + usuario + ", dtModificacao=" + dtModificacao + ", descricaoIdeiaAnterior=" + descricaoIdeiaAnterior + ", descricaoIdeiaAtual=" + descricaoIdeiaAtual + ", descricaoIdeiaLider=" + descricaoIdeiaLider + ", flEditadoLider=" + flEditadoLider + ", flSalvado=" + flSalvado + '}';
    }

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

    public void setDtModificacao() {
        this.dtModificacao = Data.horaAtual();
    }

    public String getDescricaoIdeiaAnterior() {
        return descricaoIdeiaAnterior;
    }

    public void setDescricaoIdeiaAnterior(String descricaoIdeiaAnterior) {
        this.descricaoIdeiaAnterior = descricaoIdeiaAnterior;
    }

    public String getDescricaoIdeiaAtual() {
        return descricaoIdeiaAtual;
    }

    public void setDescricaoIdeiaAtual(String descricaoIdeiaAtual) {
        this.descricaoIdeiaAtual = descricaoIdeiaAtual;
    }

    public String getDescricaoIdeiaLider() {
        return descricaoIdeiaLider;
    }

    public void setDescricaoIdeiaLider(String descricaoIdeiaLider) {
        this.descricaoIdeiaLider = descricaoIdeiaLider;
    }

    public String getFlEditadoLider() {
        return flEditadoLider;
    }

    public void setFlEditadoLider(String flEditadoLider) {
        this.flEditadoLider = flEditadoLider;
    }

    public String getFlSalvado() {
        return flSalvado;
    }

    public void setFlSalvado(String flSalvado) {
        this.flSalvado = flSalvado;
    }
    
    
}
