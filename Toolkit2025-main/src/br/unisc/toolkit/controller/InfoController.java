package br.unisc.toolkit.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.unisc.toolkit.classes.AdminCookies;

@Controller
@RequestMapping("/")
public class InfoController {

	AdminCookies cookie = new AdminCookies();

	// UX-INFO-SEM-GUARD: unico page controller do Toolkit sem o guard de cookie de ideia
	// presente nos demais -- deixava acessar a pagina sem nunca ter passado pelo fluxo.
	@GetMapping("/informacoes")
    public String info(Model theModel, HttpServletRequest request) {
		if (cookie.getCookieIdeiaCodigo(request) == null) {
			theModel.addAttribute("pageTitle", "Erro");
			return "redirect";
		}

		theModel.addAttribute("pageTitle", "Informações Gerais");

        return "informacoes";
    }
}
