package br.unisc.toolkit.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

// TEST-0X: AvisoController -- confirma que /aviso-etapa-encerrada (destino dos 21 pontos
// bloqueados por StatusGuard nos outros controllers) de fato mapeia e resolve a view.
public class AvisoControllerTest {

	private MockMvc mvc;

	@Before
	public void setup() {
		// sem resolver explicito, o nome da view bate com o ultimo segmento da URL e o
		// MockMvc acusa "Circular view path" (forward-por-nome-de-view e' o default sem isso).
		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
		resolver.setPrefix("/WEB-INF/view/");
		resolver.setSuffix(".jsp");
		mvc = MockMvcBuilders.standaloneSetup(new AvisoController()).setViewResolvers(resolver).build();
	}

	@Test
	public void aviso_resolveView() throws Exception {
		mvc.perform(get("/aviso-etapa-encerrada"))
				.andExpect(status().isOk())
				.andExpect(view().name("aviso-etapa-encerrada"));
	}
}
