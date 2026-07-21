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
		    // SEC-23: so aceita o ideiaId se a assinatura HMAC bater.
		    if (!AssinaturaCaixa.valida(valor.trim(), sig)) {
		        return null;
		    }
		    // TK-01: trata vazio/nao-numerico sem quebrar.
		    try {
		        id = Long.valueOf(valor.trim());
		    } catch (NumberFormatException e) {
		        id = null;
		    }
		}

		return id;
	}
}
