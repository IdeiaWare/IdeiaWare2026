package br.unisc.toolkit.classes;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * TEST-01: testes da validacao server-side do Toolkit (Onda 1). Logica pura,
 * sem Spring/banco. Trava as regras de persona (nome/idade), atributo de empatia
 * e POV contra regressao.
 */
public class ToolkitValidacaoTest {

	@Test
	public void textoValido_nulosVaziosEEspacos() {
		assertFalse(ToolkitValidacao.textoValido(null, 10));
		assertFalse(ToolkitValidacao.textoValido("", 10));
		assertFalse(ToolkitValidacao.textoValido("   ", 10)); // so espacos -> trim vazio
	}

	@Test
	public void textoValido_ok() {
		assertTrue(ToolkitValidacao.textoValido("ok", 10));
		assertTrue(ToolkitValidacao.textoValido("  ok  ", 10)); // trim
	}

	@Test
	public void textoValido_respeitaMaxLen() {
		assertTrue(ToolkitValidacao.textoValido("1234567890", 10));   // 10 == 10
		assertFalse(ToolkitValidacao.textoValido("12345678901", 10)); // 11 > 10
	}

	@Test
	public void idadeValida_faixa1a120() {
		assertFalse(ToolkitValidacao.idadeValida(0));
		assertFalse(ToolkitValidacao.idadeValida(-5));
		assertTrue(ToolkitValidacao.idadeValida(1));
		assertTrue(ToolkitValidacao.idadeValida(30));
		assertTrue(ToolkitValidacao.idadeValida(120));
		assertFalse(ToolkitValidacao.idadeValida(121));
	}

	@Test
	public void algumTextoValido_exigePeloMenosUm() {
		assertFalse(ToolkitValidacao.algumTextoValido());
		assertFalse(ToolkitValidacao.algumTextoValido((String[]) null));
		assertFalse(ToolkitValidacao.algumTextoValido(null, "", "   "));
		assertTrue(ToolkitValidacao.algumTextoValido(null, "x", ""));
	}
}
