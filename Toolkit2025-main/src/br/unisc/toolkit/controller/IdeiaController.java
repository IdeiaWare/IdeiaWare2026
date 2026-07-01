package br.unisc.toolkit.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.unisc.toolkit.classes.AdminCookies;
import br.unisc.toolkit.entity.Ideia;
import br.unisc.toolkit.entity.Persona;
import br.unisc.toolkit.service.IdeiaService;

@Controller
@RequestMapping("/ideia")
public class IdeiaController {

	@Autowired
	private IdeiaService ideiaService;
	
	AdminCookies cookie = new AdminCookies();
	
	
	@GetMapping("/finalize")
	public String finalizeIdeia(HttpServletRequest request){
		if(cookie.getCookieIdeiaCodigo(request) != null){
			Long ideiaCodigo = cookie.getCookieIdeiaCodigo(request);

			Ideia theIdeia = ideiaService.getIdeia(ideiaCodigo);

			// TK-02: se a ideia do cookie nao existir mais, evita NPE no finalize.
			if (theIdeia == null) {
				return "redirect";
			}

			ideiaService.finalize(theIdeia);

			// Mostra a tela de confirmacao "Caixa de Ferramentas finalizada"; o botao dela
			// leva para "Minhas Ideias" do LIC (ver finalize.jsp).
			return "finalize";

		}else {
			return "redirect";
		}
	}
}
