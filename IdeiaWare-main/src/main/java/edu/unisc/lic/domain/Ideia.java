package edu.unisc.lic.domain;

import edu.unisc.lic.classes.Data;
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
public class Ideia extends GenericDomain {

    //Chave estrangeira
    @ManyToOne
    @JoinColumn(nullable = false) // chave estrangeira é obrigatória
    private Usuario usuario;

    //Atributos
    @Column(length = 50, nullable = false)
    private String titulo;

    @Column(length = 1500, nullable = false)
    private String descricao;

    @Column(length = 200)
    private String motivoRejeicao;

    @Column(length = 2, nullable = false)
    private String status;

    @Column(length = 2, nullable = false)
    private String statusGrupo;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dtCriacao;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date dtInicioDesenv;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date dtFimDesenv;
    
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date dtValidacao;
    
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date dtRejeicao;
    
    @ManyToOne
    @JoinColumn(nullable = true) // chave estrangeira é obrigatória
    private Usuario gestor;

    //Métodos Construtores
    public Ideia() {
        usuario = new Usuario();
    }

    public Ideia(Usuario usuario, String titulo, String descricao, String status, String statusGrupo) {
        this.usuario = usuario;
        this.titulo = titulo;
        this.descricao = descricao;
        this.status = status;
        this.statusGrupo = statusGrupo;
    }

    //Métodos
    @Override
    public String toString() {
        return "Ideia{" + "usuario=" + usuario + ", titulo=" + titulo + ", descricao=" + descricao + ", motivoRejeicao=" + motivoRejeicao + ", status=" + status + ", statusGrupo=" + statusGrupo + ", dtCriacao=" + dtCriacao + ", dtInicioDesenv=" + dtInicioDesenv + ", dtFimDesenv=" + dtFimDesenv + '}';
    }

    //Getters and Setters
    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public Date getDtInicioDesenv() {
        return dtInicioDesenv;
    }

    public void setDtInicioDesenv(Date dtInicioDesenv) {
        this.dtInicioDesenv = dtInicioDesenv;
    }

    public void setDtInicioDesenv() {
        this.dtInicioDesenv = Data.horaAtual();
    }

    public Date getDtFimDesenv() {
        return dtFimDesenv;
    }

    public void setDtFimDesenv(Date dtFimDesenv) {
        this.dtFimDesenv = dtFimDesenv;
    }

    public void setDtFimDesenv() {
        this.dtFimDesenv = Data.horaAtual();
    }

    public String getStatusGrupo() {
        return statusGrupo;
    }

    public void setStatusGrupo(String statusGrupo) {
        this.statusGrupo = statusGrupo;
    }

    public String getMotivoRejeicao() {
        return motivoRejeicao;
    }

    public void setMotivoRejeicao(String motivoRejeicao) {
        this.motivoRejeicao = motivoRejeicao;
    }

	public Date getDtValidacao() {
		return dtValidacao;
	}

	public void setDtValidacao() {
		this.dtValidacao = Data.horaAtual();
	}

	public Date getDtRejeicao() {
		return dtRejeicao;
	}

	public void setDtRejeicao() {
		this.dtRejeicao = Data.horaAtual();
	}

	public Usuario getGestor() {
		return gestor;
	}

	public void setGestor(Usuario gestor) {
		this.gestor = gestor;
	}

	
}
