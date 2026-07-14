package br.unisc.toolkit.controller;

import java.io.IOException;
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
import br.unisc.toolkit.classes.ArquivoExport;
import br.unisc.toolkit.classes.ToolkitValidacao;
import org.springframework.validation.BindingResult;
import br.unisc.toolkit.entity.ExportFile;
import br.unisc.toolkit.entity.Persona;
import br.unisc.toolkit.entity.PointOfView;
import br.unisc.toolkit.entity.PointOfViewInfo;
import br.unisc.toolkit.service.PointOfViewService;
import br.unisc.toolkit.service.ExportFileService;
import br.unisc.toolkit.service.PersonaService;

@Controller
@RequestMapping("/point-of-view")
public class PointOfViewController {

	@Autowired
	private PointOfViewService pointOfViewService;

	@Autowired
	private PersonaService personaService;
	
	@Autowired
	private ExportFileService exportFileService;
	
	AdminCookies cookie = new AdminCookies();
	
	// TK-45b: personas via query string (?personas=...) em vez de path variable (espaco/"+" dava 404 no Tomcat).
	@GetMapping("/criar-pov")
	public String showPOVTemplate(@RequestParam("personas") List<String> personas, Model theModel, HttpServletRequest request){
		// TK-COOKIE-GUARD: guard de cookie (unico GET deste controller que nao conferia antes).
		if (cookie.getCookieIdeiaCodigo(request) == null) {
			theModel.addAttribute("pageTitle", "Erro");
			return "redirect";
		}

		PointOfView thePOV = new PointOfView();

		theModel.addAttribute("pageTitle", "Novo Point of View");
		theModel.addAttribute("personas", personas);
		theModel.addAttribute("pov", thePOV);

		return "form-pov";
	}
	
	@PostMapping("/salvar-pov")
	public String savePointOfView(@ModelAttribute("pointOfView") PointOfView thePOV, BindingResult result, HttpServletRequest request, RedirectAttributes redirectAttrs){

		if(cookie.getCookieIdeiaCodigo(request) != null){
			// SEC-24: se vier id (UPDATE), confere que o POV e DESTA ideia antes de salvar (IDOR de escrita).
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

			// TK-TXN: save + reassociar personas NUMA UNICA transacao (antes, falha no meio deixava associacoes parciais).
			pointOfViewService.criarComPersonas(thePOV);

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
			
			theModel.addAttribute("pageTitle", "Point of Views - Lista");
			
			List<Persona> allPersonas = personaService.getPersonas(ideiaCodigo);
			theModel.addAttribute("personas", allPersonas);

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
		// SEC-23: exige o cookie assinado e escopa o POV por ideia (nao da pra ver POV de outra ideia chutando o povId).
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
	public String saveOverview(@ModelAttribute("overview") ExportFile file, Model theModel, HttpServletRequest request, RedirectAttributes redirectAttrs) throws IOException {
		if(cookie.getCookieIdeiaCodigo(request) != null){
			Long ideiaCodigo = cookie.getCookieIdeiaCodigo(request);
			file.setIdeiaCodigo(ideiaCodigo);
			file.setCreated(new Date());
			file.setFileLocation(ArquivoExport.salvar(file.getFileLocation(), ideiaCodigo));

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
			// SEC-24: se vier id (UPDATE), confere que o POV e DESTA ideia antes de salvar (IDOR de escrita).
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

			// TK-TXN: remove+save+reassociar NUMA UNICA transacao (antes, 2+N chamadas @Transactional separadas).
			pointOfViewService.atualizarComPersonas(thePOV);

			redirectAttrs.addFlashAttribute("toastOk", "Point of View atualizado com sucesso.");
			return "redirect:/point-of-view/lista";
		}
		else{
			return "redirect";
		}		
	}
	
	// TK-26: virou POST (mesmo motivo do PersonaController.deletar).
	@PostMapping("/deletar")
	public String deletePointOfView(@RequestParam("povId") int theId, Model theModel, HttpServletRequest request){
		// TK-03: exige cookie de ideia; o filtro por ideia_codigo no DAO impede IDOR
		if(cookie.getCookieIdeiaCodigo(request) != null){
			pointOfViewService.deletePointOfView(theId, cookie.getCookieIdeiaCodigo(request));
		}

		return "redirect:/point-of-view/lista";
	}
	
	private void displayPointOfView(Model theModel, List<Object> povItemsList){
		// TK-ORD: LinkedHashMap (nao HashMap) preserva a ordem da query (pov_id DESC), mais novos em cima.
		Map<Integer, PointOfViewInfo> pointOfViewsInfo = new LinkedHashMap<Integer, PointOfViewInfo>();
		List<PointOfViewInfo> infosList = new ArrayList<PointOfViewInfo>();
		
		for (int i=0; i < povItemsList.size(); i++){
			Object[] row = (Object[]) povItemsList.get(i);
			
			// TK-18: (Number).intValue() em vez de cast (Integer) cru (native query pode devolver BigInteger).
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
}
