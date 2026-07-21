package edu.unisc.lic.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@SuppressWarnings("serial")
@Entity
public class Canva extends GenericDomain {

    @ManyToOne
    @JoinColumn(nullable = false)
    private Ideia ideia;

    @Column(nullable = false)
    private String text;

    @Column(length = 6, nullable = false)
    private String color;

    @Column(length = 14, nullable = false)
    private String attribute;

    public Canva() {
        ideia = new Ideia();
    }

    public Canva(Ideia ideia, String text, String color, String attribute) {
        this.ideia = ideia;
        this.text = text;
        this.color = color;
        this.attribute = attribute;
    }

    @Override
	public String toString() {
		return "Canva [ideia=" + ideia + ", text=" + text + ", color=" + color + ", attribute=" + attribute + "]";
	}

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
