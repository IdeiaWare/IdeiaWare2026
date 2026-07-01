package br.unisc.toolkit.entity;

import java.util.Arrays;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="pov")
public class PointOfView {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="pov_id")
	private int id;
	
	@Transient
	private Integer[] personasId;
	
	@Column(name="ideia_codigo")
	private Long ideiaCodigo;
	
	@Column(name="user")
	private String userText;
	
	@Column(name="need")
	private String needText;
	
	@Column(name="insight")
	private String insightText;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public Integer[] getPersonasId() {
		return personasId;
	}

	public void setPersonasId(Integer[] personaId) {
		this.personasId = personaId;
	}

	public String getUserText() {
		return userText;
	}

	public void setUserText(String userText) {
		this.userText = userText;
	}

	public String getNeedText() {
		return needText;
	}

	public void setNeedText(String needText) {
		this.needText = needText;
	}

	public String getInsightText() {
		return insightText;
	}

	public void setInsightText(String insightText) {
		this.insightText = insightText;
	}

	public Long getIdeiaCodigo() {
		return ideiaCodigo;
	}

	public void setIdeiaCodigo(Long ideiaCodigo) {
		this.ideiaCodigo = ideiaCodigo;
	}

	@Override
	public String toString() {
		return "PointOfView [id=" + id + ", personasId=" + Arrays.toString(personasId) + ", needText="
				+ needText + ", insightText=" + insightText + "]";
	}

}
