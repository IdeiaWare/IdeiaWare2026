package br.unisc.toolkit.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.unisc.toolkit.entity.Ideia;
import br.unisc.toolkit.entity.Persona;

@Repository
public class IdeiaDAOImpl implements IdeiaDAO {

	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public void finalize(Ideia theIdeia) {
		Session currentSession = sessionFactory.getCurrentSession();

		theIdeia.setStatus("CV");

		currentSession.saveOrUpdate(theIdeia);
	}
	
	@Override
	public Ideia getIdeia(Long ideiaCodigo) {
		Session currentSession = sessionFactory.getCurrentSession();
		
		Query<Ideia> theQuery = currentSession.createQuery("from Ideia where codigo=:IdeiaId", Ideia.class);
		theQuery.setParameter("IdeiaId", ideiaCodigo);

		// TK-02: uniqueResult retorna null se nao existir.
		Ideia theIdeia = theQuery.uniqueResult();

		return theIdeia;
	}

}
