package br.unisc.toolkit.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// UX-PADRAO-ETAPA-FINALIZADA: hop intermediario que mostra o alert() antes do redirect final.
@Controller
public class AvisoController {

	@GetMapping("/aviso-etapa-encerrada")
	public String aviso() {
		return "aviso-etapa-encerrada";
	}
}
