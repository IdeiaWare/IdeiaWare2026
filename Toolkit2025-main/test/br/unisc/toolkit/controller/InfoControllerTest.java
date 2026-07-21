package br.unisc.toolkit.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import javax.servlet.http.Cookie;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import br.unisc.toolkit.classes.AssinaturaCaixa;

// TEST-0X: InfoController -- UX-INFO-SEM-GUARD: agora exige o mesmo guard de cookie de ideia das demais paginas.
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
	public void comCookieDeIdeia_retornaViewComTitulo() throws Exception {
		mvc.perform(get("/informacoes")
				.cookie(new Cookie("ideiaId", "5"), new Cookie("ideiaSig", AssinaturaCaixa.assinar("5"))))
				.andExpect(status().isOk())
				.andExpect(view().name("informacoes"))
				.andExpect(model().attribute("pageTitle", "Informações Gerais"));
	}

	@Test
	public void semCookieDeIdeia_redirecionaParaErro() throws Exception {
		mvc.perform(get("/informacoes"))
				.andExpect(view().name("redirect"))
				.andExpect(model().attribute("pageTitle", "Erro"));
	}
}
