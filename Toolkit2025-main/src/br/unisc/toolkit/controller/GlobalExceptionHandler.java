package br.unisc.toolkit.controller;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * TK-EXC: tratamento de excecoes centralizado (alem do <error-page> do web.xml).
 *
 * Garante que NENHUMA excecao chegue crua ao usuario e permite respostas mais
 * amigaveis/contextualizadas. Renderiza a view "error" (error.jsp), que mostra a
 * mensagem em errorMsg.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * Param de URL com tipo invalido — ex.: /persona/empatia/mapa?personaId=abc,
	 * onde personaId e int. Antes virava 400 cru; agora cai numa pagina amigavel.
	 */
	// REVISAO 2026-07-08 (varredura Toolkit, achado BAIXA): nenhum handler setava o
	// status HTTP -- toda excecao tratada aqui voltava como 200 OK (mascarava a
	// semantica HTTP real pra qualquer monitoramento/log de acesso). @ResponseStatus
	// funciona normalmente aqui mesmo com retorno de view name (nao e @ResponseBody).
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public String handleTypeMismatch(MethodArgumentTypeMismatchException ex, Model model) {
		model.addAttribute("pageTitle", "Erro");
		model.addAttribute("errorMsg", "Parametro invalido na requisicao.");
		return "error";
	}

	/**
	 * Rede final: qualquer excecao nao tratada nos controllers vira pagina amigavel.
	 */
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
