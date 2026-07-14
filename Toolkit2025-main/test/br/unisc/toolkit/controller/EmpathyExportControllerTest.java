package br.unisc.toolkit.controller;

import static java.util.Collections.emptyList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.servlet.http.Cookie;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import br.unisc.toolkit.classes.AssinaturaCaixa;
import br.unisc.toolkit.entity.ExportFile;
import br.unisc.toolkit.entity.Persona;
import br.unisc.toolkit.service.EmpathyService;
import br.unisc.toolkit.service.ExportFileService;
import br.unisc.toolkit.service.PersonaService;

// TEST-0X: EmpathyExportController -- trava o TK-EMP-GUARD (guard de cookie explicito nas 2
// telas de visao, adicionado nesta rodada -- antes sem cookie a JSP explodia em vez de
// redirecionar) e o TK-02 (persona sumida nao quebra com NPE, so redireciona).
public class EmpathyExportControllerTest {

	private MockMvc mvc;
	private EmpathyService empathyService;
	private PersonaService personaService;
	private ExportFileService exportFileService;

	@Before
	public void setup() {
		System.setProperty("ideiaware.exports.dir", System.getProperty("java.io.tmpdir"));
		EmpathyExportController controller = new EmpathyExportController();
		empathyService = mock(EmpathyService.class);
		personaService = mock(PersonaService.class);
		exportFileService = mock(ExportFileService.class);
		ReflectionTestUtils.setField(controller, "empathyService", empathyService);
		ReflectionTestUtils.setField(controller, "personaService", personaService);
		ReflectionTestUtils.setField(controller, "exportFileService", exportFileService);
		mvc = MockMvcBuilders.standaloneSetup(controller).build();
	}

	@Test
	public void visaoGeral_semCookie_redireciona() throws Exception { // TK-EMP-GUARD
		mvc.perform(get("/persona/empatia/visao-geral").param("personaId", "1"))
				.andExpect(view().name("redirect"));
		verify(personaService, never()).getPersona(anyInt(), anyLong());
	}

	@Test
	public void visaoGeral_personaNaoEncontrada_redireciona() throws Exception { // TK-02
		when(personaService.getPersona(1, 5L)).thenReturn(null);

		mvc.perform(get("/persona/empatia/visao-geral")
				.cookie(new Cookie("ideiaId", "5"), new Cookie("ideiaSig", AssinaturaCaixa.assinar("5")))
				.param("personaId", "1"))
				.andExpect(view().name("redirect"));
	}

	@Test
	public void visaoGeral_comCookieEPersonaValida_mostraView() throws Exception {
		when(personaService.getPersona(1, 5L)).thenReturn(new Persona("Joao", 30));
		when(empathyService.getAttributes(anyInt(), anyString(), anyLong())).thenReturn(emptyList());

		mvc.perform(get("/persona/empatia/visao-geral")
				.cookie(new Cookie("ideiaId", "5"), new Cookie("ideiaSig", AssinaturaCaixa.assinar("5")))
				.param("personaId", "1"))
				.andExpect(status().isOk())
				.andExpect(view().name("empathy-map-overview"));
	}

	@Test
	public void visaoDetalhada_semCookie_redireciona() throws Exception { // TK-EMP-GUARD
		mvc.perform(get("/persona/empatia/visao-detalhada").param("personaId", "1"))
				.andExpect(view().name("redirect"));
		verify(personaService, never()).getPersona(anyInt(), anyLong());
	}

	@Test
	public void exportarGeral_semCookie_naoSalva() throws Exception {
		mvc.perform(post("/persona/empatia/exportar-geral"))
				.andExpect(view().name("redirect"));
		verify(exportFileService, never()).saveFile(any(ExportFile.class));
	}

	@Test
	public void exportarGeral_comCookie_salvaERedireciona() throws Exception {
		String base64 = Base64.getEncoder().encodeToString("conteudo-pdf-fake".getBytes(StandardCharsets.UTF_8));
		mvc.perform(post("/persona/empatia/exportar-geral")
				.cookie(new Cookie("ideiaId", "5"), new Cookie("ideiaSig", AssinaturaCaixa.assinar("5")))
				.param("fileLocation", "data:application/pdf;base64," + base64))
				.andExpect(status().is3xxRedirection());
		verify(exportFileService).saveFile(any(ExportFile.class));
	}
}
