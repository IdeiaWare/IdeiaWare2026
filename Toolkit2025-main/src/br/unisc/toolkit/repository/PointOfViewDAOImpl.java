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
		// REVISAO 2026-07-08 (varredura Toolkit, achado BAIXA): removido o prefixo
		// "lic_bd." hardcoded -- a native query ja roda na conexao JDBC apontada pro
		// schema certo (fragil so se o nome do banco mudar; sem risco de SQL injection,
		// parametros ja eram bindados).
		// REVISAO 2026-07-08 (varredura Toolkit, achado ALTA -- IDOR, defesa em
		// profundidade): "AND persona.ideia_codigo=pov.ideia_codigo" adicionado. O fix
		// principal e no savePersonasPOVItem() do controller (impede o vinculo cross-
		// ideia de ser criado); isto aqui garante que mesmo um vinculo indevido pre-
		// existente (dado legado, ou um bug futuro que reabra o mesmo problema) nao
		// vaze nome de persona de outra ideia nesta listagem.
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

		// SEC-23 (IDOR): escopo por ideia_codigo -> nao da p/ ler o POV de OUTRA ideia
		// chutando o pov_id (antes a query buscava so por pov_id).
		// REVISAO 2026-07-08 (varredura Toolkit, achado BAIXA): removido o prefixo
		// "lic_bd." hardcoded -- mesmo motivo da query acima.
		// REVISAO 2026-07-08 (varredura Toolkit, achado ALTA -- IDOR, defesa em
		// profundidade): mesmo motivo da query getPointOfViews acima.
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

		// REVISAO 2026-07-08 (varredura Toolkit, achado BAIXA): defesa em profundidade --
		// hoje os 2 controllers que chamam este metodo ja conferem povPertenceAIdeia()
		// antes de um UPDATE (SEC-24), entao isto nao e exploravel agora; mas o DAO em
		// si nao tinha NENHUMA blindagem propria (diferente de Persona/Empathy, que
		// reforcam mesmo se o controller esquecer) -- fragil a um refactor futuro
		// reabrir a mesma classe de IDOR. Mesmo padrao de evict() do povPertenceAIdeia()
		// acima, pra nao conflitar com o saveOrUpdate logo em seguida.
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

		// TK-03: filtra por ideia_codigo para impedir deletar POV de outra ideia (IDOR)
		// REVISAO 2026-07-08 (varredura Toolkit, achado BAIXA): ideia_codigo -> ideiaCodigo
		// (nome de propriedade, nao de coluna -- mesmo motivo documentado em EmpathyDAOImpl).
		Query theQuery = currentSession.createQuery("delete from PointOfView where id=:ID and ideiaCodigo=:ideiaCodigo");
		theQuery.setParameter("ID", theId);
		theQuery.setParameter("ideiaCodigo", ideiaCodigo);

		theQuery.executeUpdate();

	}

	
}
