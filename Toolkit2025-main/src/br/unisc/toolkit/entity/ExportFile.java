package br.unisc.toolkit.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="Export_File")
public class ExportFile {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="file_id")
	private Long id;
	
	@Column(name="ideia_codigo")
	private Long ideiaCodigo;
	
	@Column(name="file_name")
	private String fileName;

	// E1: longtext -- PDF em base64 ultrapassa os 255 chars do VARCHAR padrao.
	@Column(name="file_location", columnDefinition="longtext")
	private String fileLocation;
	
	@Column(name="file_type_identification")
	private String fileTypeIdentification;
	
	@Column(name="created")
	@Temporal(TemporalType.TIMESTAMP)
    private java.util.Date created;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileLocation() {
		return fileLocation;
	}

	public void setFileLocation(String fileLocation) {
		this.fileLocation = fileLocation;
	}

	public String getFileTypeIdentification() {
		return fileTypeIdentification;
	}

	public void setFileTypeIdentification(String fileTypeIdentification) {
		this.fileTypeIdentification = fileTypeIdentification;
	}

	public Long getIdeiaCodigo() {
		return ideiaCodigo;
	}

	public void setIdeiaCodigo(Long ideiaCodigo) {
		this.ideiaCodigo = ideiaCodigo;
	}

	public java.util.Date getCreated() {
		return created;
	}

	public void setCreated(java.util.Date created) {
		this.created = created;
	}
}
