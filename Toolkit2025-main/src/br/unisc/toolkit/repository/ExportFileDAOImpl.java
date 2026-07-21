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
		Session currentSession = sessionFactory.getCurrentSession();

		// TK-HQL: nome de propriedade Java, nao de coluna.
		Query<ExportFile> theQuery =
				currentSession.createQuery("from ExportFile where ideiaCodigo=:IdeiaCodigo order by fileName", ExportFile.class);
		theQuery.setParameter("IdeiaCodigo", ideiaCodigo);

		List<ExportFile> files = theQuery.getResultList();

		return files;
	}

}
