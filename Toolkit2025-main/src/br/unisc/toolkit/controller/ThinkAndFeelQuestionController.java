package br.unisc.toolkit.controller;

import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.unisc.toolkit.classes.AdminCookies;
import br.unisc.toolkit.classes.Constantes;
import br.unisc.toolkit.classes.StatusGuard;
import br.unisc.toolkit.classes.ToolkitValidacao;
import org.springframework.validation.BindingResult;
import br.unisc.toolkit.entity.Empathy;
import br.unisc.toolkit.entity.Persona;
import br.unisc.toolkit.service.IdeiaService;
import br.unisc.toolkit.service.EmpathyService;
import br.unisc.toolkit.service.PersonaService;

@Controller
@RequestMapping("/persona/empatia")
public class ThinkAndFeelQuestionController {

	@Autowired
	private EmpathyService empathyService;

	@Autowired
	private IdeiaService ideiaService;

	AdminCookies cookie = new AdminCookies();
	
	@GetMapping("/o-que-pensa-e-sente")
	public String showThinkAndFeelQuestion(@RequestParam("personaId") int theId, Model theModel, HttpServletRequest request){
		Long ideiaCodigo = Long.valueOf(0);
		
		if(cookie.getCookieIdeiaCodigo(request) != null){
			ideiaCodigo = cookie.getCookieIdeiaCodigo(request);
			
			List<Empathy> theEmpaties = empathyService.getAttributes(theId, "think_feel", ideiaCodigo);

			Empathy theEmpathy = new Empathy();
					
			theModel.addAttribute("pageTitle", "O que Pensa e Sente");
			theModel.addAttribute("attribute", theEmpathy);
			theModel.addAttribute("personaId", theId);
			
			theModel.addAttribute("attributes", theEmpaties);
			
			return "think-and-feel";
		}
		else{
			theModel.addAttribute("pageTitle", "Erro");
			return "redirect";
		}		
	}
	
	@PostMapping("/o-que-pensa-e-sente/save-attribute")
	public String saveAttribute(@ModelAttribute("attribute") Empathy theEmpathy, BindingResult result, HttpServletRequest request, RedirectAttributes redirectAttrs){
		if(cookie.getCookieIdeiaCodigo(request) != null){
		   // UX-TOOLKIT-STATUS-GUARD: bloqueia escrita fora da etapa Caixa de Ferramentas.
		   if (!StatusGuard.podeEscrever(ideiaService, cookie.getCookieIdeiaCodigo(request))) {
			   // UX-PADRAO-ETAPA-FINALIZADA: antes redirecionava sem nenhum aviso.
			   redirectAttrs.addFlashAttribute("etapaEncerradaErro", "Esta etapa já foi encerrada.");
			   redirectAttrs.addFlashAttribute("redirecionarPara", Constantes.paginaMinhaIdeia(request));
			   return "redirect:/aviso-etapa-encerrada";
		   }
		   // TK-VAL: backstop server-side.
			if (result.hasErrors() || !ToolkitValidacao.textoValido(theEmpathy.getAttributeText(), 10000)) {
				return "redirect:/persona/empatia/mapa?personaId=" + theEmpathy.getPersonaId();
			}
			theEmpathy.setIdeiaCodigo(cookie.getCookieIdeiaCodigo(request));
			// TK-ATTR: forca "think_feel" no servidor.
			theEmpathy.setAttribute("think_feel");

			empathyService.saveEmpathyAttribute(theEmpathy);
				
			return "redirect:/persona/empatia/o-que-pensa-e-sente?personaId=" + theEmpathy.getPersonaId();
		}
		else{
			return "redirect";
		}
	}
	
	// TK-26: virou POST.
	@PostMapping("/o-que-pensa-e-sente/delete")
	public String deleteAttribute(@RequestParam("personaId") int personaId,
								@RequestParam("attributeId") int attributeId,
								Model theModel, HttpServletRequest request, RedirectAttributes redirectAttrs)
	{
		// TK-03: exige cookie de ideia contra IDOR.
		// UX-TOOLKIT-STATUS-GUARD: bloqueia escrita fora da etapa Caixa de Ferramentas.
		if(cookie.getCookieIdeiaCodigo(request) != null){
			if (StatusGuard.podeEscrever(ideiaService, cookie.getCookieIdeiaCodigo(request))) {
				empathyService.deleteAttribute(attributeId, cookie.getCookieIdeiaCodigo(request));
			} else {
				// UX-PADRAO-ETAPA-FINALIZADA: antes falhava em silencio (sem toast, sem excluir).
				redirectAttrs.addFlashAttribute("etapaEncerradaErro", "Esta etapa já foi encerrada.");
				redirectAttrs.addFlashAttribute("redirecionarPara", Constantes.paginaMinhaIdeia(request));
				return "redirect:/aviso-etapa-encerrada";
			}
		}

		return "redirect:/persona/empatia/o-que-pensa-e-sente?personaId=" + personaId;
	}
}
