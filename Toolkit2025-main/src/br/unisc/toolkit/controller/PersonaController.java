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
import br.unisc.toolkit.classes.ToolkitValidacao;
import org.springframework.validation.BindingResult;
import br.unisc.toolkit.entity.Persona;

import br.unisc.toolkit.service.PersonaService;

@Controller
@RequestMapping("/persona")
public class PersonaController {
	
	// need to inject our persona service
	@Autowired
	private PersonaService personaService;
	
	AdminCookies cookie = new AdminCookies();
	
	@GetMapping("/lista")
	public String listPersonas(Model theModel, HttpServletRequest request){
		Long ideiaCodigo = Long.valueOf(0);
		
		if(cookie.getCookieIdeiaCodigo(request) != null){
			ideiaCodigo = cookie.getCookieIdeiaCodigo(request);
			
			// get personas from the service
			List<Persona> thePersonas = personaService.getPersonas(ideiaCodigo);
			
			// add the personas to the model
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
			// TK-VAL: backstop server-side (client-side e burlavel); BindingResult captura idade nao-numerica (evita 400).
			if (result.hasErrors() || !ToolkitValidacao.textoValido(thePersona.getName(), 45)
					|| !ToolkitValidacao.idadeValida(thePersona.getAge())) {
				redirectAttrs.addFlashAttribute("toastErro", "Não foi possível salvar a persona. Verifique o nome e a idade.");
				return "redirect:/persona/lista";
			}
			thePersona.setIdeiaCodigo(cookie.getCookieIdeiaCodigo(request));

			// save the persona using our service
			personaService.savePersona(thePersona);

			redirectAttrs.addFlashAttribute("toastOk", "Persona salva com sucesso.");
			return "redirect:/persona/lista";
		}
		else{
			return "redirect";
		}	
	}
	
	@GetMapping("/deletar")
	public String deletePersona(@RequestParam("personaId") int theId, Model theModel, HttpServletRequest request){
		// TK-03: exige cookie de ideia; o filtro por ideia_codigo no DAO impede IDOR
		if(cookie.getCookieIdeiaCodigo(request) != null){
			personaService.deletePersona(theId, cookie.getCookieIdeiaCodigo(request));
		}

		return "redirect:/persona/lista";
	}
	
	@GetMapping("/empatia/mapa")
	public String showPersonaPreData(@RequestParam("personaId") int theId, Model theModel, HttpServletRequest request){
		Long ideiaCodigo = Long.valueOf(0);
		
		if(cookie.getCookieIdeiaCodigo(request) != null){
			 ideiaCodigo = cookie.getCookieIdeiaCodigo(request);
			 
			// get the customer from our service
			Persona thePersona = personaService.getPersona(theId, ideiaCodigo);
			
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
