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
		Query<ExportFile> theQuery = 
				currentSession.createQuery("from ExportFile where ideia_codigo=:IdeiaCodigo order by file_name", ExportFile.class);
		theQuery.setParameter("IdeiaCodigo", ideiaCodigo);
		
		// execute query and get result list
		List<ExportFile> files = theQuery.getResultList();
		
		// return the results
		return files;
	}

}
