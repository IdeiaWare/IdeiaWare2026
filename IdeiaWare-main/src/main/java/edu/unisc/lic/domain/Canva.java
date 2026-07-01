package edu.unisc.lic.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 *
 * @author yanrodrigues
 */
@SuppressWarnings("serial")
@Entity
public class Canva extends GenericDomain {

    //Chave estrangeira
    @ManyToOne
    @JoinColumn(nullable = false) // chave estrangeira é obrigatória
    private Ideia ideia;

    //Atributos
    @Column(nullable = false)
    private String text;

    @Column(length = 6, nullable = false)
    private String color;

    @Column(length = 14, nullable = false)
    private String attribute;

    //Métodos Construtores
    public Canva() {
        ideia = new Ideia();
    }

    public Canva(Ideia ideia, String text, String color, String attribute) {
        this.ideia = ideia;
        this.text = text;
        this.color = color;
        this.attribute = attribute;
    }

    //Métodos
    @Override
	public String toString() {
		return "Canva [ideia=" + ideia + ", text=" + text + ", color=" + color + ", attribute=" + attribute + "]";
	}

    //Getters and Setters
	public Ideia getIdeia() {
		return ideia;
	}
	
	public void setIdeia(Ideia ideia) {
		this.ideia = ideia;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
	
}
