package br.unisc.toolkit.classes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.junit.Test;

/**
 * TEST-02: leitura do cookie ideiaId (AdminCookies). Trava a blindagem TK-01
 * (cookie vazio / nao-numerico nao deve lancar NumberFormatException).
 */
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
	public void cookieNaoNumerico_naoLancaERetornaNull() { // TK-01
		assertNull(cookies.getCookieIdeiaCodigo(reqCom(new Cookie("ideiaId", "abc"))));
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
