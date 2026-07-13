package br.unisc.toolkit.controller;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.servlet.http.Cookie;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import br.unisc.toolkit.classes.AssinaturaCaixa;
import br.unisc.toolkit.entity.PointOfView;
import br.unisc.toolkit.service.ExportFileService;
import br.unisc.toolkit.service.PersonaService;
import br.unisc.toolkit.service.PointOfViewService;

// TEST-02: PointOfView via MockMvc -- backstop server-side (exige >=1 texto e >=1 persona).
public class PointOfViewControllerTest {

	private MockMvc mvc;
	private PointOfViewService povService;

	@Before
	public void setup() {
		PointOfViewController controller = new PointOfViewController();
		povService = mock(PointOfViewService.class);
		ReflectionTestUtils.setField(controller, "pointOfViewService", povService);
		ReflectionTestUtils.setField(controller, "personaService", mock(PersonaService.class));
		ReflectionTestUtils.setField(controller, "exportFileService", mock(ExportFileService.class));
		mvc = MockMvcBuilders.standaloneSetup(controller).build();
	}

	@Test
	public void povValido_salva() throws Exception {
		mvc.perform(post("/point-of-view/salvar-pov")
				.cookie(new Cookie("ideiaId", "5"), new Cookie("ideiaSig", AssinaturaCaixa.assinar("5")))
				.param("userText", "O usuario quer X")
				.param("personasId", "1"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/point-of-view/lista"));
		// TK-TXN: save + reassociar consolidados em criarComPersonas (controller nao chama savePOV() direto).
		verify(povService).criarComPersonas(any(PointOfView.class));
	}

	@Test
	public void povSemTexto_naoSalva() throws Exception {
		mvc.perform(post("/point-of-view/salvar-pov")
				.cookie(new Cookie("ideiaId", "5"), new Cookie("ideiaSig", AssinaturaCaixa.assinar("5")))
				.param("userText", "").param("needText", "").param("insightText", "")
				.param("personasId", "1"))
				.andExpect(status().is3xxRedirection());
		verify(povService, never()).criarComPersonas(any());
	}

	@Test
	public void povSemPersona_naoSalva() throws Exception {
		mvc.perform(post("/point-of-view/salvar-pov")
				.cookie(new Cookie("ideiaId", "5"), new Cookie("ideiaSig", AssinaturaCaixa.assinar("5")))
				.param("userText", "tem texto, mas sem persona"))
				.andExpect(status().is3xxRedirection());
		verify(povService, never()).criarComPersonas(any());
	}
}
