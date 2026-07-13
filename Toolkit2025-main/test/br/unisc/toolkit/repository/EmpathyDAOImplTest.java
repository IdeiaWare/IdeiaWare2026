package br.unisc.toolkit.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import br.unisc.toolkit.entity.Empathy;

// TEST-03/04: EmpathyDAOImpl contra H2 -- trava o escopo por ideiaCodigo (SEC-24/TK-03/TK-HQL).
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext-test.xml")
@Transactional
public class EmpathyDAOImplTest {

	@Autowired
	private EmpathyDAO empathyDAO;

	private Empathy novoAtributo(int personaId, String attribute, String texto, long ideiaCodigo) {
		Empathy e = new Empathy();
		e.setPersonaId(personaId);
		e.setAttribute(attribute);
		e.setAttributeText(texto);
		e.setCardColor("yellow");
		e.setIdeiaCodigo(ideiaCodigo);
		empathyDAO.saveEmpathyAttribute(e);
		return e;
	}

	@Test
	public void saveEmpathyAttribute_eDepoisGetAttributes_retornaOSalvo() {
		novoAtributo(1, "gain", "Economiza tempo", 1L);

		List<Empathy> resultado = empathyDAO.getAttributes(1, "gain", 1L);

		assertEquals(1, resultado.size());
		assertEquals("Economiza tempo", resultado.get(0).getAttributeText());
	}

	@Test
	public void getAttributes_filtraPorPersonaAttributeEIdeiaAoMesmoTempo() { // TK-HQL
		novoAtributo(1, "gain", "Ganho da persona 1", 1L);
		novoAtributo(2, "gain", "Ganho da persona 2", 1L); // outra persona
		novoAtributo(1, "pain", "Dor da persona 1", 1L);   // outro atributo
		novoAtributo(1, "gain", "Ganho de outra ideia", 2L); // outra ideia

		List<Empathy> resultado = empathyDAO.getAttributes(1, "gain", 1L);

		assertEquals(1, resultado.size());
		assertEquals("Ganho da persona 1", resultado.get(0).getAttributeText());
	}

	@Test
	public void saveEmpathyAttribute_updateDeOutraIdeia_naoSalva() { // SEC-24
		Empathy original = novoAtributo(1, "gain", "Original", 1L);

		Empathy forjado = new Empathy();
		forjado.setId(original.getId());
		forjado.setPersonaId(1);
		forjado.setAttribute("gain");
		forjado.setAttributeText("Hackeado");
		forjado.setIdeiaCodigo(2L); // ideia errada
		empathyDAO.saveEmpathyAttribute(forjado);

		List<Empathy> aindaOriginal = empathyDAO.getAttributes(1, "gain", 1L);
		assertEquals(1, aindaOriginal.size());
		assertEquals("Original", aindaOriginal.get(0).getAttributeText());
	}

	@Test
	public void deleteAttribute_comIdeiaCodigoDeOutraIdeia_naoApaga() { // TK-03/TK-HQL
		Empathy salvo = novoAtributo(1, "gain", "Texto", 1L);

		empathyDAO.deleteAttribute(salvo.getId(), 2L);

		assertTrue(empathyDAO.getAttributes(1, "gain", 1L).stream().anyMatch(e -> e.getId() == salvo.getId()));
	}

	@Test
	public void deleteAttribute_daPropriaIdeia_apaga() {
		Empathy salvo = novoAtributo(1, "gain", "Texto", 1L);

		empathyDAO.deleteAttribute(salvo.getId(), 1L);

		assertTrue(empathyDAO.getAttributes(1, "gain", 1L).isEmpty());
	}
}
