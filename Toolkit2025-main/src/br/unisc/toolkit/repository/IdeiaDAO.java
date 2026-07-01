package br.unisc.toolkit.repository;

import org.hibernate.Session;
import org.hibernate.query.Query;

import br.unisc.toolkit.entity.Ideia;
import br.unisc.toolkit.entity.Persona;

public interface IdeiaDAO {

	public void finalize(Ideia theIdeia);
	
	public Ideia getIdeia(Long ideiaCodigo);

}
