package edu.unisc.lic.util;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * CSRF-02: protecao CSRF por token (padrao "double-submit cookie").
 *
 * O servidor mantem um token num cookie XSRF-TOKEN (NAO HttpOnly, para o csrf.js
 * conseguir ler). Em todo POST, o filtro compara o param 'csrfToken' (ou o header
 * X-CSRF-Token) com o cookie: um site atacante nao consegue ler o cookie do usuario
 * nem forjar o param/header igual, entao POST cross-site e barrado (403). Defesa em
 * profundidade alem do SameSite=Lax do cookie de sessao.
 *
 * O campo escondido dos forms e o header das chamadas AJAX sao adicionados pelo
 * resources/js/csrf.js (incluido em todas as paginas), sem precisar editar cada form.
 */
public class CsrfFilter implements Filter {

	private static final String COOKIE = "XSRF-TOKEN";
	private static final String PARAM = "csrfToken";
	private static final SecureRandom RNG = new SecureRandom();

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;

		String token = readCookie(request);
		if (token == null) {
			token = new BigInteger(130, RNG).toString(32);
			Cookie c = new Cookie(COOKIE, token);
			c.setPath(request.getContextPath().isEmpty() ? "/" : request.getContextPath());
			response.addCookie(c);
		}
		request.setAttribute("csrfToken", token);

		if ("POST".equalsIgnoreCase(request.getMethod())) {
			String sent = request.getParameter(PARAM);
			if (sent == null) {
				sent = request.getHeader("X-CSRF-Token");
			}
			if (sent == null || !sent.equals(token)) {
				response.sendError(HttpServletResponse.SC_FORBIDDEN, "CSRF token invalido ou ausente.");
				return;
			}
		}

		chain.doFilter(req, resp);
	}

	private String readCookie(HttpServletRequest request) {
		if (request.getCookies() != null) {
			for (Cookie c : request.getCookies()) {
				if (COOKIE.equals(c.getName()) && c.getValue() != null && !c.getValue().isEmpty()) {
					return c.getValue();
				}
			}
		}
		return null;
	}
}
