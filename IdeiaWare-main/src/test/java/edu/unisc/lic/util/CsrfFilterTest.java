package edu.unisc.lic.util;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.FilterChain;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

// TEST-04: CsrfFilter roda em toda rota -- cobre os 2 lados do double-submit cookie (bloqueio sem token + liberacao com token valido).
public class CsrfFilterTest {

	private static final String COOKIE_NOME = "XSRF-TOKEN";
	private static final String TOKEN_VALIDO = "abc123";
	private static final String PARAM_TOKEN = "csrfToken";

	private final CsrfFilter filtro = new CsrfFilter();

	@Test
	public void get_semCookieAinda_criaCookieENaoBloqueia() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);

		when(request.getMethod()).thenReturn("GET");
		when(request.getCookies()).thenReturn(null);
		when(request.getContextPath()).thenReturn("/LIC");

		filtro.doFilter(request, response, chain);

		ArgumentCaptor<Cookie> captor = ArgumentCaptor.forClass(Cookie.class);
		verify(response, times(1)).addCookie(captor.capture());
		assertEquals(COOKIE_NOME, captor.getValue().getName());
		verify(chain, times(1)).doFilter(request, response);
		verify(response, never()).sendError(anyInt(), anyString());
	}

	@Test
	public void post_comTokenCorretoNoParametro_naoBloqueia() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);

		when(request.getCookies()).thenReturn(new Cookie[]{new Cookie(COOKIE_NOME, TOKEN_VALIDO)});
		when(request.getMethod()).thenReturn("POST");
		when(request.getParameter(PARAM_TOKEN)).thenReturn(TOKEN_VALIDO);

		filtro.doFilter(request, response, chain);

		verify(chain, times(1)).doFilter(request, response);
		verify(response, never()).sendError(anyInt(), anyString());
	}

	@Test
	public void post_comTokenCorretoNoHeader_naoBloqueia() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);

		when(request.getCookies()).thenReturn(new Cookie[]{new Cookie(COOKIE_NOME, TOKEN_VALIDO)});
		when(request.getMethod()).thenReturn("POST");
		when(request.getParameter(PARAM_TOKEN)).thenReturn(null);
		when(request.getHeader("X-CSRF-Token")).thenReturn(TOKEN_VALIDO);

		filtro.doFilter(request, response, chain);

		verify(chain, times(1)).doFilter(request, response);
	}

	@Test
	public void post_semToken_bloqueiaCom403ENaoChamaChain() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);

		when(request.getCookies()).thenReturn(new Cookie[]{new Cookie(COOKIE_NOME, TOKEN_VALIDO)});
		when(request.getMethod()).thenReturn("POST");
		when(request.getParameter(PARAM_TOKEN)).thenReturn(null);
		when(request.getHeader("X-CSRF-Token")).thenReturn(null);

		filtro.doFilter(request, response, chain);

		verify(response, times(1)).sendError(eq(HttpServletResponse.SC_FORBIDDEN), anyString());
		verify(chain, never()).doFilter(request, response);
	}

	@Test
	public void post_comTokenErrado_bloqueiaCom403() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);

		when(request.getCookies()).thenReturn(new Cookie[]{new Cookie(COOKIE_NOME, TOKEN_VALIDO)});
		when(request.getMethod()).thenReturn("POST");
		when(request.getParameter(PARAM_TOKEN)).thenReturn("token-forjado");

		filtro.doFilter(request, response, chain);

		verify(response, times(1)).sendError(eq(HttpServletResponse.SC_FORBIDDEN), anyString());
		verify(chain, never()).doFilter(request, response);
	}
}
