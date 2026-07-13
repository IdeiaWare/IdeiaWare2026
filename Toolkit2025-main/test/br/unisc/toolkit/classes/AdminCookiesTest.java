package br.unisc.toolkit.classes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.junit.Test;

// TEST-02: leitura do cookie ideiaId -- trava a blindagem TK-01 (cookie vazio/nao-numerico nao lanca NumberFormatException).
public class AdminCookiesTest {

	private final AdminCookies cookies = new AdminCookies();

	private HttpServletRequest reqCom(Cookie... cks) {
		HttpServletRequest req = mock(HttpServletRequest.class);
		when(req.getCookies()).thenReturn(cks.length == 0 ? null : cks);
		return req;
	}

	@Test
	public void cookieValido_retornaLong() {
		assertEquals(Long.valueOf(5), cookies.getCookieIdeiaCodigo(reqCom(
				new Cookie("ideiaId", "5"), new Cookie("ideiaSig", AssinaturaCaixa.assinar("5")))));
	}

	@Test
	public void semCookies_retornaNull() {
		assertNull(cookies.getCookieIdeiaCodigo(reqCom()));
	}

	@Test
	public void cookieVazio_retornaNull() { // TK-01
		assertNull(cookies.getCookieIdeiaCodigo(reqCom(new Cookie("ideiaId", ""))));
	}

	@Test
	public void cookieNaoNumerico_naoLancaERetornaNull() { // TK-01 -- assinatura valida p/ realmente exercitar o catch de NumberFormatException
		assertNull(cookies.getCookieIdeiaCodigo(reqCom(
				new Cookie("ideiaId", "abc"), new Cookie("ideiaSig", AssinaturaCaixa.assinar("abc")))));
	}

	@Test
	public void assinaturaAusente_retornaNull() { // SEC-23: ideiaId numerico valido, sem ideiaSig -- cookie forjado
		assertNull(cookies.getCookieIdeiaCodigo(reqCom(new Cookie("ideiaId", "5"))));
	}

	@Test
	public void assinaturaErrada_retornaNull() { // SEC-23: ideiaSig nao bate com nenhum HMAC valido
		assertNull(cookies.getCookieIdeiaCodigo(reqCom(
				new Cookie("ideiaId", "5"), new Cookie("ideiaSig", "assinatura-forjada"))));
	}

	@Test
	public void assinaturaDeOutroValor_retornaNull() { // SEC-23: ideiaSig valido, mas assinando um ideiaId DIFERENTE
		assertNull(cookies.getCookieIdeiaCodigo(reqCom(
				new Cookie("ideiaId", "5"), new Cookie("ideiaSig", AssinaturaCaixa.assinar("6")))));
	}

	@Test
	public void cookieComEspacos_trimEParseia() {
		assertEquals(Long.valueOf(7), cookies.getCookieIdeiaCodigo(reqCom(
				new Cookie("ideiaId", "  7  "), new Cookie("ideiaSig", AssinaturaCaixa.assinar("7")))));
	}

	@Test
	public void outroCookie_ignorado() {
		assertNull(cookies.getCookieIdeiaCodigo(reqCom(new Cookie("outro", "5"))));
	}
}
