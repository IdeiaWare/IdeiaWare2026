package br.unisc.toolkit.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class Usuario  extends GenericDomain implements Serializable{
	//Atributos
    @Column(length = 64, nullable = false)
    private String nome;

    @Column(length = 32, nullable = false)
    private String usuario;

    @Column(length = 32, nullable = false)
    private String senha;

    @Column(length = 3, nullable = false)
    private String permissao;

    //Métodos Construtores
    public Usuario() {
    }

    public Usuario(String nome, String usuario, String senha, String permissao) {
        this.nome = nome;
        this.usuario = usuario;
        this.senha = senha;
        this.permissao = permissao;
    }

//	@Column(precision = 7, scale = 2, nullable = false) // precision são quantos números ao total, scale é quantos números após a vírgula
//	private BigDecimal salario;                         // xxxxx,xx
    
    //Métodos
    @Override
    public String toString() {
        return "Usuario{" + "nome(" + nome + "), usuario(" + usuario + "), senha(" + senha + "), permissao(" + permissao + ")}";
    }
    
    
    //Getters and Setters
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getPermissao() {
        return permissao;
    }

    public void setPermissao(String permissao) {
        this.permissao = permissao;
    }

}
