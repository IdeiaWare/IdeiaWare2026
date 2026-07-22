package br.unisc.toolkit.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Before;
import org.junit.Test;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

// TEST-0X: GlobalExceptionHandler -- trava o TK-28, usa um controller descartavel pra disparar excecoes reais.
public class GlobalExceptionHandlerTest {

	@Controller
	static class ControllerDescartavel {
		@GetMapping("/boom-type-mismatch")
		public String boomTypeMismatch(@RequestParam("n") int n) {
			return "nunca-chega-aqui";
		}

		@GetMapping("/boom-generico")
		public String boomGenerico() {
			throw new RuntimeException("falha inesperada");
		}
	}

	private MockMvc mvc;

	@Before
	public void setup() {
		mvc = MockMvcBuilders.standaloneSetup(new ControllerDescartavel())
				.setControllerAdvice(new GlobalExceptionHandler())
				.build();
	}

	@Test
	public void parametroComTipoErrado_retorna400EViewDeErro() throws Exception { // TK-28
		mvc.perform(get("/boom-type-mismatch").param("n", "abc"))
				.andExpect(status().isBadRequest())
				.andExpect(view().name("error"));
	}

	@Test
	public void excecaoNaoTratada_retorna500EViewDeErro() throws Exception { // TK-28
		mvc.perform(get("/boom-generico"))
				.andExpect(status().isInternalServerError())
				.andExpect(view().name("error"));
	}
}
