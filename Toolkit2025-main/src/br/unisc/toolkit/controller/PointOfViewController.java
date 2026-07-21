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
import br.unisc.toolkit.classes.Constantes;
import br.unisc.toolkit.classes.ArquivoExport;
import br.unisc.toolkit.classes.StatusGuard;
import br.unisc.toolkit.classes.ToolkitValidacao;
import org.springframework.validation.BindingResult;
import br.unisc.toolkit.entity.ExportFile;
import br.unisc.toolkit.entity.Persona;
import br.unisc.toolkit.entity.PointOfView;
import br.unisc.toolkit.entity.PointOfViewInfo;
import br.unisc.toolkit.service.IdeiaService;
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

	@Autowired
	private IdeiaService ideiaService;

	AdminCookies cookie = new AdminCookies();
	
	// TK-45b: personas via query string em vez de path variable.
	@GetMapping("/criar-pov")
	public String showPOVTemplate(@RequestParam("personas") List<String> personas, Model theModel, HttpServletRequest request){
		// TK-COOKIE-GUARD: guard de cookie explicito.
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
			// UX-TOOLKIT-STATUS-GUARD: bloqueia escrita fora da etapa Caixa de Ferramentas.
			if (!StatusGuard.podeEscrever(ideiaService, cookie.getCookieIdeiaCodigo(request))) {
				redirectAttrs.addFlashAttribute("etapaEncerradaErro", "Esta etapa já foi encerrada.");
				redirectAttrs.addFlashAttribute("redirecionarPara", Constantes.paginaMinhaIdeia(request));
				return "redirect:/aviso-etapa-encerrada";
			}
			// SEC-24: se vier id (UPDATE), confere que o POV e desta ideia.
			if (thePOV.getId() != 0
					&& !pointOfViewService.povPertenceAIdeia(thePOV.getId(), cookie.getCookieIdeiaCodigo(request))) {
				return "redirect:/point-of-view/lista";
			}
			// TK-VAL: exige ao menos 1 texto e 1 persona selecionada.
			if (result.hasErrors()
					|| !ToolkitValidacao.algumTextoValido(thePOV.getUserText(), thePOV.getNeedText(), thePOV.getInsightText())
					|| thePOV.getPersonasId() == null || thePOV.getPersonasId().length == 0) {
				redirectAttrs.addFlashAttribute("toastErro", "Não foi possível criar o Point of View. Selecione ao menos uma persona e preencha um dos campos.");
				return "redirect:/point-of-view/lista";
			}
			thePOV.setIdeiaCodigo(cookie.getCookieIdeiaCodigo(request));

			// TK-TXN: save + reassociar personas numa unica transacao.
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
		// SEC-23: exige cookie assinado e escopa o POV por ideia.
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
			// UX-TOOLKIT-EXPORT-TRAVADO: bloqueia exportacao fora da etapa Caixa de Ferramentas.
			if (!StatusGuard.podeEscrever(ideiaService, ideiaCodigo)) {
				redirectAttrs.addFlashAttribute("etapaEncerradaErro", "Esta etapa já foi encerrada.");
				redirectAttrs.addFlashAttribute("redirecionarPara", Constantes.paginaMinhaIdeia(request));
				return "redirect:/aviso-etapa-encerrada";
			}
			file.setIdeiaCodigo(ideiaCodigo);
			file.setCreated(new Date());
			file.setFileLocation(ArquivoExport.salvar(file.getFileLocation(), ideiaCodigo));

			exportFileService.saveFile(file);

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
			// UX-TOOLKIT-STATUS-GUARD: bloqueia escrita fora da etapa Caixa de Ferramentas.
			if (!StatusGuard.podeEscrever(ideiaService, cookie.getCookieIdeiaCodigo(request))) {
				redirectAttrs.addFlashAttribute("etapaEncerradaErro", "Esta etapa já foi encerrada.");
				redirectAttrs.addFlashAttribute("redirecionarPara", Constantes.paginaMinhaIdeia(request));
				return "redirect:/aviso-etapa-encerrada";
			}
			// SEC-24: se vier id (UPDATE), confere que o POV e desta ideia.
			if (thePOV.getId() != 0
					&& !pointOfViewService.povPertenceAIdeia(thePOV.getId(), cookie.getCookieIdeiaCodigo(request))) {
				return "redirect:/point-of-view/lista";
			}
			// TK-VAL: exige ao menos 1 texto e 1 persona selecionada.
			if (result.hasErrors()
					|| !ToolkitValidacao.algumTextoValido(thePOV.getUserText(), thePOV.getNeedText(), thePOV.getInsightText())
					|| thePOV.getPersonasId() == null || thePOV.getPersonasId().length == 0) {
				redirectAttrs.addFlashAttribute("toastErro", "Não foi possível salvar o Point of View. Selecione ao menos uma persona e preencha um dos campos.");
				return "redirect:/point-of-view/lista";
			}
			thePOV.setIdeiaCodigo(cookie.getCookieIdeiaCodigo(request));

			// TK-TXN: remove+save+reassociar numa unica transacao.
			pointOfViewService.atualizarComPersonas(thePOV);

			redirectAttrs.addFlashAttribute("toastOk", "Point of View atualizado com sucesso.");
			return "redirect:/point-of-view/lista";
		}
		else{
			return "redirect";
		}		
	}
	
	// TK-26: virou POST.
	@PostMapping("/deletar")
	public String deletePointOfView(@RequestParam("povId") int theId, Model theModel, HttpServletRequest request, RedirectAttributes redirectAttrs){
		// TK-03: exige cookie de ideia contra IDOR.
		// UX-TOOLKIT-STATUS-GUARD: bloqueia escrita fora da etapa Caixa de Ferramentas.
		if(cookie.getCookieIdeiaCodigo(request) != null){
			if (StatusGuard.podeEscrever(ideiaService, cookie.getCookieIdeiaCodigo(request))) {
				pointOfViewService.deletePointOfView(theId, cookie.getCookieIdeiaCodigo(request));
			} else {
				// UX-PADRAO-ETAPA-FINALIZADA: antes falhava em silencio (sem toast, sem excluir).
				redirectAttrs.addFlashAttribute("etapaEncerradaErro", "Esta etapa já foi encerrada.");
				redirectAttrs.addFlashAttribute("redirecionarPara", Constantes.paginaMinhaIdeia(request));
				return "redirect:/aviso-etapa-encerrada";
			}
		}

		return "redirect:/point-of-view/lista";
	}
	
	private void displayPointOfView(Model theModel, List<Object> povItemsList){
		// TK-ORD: LinkedHashMap preserva a ordem da query (mais novos em cima).
		Map<Integer, PointOfViewInfo> pointOfViewsInfo = new LinkedHashMap<Integer, PointOfViewInfo>();
		List<PointOfViewInfo> infosList = new ArrayList<PointOfViewInfo>();
		
		for (int i=0; i < povItemsList.size(); i++){
			Object[] row = (Object[]) povItemsList.get(i);
			
			// TK-18: (Number).intValue() em vez de cast (Integer) cru.
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
