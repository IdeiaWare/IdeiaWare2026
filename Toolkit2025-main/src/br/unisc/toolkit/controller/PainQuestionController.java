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
public class PainQuestionController {

	// need to inject our empathy service
	@Autowired
	private EmpathyService empathyService;
	
	AdminCookies cookie = new AdminCookies();
	
	@GetMapping("/quais-sao-as-dores")
	public String showThinkAndFeel(@RequestParam("personaId") int theId, Model theModel, HttpServletRequest request){
		Long ideiaCodigo = Long.valueOf(0);
		
		if(cookie.getCookieIdeiaCodigo(request) != null){
			ideiaCodigo = cookie.getCookieIdeiaCodigo(request);
			
			// get empathy attributes from the service
			List<Empathy> theEmpaties = empathyService.getAttributes(theId, "pain", ideiaCodigo);
			
			// create model attribute to bind form data
			Empathy theEmpathy = new Empathy();
					
			theModel.addAttribute("pageTitle", "Quais são as Dores");
			theModel.addAttribute("attribute", theEmpathy);
			theModel.addAttribute("personaId", theId);
			
			theModel.addAttribute("attributes", theEmpaties);
			
			return "pain";
		}
		else{
			theModel.addAttribute("pageTitle", "Erro");
			return "redirect";
		}
	}
	
	@PostMapping("/quais-sao-as-dores/save-attribute")
	public String saveAttribute(@ModelAttribute("attribute") Empathy theEmpathy, BindingResult result, HttpServletRequest request){
		if(cookie.getCookieIdeiaCodigo(request) != null){
		   // TK-VAL: backstop server-side (client-side e burlavel); BindingResult evita 400.
			if (result.hasErrors() || !ToolkitValidacao.textoValido(theEmpathy.getAttributeText(), 10000)) {
				return "redirect:/persona/empatia/mapa?personaId=" + theEmpathy.getPersonaId();
			}
			theEmpathy.setIdeiaCodigo(cookie.getCookieIdeiaCodigo(request));
			// REVISAO 2026-07-08 (varredura Toolkit, achado BAIXA): "attribute" (o tipo do
			// quadrante) vinha 100% do form:hidden, nunca setado no servidor -- um POST
			// direto pra este endpoint com attribute=gain gravava tipo arbitrario. Forca
			// o valor correto no servidor, igual ja e feito com ideiaCodigo acima.
			theEmpathy.setAttribute("pain");

		   // save the empathy attribute using our service
		   empathyService.saveEmpathyAttribute(theEmpathy);
				
		   return "redirect:/persona/empatia/quais-sao-as-dores?personaId=" + theEmpathy.getPersonaId();
		}
		else{
			return "redirect";
		}		
	}
	
	// REVISAO 2026-07-08 (varredura Toolkit, achado MEDIA -- csrfToken em GET): virou
	// POST -- mesmo motivo do GainQuestionController.
	@PostMapping("/quais-sao-as-dores/delete")
	public String deleteAttribute(@RequestParam("personaId") int personaId,
								@RequestParam("attributeId") int attributeId,
								Model theModel, HttpServletRequest request)
	{
		// TK-03: exige cookie de ideia; o filtro por ideia_codigo no DAO impede IDOR
		if(cookie.getCookieIdeiaCodigo(request) != null){
			empathyService.deleteAttribute(attributeId, cookie.getCookieIdeiaCodigo(request));
		}

		return "redirect:/persona/empatia/quais-sao-as-dores?personaId=" + personaId;
	}
}
