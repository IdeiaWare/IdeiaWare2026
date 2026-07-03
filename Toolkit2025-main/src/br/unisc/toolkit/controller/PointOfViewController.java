package br.unisc.toolkit.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.unisc.toolkit.classes.AdminCookies;
import br.unisc.toolkit.classes.ToolkitValidacao;
import org.springframework.validation.BindingResult;
import br.unisc.toolkit.entity.ExportFile;
import br.unisc.toolkit.entity.Persona;
import br.unisc.toolkit.entity.PersonaPointOfView;
import br.unisc.toolkit.entity.PointOfView;
import br.unisc.toolkit.entity.PointOfViewInfo;
import br.unisc.toolkit.service.PointOfViewService;
import br.unisc.toolkit.service.ExportFileService;
import br.unisc.toolkit.service.PersonaPointOfViewService;
import br.unisc.toolkit.service.PersonaService;

@Controller
@RequestMapping("/point-of-view")
public class PointOfViewController {
	
	@Autowired
	private PointOfViewService pointOfViewService;
	
	@Autowired
	private PersonaPointOfViewService personaPointOfViewService;
	
	@Autowired
	private PersonaService personaService;
	
	@Autowired
	private ExportFileService exportFileService;
	
	AdminCookies cookie = new AdminCookies();
	
	// Recebe as personas via query string (?personas=...) em vez de path variable.
	// Nomes de persona contem espacos e "+", que no path causavam 404 no Tomcat 9.
	@GetMapping("/criar-pov")
	public String showPOVTemplate(@RequestParam("personas") List<String> personas, Model theModel){
		
		PointOfView thePOV = new PointOfView();
		
		theModel.addAttribute("pageTitle", "Novo Point of View");
		theModel.addAttribute("personas", personas);
		theModel.addAttribute("pov", thePOV);
		
		return "form-pov";
	}
	
	@PostMapping("/salvar-pov")
	public String savePointOfView(@ModelAttribute("pointOfView") PointOfView thePOV, BindingResult result, HttpServletRequest request, RedirectAttributes redirectAttrs){

		if(cookie.getCookieIdeiaCodigo(request) != null){
			// SEC-24 (IDOR de escrita): se vier um id (UPDATE), confere que o POV e DESTA
			// ideia ANTES de mexer na tabela auxiliar / salvar -> bloqueia editar ou
			// sobrescrever POV de outra ideia chutando o pov_id.
			if (thePOV.getId() != 0
					&& !pointOfViewService.povPertenceAIdeia(thePOV.getId(), cookie.getCookieIdeiaCodigo(request))) {
				return "redirect:/point-of-view/lista";
			}
			// TK-VAL: backstop server-side. Exige ao menos 1 texto e 1 persona selecionada.
			if (result.hasErrors()
					|| !ToolkitValidacao.algumTextoValido(thePOV.getUserText(), thePOV.getNeedText(), thePOV.getInsightText())
					|| thePOV.getPersonasId() == null || thePOV.getPersonasId().length == 0) {
				redirectAttrs.addFlashAttribute("toastErro", "Não foi possível criar o Point of View. Selecione ao menos uma persona e preencha um dos campos.");
				return "redirect:/point-of-view/lista";
			}
			thePOV.setIdeiaCodigo(cookie.getCookieIdeiaCodigo(request));

			pointOfViewService.savePOV(thePOV);

			savePersonasPOVItem(thePOV);

			redirectAttrs.addFlashAttribute("toastOk", "Point of View criado com sucesso.");
			return "redirect:/point-of-view/lista";
		}
		else{
			return "redirect";
		}
	}
	
