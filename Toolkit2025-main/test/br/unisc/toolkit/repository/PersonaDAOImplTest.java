package br.unisc.toolkit.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import br.unisc.toolkit.entity.Persona;

// TEST-03/04: PersonaDAOImpl contra H2 -- trava o escopo por ideiaCodigo (SEC-24/TK-03/TK-HQL),
// nunca testado antes (Toolkit nao tinha nenhum teste de DAO).
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext-test.xml")
@Transactional
public class PersonaDAOImplTest {

	@Autowired
	private PersonaDAO personaDAO;

	private Persona novaPersona(String nome, int idade, long ideiaCodigo) {
		Persona p = new Persona(nome, idade);
		p.setIdeiaCodigo(ideiaCodigo);
		personaDAO.savePersona(p);
		return p;
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
}
