package br.unisc.toolkit.repository;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.unisc.toolkit.entity.Empathy;
import br.unisc.toolkit.entity.Persona;

@Repository
public class EmpathyDAOImpl implements EmpathyDAO {

	@Override
	public List<Empathy> getAttributes(int theId, String attribute, Long ideiaCodigo) {
		// get the current hibernate session
		Session currentSession = sessionFactory.getCurrentSession();
		
		// create a query
		Query<Empathy> theQuery = 
				currentSession.createQuery("from Empathy where fk_persona_id=:ID and attribute=:Attribute and ideia_codigo=:IdeiaCodigo", Empathy.class);
		theQuery.setParameter("ID", theId);
		theQuery.setParameter("Attribute", attribute);
		theQuery.setParameter("IdeiaCodigo", ideiaCodigo);
		
		// execute query and get result list
		List<Empathy> attributes = theQuery.getResultList();
		
		// return the results
		return attributes;
	}
	
	// need to inject the session factory
	@Autowired
	private SessionFactory sessionFactory;
		
	@Override
	public void saveEmpathyAttribute(Empathy theEmpathy) {
		// get current hibernate session
		Session currentSession = sessionFactory.getCurrentSession();

		// SEC-24 (IDOR de escrita): num UPDATE (id != 0), so salva se o atributo existente
		// for da MESMA ideia -> bloqueia sobrescrever atributo de outra ideia pelo id.
		if (theEmpathy.getId() != 0) {
			Empathy existente = currentSession.get(Empathy.class, theEmpathy.getId());
			if (existente == null || existente.getIdeiaCodigo() == null
					|| !existente.getIdeiaCodigo().equals(theEmpathy.getIdeiaCodigo())) {
				return;
			}
			currentSession.evict(existente);
		}

		currentSession.saveOrUpdate(theEmpathy);

	}

	@Override
	public void deleteAttribute(int attributeId, Long ideiaCodigo) {
		// get the current hibernate sesion
		Session currentSession = sessionFactory.getCurrentSession();

		// TK-03: filtra por ideia_codigo para impedir deletar atributo de outra ideia (IDOR)
		Query theQuery = currentSession.createQuery("delete from Empathy where id=:ID and ideia_codigo=:ideiaCodigo");
		theQuery.setParameter("ID", attributeId);
		theQuery.setParameter("ideiaCodigo", ideiaCodigo);

		theQuery.executeUpdate();

	}

}
