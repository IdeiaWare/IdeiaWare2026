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

import br.unisc.toolkit.classes.AdminCookies;
import br.unisc.toolkit.classes.ToolkitValidacao;
import org.springframework.validation.BindingResult;
import br.unisc.toolkit.entity.Empathy;
import br.unisc.toolkit.service.EmpathyService;

@Controller
@RequestMapping("/persona/empatia")
public class SeeQuestionController {
	
	// need to inject our empathy service
	@Autowired
	private EmpathyService empathyService;
	
	AdminCookies cookie = new AdminCookies();
	
	@GetMapping("/o-que-ve")
	public String showSeeQuestion(@RequestParam("personaId") int theId, Model theModel, HttpServletRequest request){
		Long ideiaCodigo = Long.valueOf(0);
		
		if(cookie.getCookieIdeiaCodigo(request) != null){
			ideiaCodigo = cookie.getCookieIdeiaCodigo(request);
			
			// get empathy attributes from the service
			List<Empathy> theEmpaties = empathyService.getAttributes(theId, "see", ideiaCodigo);
			
			// create model attribute to bind form data
			Empathy theEmpathy = new Empathy();
					
			theModel.addAttribute("pageTitle", "O que Vê");
			theModel.addAttribute("attribute", theEmpathy);
			theModel.addAttribute("personaId", theId);
			
			theModel.addAttribute("attributes", theEmpaties);
			
			return "see";
		}
		else{
			theModel.addAttribute("pageTitle", "Erro");
			return "redirect";
		}
	}
	
	@PostMapping("/o-que-ve/save-attribute")
	public String saveAttribute(@ModelAttribute("attribute") Empathy theEmpathy, BindingResult result, HttpServletRequest request){
		if(cookie.getCookieIdeiaCodigo(request) != null){
			// TK-VAL: backstop server-side (client-side e burlavel); BindingResult evita 400.
			if (result.hasErrors() || !ToolkitValidacao.textoValido(theEmpathy.getAttributeText(), 10000)) {
				return "redirect:/persona/empatia/mapa?personaId=" + theEmpathy.getPersonaId();
			}
			theEmpathy.setIdeiaCodigo(cookie.getCookieIdeiaCodigo(request));
			// TK-ATTR: forca "see" no servidor (antes vinha 100% do form:hidden, POST direto trocava o tipo).
			theEmpathy.setAttribute("see");

			// save the empathy attribute using our service
			empathyService.saveEmpathyAttribute(theEmpathy);
			
			return "redirect:/persona/empatia/o-que-ve?personaId=" + theEmpathy.getPersonaId();
		}
		else{
			return "redirect";
		}
	}
	
	// TK-26: virou POST (mesmo motivo do GainQuestionController).
	@PostMapping("/o-que-ve/delete")
	public String deleteAttribute(@RequestParam("personaId") int personaId,
								@RequestParam("attributeId") int attributeId,
								Model theModel, HttpServletRequest request)
	{
		// TK-03: exige cookie de ideia; o filtro por ideia_codigo no DAO impede IDOR
		if(cookie.getCookieIdeiaCodigo(request) != null){
			empathyService.deleteAttribute(attributeId, cookie.getCookieIdeiaCodigo(request));
		}

		return "redirect:/persona/empatia/o-que-ve?personaId=" + personaId;
	}
}
