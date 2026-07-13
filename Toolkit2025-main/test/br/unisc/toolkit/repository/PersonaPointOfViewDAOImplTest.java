package br.unisc.toolkit.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.hibernate.SessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import br.unisc.toolkit.entity.PersonaPointOfView;

// TEST-03/04: PersonaPointOfViewDAOImpl contra H2 -- trava TK-HQL (pointOfViewID por propriedade)
// e TK-23 (defesa em profundidade: so remove o vinculo se a ideiaCodigo bater).
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext-test.xml")
@Transactional
public class PersonaPointOfViewDAOImplTest {

	@Autowired
	private PersonaPointOfViewDAO personaPointOfViewDAO;

	@Autowired
	private SessionFactory sessionFactory;

	private long contarVinculos(int povId) {
		Long count = (Long) sessionFactory.getCurrentSession()
				.createQuery("select count(*) from PersonaPointOfView where pointOfViewID=:povId")
				.setParameter("povId", povId)
				.uniqueResult();
		return count;
	}

	private PersonaPointOfView novoVinculo(int personaId, int povId, long ideiaCodigo) {
		PersonaPointOfView v = new PersonaPointOfView();
		v.setPersonaID(personaId);
		v.setPointOfViewID(povId);
		v.setIdeiaCodigo(ideiaCodigo);
		personaPointOfViewDAO.savePersonaPOV(v);
		return v;
	}

	@Test
	public void savePersonaPOV_persisteVinculo() {
		novoVinculo(1, 10, 1L);

		assertEquals(1, contarVinculos(10));
	}

	@Test
	public void removePOVIdFromAuxiliarTable_comIdeiaCodigoCorreto_remove() {
		novoVinculo(1, 10, 1L);
		novoVinculo(2, 10, 1L);

		personaPointOfViewDAO.removePOVIdFromAuxiliarTable(10, 1L);

		assertEquals(0, contarVinculos(10));
	}

	@Test
	public void removePOVIdFromAuxiliarTable_comIdeiaCodigoDeOutraIdeia_naoRemove() { // TK-23
		novoVinculo(1, 10, 1L);

		personaPointOfViewDAO.removePOVIdFromAuxiliarTable(10, 2L);

		assertEquals(1, contarVinculos(10));
	}
}
