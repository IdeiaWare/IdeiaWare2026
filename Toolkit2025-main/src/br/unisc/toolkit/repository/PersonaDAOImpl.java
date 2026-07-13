package br.unisc.toolkit.repository;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.unisc.toolkit.entity.Persona;

@Repository
public class PersonaDAOImpl implements PersonaDAO {
	
	// need to inject the session factory
	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public List<Persona> getPersonas(Long ideiaCodigo) {
		
		// get the current hibernate session
		Session currentSession = sessionFactory.getCurrentSession();
		
		// TK-HQL: ideiaCodigo (propriedade, nao coluna) -- mesmo motivo do EmpathyDAOImpl.
		Query<Persona> theQuery =
				currentSession.createQuery("from Persona where ideiaCodigo=:IdeiaCodigo order by name", Persona.class);
		theQuery.setParameter("IdeiaCodigo", ideiaCodigo);
		
		// execute query and get result list
		List<Persona> personas = theQuery.getResultList();
		
		// return the results
		return personas;
	}

	@Override
	public void savePersona(Persona thePersona) {
		// get current hibernate session
		Session currentSession = sessionFactory.getCurrentSession();

		// SEC-24: num UPDATE, so salva se a persona existente for da MESMA ideia (IDOR de escrita).
		if (thePersona.getId() != 0) {
			Persona existente = currentSession.get(Persona.class, thePersona.getId());
			if (existente == null || existente.getIdeiaCodigo() == null
					|| !existente.getIdeiaCodigo().equals(thePersona.getIdeiaCodigo())) {
				return;
			}
			currentSession.evict(existente); // evita NonUniqueObject no saveOrUpdate
		}

		// save/update the customer ... finally LOL
		currentSession.saveOrUpdate(thePersona);
	}

	@Override
	public void deletePersona(int theId, Long ideiaCodigo) {
		// get the current hibernate sesion
		Session currentSession = sessionFactory.getCurrentSession();

		// TK-03/TK-HQL: filtra por ideiaCodigo (propriedade, nao coluna) pra impedir deletar de outra ideia.
		Query theQuery = currentSession.createQuery("delete from Persona where id=:ID and ideiaCodigo=:ideiaCodigo");
		theQuery.setParameter("ID", theId);
		theQuery.setParameter("ideiaCodigo", ideiaCodigo);

		theQuery.executeUpdate();

	}

	@Override
	public Persona getPersona(int theId, Long ideiaCodigo) {
		// get the curent hibernate session
		Session currentSession = sessionFactory.getCurrentSession();
		
		// TK-HQL: id/ideiaCodigo (propriedade, nao coluna).
		Query<Persona> theQuery =
				currentSession.createQuery("from Persona where id=:PersonaId and ideiaCodigo=:IdeiaCodigo order by name", Persona.class);
		theQuery.setParameter("PersonaId", theId);
		theQuery.setParameter("IdeiaCodigo", ideiaCodigo);

		// TK-02: uniqueResult retorna null em vez de lancar NoResultException.
		Persona thePersona = theQuery.uniqueResult();

		return thePersona;
	}
}
