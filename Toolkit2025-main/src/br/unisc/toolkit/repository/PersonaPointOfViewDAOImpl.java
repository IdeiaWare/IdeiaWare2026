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

		// REVISAO 2026-07-08 (varredura Toolkit, achados BAIXA):
		// (1) "pov_id" e o nome da COLUNA fisica, nao da propriedade Java (pointOfViewID)
		//     -- funcionava por coincidencia (fallback do parser HQL classico do
		//     Hibernate 5.x pra SQL literal quando nao reconhece a propriedade);
		//     trocado pelo nome de propriedade correto.
		// (2) sem filtro por ideia -- hoje nao exploravel (o unico caller,
		//     PointOfViewController, ja confere povPertenceAIdeia antes), mas defesa em
		//     profundidade (mesmo padrao ja usado em Persona/Empathy).
		Query theQuery = currentSession.createQuery("DELETE FROM PersonaPointOfView WHERE pointOfViewID=:povID AND ideiaCodigo=:ideiaCodigo");
		theQuery.setParameter("povID", povID);
		theQuery.setParameter("ideiaCodigo", ideiaCodigo);

		theQuery.executeUpdate();

	}
}
