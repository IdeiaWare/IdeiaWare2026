package br.unisc.toolkit.repository;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.unisc.toolkit.entity.PointOfView;

@Repository
public class PointOfViewDAOImpl implements PointOfViewDAO {
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public List<Object> getPointOfViews(Long ideiaCodigo) {
		
		// get the current hibernate session
		Session currentSession = sessionFactory.getCurrentSession();
		
		// TK-ORD: ORDER BY pov_id DESC (mais recentes em cima, combina com o LinkedHashMap do controller).
		// TK-23: "AND persona.ideia_codigo=pov.ideia_codigo" -- defesa em profundidade contra vinculo cross-ideia.
		Query<Object> theQuery =
				currentSession.createNativeQuery("SELECT persona_pov.ID, persona.name, persona.persona_id, pov.user, pov.need, pov.insight, pov.pov_id FROM persona_pov inner join persona on (persona.persona_id = persona_pov.persona_id) inner join pov on (pov.pov_id = persona_pov.pov_id) where pov.ideia_codigo=:IdeiaCodigo AND persona.ideia_codigo=pov.ideia_codigo ORDER BY pov.pov_id DESC");
		theQuery.setParameter("IdeiaCodigo", ideiaCodigo);
		
		// execute query and get result list
		List<Object> pointOfViews = theQuery.getResultList();
		
		// return the results
		return pointOfViews;
	}
	
	@Override
	public List<Object> getSpecificPointOfView(int theId, Long ideiaCodigo) {
		// get the current hibernate session
		Session currentSession = sessionFactory.getCurrentSession();

		// SEC-23: escopo por ideia_codigo (antes buscava so por pov_id, IDOR). TK-23: mesmo motivo da query acima.
		Query<Object> theQuery =
				currentSession.createNativeQuery("SELECT persona_pov.ID, persona.name, persona.persona_id, pov.user, pov.need, pov.insight, pov.pov_id FROM persona_pov inner join persona on (persona.persona_id = persona_pov.persona_id) inner join pov on (pov.pov_id = persona_pov.pov_id) where pov.pov_id=:ID and pov.ideia_codigo=:IdeiaCodigo and persona.ideia_codigo=pov.ideia_codigo");
		theQuery.setParameter("ID", theId);
		theQuery.setParameter("IdeiaCodigo", ideiaCodigo);
		
		// execute query and get result list
		List<Object> pointOfView = theQuery.getResultList();
		
		// return the results
		return pointOfView;
	}	
	
	@Override
	public boolean povPertenceAIdeia(int povId, Long ideiaCodigo) {
		Session currentSession = sessionFactory.getCurrentSession();

		// SEC-24: confere o dono do POV e evict() antes do saveOrUpdate (evita NonUniqueObject).
		PointOfView pov = currentSession.get(PointOfView.class, povId);
		if (pov == null) {
			return false;
		}
		boolean ok = ideiaCodigo != null && ideiaCodigo.equals(pov.getIdeiaCodigo());
		currentSession.evict(pov);
		return ok;
	}

	@Override
	public void savePOV(PointOfView thePOV) {
		Session currentSession = sessionFactory.getCurrentSession();

		// SEC-24: mesma blindagem defensiva do DAO que Persona/Empathy ja tem (nao depende so do controller).
		if (thePOV.getId() != 0) {
			PointOfView existente = currentSession.get(PointOfView.class, thePOV.getId());
			if (existente != null) {
				boolean mesmaIdeia = existente.getIdeiaCodigo() != null
						&& existente.getIdeiaCodigo().equals(thePOV.getIdeiaCodigo());
				currentSession.evict(existente);
				if (!mesmaIdeia) {
					return;
				}
			}
		}

		currentSession.saveOrUpdate(thePOV);
	}

	@Override
	public void deletePointOfView(int theId, Long ideiaCodigo) {
		// get the current hibernate sesion
		Session currentSession = sessionFactory.getCurrentSession();

		// TK-03/TK-HQL: filtra por ideiaCodigo (propriedade, nao coluna) pra impedir deletar de outra ideia.
		Query theQuery = currentSession.createQuery("delete from PointOfView where id=:ID and ideiaCodigo=:ideiaCodigo");
		theQuery.setParameter("ID", theId);
		theQuery.setParameter("ideiaCodigo", ideiaCodigo);

		theQuery.executeUpdate();

	}

	
}
