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
	public void removePOVIdFromAuxiliarTable(int povID) {
		// get the current hibernate session
		Session currentSession = sessionFactory.getCurrentSession();
				
		Query theQuery = currentSession.createQuery("DELETE FROM PersonaPointOfView WHERE pov_id=:povID");
		theQuery.setParameter("povID", povID);
		
		theQuery.executeUpdate();
		
	}
}
