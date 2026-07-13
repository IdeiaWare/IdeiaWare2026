package br.unisc.toolkit.repository;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.unisc.toolkit.entity.PersonaPointOfView;

@Repository
public class PersonaPointOfViewDAOImpl implements PersonaPointOfViewDAO {

	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public void savePersonaPOV(PersonaPointOfView personaPOV) {
		Session currentSession = sessionFactory.getCurrentSession();

		currentSession.save(personaPOV);		
	}

	@Override
	public void removePOVIdFromAuxiliarTable(int povID, Long ideiaCodigo) {
		// get the current hibernate session
		Session currentSession = sessionFactory.getCurrentSession();

		// TK-HQL: pointOfViewID (propriedade, nao coluna) + TK-23: filtro por ideiaCodigo (defesa em profundidade).
		Query theQuery = currentSession.createQuery("DELETE FROM PersonaPointOfView WHERE pointOfViewID=:povID AND ideiaCodigo=:ideiaCodigo");
		theQuery.setParameter("povID", povID);
		theQuery.setParameter("ideiaCodigo", ideiaCodigo);

		theQuery.executeUpdate();

	}
}
