package br.unisc.toolkit.classes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

// TEST-04: AssinaturaCaixa.valida() e a base do SEC-23, nunca tinha teste direto.
public class AssinaturaCaixaTest {

	@Test
	public void assinar_mesmoValor_produzSempreAMesmaAssinatura() {
		assertEquals(AssinaturaCaixa.assinar("5"), AssinaturaCaixa.assinar("5"));
	}

	@Test
	public void assinar_valoresDiferentes_produzAssinaturasDiferentes() {
		assertNotEquals(AssinaturaCaixa.assinar("5"), AssinaturaCaixa.assinar("6"));
	}

	@Test
	public void valida_assinaturaCorreta_retornaTrue() {
		assertTrue(AssinaturaCaixa.valida("5", AssinaturaCaixa.assinar("5")));
	}

	@Test
	public void valida_assinaturaForjada_retornaFalse() { // SEC-23
		assertFalse(AssinaturaCaixa.valida("5", "qualquer-coisa-forjada"));
	}

	@Test
	public void valida_assinaturaDeOutroValor_retornaFalse() { // SEC-23: reaproveitar assinatura de outro ideiaId
		assertFalse(AssinaturaCaixa.valida("5", AssinaturaCaixa.assinar("6")));
	}

	@Test
	public void valida_valorNull_retornaFalse() {
		assertFalse(AssinaturaCaixa.valida(null, AssinaturaCaixa.assinar("5")));
	}

	@Test
	public void valida_assinaturaNull_retornaFalse() {
		assertFalse(AssinaturaCaixa.valida("5", null));
	}

	@Test
	public void valida_assinaturaVazia_retornaFalse() {
		assertFalse(AssinaturaCaixa.valida("5", ""));
	}
}
