package br.unisc.toolkit.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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

// TEST-03/04: PointOfViewDAOImpl contra H2 -- trava o JOIN nativo, o TK-ORD e o escopo por ideiaCodigo.
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext-test.xml")
@Transactional
public class PointOfViewDAOImplTest {

	@Autowired
	private PointOfViewDAO povDAO;

	@Autowired
	private PersonaDAO personaDAO;

	@Autowired
	private PersonaPointOfViewDAO personaPovDAO;

	@Autowired
	private SessionFactory sessionFactory;

	private Persona novaPersona(String nome, long ideiaCodigo) {
		Persona p = new Persona(nome, 30);
		p.setIdeiaCodigo(ideiaCodigo);
		personaDAO.savePersona(p);
		return p;
	}

	private PointOfView novoPOV(long ideiaCodigo) {
		PointOfView pov = new PointOfView();
		pov.setUserText("Usuario");
		pov.setNeedText("Necessidade");
		pov.setInsightText("Insight");
		pov.setIdeiaCodigo(ideiaCodigo);
		povDAO.savePOV(pov);
		return pov;
	}

	private void vincula(Persona persona, PointOfView pov, long ideiaCodigo) {
		PersonaPointOfView vinculo = new PersonaPointOfView();
		vinculo.setPersonaID(persona.getId());
		vinculo.setPointOfViewID(pov.getId());
		vinculo.setIdeiaCodigo(ideiaCodigo);
		personaPovDAO.savePersonaPOV(vinculo);
	}

	@Test
	public void getPointOfViews_retornaJoinComPersonaOrdenadoPorPovIdDesc() { // TK-ORD
		Persona persona = novaPersona("Joao", 1L);
		PointOfView pov1 = novoPOV(1L);
		vincula(persona, pov1, 1L);
		PointOfView pov2 = novoPOV(1L);
		vincula(persona, pov2, 1L);

		List<Object> resultado = povDAO.getPointOfViews(1L);

		assertEquals(2, resultado.size());
		Object[] primeiraLinha = (Object[]) resultado.get(0);
		// pov.pov_id e a ultima coluna da projecao -- pov2 (mais recente) deve vir primeiro.
		assertEquals(pov2.getId(), ((Number) primeiraLinha[6]).intValue());
	}

	@Test
	public void getPointOfViews_naoRetornaVinculoDePersonaDeOutraIdeia() { // TK-23, defesa em profundidade
		Persona personaOutraIdeia = novaPersona("Intruso", 2L);
		PointOfView pov = novoPOV(1L);
		// SEC-24: vincula uma persona de OUTRA ideia, testa a defesa mesmo assim.
		vincula(personaOutraIdeia, pov, 1L);

		assertTrue(povDAO.getPointOfViews(1L).isEmpty());
	}

	@Test
	public void getSpecificPointOfView_comIdeiaCodigoDeOutraIdeia_retornaVazio() { // SEC-23
		Persona persona = novaPersona("Joao", 1L);
		PointOfView pov = novoPOV(1L);
		vincula(persona, pov, 1L);

		assertTrue(povDAO.getSpecificPointOfView(pov.getId(), 2L).isEmpty());
	}

	@Test
	public void getSpecificPointOfView_daPropriaIdeia_retornaLinha() {
		Persona persona = novaPersona("Joao", 1L);
		PointOfView pov = novoPOV(1L);
		vincula(persona, pov, 1L);

		assertEquals(1, povDAO.getSpecificPointOfView(pov.getId(), 1L).size());
	}

	@Test
	public void povPertenceAIdeia_mesmaIdeia_retornaTrue() {
		PointOfView pov = novoPOV(1L);

		assertTrue(povDAO.povPertenceAIdeia(pov.getId(), 1L));
	}

	@Test
	public void povPertenceAIdeia_outraIdeia_retornaFalse() { // SEC-24
		PointOfView pov = novoPOV(1L);

		assertFalse(povDAO.povPertenceAIdeia(pov.getId(), 2L));
	}

	@Test
	public void povPertenceAIdeia_idInexistente_retornaFalse() {
		assertFalse(povDAO.povPertenceAIdeia(999999, 1L));
	}

	@Test
	public void savePOV_updateDeOutraIdeia_naoSalva() { // SEC-24
		Persona persona = novaPersona("Joao", 1L);
		PointOfView original = novoPOV(1L);
		vincula(persona, original, 1L);

		PointOfView forjado = new PointOfView();
		forjado.setId(original.getId());
		forjado.setUserText("Hackeado");
		forjado.setNeedText("Hackeado");
		forjado.setInsightText("Hackeado");
		forjado.setIdeiaCodigo(2L);
		povDAO.savePOV(forjado);

		List<Object> aindaOriginal = povDAO.getSpecificPointOfView(original.getId(), 1L);
		assertTrue(aindaOriginal.isEmpty() == false);
		Object[] linha = (Object[]) aindaOriginal.get(0);
		assertEquals("Usuario", linha[3]);
	}

	@Test
	public void deletePointOfView_comIdeiaCodigoDeOutraIdeia_naoApaga() { // TK-03/TK-HQL
		PointOfView pov = novoPOV(1L);

		povDAO.deletePointOfView(pov.getId(), 2L);

		assertTrue(povDAO.povPertenceAIdeia(pov.getId(), 1L));
	}

	@Test
	public void deletePointOfView_daPropriaIdeia_apaga() {
		PointOfView pov = novoPOV(1L);

		povDAO.deletePointOfView(pov.getId(), 1L);
		// bulk HQL nao atualiza o 1o-nivel de cache da sessao, precisa limpar.
		sessionFactory.getCurrentSession().clear();

		assertFalse(povDAO.povPertenceAIdeia(pov.getId(), 1L));
	}
}
