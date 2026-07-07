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

/**
 *
 * @author viniciussdsilva
 */
@SuppressWarnings("serial")
@Entity
// K.8 #2 (2026-07-06): unique(usuario_codigo, ideia_codigo) trava no BANCO que o mesmo
// usuario seja inserido 2x como participante da mesma ideia. Antes, EntrarIdeiaServlet
// fazia "verifica se ja existe -> insere" em 2 passos sem trava real: clique duplo em
// "Entrar" (ou um retry de rede) podia passar os 2 pela checagem e inserir 2 vinculos.
@Table(uniqueConstraints = @UniqueConstraint(name = "uk_ideiausuario_par", columnNames = {"usuario_codigo", "ideia_codigo"}))
public class IdeiaUsuario extends GenericDomain {

    // Chaves estrangeiras
    @ManyToOne
    @JoinColumn(name = "usuario_codigo", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "ideia_codigo", nullable = false)
    private Ideia ideia;

    @Column(length = 1)
    private String flLider;

    // M.2 (2026-07-06): status do vinculo na "lista de espera" do grupo -- P(endente)/
    // A(provado)/R(ejeitado). Ver StatusIdeia.VINCULO_*. NULL = vinculo legado (criado
    // antes desta feature) -> tratado como aprovado nas telas. O lider aprova/rejeita
    // (AprovarMembroServlet/RejeitarMembroServlet) enquanto o grupo esta aberto.
    @Column(length = 1)
    private String flStatusVinculo;

    // M.3 (2026-07-06): motivo informado pelo lider ao REJEITAR a entrada de alguem.
    @Column(length = 200)
    private String motivoRejeicaoMembro;

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
