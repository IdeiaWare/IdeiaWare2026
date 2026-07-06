package edu.unisc.lic.util;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;

/**
 * TEST-04 (2026-07-06): SecurityHeadersFilter (SEC-26) -- roda em toda rota, seta
 * Referrer-Policy e sempre segue a cadeia (nunca bloqueia nada).
 */
public class SecurityHeadersFilterTest {

	private final SecurityHeadersFilter filtro = new SecurityHeadersFilter();

	@Test
	public void doFilter_setaReferrerPolicyEChamaChain() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);

		filtro.doFilter(request, response, chain);

		verify(response, times(1)).setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
		verify(chain, times(1)).doFilter(request, response);
	}
}