	@GetMapping("/lista")
	public String listPointOfViews(Model theModel, HttpServletRequest request){
		Long ideiaCodigo = Long.valueOf(0);
		
		if(cookie.getCookieIdeiaCodigo(request) != null){
			ideiaCodigo = cookie.getCookieIdeiaCodigo(request);
			
			List<Object> thePointOfViews = pointOfViewService.getPointOfViews(ideiaCodigo);
			
			if (thePointOfViews.size() > 0)
				displayPointOfView(theModel, thePointOfViews);
			
			theModel.addAttribute("pageTitle", "Point Of Views - Lista");
			
			//Get all personas for poit of view autocomplete on edit
			List<Persona> allPersonas = personaService.getPersonas(ideiaCodigo);
			theModel.addAttribute("personas", allPersonas);
			
			// Trecho para atualizar o Point Of View
			PointOfView thePOV = new PointOfView();
			theModel.addAttribute("pov", thePOV);
			
			return "list-point-of-view";
		}
		else{
			theModel.addAttribute("pageTitle", "Erro");
			return "redirect";
		}
	}
	
	@GetMapping("/visao-geral")
	public String showFilledPOVOverview(@RequestParam("povId") int theId, Model theModel, HttpServletRequest request){
		// SEC-23: exige o cookie (assinado) e ESCOPA o POV por ideia -> nao da p/ ver o
		// POV de outra ideia chutando o povId.
		Long ideiaCodigo = cookie.getCookieIdeiaCodigo(request);
		if (ideiaCodigo == null) {
			theModel.addAttribute("pageTitle", "Erro");
			return "redirect";
		}

		List<Object> thePointOfViewItem = pointOfViewService.getSpecificPointOfView(theId, ideiaCodigo);

		if (thePointOfViewItem.size() > 0)
			displayPointOfView(theModel, thePointOfViewItem);
		
		theModel.addAttribute("pageTitle", "Point of View - Visão Geral");
		
		ExportFile exportFile = new ExportFile();
		theModel.addAttribute("overview", exportFile);
		
		return "point-of-view-overview";
	}
	
	@PostMapping("/exportar-geral")
	public String saveOverview(@ModelAttribute("overview") ExportFile file, Model theModel, HttpServletRequest request, RedirectAttributes redirectAttrs){
		if(cookie.getCookieIdeiaCodigo(request) != null){
			file.setIdeiaCodigo(cookie.getCookieIdeiaCodigo(request));
			file.setCreated(new Date());

			exportFileService.saveFile(file);

			// Apos exportar, volta para a listagem de POV (a pedido do usuario) + toast de sucesso.
			redirectAttrs.addFlashAttribute("toastOk", "Exportação concluída. O arquivo foi salvo na Retenção do Conhecimento.");
			return "redirect:/point-of-view/lista";
		}
		else{
			return "redirect";
		}
	}
	
	@PostMapping("/atualizar")
	public String savePOV(@ModelAttribute("pov") PointOfView thePOV, BindingResult result, HttpServletRequest request, RedirectAttributes redirectAttrs){
		if(cookie.getCookieIdeiaCodigo(request) != null){
			// SEC-24 (IDOR de escrita): se vier um id (UPDATE), confere que o POV e DESTA
			// ideia ANTES de mexer na tabela auxiliar / salvar -> bloqueia editar ou
			// sobrescrever POV de outra ideia chutando o pov_id.
			if (thePOV.getId() != 0
					&& !pointOfViewService.povPertenceAIdeia(thePOV.getId(), cookie.getCookieIdeiaCodigo(request))) {
				return "redirect:/point-of-view/lista";
			}
			// TK-VAL: backstop server-side. Exige ao menos 1 texto e 1 persona selecionada.
			if (result.hasErrors()
					|| !ToolkitValidacao.algumTextoValido(thePOV.getUserText(), thePOV.getNeedText(), thePOV.getInsightText())
					|| thePOV.getPersonasId() == null || thePOV.getPersonasId().length == 0) {
				redirectAttrs.addFlashAttribute("toastErro", "Não foi possível salvar o Point of View. Selecione ao menos uma persona e preencha um dos campos.");
				return "redirect:/point-of-view/lista";
			}
			thePOV.setIdeiaCodigo(cookie.getCookieIdeiaCodigo(request));

			personaPointOfViewService.removePOVIdFromAuxiliarTable(thePOV.getId());
			pointOfViewService.savePOV(thePOV);

			savePersonasPOVItem(thePOV);

			redirectAttrs.addFlashAttribute("toastOk", "Point of View atualizado com sucesso.");
			return "redirect:/point-of-view/lista";
		}
		else{
			return "redirect";
		}		
	}
	
