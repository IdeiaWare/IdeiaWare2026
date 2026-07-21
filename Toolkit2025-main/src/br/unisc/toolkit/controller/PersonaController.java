package br.unisc.toolkit.controller;

import java.util.List;

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
import br.unisc.toolkit.entity.Persona;

import br.unisc.toolkit.service.IdeiaService;
import br.unisc.toolkit.service.PersonaService;

@Controller
@RequestMapping("/persona")
public class PersonaController {

	@Autowired
	private PersonaService personaService;

	@Autowired
	private IdeiaService ideiaService;

	AdminCookies cookie = new AdminCookies();
	
	@GetMapping("/lista")
	public String listPersonas(Model theModel, HttpServletRequest request){
		Long ideiaCodigo = Long.valueOf(0);
		
		if(cookie.getCookieIdeiaCodigo(request) != null){
			ideiaCodigo = cookie.getCookieIdeiaCodigo(request);
			
			List<Persona> thePersonas = personaService.getPersonas(ideiaCodigo);

			theModel.addAttribute("pageTitle", "Personas - Lista");
			theModel.addAttribute("personas", thePersonas);
			
			Persona thePersona = new Persona();
			theModel.addAttribute("persona", thePersona);
			
			return "list-personas";
		}
		else{
			theModel.addAttribute("pageTitle", "Erro");
			return "redirect";
		}
	}
	
	@PostMapping("/salvar-persona")
	public String savePersona(@ModelAttribute("persona") Persona thePersona, BindingResult result, HttpServletRequest request, RedirectAttributes redirectAttrs){
		if(cookie.getCookieIdeiaCodigo(request) != null){
			// UX-TOOLKIT-STATUS-GUARD: bloqueia escrita fora da etapa Caixa de Ferramentas.
			if (!StatusGuard.podeEscrever(ideiaService, cookie.getCookieIdeiaCodigo(request))) {
				redirectAttrs.addFlashAttribute("etapaEncerradaErro", "Esta etapa já foi encerrada.");
				redirectAttrs.addFlashAttribute("redirecionarPara", Constantes.paginaMinhaIdeia(request));
				return "redirect:/aviso-etapa-encerrada";
			}
			// TK-VAL: backstop server-side.
			if (result.hasErrors() || !ToolkitValidacao.textoValido(thePersona.getName(), 45)
					|| !ToolkitValidacao.idadeValida(thePersona.getAge())) {
				redirectAttrs.addFlashAttribute("toastErro", "Não foi possível salvar a persona. Verifique o nome e a idade.");
				return "redirect:/persona/lista";
			}
			thePersona.setIdeiaCodigo(cookie.getCookieIdeiaCodigo(request));

			personaService.savePersona(thePersona);

			redirectAttrs.addFlashAttribute("toastOk", "Persona salva com sucesso.");
			return "redirect:/persona/lista";
		}
		else{
			return "redirect";
		}	
	}
	
	// TK-26: virou POST.
	@PostMapping("/deletar")
	public String deletePersona(@RequestParam("personaId") int theId, Model theModel, HttpServletRequest request, RedirectAttributes redirectAttrs){
		// TK-03: exige cookie de ideia contra IDOR.
		// UX-TOOLKIT-STATUS-GUARD: bloqueia escrita fora da etapa Caixa de Ferramentas.
		if(cookie.getCookieIdeiaCodigo(request) != null){
			if (StatusGuard.podeEscrever(ideiaService, cookie.getCookieIdeiaCodigo(request))) {
				personaService.deletePersona(theId, cookie.getCookieIdeiaCodigo(request));
			} else {
				// UX-PADRAO-ETAPA-FINALIZADA: antes falhava em silencio (sem toast, sem excluir).
				redirectAttrs.addFlashAttribute("etapaEncerradaErro", "Esta etapa já foi encerrada.");
				redirectAttrs.addFlashAttribute("redirecionarPara", Constantes.paginaMinhaIdeia(request));
				return "redirect:/aviso-etapa-encerrada";
			}
		}

		return "redirect:/persona/lista";
	}
	
	@GetMapping("/empatia/mapa")
	public String showPersonaPreData(@RequestParam("personaId") int theId, Model theModel, HttpServletRequest request){
		Long ideiaCodigo = Long.valueOf(0);
		
		if(cookie.getCookieIdeiaCodigo(request) != null){
			 ideiaCodigo = cookie.getCookieIdeiaCodigo(request);
			 
			Persona thePersona = personaService.getPersona(theId, ideiaCodigo);

			// TK-02: getPersona() pode voltar null.
			if (thePersona == null) {
				theModel.addAttribute("pageTitle", "Erro");
				return "redirect";
			}

			theModel.addAttribute("pageTitle", "Mapa da Empatia");
			
			theModel.addAttribute("persona", thePersona);
					
			return "empathy-map";
		}
		else{
			theModel.addAttribute("pageTitle", "Erro");
			return "redirect";
		}
	}
}
