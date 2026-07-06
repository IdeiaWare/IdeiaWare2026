package edu.unisc.lic.classes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * TEST-04: AssinaturaCaixa.assinar() e a base do SEC-23 (impede forjar o cookie
 * ideiaId entre LIC e Toolkit). Logica pura (HMAC-SHA256), sem banco -- barata e de
 * alto valor: se a assinatura mudar de comportamento sem querer, essa protecao
 * degrada silenciosamente.
 */
public class AssinaturaCaixaTest {

	@Test
	public void assinar_mesmaEntrada_geraMesmaAssinatura() {
		assertEquals(AssinaturaCaixa.assinar("42"), AssinaturaCaixa.assinar("42"));
	}

	@Test
	public void assinar_entradasDiferentes_geramAssinaturasDiferentes() {
		assertNotEquals(AssinaturaCaixa.assinar("42"), AssinaturaCaixa.assinar("43"));
	}

	@Test
	public void assinar_retornaHexDe64Caracteres() {
		// HMAC-SHA256 = 32 bytes = 64 caracteres hexadecimais
		String assinatura = AssinaturaCaixa.assinar("100");
		assertNotNull(assinatura);
		assertEquals(64, assinatura.length());
		assertTrue("deve conter so digitos hexadecimais", assinatura.matches("[0-9a-f]{64}"));
	}

	@Test
	public void assinar_valorVazio_naoLancaExcecao() {
		String assinatura = AssinaturaCaixa.assinar("");
		assertNotNull(assinatura);
		assertEquals(64, assinatura.length());
	}
}
