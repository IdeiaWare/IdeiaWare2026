package br.unisc.toolkit.classes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class AdminCookies {
	public Long getCookieIdeiaCodigo(HttpServletRequest request){
		Long id = null;
		String valor = null;
		String sig = null;

		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
		 for (Cookie cookie : cookies) {
		   if ("ideiaId".equals(cookie.getName())) {
		       valor = cookie.getValue();
		   } else if ("ideiaSig".equals(cookie.getName())) {
		       sig = cookie.getValue();
		   }
		  }
		}

		if (valor != null && !valor.trim().isEmpty()) {
		    // SEC-23: so aceita o ideiaId se a ASSINATURA (HMAC, posta pelo LIC ao entrar
		    // na Caixa) bater -> o cookie ideiaId deixa de ser forjavel. Sem assinatura
		    // valida = sem acesso (retorna null -> os controllers caem no "redirect").
		    if (!AssinaturaCaixa.valida(valor.trim(), sig)) {
		        return null;
		    }
		    // TK-01: trata vazio/nao-numerico sem quebrar (NumberFormatException).
		    try {
		        id = Long.valueOf(valor.trim());
		    } catch (NumberFormatException e) {
		        id = null;
		    }
		}

		return id;
	}
}
