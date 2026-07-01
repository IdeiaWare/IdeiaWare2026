package edu.unisc.lic.classes;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * TEST-01: trava os valores das constantes de status. Como esses codigos sao
 * gravados no banco e comparados em varios JSP/servlets, mudar um valor sem
 * querer quebraria fluxos silenciosamente — este teste pega isso.
 */
public class StatusIdeiaTest {

	@Test
	public void statusDaIdeia() {
		assertEquals("PE", StatusIdeia.PENDENTE);
		assertEquals("VA", StatusIdeia.VALIDADA);
		assertEquals("RE", StatusIdeia.REJEITADA);
		assertEquals("DE", StatusIdeia.EM_DESENVOLVIMENTO);
		assertEquals("ST", StatusIdeia.STORYTELLING);
		assertEquals("CF", StatusIdeia.CAIXA_FERRAMENTAS);
		assertEquals("CV", StatusIdeia.CANVAS);
		assertEquals("FN", StatusIdeia.FINALIZADO);
	}

	@Test
	public void statusDoGrupo() {
		assertEquals("AB", StatusIdeia.GRUPO_ABERTO);
		assertEquals("FE", StatusIdeia.GRUPO_FECHADO);
	}
}
