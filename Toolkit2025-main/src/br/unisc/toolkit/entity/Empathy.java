package br.unisc.toolkit.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="empathy")
public class Empathy {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private int id;
	
	@Column(name="ideia_codigo")
	private Long ideiaCodigo;
	
	@Column(name="fk_persona_id")
	private int personaId;
	
	@Column(name="attribute_text")
	private String attributeText;
	
	@Column(name="attribute")
	private String attribute;
	
	@Column(name="card_color")
	private String cardColor;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPersonaId() {
		return personaId;
	}

	public void setPersonaId(int personaId) {
		this.personaId = personaId;
	}

	public String getAttributeText() {
		return attributeText;
	}

	public void setAttributeText(String attributeText) {
		this.attributeText = attributeText;
	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	public String getCardColor() {
		return cardColor;
	}

	public void setCardColor(String cardColor) {
		this.cardColor = cardColor;
	}

	public Long getIdeiaCodigo() {
		return ideiaCodigo;
	}

	public void setIdeiaCodigo(Long ideiaCodigo) {
		this.ideiaCodigo = ideiaCodigo;
	}

	@Override
	public String toString() {
		return "Empathy [id=" + id + ", personaId=" + personaId + ", attributeText=" + attributeText + ", attribute="
				+ attribute + ", cardColor=" + cardColor + "]";
	}

}
