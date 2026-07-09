package br.unisc.toolkit.repository;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.unisc.toolkit.entity.ExportFile;
import br.unisc.toolkit.entity.Persona;

@Repository
public class ExportFileDAOImpl implements ExportFileDAO {

	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public void saveExportedFile(ExportFile file) {
		Session currentSession = sessionFactory.getCurrentSession();
		currentSession.save(file);		
	}

	@Override
	public List<ExportFile> getFiles(Long ideiaCodigo) {
		// get the current hibernate session
		Session currentSession = sessionFactory.getCurrentSession();
		
		// create a query ... sort by name
		// REVISAO 2026-07-08 (varredura Toolkit, achado BAIXA): HQL usava nome de
		// COLUNA (ideia_codigo, file_name) em vez de nome de PROPRIEDADE Java
		// (ideiaCodigo, fileName) -- funcionava por coincidencia, mesmo motivo
		// documentado em EmpathyDAOImpl.
		Query<ExportFile> theQuery =
				currentSession.createQuery("from ExportFile where ideiaCodigo=:IdeiaCodigo order by fileName", ExportFile.class);
		theQuery.setParameter("IdeiaCodigo", ideiaCodigo);
		
		// execute query and get result list
		List<ExportFile> files = theQuery.getResultList();
		
		// return the results
		return files;
	}

}
