package br.unisc.toolkit.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

// TEST-0X: InfoController -- pagina estatica de informacoes, sem guard de cookie (nao depende de ideia).
public class InfoControllerTest {

	private MockMvc mvc;

	@Before
	public void setup() {
		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
		resolver.setPrefix("/WEB-INF/view/");
		resolver.setSuffix(".jsp");
		mvc = MockMvcBuilders.standaloneSetup(new InfoController()).setViewResolvers(resolver).build();
	}

	@Test
	public void informacoes_retornaViewComTitulo() throws Exception {
		mvc.perform(get("/informacoes"))
				.andExpect(status().isOk())
				.andExpect(view().name("informacoes"))
				.andExpect(model().attribute("pageTitle", "Informações Gerais"));
	}
}
