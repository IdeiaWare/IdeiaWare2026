package br.unisc.toolkit.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

// TEST-0X: ErrorController -- trava o mapeamento status->mensagem (inclusive TK-27, o 403 que o
// CsrfInterceptor gera) e o TK-06 (sem status_code na request, cai no padrao 500 sem lancar NPE).
public class ErrorControllerTest {

	private MockMvc mvc;

	@Before
	public void setup() {
		// mesmo ViewResolver de producao (toolkit-servlet.xml) -- sem ele, MockMvc resolve a
		// view "error" direto pra URL "/error" (igual a request) e o forward vira "circular".
		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
		resolver.setPrefix("/WEB-INF/view/");
		resolver.setSuffix(".jsp");
		mvc = MockMvcBuilders.standaloneSetup(new ErrorController()).setViewResolvers(resolver).build();
	}

	@Test
	public void codigo400_mensagemDeRequisicaoInvalida() throws Exception {
		mvc.perform(get("/error").requestAttr("javax.servlet.error.status_code", 400))
				.andExpect(view().name("error"))
				.andExpect(model().attribute("errorMsg", "A requisição não pôde ser entendida."));
	}

	@Test
	public void codigo401_mensagemDeNaoAutenticado() throws Exception {
		mvc.perform(get("/error").requestAttr("javax.servlet.error.status_code", 401))
				.andExpect(model().attribute("errorMsg", "Você precisa estar autenticado para acessar isso."));
	}

	@Test
	public void codigo403_mensagemDeAcaoBloqueada() throws Exception { // TK-27: gerado pelo CsrfInterceptor
		mvc.perform(get("/error").requestAttr("javax.servlet.error.status_code", 403))
				.andExpect(model().attributeExists("errorMsg"));
	}

	@Test
	public void codigo404_mensagemDePaginaNaoEncontrada() throws Exception {
		mvc.perform(get("/error").requestAttr("javax.servlet.error.status_code", 404))
				.andExpect(model().attribute("errorMsg", "Página não encontrada."));
	}

	@Test
	public void codigo500_mensagemDeErroInterno() throws Exception {
		mvc.perform(get("/error").requestAttr("javax.servlet.error.status_code", 500))
				.andExpect(model().attribute("errorMsg", "Ocorreu um erro interno. Tente novamente mais tarde."));
	}

	@Test
	public void semStatusCodeNaRequest_usaPadrao500SemLancarNPE() throws Exception { // TK-06
		mvc.perform(get("/error"))
				.andExpect(status().isOk())
				.andExpect(model().attribute("errorMsg", "Ocorreu um erro interno. Tente novamente mais tarde."));
	}

	@Test
	public void aceitaPOST_naoDa405() throws Exception {
		mvc.perform(post("/error").requestAttr("javax.servlet.error.status_code", 500))
				.andExpect(view().name("error"));
	}
}
