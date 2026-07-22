package br.unisc.toolkit.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.hibernate.SessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import br.unisc.toolkit.entity.Persona;
import br.unisc.toolkit.entity.PersonaPointOfView;
import br.unisc.toolkit.entity.PointOfView;

// TEST-03/04: PersonaDAOImpl contra H2 -- trava o escopo por ideiaCodigo (SEC-24/TK-03/TK-HQL).
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext-test.xml")
@Transactional
public class PersonaDAOImplTest {

	@Autowired
	private PersonaDAO personaDAO;

	@Autowired
	private PointOfViewDAO pointOfViewDAO;

	@Autowired
	private PersonaPointOfViewDAO personaPointOfViewDAO;

	@Autowired
	private SessionFactory sessionFactory;

	private Persona novaPersona(String nome, int idade, long ideiaCodigo) {
		Persona p = new Persona(nome, idade);
		p.setIdeiaCodigo(ideiaCodigo);
		personaDAO.savePersona(p);
		return p;
	}

	private PointOfView novoPOV(long ideiaCodigo) {
		PointOfView pov = new PointOfView();
		pov.setIdeiaCodigo(ideiaCodigo);
		pov.setUserText("um usuario");
		pov.setNeedText("uma necessidade");
		pov.setInsightText("um insight");
		pointOfViewDAO.savePOV(pov);
		return pov;
	}

	private void vincula(int personaId, int povId, long ideiaCodigo) {
		PersonaPointOfView vinculo = new PersonaPointOfView();
		vinculo.setPersonaID(personaId);
		vinculo.setPointOfViewID(povId);
		vinculo.setIdeiaCodigo(ideiaCodigo);
		personaPointOfViewDAO.savePersonaPOV(vinculo);
	}

	@Test
	public void savePersona_eDepoisGetPersona_retornaPersonaSalva() {
		Persona salva = novaPersona("Joao", 30, 1L);

		Persona recarregada = personaDAO.getPersona(salva.getId(), 1L);

		assertEquals("Joao", recarregada.getName());
		assertEquals(30, recarregada.getAge());
	}

	@Test
	public void getPersona_comIdeiaCodigoDeOutraIdeia_retornaNull() {
		Persona salva = novaPersona("Joao", 30, 1L);

		assertNull(personaDAO.getPersona(salva.getId(), 2L));
	}

	@Test
	public void getPersonas_retornaSoAsDaIdeiaEEmOrdemAlfabetica() {
		novaPersona("Zeca", 20, 1L);
		novaPersona("Ana", 25, 1L);
		novaPersona("Bruno", 40, 2L); // outra ideia -- nao deve aparecer

		List<Persona> resultado = personaDAO.getPersonas(1L);

		assertEquals(2, resultado.size());
		assertEquals("Ana", resultado.get(0).getName());
		assertEquals("Zeca", resultado.get(1).getName());
	}

	@Test
	public void savePersona_updateDeOutraIdeia_naoSalva() { // SEC-24
		Persona salva = novaPersona("Joao", 30, 1L);

		Persona forjada = new Persona("Joao Hackeado", 99);
		forjada.setId(salva.getId());
		forjada.setIdeiaCodigo(2L); // tenta reescrever uma persona da ideia 1 como se fosse da 2
		personaDAO.savePersona(forjada);

		Persona aindaOriginal = personaDAO.getPersona(salva.getId(), 1L);
		assertEquals("Joao", aindaOriginal.getName());
		assertEquals(30, aindaOriginal.getAge());
	}

	@Test
	public void deletePersona_comIdeiaCodigoDeOutraIdeia_naoApaga() { // TK-03/TK-HQL
		Persona salva = novaPersona("Joao", 30, 1L);

		personaDAO.deletePersona(salva.getId(), 2L);

		assertTrue(personaDAO.getPersonas(1L).stream().anyMatch(p -> p.getId() == salva.getId()));
	}

	@Test
	public void deletePersona_daPropriaIdeia_apaga() {
		Persona salva = novaPersona("Joao", 30, 1L);

		personaDAO.deletePersona(salva.getId(), 1L);

		assertNull(personaDAO.getPersona(salva.getId(), 1L));
	}

	@Test
	public void deletePersona_apagaPOVQueSoEssaPersonaLigava() { // TK-POV-ORFAO
		Persona persona = novaPersona("Joao", 30, 1L);
		PointOfView pov = novoPOV(1L);
		vincula(persona.getId(), pov.getId(), 1L);
		// flush+clear evita que o cache de 1o nivel do Hibernate mascare o DELETE HQL em bloco.
		sessionFactory.getCurrentSession().flush();
		sessionFactory.getCurrentSession().clear();

		personaDAO.deletePersona(persona.getId(), 1L);

		assertFalse("POV orfao deveria ter sido apagado junto", pointOfViewDAO.povPertenceAIdeia(pov.getId(), 1L));
	}

	@Test
	public void deletePersona_naoApagaPOVAindaLigadoAOutraPersona() { // TK-POV-ORFAO
		Persona persona1 = novaPersona("Joao", 30, 1L);
		Persona persona2 = novaPersona("Maria", 28, 1L);
		PointOfView pov = novoPOV(1L);
		vincula(persona1.getId(), pov.getId(), 1L);
		vincula(persona2.getId(), pov.getId(), 1L);
		sessionFactory.getCurrentSession().flush();
		sessionFactory.getCurrentSession().clear();

		personaDAO.deletePersona(persona1.getId(), 1L);

		assertTrue("POV ainda ligado a outra persona nao deveria ser apagado", pointOfViewDAO.povPertenceAIdeia(pov.getId(), 1L));
	}
}
