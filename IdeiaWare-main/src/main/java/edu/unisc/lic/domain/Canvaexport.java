package edu.unisc.lic.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import edu.unisc.lic.classes.Data;

@SuppressWarnings("serial")
@Entity
public class Canvaexport extends GenericDomain {

    // K.8 #4: unique=true trava no BANCO que uma ideia tenha mais de 1 export de Canvas.
	@OneToOne
    @JoinColumn(name = "ideia_codigo", nullable = false, unique = true)
    private Ideia ideia;

    // PDF-DISCO
    @Column(length = 255, nullable = false)
    private String file;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    public Canvaexport() {
        ideia = new Ideia();
    }

    public Canvaexport(Ideia ideia, String file, Date created) {
        this.ideia = ideia;
        this.file = file;
        this.created = created;
    }
	@Override
	public String toString() {
		return "Canvaexport {ideia=" + ideia + ", file=" + file + ", date=" + created + "}";
	}

	public Ideia getIdeia() {
		return ideia;
	}

	public void setIdeia(Ideia ideia) {
		this.ideia = ideia;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public Date getDate() {
		return created;
	}

	public void setDate() {
		this.created = Data.horaAtual();
	}
    
}
