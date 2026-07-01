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
		
		// ORDER BY pov_id DESC: lista os POV mais recentes em cima (pov_id e
		// auto-increment). Combina com o LinkedHashMap do controller, que preserva
		// esta ordem ao montar o mapa exibido.
		Query<Object> theQuery =
				currentSession.createNativeQuery("SELECT persona_pov.ID, persona.name, persona.persona_id, pov.user, pov.need, pov.insight, pov.pov_id FROM lic_bd.persona_pov inner join persona on (persona.persona_id = persona_pov.persona_id) inner join pov on (pov.pov_id = persona_pov.pov_id) where pov.ideia_codigo=:IdeiaCodigo ORDER BY pov.pov_id DESC");
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

		// SEC-23 (IDOR): escopo por ideia_codigo -> nao da p/ ler o POV de OUTRA ideia
		// chutando o pov_id (antes a query buscava so por pov_id).
		Query<Object> theQuery =
				currentSession.createNativeQuery("SELECT persona_pov.ID, persona.name, persona.persona_id, pov.user, pov.need, pov.insight, pov.pov_id FROM lic_bd.persona_pov inner join persona on (persona.persona_id = persona_pov.persona_id) inner join pov on (pov.pov_id = persona_pov.pov_id) where pov.pov_id=:ID and pov.ideia_codigo=:IdeiaCodigo");
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

		// SEC-24: confere o dono do POV e tira a entidade da sessao (evict) -> nao
		// conflita com o saveOrUpdate que vem depois (NonUniqueObject).
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

		currentSession.saveOrUpdate(thePOV);
	}

	@Override
	public void deletePointOfView(int theId, Long ideiaCodigo) {
		// get the current hibernate sesion
		Session currentSession = sessionFactory.getCurrentSession();

		// TK-03: filtra por ideia_codigo para impedir deletar POV de outra ideia (IDOR)
		Query theQuery = currentSession.createQuery("delete from PointOfView where id=:ID and ideia_codigo=:ideiaCodigo");
		theQuery.setParameter("ID", theId);
		theQuery.setParameter("ideiaCodigo", ideiaCodigo);

		theQuery.executeUpdate();

	}

	
}
