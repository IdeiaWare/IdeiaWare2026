package br.unisc.toolkit.controller;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
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
import br.unisc.toolkit.entity.Ideia;
import br.unisc.toolkit.entity.Persona;
import br.unisc.toolkit.service.IdeiaService;
import br.unisc.toolkit.service.PersonaService;

// TEST-02: PersonaController via MockMvc -- trava a validacao server-side + guard de cookie pelo stack real do Spring MVC.
public class PersonaControllerTest {

	private MockMvc mvc;
	private PersonaService service;
	private IdeiaService ideiaService;

	@Before
	public void setup() {
		PersonaController controller = new PersonaController();
		service = mock(PersonaService.class);
		ReflectionTestUtils.setField(controller, "personaService", service);
		// UX-TOOLKIT-STATUS-GUARD: ideia mockada com status CF (Caixa de Ferramentas) libera escrita.
		ideiaService = mock(IdeiaService.class);
		Ideia ideiaCF = new Ideia();
		ideiaCF.setStatus("CF");
		when(ideiaService.getIdeia(any())).thenReturn(ideiaCF);
		ReflectionTestUtils.setField(controller, "ideiaService", ideiaService);
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

	@Test
	public void ideiaForaDaEtapaCF_naoSalva() throws Exception { // UX-TOOLKIT-STATUS-GUARD
		Ideia ideiaCanvas = new Ideia();
		ideiaCanvas.setStatus("CV");
		when(ideiaService.getIdeia(any())).thenReturn(ideiaCanvas);

		mvc.perform(post("/persona/salvar-persona")
				.cookie(new Cookie("ideiaId", "5"), new Cookie("ideiaSig", AssinaturaCaixa.assinar("5")))
				.param("name", "Joao")
				.param("age", "30"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/aviso-etapa-encerrada")) // UX-PADRAO-ETAPA-FINALIZADA
				.andExpect(flash().attribute("etapaEncerradaErro", "Esta etapa já foi encerrada."))
				.andExpect(flash().attribute("redirecionarPara", "/LIC/minha-ideia.jsp"));
		verify(service, never()).savePersona(any());
	}

	@Test
	public void lista_semCookie_redireciona() throws Exception {
		mvc.perform(get("/persona/lista"))
				.andExpect(view().name("redirect"));
	}

	@Test
	public void lista_comCookie_mostraLista() throws Exception {
		when(service.getPersonas(5L)).thenReturn(java.util.Collections.<Persona>emptyList());

		mvc.perform(get("/persona/lista")
				.cookie(new Cookie("ideiaId", "5"), new Cookie("ideiaSig", AssinaturaCaixa.assinar("5"))))
				.andExpect(status().isOk())
				.andExpect(view().name("list-personas"));
	}

	@Test
	public void deletar_comCookieEEtapaCF_deleta() throws Exception {
		mvc.perform(post("/persona/deletar")
				.cookie(new Cookie("ideiaId", "5"), new Cookie("ideiaSig", AssinaturaCaixa.assinar("5")))
				.param("personaId", "3"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/persona/lista"));
		verify(service).deletePersona(3, 5L);
	}

	@Test
	public void deletar_semCookie_naoDeleta() throws Exception { // TK-03
		mvc.perform(post("/persona/deletar").param("personaId", "3"))
				.andExpect(status().is3xxRedirection());
		verify(service, never()).deletePersona(org.mockito.ArgumentMatchers.anyInt(), any());
	}

	@Test
	public void deletar_ideiaForaDaEtapaCF_naoDeleta() throws Exception { // UX-TOOLKIT-STATUS-GUARD
		Ideia ideiaCanvas = new Ideia();
		ideiaCanvas.setStatus("CV");
		when(ideiaService.getIdeia(any())).thenReturn(ideiaCanvas);

		mvc.perform(post("/persona/deletar")
				.cookie(new Cookie("ideiaId", "5"), new Cookie("ideiaSig", AssinaturaCaixa.assinar("5")))
				.param("personaId", "3"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/aviso-etapa-encerrada")) // UX-PADRAO-ETAPA-FINALIZADA
				.andExpect(flash().attribute("etapaEncerradaErro", "Esta etapa já foi encerrada."))
				.andExpect(flash().attribute("redirecionarPara", "/LIC/minha-ideia.jsp"));
		verify(service, never()).deletePersona(org.mockito.ArgumentMatchers.anyInt(), any());
	}

	@Test
	public void empatiaMapa_semCookie_redireciona() throws Exception {
		mvc.perform(get("/persona/empatia/mapa").param("personaId", "3"))
				.andExpect(view().name("redirect"));
	}

	@Test
	public void empatiaMapa_personaNaoEncontrada_redireciona() throws Exception { // TK-02
		when(service.getPersona(3, 5L)).thenReturn(null);

		mvc.perform(get("/persona/empatia/mapa")
				.cookie(new Cookie("ideiaId", "5"), new Cookie("ideiaSig", AssinaturaCaixa.assinar("5")))
				.param("personaId", "3"))
				.andExpect(view().name("redirect"));
	}

	@Test
	public void empatiaMapa_comCookieEPersonaValida_mostraMapa() throws Exception {
		when(service.getPersona(3, 5L)).thenReturn(new Persona("Joao", 30));

		mvc.perform(get("/persona/empatia/mapa")
				.cookie(new Cookie("ideiaId", "5"), new Cookie("ideiaSig", AssinaturaCaixa.assinar("5")))
				.param("personaId", "3"))
				.andExpect(status().isOk())
				.andExpect(view().name("empathy-map"));
	}
}
