package br.unisc.toolkit.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "persona_pov")
public class PersonaPointOfView {

	// @Id no surrogate 'id' (auto_increment), NAO persona_id (que se repete -- 1 persona, N POVs).
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	@Column(name = "persona_id")
	private int personaID;

	@Column(name = "pov_id")
	private int pointOfViewID;

	@Column(name = "ideia_codigo")
	private Long ideiaCodigo;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPersonaID() {
		return personaID;
	}

	public void setPersonaID(int personaID) {
		this.personaID = personaID;
	}

	public int getPointOfViewID() {
		return pointOfViewID;
	}

	public void setPointOfViewID(int pointOfViewID) {
		this.pointOfViewID = pointOfViewID;
	}

	public Long getIdeiaCodigo() {
		return ideiaCodigo;
	}

	public void setIdeiaCodigo(Long ideiaCodigo) {
		this.ideiaCodigo = ideiaCodigo;
	}
}
