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
		
		// create a query ... sort by name
		Query<Persona> theQuery = 
				currentSession.createQuery("from Persona where ideia_codigo=:IdeiaCodigo order by name", Persona.class);
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

		// SEC-24 (IDOR de escrita): num UPDATE (id != 0), so salva se a persona existente
		// for da MESMA ideia -> bloqueia sobrescrever persona de outra ideia chutando o id.
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

		// TK-03: filtra por ideia_codigo para impedir deletar persona de outra ideia (IDOR)
		Query theQuery = currentSession.createQuery("delete from Persona where id=:ID and ideia_codigo=:ideiaCodigo");
		theQuery.setParameter("ID", theId);
		theQuery.setParameter("ideiaCodigo", ideiaCodigo);

		theQuery.executeUpdate();

	}

	@Override
	public Persona getPersona(int theId, Long ideiaCodigo) {
		// get the curent hibernate session
		Session currentSession = sessionFactory.getCurrentSession();
		
		Query<Persona> theQuery = 
				currentSession.createQuery("from Persona where persona_id=:PersonaId and ideia_codigo=:IdeiaCodigo order by name", Persona.class);
		theQuery.setParameter("PersonaId", theId);
		theQuery.setParameter("IdeiaCodigo", ideiaCodigo);
		// now retrieve/read from database using the primary key
		//Persona thePersona = currentSession.get(Persona.class, theId);

		// TK-02: uniqueResult retorna null em vez de lancar NoResultException.
		Persona thePersona = theQuery.uniqueResult();

		return thePersona;
	}
}
