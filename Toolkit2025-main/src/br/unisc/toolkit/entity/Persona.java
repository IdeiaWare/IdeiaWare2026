package br.unisc.toolkit.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="persona")
public class Persona {

	public Persona() {
    }
 
    public Persona(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="persona_id")
	private int id;
	
	@Column(name="ideia_codigo")
	private Long ideiaCodigo;
	
	@Column(name="name")
	private String name;
	
	@Column(name="age")
	private int age;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public Long getIdeiaCodigo() {
		return ideiaCodigo;
	}

	public void setIdeiaCodigo(Long ideiaCodigo) {
		this.ideiaCodigo = ideiaCodigo;
	}
	
}
