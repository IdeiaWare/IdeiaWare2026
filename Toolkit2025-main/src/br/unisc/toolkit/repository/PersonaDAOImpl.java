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

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private PointOfViewDAO pointOfViewDAO;

	@Override
	public List<Persona> getPersonas(Long ideiaCodigo) {

		Session currentSession = sessionFactory.getCurrentSession();

		// TK-HQL: ideiaCodigo e propriedade Java, nao coluna.
		Query<Persona> theQuery =
				currentSession.createQuery("from Persona where ideiaCodigo=:IdeiaCodigo order by name", Persona.class);
		theQuery.setParameter("IdeiaCodigo", ideiaCodigo);

		List<Persona> personas = theQuery.getResultList();

		return personas;
	}

	@Override
	public void savePersona(Persona thePersona) {
		Session currentSession = sessionFactory.getCurrentSession();

		// SEC-24: em UPDATE, so salva se a persona existente for da mesma ideia.
		if (thePersona.getId() != 0) {
			Persona existente = currentSession.get(Persona.class, thePersona.getId());
			if (existente == null || existente.getIdeiaCodigo() == null
					|| !existente.getIdeiaCodigo().equals(thePersona.getIdeiaCodigo())) {
				return;
			}
			currentSession.evict(existente);
		}

		currentSession.saveOrUpdate(thePersona);
	}

	@Override
	public void deletePersona(int theId, Long ideiaCodigo) {
		Session currentSession = sessionFactory.getCurrentSession();

		// TK-POV-ORFAO: acha POVs que SO' esta persona liga, senao ficam orfaos no banco.
		Query orfaosQuery = currentSession.createNativeQuery(
				"SELECT pp1.pov_id FROM persona_pov pp1 "
				+ "WHERE pp1.persona_id = :personaId AND pp1.ideia_codigo = :ideiaCodigo "
				+ "AND NOT EXISTS (SELECT 1 FROM persona_pov pp2 WHERE pp2.pov_id = pp1.pov_id AND pp2.persona_id <> pp1.persona_id)");
		orfaosQuery.setParameter("personaId", theId);
		orfaosQuery.setParameter("ideiaCodigo", ideiaCodigo);
		@SuppressWarnings("unchecked")
		List<Number> povsOrfaos = orfaosQuery.getResultList();

		// TK-03/TK-HQL: filtra por ideiaCodigo pra impedir deletar de outra ideia.
		Query theQuery = currentSession.createQuery("delete from Persona where id=:ID and ideiaCodigo=:ideiaCodigo");
		theQuery.setParameter("ID", theId);
		theQuery.setParameter("ideiaCodigo", ideiaCodigo);

		theQuery.executeUpdate();

		for (Number povId : povsOrfaos) {
			pointOfViewDAO.deletePointOfView(povId.intValue(), ideiaCodigo);
		}
	}

	@Override
	public Persona getPersona(int theId, Long ideiaCodigo) {
		Session currentSession = sessionFactory.getCurrentSession();

		// TK-HQL: id/ideiaCodigo sao propriedade, nao coluna.
		Query<Persona> theQuery =
				currentSession.createQuery("from Persona where id=:PersonaId and ideiaCodigo=:IdeiaCodigo order by name", Persona.class);
		theQuery.setParameter("PersonaId", theId);
		theQuery.setParameter("IdeiaCodigo", ideiaCodigo);

		// TK-02: uniqueResult retorna null em vez de lancar excecao.
		Persona thePersona = theQuery.uniqueResult();

		return thePersona;
	}
}
