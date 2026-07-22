package edu.unisc.lic.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import java.util.Date;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@SuppressWarnings("serial")
@Entity
@Table(name = "Export_File")
public class ExportFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Long id;

    @Column(name = "file_name")
    private String fileName;

    @Column(name="created")
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date created;

    @Column(name = "file_location", length = 255, nullable = false)
    private String fileLocation;

    @Column(name = "file_type_identification", length = 14, nullable = false)
    private String fileTypeIdentification;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Ideia ideia;

    public ExportFile() {
        ideia = new Ideia();
    }

    public ExportFile(Ideia ideia, String caminho) {
        this.ideia = ideia;
        this.fileLocation = caminho;
    }

    @Override
    public String toString() {
        return "ExportedFile{" + "ideia=" + ideia + ", caminho=" + fileLocation + ", tipo=" + fileTypeIdentification + '}';
    }

    public Ideia getIdeia() {
        return ideia;
    }

    public void setIdeia(Ideia ideia) {
        this.ideia = ideia;
    }

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

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
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

}