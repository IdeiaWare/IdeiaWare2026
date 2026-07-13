package br.unisc.toolkit.classes;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;

// TEST-02: logica do CsrfInterceptor -- trava CSRF-01 (POST/GET-delete exigem token igual ao cookie; GET comum so gera o cookie).
public class CsrfInterceptorTest {

	private CsrfInterceptor interceptor;
	private HttpServletRequest req;
	private HttpServletResponse resp;

	@Before
	public void setup() {
		interceptor = new CsrfInterceptor();
		req = mock(HttpServletRequest.class);
		resp = mock(HttpServletResponse.class);
		when(req.getRequestURI()).thenReturn("/toolkit/persona/lista");
		when(req.getContextPath()).thenReturn("/toolkit");
	}

	private void comCookie(String token) {
		when(req.getCookies()).thenReturn(new Cookie[] { new Cookie("XSRF-TOKEN", token) });
	}

	@Test
	public void get_semCookie_geraCookieEPassa() throws Exception {
		when(req.getMethod()).thenReturn("GET");
		when(req.getCookies()).thenReturn(null);
		assertTrue(interceptor.preHandle(req, resp, null));
		verify(resp).addCookie(any(Cookie.class));            // gerou o XSRF-TOKEN
		verify(req).setAttribute(eq("csrfToken"), anyString()); // exposto p/ os forms
	}

	@Test
	public void post_tokenCorreto_passa() throws Exception {
		when(req.getMethod()).thenReturn("POST");
		comCookie("tok123");
		when(req.getParameter("csrfToken")).thenReturn("tok123");
		assertTrue(interceptor.preHandle(req, resp, null));
		verify(resp, never()).sendError(anyInt(), anyString());
	}

	@Test
	public void post_semToken_403() throws Exception {
		when(req.getMethod()).thenReturn("POST");
		comCookie("tok123");
		when(req.getParameter("csrfToken")).thenReturn(null);
		when(req.getHeader("X-CSRF-Token")).thenReturn(null);
		assertFalse(interceptor.preHandle(req, resp, null));
		verify(resp).sendError(eq(HttpServletResponse.SC_FORBIDDEN), anyString());
	}

	@Test
	public void post_tokenErrado_403() throws Exception {
		when(req.getMethod()).thenReturn("POST");
		comCookie("tok123");
		when(req.getParameter("csrfToken")).thenReturn("ERRADO");
		assertFalse(interceptor.preHandle(req, resp, null));
		verify(resp).sendError(eq(HttpServletResponse.SC_FORBIDDEN), anyString());
	}

	@Test
	public void getDelete_comToken_passa() throws Exception {
		when(req.getMethod()).thenReturn("GET");
		when(req.getRequestURI()).thenReturn("/toolkit/persona/deletar");
		comCookie("tok123");
		when(req.getParameter("csrfToken")).thenReturn("tok123");
		assertTrue(interceptor.preHandle(req, resp, null));
	}

	@Test
	public void getDelete_semToken_403() throws Exception {
		when(req.getMethod()).thenReturn("GET");
		when(req.getRequestURI()).thenReturn("/toolkit/persona/deletar");
		comCookie("tok123");
		when(req.getParameter("csrfToken")).thenReturn(null);
		when(req.getHeader("X-CSRF-Token")).thenReturn(null);
		assertFalse(interceptor.preHandle(req, resp, null));
		verify(resp).sendError(eq(HttpServletResponse.SC_FORBIDDEN), anyString());
	}

	// TK-24: barra final na URI pulava o endsWith("/delete") e escapava da checagem de token.
	@Test
	public void getFinalizeComBarraFinal_semToken_403() throws Exception {
		when(req.getMethod()).thenReturn("GET");
		when(req.getRequestURI()).thenReturn("/toolkit/ideia/finalize/");
		comCookie("tok123");
		when(req.getParameter("csrfToken")).thenReturn(null);
		when(req.getHeader("X-CSRF-Token")).thenReturn(null);
		assertFalse(interceptor.preHandle(req, resp, null));
		verify(resp).sendError(eq(HttpServletResponse.SC_FORBIDDEN), anyString());
	}

	// TK-25: Spring despacha HEAD pro mesmo @GetMapping do GET -- sem o fix, HEAD escapava da checagem.
	@Test
	public void headDelete_semToken_403() throws Exception {
		when(req.getMethod()).thenReturn("HEAD");
		when(req.getRequestURI()).thenReturn("/toolkit/persona/deletar");
		comCookie("tok123");
		when(req.getParameter("csrfToken")).thenReturn(null);
		when(req.getHeader("X-CSRF-Token")).thenReturn(null);
		assertFalse(interceptor.preHandle(req, resp, null));
		verify(resp).sendError(eq(HttpServletResponse.SC_FORBIDDEN), anyString());
	}
}