	@GetMapping("/deletar")
	public String deletePointOfView(@RequestParam("povId") int theId, Model theModel, HttpServletRequest request){
		// TK-03: exige cookie de ideia; o filtro por ideia_codigo no DAO impede IDOR
		if(cookie.getCookieIdeiaCodigo(request) != null){
			pointOfViewService.deletePointOfView(theId, cookie.getCookieIdeiaCodigo(request));
		}

		return "redirect:/point-of-view/lista";
	}
	
	private void displayPointOfView(Model theModel, List<Object> povItemsList){
		// LinkedHashMap (nao HashMap): preserva a ordem de insercao = a ordem da
		// query (pov_id DESC) -> os POV mais novos aparecem em cima na lista.
		Map<Integer, PointOfViewInfo> pointOfViewsInfo = new LinkedHashMap<Integer, PointOfViewInfo>();
		List<PointOfViewInfo> infosList = new ArrayList<PointOfViewInfo>();
		
		for (int i=0; i < povItemsList.size(); i++){
			Object[] row = (Object[]) povItemsList.get(i);
			
			// TK-18: native query do MySQL pode devolver BigInteger nas colunas
			// numericas; o cast (Integer) cru dava ClassCastException. (Number).
			// intValue() aceita Integer/Long/BigInteger sem quebrar (blindagem).
			int id = ((Number) Arrays.asList(row).get(0)).intValue();
			String names = (String) Arrays.asList(row).get(1);
			int personaID = ((Number) Arrays.asList(row).get(2)).intValue();
			String user = (String) Arrays.asList(row).get(3);
			String need = (String) Arrays.asList(row).get(4);
			String insight = (String) Arrays.asList(row).get(5);
			int povID = ((Number) Arrays.asList(row).get(6)).intValue();
			
			infosList.add(new PointOfViewInfo(id, names,
					 							personaID, Integer.toString(personaID), 
					 							user, need, insight, povID));			
		}
		
		for (int i=0; i < infosList.size(); i++){
			
			if (infosList.size() > 0 && !pointOfViewsInfo.containsKey(infosList.get(i).getPovID())){
				PointOfViewInfo currentInfos = infosList.get(i);
                pointOfViewsInfo.put(currentInfos.getPovID(), currentInfos);
			}
			else {
				PointOfViewInfo info = (PointOfViewInfo)pointOfViewsInfo.get(infosList.get(i).getPovID());
			
				String existingNames = info.getNames();
				String existingPersonasID = Integer.toString(info.getPersonaID());
				
				String name = infosList.get(i).getNames();
				String personaID = Integer.toString(infosList.get(i).getPersonaID());
				
				info.setNames(existingNames + ", " + name);
				info.setPersonasID(existingPersonasID + "|" + personaID);
				
				pointOfViewsInfo.put(infosList.get(i).getPovID(), info);
			}
		}
		
		theModel.addAttribute("povs", pointOfViewsInfo);
	}
	
	private void savePersonasPOVItem(PointOfView thePOV){
		int povID = thePOV.getId();
		Long ideiaCodigo = thePOV.getIdeiaCodigo();
		
		for (int id : thePOV.getPersonasId()){
			PersonaPointOfView personaPOV = new PersonaPointOfView();
			personaPOV.setPersonaID(id);
			personaPOV.setPointOfViewID(povID);
			personaPOV.setIdeiaCodigo(ideiaCodigo);

			personaPointOfViewService.savePersonaPOV(personaPOV);
		}
	}
}
