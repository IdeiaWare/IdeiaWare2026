package br.unisc.toolkit.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.unisc.toolkit.classes.AdminCookies;
import br.unisc.toolkit.classes.Constantes;
import br.unisc.toolkit.classes.StatusGuard;
import br.unisc.toolkit.entity.Ideia;
import br.unisc.toolkit.entity.Persona;
import br.unisc.toolkit.service.IdeiaService;

@Controller
@RequestMapping("/ideia")
public class IdeiaController {

	@Autowired
	private IdeiaService ideiaService;

	AdminCookies cookie = new AdminCookies();


	// TK-26: virou POST (link fica no header de toda pagina do modulo).
	@PostMapping("/finalize")
	public String finalizeIdeia(HttpServletRequest request, RedirectAttributes redirectAttrs){
		if(cookie.getCookieIdeiaCodigo(request) != null){
			Long ideiaCodigo = cookie.getCookieIdeiaCodigo(request);

			Ideia theIdeia = ideiaService.getIdeia(ideiaCodigo);

			// TK-02: evita NPE se a ideia do cookie nao existir mais.
			if (theIdeia == null) {
				return "redirect";
			}

			// UX-TOOLKIT-FINALIZE-TRAVADO: bloqueia finalizar fora da etapa Caixa de Ferramentas.
			// UX-PADRAO-ETAPA-FINALIZADA: antes caia na view generica "redirect", que termina no login.
			if (!StatusGuard.podeEscrever(ideiaService, ideiaCodigo)) {
				redirectAttrs.addFlashAttribute("etapaEncerradaErro", "Esta etapa já foi encerrada.");
				redirectAttrs.addFlashAttribute("redirecionarPara", Constantes.paginaMinhaIdeia(request));
				return "redirect:/aviso-etapa-encerrada";
			}

			ideiaService.finalize(theIdeia);

			// TK-11: tela de confirmacao de finalizacao.
			return "finalize";

		}else {
			return "redirect";
		}
	}
}
