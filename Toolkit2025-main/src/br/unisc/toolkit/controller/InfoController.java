package br.unisc.toolkit.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class InfoController {
	
	@GetMapping("/informacoes")
    public String info(Model theModel) {
		theModel.addAttribute("pageTitle", "Informações Gerais");
		
        return "informacoes";
    }
}
