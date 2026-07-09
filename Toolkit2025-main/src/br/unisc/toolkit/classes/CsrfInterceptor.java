package br.unisc.toolkit.classes;

import java.math.BigInteger;
import java.security.SecureRandom;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * CSRF-01: protecao CSRF por token (padrao "double-submit cookie").
 *
 * O token vive num cookie HttpOnly (XSRF-TOKEN). Em toda requisicao o servidor
 * coloca o MESMO valor num atributo de request ('csrfToken') -> os forms renderizam
 * esse valor num <input hidden name="csrfToken">. Em todo POST o param 'csrfToken'
 * (ou o header X-CSRF-Token) e comparado ao cookie: como um site atacante nao
 * consegue LER o cookie nem forjar o param igual, um POST cross-site e barrado.
 * E defesa-em-profundidade alem do SameSite=Lax do cookie de sessao.
 */
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

		// Valida POST e tambem os GET que MUDAM estado (deletes via link), que o
		// SameSite=Lax nao protege em navegacao top-level.
		// REVISAO 2026-07-08 (varredura Toolkit, achado MEDIA): HEAD tambem precisa
		// entrar aqui -- o Spring despacha HEAD pro MESMO @GetMapping do GET (o metodo
		// roda inteiro, so o corpo da resposta e descartado), entao sem isso um
		// DELETE/finalize EXECUTAVA DE VERDADE numa requisicao HEAD sem checar token
		// (crawler/link-checker same-site que usa HEAD disparava exclusao sem querer).
		// REVISAO 2026-07-08 (varredura Toolkit, achado ALTA): bypass via barra final na
		// URL -- o Spring 5.3.x (trailing-slash-match=true por padrao, sem config
		// customizando) roteia ".../delete/?..." pro MESMO handler de ".../delete", mas
		// o endsWith("/delete") abaixo nao batia, pulando a checagem de token. Barra
		// final removida ANTES de comparar (.contains() ja nao sofria disso).
		String uri = req.getRequestURI();
		if (uri.endsWith("/")) {
			uri = uri.substring(0, uri.length() - 1);
		}
		String method = req.getMethod();
		boolean stateChanging = "POST".equalsIgnoreCase(method)
				|| (("GET".equalsIgnoreCase(method) || "HEAD".equalsIgnoreCase(method))
						&& (uri.contains("/deletar") || uri.endsWith("/delete")
								|| uri.endsWith("/finalize")));   // /finalize muda o status da ideia
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
