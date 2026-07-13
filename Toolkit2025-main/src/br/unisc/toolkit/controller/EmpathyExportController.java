package br.unisc.toolkit.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.unisc.toolkit.classes.AdminCookies;
import br.unisc.toolkit.entity.Empathy;
import br.unisc.toolkit.entity.ExportFile;
import br.unisc.toolkit.entity.Persona;
import br.unisc.toolkit.service.EmpathyService;
import br.unisc.toolkit.service.ExportFileService;
import br.unisc.toolkit.service.PersonaService;

@Controller
@RequestMapping("/persona/empatia")
public class EmpathyExportController {

	@Autowired
	private EmpathyService empathyService;
	
	@Autowired
	private PersonaService personaService;
	
	@Autowired
	private ExportFileService exportFileService;
	
	AdminCookies cookie = new AdminCookies();
	
	// TK-EMP-GUARD: guard de cookie explicito (antes, sem cookie a JSP explodia em vez de redirecionar).
	@GetMapping("/visao-geral")
	public String showFilledEmpathyMapOverview(@RequestParam("personaId") int theId, Model theModel,  HttpServletRequest request){
		if (cookie.getCookieIdeiaCodigo(request) == null || !personaView(theId, theModel, "overview", request)) {
			theModel.addAttribute("pageTitle", "Erro");
			return "redirect";
		}

		return "empathy-map-overview";
	}

	@GetMapping("/visao-detalhada")
	public String showFilledEmpathyMapDetailed(@RequestParam("personaId") int theId, Model theModel, HttpServletRequest request){
		if (cookie.getCookieIdeiaCodigo(request) == null || !personaView(theId, theModel, "detailed", request)) {
			theModel.addAttribute("pageTitle", "Erro");
			return "redirect";
		}
		return "empathy-map-detailed";
	}
	
	@PostMapping("/exportar-geral")
	public String saveOverview(@ModelAttribute("overview") ExportFile file, HttpServletRequest request, Model theModel, RedirectAttributes redirectAttrs){
		if(cookie.getCookieIdeiaCodigo(request) != null){
		   file.setIdeiaCodigo(cookie.getCookieIdeiaCodigo(request));
		   file.setCreated(new Date());

		   exportFileService.saveFile(file);

		   // Apos exportar, volta para a listagem de personas (a pedido do usuario) + toast.
		   redirectAttrs.addFlashAttribute("toastOk", "Exportação concluída. O arquivo foi salvo na Retenção do Conhecimento.");
		   return "redirect:/persona/lista";
		}
		else{
			return "redirect";
		}		
	}
	
	@PostMapping("/exportar-detalhada")
	public String saveDetailed(@ModelAttribute("detailed") ExportFile file, HttpServletRequest request, Model theModel, RedirectAttributes redirectAttrs){
		if(cookie.getCookieIdeiaCodigo(request) != null){
		   file.setIdeiaCodigo(cookie.getCookieIdeiaCodigo(request));
		   file.setCreated(new Date());

		   exportFileService.saveFile(file);

		   // Apos exportar, volta para a listagem de personas (a pedido do usuario) + toast.
		   redirectAttrs.addFlashAttribute("toastOk", "Exportação concluída. O arquivo foi salvo na Retenção do Conhecimento.");
		   return "redirect:/persona/lista";
		}
		else{
			return "redirect";
		}		
	}
	
	// TK-02: getPersona() pode voltar null; boolean permite ao caller redirecionar em vez de renderizar quebrado.
	private boolean personaView(int theId, Model theModel, String viewType, HttpServletRequest request){
		Long ideiaCodigo = Long.valueOf(0);

		if(cookie.getCookieIdeiaCodigo(request) != null){
			ideiaCodigo = cookie.getCookieIdeiaCodigo(request);

			Persona thePersona = personaService.getPersona(theId, ideiaCodigo);
			if (thePersona == null) {
				return false;
			}
			List<Empathy> EmpatiesThinkFeel = empathyService.getAttributes(theId, "think_feel", ideiaCodigo);
			List<Empathy> EmpatiesSee = empathyService.getAttributes(theId, "see", ideiaCodigo);
			List<Empathy> EmpatiesSayDo = empathyService.getAttributes(theId, "say_do", ideiaCodigo);
			List<Empathy> EmpatiesHear = empathyService.getAttributes(theId, "hear", ideiaCodigo);
			List<Empathy> EmpatiesPain = empathyService.getAttributes(theId, "pain", ideiaCodigo);
			List<Empathy> EmpatiesGain = empathyService.getAttributes(theId, "gain", ideiaCodigo);

			theModel.addAttribute("pageTitle", "Mapa de Empatia - Exportar");
			theModel.addAttribute("persona", thePersona);
			theModel.addAttribute("thinkFeelAtributes", EmpatiesThinkFeel);
			theModel.addAttribute("seeAtributes", EmpatiesSee);
			theModel.addAttribute("sayDoAtributes", EmpatiesSayDo);
			theModel.addAttribute("hearAtributes", EmpatiesHear);
			theModel.addAttribute("painAtributes", EmpatiesPain);
			theModel.addAttribute("gainAtributes", EmpatiesGain);

			ExportFile exportFile = new ExportFile();
			theModel.addAttribute(viewType, exportFile);
			return true;
		}
		return false;
	}
}
