package br.unisc.toolkit.classes;

import java.math.BigInteger;
import java.security.SecureRandom;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

// CSRF-01: double-submit cookie contra XSRF-TOKEN em todo POST.
public class CsrfInterceptor extends HandlerInterceptorAdapter {

	private static final String COOKIE = "XSRF-TOKEN";
	private static final String PARAM = "csrfToken";
	private static final SecureRandom RNG = new SecureRandom();

	@Override
	public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {
		String token = readCookie(req);
		if (token == null) {
			token = new BigInteger(130, RNG).toString(32);
			Cookie c = new Cookie(COOKIE, token);
			c.setHttpOnly(true);
			c.setPath(req.getContextPath().isEmpty() ? "/" : req.getContextPath());
			resp.addCookie(c);
		}
		req.setAttribute("csrfToken", token);

		// TK-25: valida GET/HEAD tambem, pois Spring despacha HEAD pro @GetMapping.
		// TK-24: barra final removida antes de comparar (bypassava o endsWith).
		String uri = req.getRequestURI();
		if (uri.endsWith("/")) {
			uri = uri.substring(0, uri.length() - 1);
		}
		String method = req.getMethod();
		boolean stateChanging = "POST".equalsIgnoreCase(method)
				|| (("GET".equalsIgnoreCase(method) || "HEAD".equalsIgnoreCase(method))
						&& (uri.contains("/deletar") || uri.endsWith("/delete")
								|| uri.endsWith("/finalize")));
		if (stateChanging) {
			String sent = req.getParameter(PARAM);
			if (sent == null) {
				sent = req.getHeader("X-CSRF-Token");
			}
			if (sent == null || !sent.equals(token)) {
				resp.sendError(HttpServletResponse.SC_FORBIDDEN, "CSRF token invalido ou ausente.");
				return false;
			}
		}
		return true;
	}

	private String readCookie(HttpServletRequest req) {
		if (req.getCookies() != null) {
			for (Cookie c : req.getCookies()) {
				if (COOKIE.equals(c.getName()) && c.getValue() != null && !c.getValue().isEmpty()) {
					return c.getValue();
				}
			}
		}
		return null;
	}
}
