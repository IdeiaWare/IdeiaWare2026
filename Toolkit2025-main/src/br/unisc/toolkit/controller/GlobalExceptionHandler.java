package br.unisc.toolkit.controller;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

// TK-EXC: tratamento de excecoes centralizado (alem do <error-page> do web.xml).
@ControllerAdvice
public class GlobalExceptionHandler {

	// TK-28: @ResponseStatus (antes, toda excecao tratada aqui voltava como 200 OK).
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public String handleTypeMismatch(MethodArgumentTypeMismatchException ex, Model model) {
		model.addAttribute("pageTitle", "Erro");
		model.addAttribute("errorMsg", "Parametro invalido na requisicao.");
		return "error";
	}

	// Rede final: qualquer excecao nao tratada nos controllers vira pagina amigavel.
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public String handleGeneric(Exception ex, Model model) {
		// log simples no console do Tomcat (catalina.out) para diagnostico
		System.err.println("[TK-EXC] Excecao nao tratada: " + ex.getClass().getSimpleName()
				+ " - " + ex.getMessage());
		model.addAttribute("pageTitle", "Erro");
		model.addAttribute("errorMsg", "Ocorreu um erro inesperado. Tente novamente.");
		return "error";
	}
}
