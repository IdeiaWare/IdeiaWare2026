package br.unisc.toolkit.controller;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import javax.servlet.http.Cookie;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import br.unisc.toolkit.classes.AssinaturaCaixa;
import br.unisc.toolkit.entity.Persona;
import br.unisc.toolkit.service.PersonaService;

// TEST-02: PersonaController via MockMvc -- trava a validacao server-side + guard de cookie pelo stack real do Spring MVC.
public class PersonaControllerTest {

	private MockMvc mvc;
	private PersonaService service;

	@Before
	public void setup() {
		PersonaController controller = new PersonaController();
		service = mock(PersonaService.class);
		ReflectionTestUtils.setField(controller, "personaService", service);
		mvc = MockMvcBuilders.standaloneSetup(controller).build();
	}

	@Test
	public void personaValida_salvaERedireciona() throws Exception {
		mvc.perform(post("/persona/salvar-persona")
				.cookie(new Cookie("ideiaId", "5"), new Cookie("ideiaSig", AssinaturaCaixa.assinar("5")))
				.param("name", "Joao")
				.param("age", "30"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/persona/lista"));
		verify(service).savePersona(any(Persona.class));
	}

	@Test
	public void idadeForaDaFaixa_naoSalva() throws Exception { // backstop TK-VAL
		mvc.perform(post("/persona/salvar-persona")
				.cookie(new Cookie("ideiaId", "5"), new Cookie("ideiaSig", AssinaturaCaixa.assinar("5")))
				.param("name", "Joao")
				.param("age", "999"))
				.andExpect(status().is3xxRedirection());
		verify(service, never()).savePersona(any());
	}

	@Test
	public void nomeVazio_naoSalva() throws Exception {
		mvc.perform(post("/persona/salvar-persona")
				.cookie(new Cookie("ideiaId", "5"), new Cookie("ideiaSig", AssinaturaCaixa.assinar("5")))
				.param("name", "")
				.param("age", "30"))
				.andExpect(status().is3xxRedirection());
		verify(service, never()).savePersona(any());
	}

	@Test
	public void idadeNaoNumerica_naoQuebra_naoSalva() throws Exception { // BindingResult evita 400
		mvc.perform(post("/persona/salvar-persona")
				.cookie(new Cookie("ideiaId", "5"), new Cookie("ideiaSig", AssinaturaCaixa.assinar("5")))
				.param("name", "Joao")
				.param("age", "abc"))
				.andExpect(status().is3xxRedirection()); // redireciona, NAO estoura 400
		verify(service, never()).savePersona(any());
	}

	@Test
	public void semCookieDeIdeia_naoSalva() throws Exception {
		mvc.perform(post("/persona/salvar-persona")
				.param("name", "Joao")
				.param("age", "30"))
				.andExpect(view().name("redirect")); // tela de erro "redirect", nao salva
		verify(service, never()).savePersona(any());
	}
}
