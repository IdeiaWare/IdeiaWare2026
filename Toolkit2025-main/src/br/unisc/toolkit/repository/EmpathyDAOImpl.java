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
		Session currentSession = sessionFactory.getCurrentSession();

		// TK-HQL: usa nome de propriedade Java, nao de coluna.
		Query<Empathy> theQuery =
				currentSession.createQuery("from Empathy where personaId=:ID and attribute=:Attribute and ideiaCodigo=:IdeiaCodigo", Empathy.class);
		theQuery.setParameter("ID", theId);
		theQuery.setParameter("Attribute", attribute);
		theQuery.setParameter("IdeiaCodigo", ideiaCodigo);

		List<Empathy> attributes = theQuery.getResultList();

		return attributes;
	}

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public void saveEmpathyAttribute(Empathy theEmpathy) {
		Session currentSession = sessionFactory.getCurrentSession();

		// SEC-24: em UPDATE, so salva se o atributo existente for da mesma ideia.
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
		Session currentSession = sessionFactory.getCurrentSession();

		// TK-03/TK-HQL: filtra por ideiaCodigo pra impedir deletar de outra ideia.
		Query theQuery = currentSession.createQuery("delete from Empathy where id=:ID and ideiaCodigo=:ideiaCodigo");
		theQuery.setParameter("ID", attributeId);
		theQuery.setParameter("ideiaCodigo", ideiaCodigo);

		theQuery.executeUpdate();

	}

}
