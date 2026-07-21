package br.unisc.toolkit.controller;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.eq;
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

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.Cookie;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import br.unisc.toolkit.classes.AssinaturaCaixa;
import br.unisc.toolkit.entity.ExportFile;
import br.unisc.toolkit.entity.Ideia;
import br.unisc.toolkit.entity.Persona;
import br.unisc.toolkit.entity.PointOfView;
import br.unisc.toolkit.service.IdeiaService;
import br.unisc.toolkit.service.ExportFileService;
import br.unisc.toolkit.service.PersonaService;
import br.unisc.toolkit.service.PointOfViewService;

// TEST-02: PointOfView via MockMvc -- backstop server-side (exige >=1 texto e >=1 persona).
public class PointOfViewControllerTest {

	private MockMvc mvc;
	private PointOfViewService povService;
	private IdeiaService ideiaService;
	private ExportFileService exportFileService;
	private PersonaService personaService;

	@Before
	public void setup() {
		System.setProperty("ideiaware.exports.dir", System.getProperty("java.io.tmpdir"));
		PointOfViewController controller = new PointOfViewController();
		povService = mock(PointOfViewService.class);
		ReflectionTestUtils.setField(controller, "pointOfViewService", povService);
		personaService = mock(PersonaService.class);
		ReflectionTestUtils.setField(controller, "personaService", personaService);
		exportFileService = mock(ExportFileService.class);
		ReflectionTestUtils.setField(controller, "exportFileService", exportFileService);
		// UX-TOOLKIT-STATUS-GUARD: ideia mockada com status CF (Caixa de Ferramentas) libera escrita.
		ideiaService = mock(IdeiaService.class);
		Ideia ideiaCF = new Ideia();
		ideiaCF.setStatus("CF");
		when(ideiaService.getIdeia(any())).thenReturn(ideiaCF);
		ReflectionTestUtils.setField(controller, "ideiaService", ideiaService);
		mvc = MockMvcBuilders.standaloneSetup(controller).build();
	}

	private Object[] linhaPov(int id, String names, int personaID, String user, String need, String insight, int povID) {
		return new Object[] { id, names, personaID, user, need, insight, povID };
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

	@Test
	public void ideiaForaDaEtapaCF_naoSalva() throws Exception { // UX-TOOLKIT-STATUS-GUARD
		Ideia ideiaCanvas = new Ideia();
		ideiaCanvas.setStatus("CV");
		when(ideiaService.getIdeia(any())).thenReturn(ideiaCanvas);

		mvc.perform(post("/point-of-view/salvar-pov")
				.cookie(new Cookie("ideiaId", "5"), new Cookie("ideiaSig", AssinaturaCaixa.assinar("5")))
				.param("userText", "O usuario quer X")
				.param("personasId", "1"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/aviso-etapa-encerrada")) // UX-PADRAO-ETAPA-FINALIZADA
				.andExpect(flash().attribute("etapaEncerradaErro", "Esta etapa já foi encerrada."))
				.andExpect(flash().attribute("redirecionarPara", "/LIC/minha-ideia.jsp"));
		verify(povService, never()).criarComPersonas(any());
	}

	@Test
	public void exportarGeral_comCookieEEtapaCF_salva() throws Exception { // UX-TOOLKIT-EXPORT-TRAVADO
		String base64 = Base64.getEncoder().encodeToString("conteudo-pdf-fake".getBytes(StandardCharsets.UTF_8));
		mvc.perform(post("/point-of-view/exportar-geral")
				.cookie(new Cookie("ideiaId", "5"), new Cookie("ideiaSig", AssinaturaCaixa.assinar("5")))
				.param("fileLocation", "data:application/pdf;base64," + base64))
				.andExpect(status().is3xxRedirection());
		verify(exportFileService).saveFile(any(ExportFile.class));
	}

	@Test
	public void exportarGeral_ideiaForaDaEtapaCF_naoSalva() throws Exception { // UX-TOOLKIT-EXPORT-TRAVADO
		Ideia ideiaCanvas = new Ideia();
		ideiaCanvas.setStatus("CV");
		when(ideiaService.getIdeia(any())).thenReturn(ideiaCanvas);

		String base64 = Base64.getEncoder().encodeToString("conteudo-pdf-fake".getBytes(StandardCharsets.UTF_8));
		mvc.perform(post("/point-of-view/exportar-geral")
				.cookie(new Cookie("ideiaId", "5"), new Cookie("ideiaSig", AssinaturaCaixa.assinar("5")))
				.param("fileLocation", "data:application/pdf;base64," + base64))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/aviso-etapa-encerrada")) // UX-PADRAO-ETAPA-FINALIZADA
				.andExpect(flash().attribute("etapaEncerradaErro", "Esta etapa já foi encerrada."))
				.andExpect(flash().attribute("redirecionarPara", "/LIC/minha-ideia.jsp"));
		verify(exportFileService, never()).saveFile(any());
	}

	@Test
	public void povSemIdMasNaoPertenceAIdeia_naoSalva() throws Exception { // SEC-24, caminho novo (id sempre 0 em criacao)
		mvc.perform(post("/point-of-view/salvar-pov")
				.cookie(new Cookie("ideiaId", "5"), new Cookie("ideiaSig", AssinaturaCaixa.assinar("5")))
				.param("userText", "texto valido")
				.param("personasId", "1"))
				.andExpect(status().is3xxRedirection());
		// id nao informado (default 0) pula a checagem SEC-24 e cai direto na validacao -- cobre o ramo id==0.
		verify(povService, never()).povPertenceAIdeia(org.mockito.ArgumentMatchers.anyInt(), any());
	}

	@Test
	public void criarPov_semCookie_redireciona() throws Exception { // TK-COOKIE-GUARD
		mvc.perform(get("/point-of-view/criar-pov").param("personas", "1", "2"))
				.andExpect(view().name("redirect"));
	}

	@Test
	public void criarPov_comCookie_mostraFormulario() throws Exception {
		mvc.perform(get("/point-of-view/criar-pov")
				.cookie(new Cookie("ideiaId", "5"), new Cookie("ideiaSig", AssinaturaCaixa.assinar("5")))
				.param("personas", "1", "2"))
				.andExpect(status().isOk())
				.andExpect(view().name("form-pov"));
	}

	@Test
	public void lista_semCookie_redireciona() throws Exception {
		mvc.perform(get("/point-of-view/lista"))
				.andExpect(view().name("redirect"));
	}

	@Test
	public void lista_comCookieSemPovs_mostraListaVazia() throws Exception {
		when(povService.getPointOfViews(5L)).thenReturn(Collections.emptyList());
		when(personaService.getPersonas(5L)).thenReturn(Collections.<Persona>emptyList());

		mvc.perform(get("/point-of-view/lista")
				.cookie(new Cookie("ideiaId", "5"), new Cookie("ideiaSig", AssinaturaCaixa.assinar("5"))))
				.andExpect(status().isOk())
				.andExpect(view().name("list-point-of-view"));
	}

	@Test
	public void lista_comDuasPersonasNoMesmoPov_agrupaNumUnicoItem() throws Exception {
		// mesma linha de raciocinio do TK-ORD: 2 linhas com povID=7 (1 por persona) devem virar 1 PointOfViewInfo so.
		List<Object> linhas = Arrays.<Object>asList(
				linhaPov(1, "Joao", 10, "user", "need", "insight", 7),
				linhaPov(2, "Maria", 11, "user", "need", "insight", 7));
		when(povService.getPointOfViews(5L)).thenReturn(linhas);
		when(personaService.getPersonas(5L)).thenReturn(Collections.<Persona>emptyList());

		org.springframework.test.web.servlet.MvcResult result = mvc.perform(get("/point-of-view/lista")
				.cookie(new Cookie("ideiaId", "5"), new Cookie("ideiaSig", AssinaturaCaixa.assinar("5"))))
				.andExpect(status().isOk())
				.andExpect(view().name("list-point-of-view"))
				.andReturn();

		java.util.Map<?, ?> povs = (java.util.Map<?, ?>) result.getModelAndView().getModel().get("povs");
		org.junit.Assert.assertEquals("as 2 linhas (mesmo povID) devem virar 1 item agrupado", 1, povs.size());
	}

	@Test
	public void visaoGeral_semCookie_redireciona() throws Exception { // SEC-23
		mvc.perform(get("/point-of-view/visao-geral").param("povId", "7"))
				.andExpect(view().name("redirect"));
	}

	@Test
	public void visaoGeral_comCookieComItem_mostraOverview() throws Exception {
		List<Object> linhas = Collections.<Object>singletonList(
				linhaPov(1, "Joao", 10, "user", "need", "insight", 7));
		when(povService.getSpecificPointOfView(7, 5L)).thenReturn(linhas);

		mvc.perform(get("/point-of-view/visao-geral")
				.cookie(new Cookie("ideiaId", "5"), new Cookie("ideiaSig", AssinaturaCaixa.assinar("5")))
				.param("povId", "7"))
				.andExpect(status().isOk())
				.andExpect(view().name("point-of-view-overview"));
	}

	@Test
	public void atualizar_comCookieEEtapaCF_atualiza() throws Exception {
		mvc.perform(post("/point-of-view/atualizar")
				.cookie(new Cookie("ideiaId", "5"), new Cookie("ideiaSig", AssinaturaCaixa.assinar("5")))
				.param("userText", "texto atualizado")
				.param("personasId", "1"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/point-of-view/lista"));
		verify(povService).atualizarComPersonas(any(PointOfView.class));
	}

	@Test
	public void atualizar_ideiaForaDaEtapaCF_naoAtualiza() throws Exception { // UX-TOOLKIT-STATUS-GUARD
		Ideia ideiaCanvas = new Ideia();
		ideiaCanvas.setStatus("CV");
		when(ideiaService.getIdeia(any())).thenReturn(ideiaCanvas);

		mvc.perform(post("/point-of-view/atualizar")
				.cookie(new Cookie("ideiaId", "5"), new Cookie("ideiaSig", AssinaturaCaixa.assinar("5")))
				.param("userText", "texto atualizado")
				.param("personasId", "1"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/aviso-etapa-encerrada")) // UX-PADRAO-ETAPA-FINALIZADA
				.andExpect(flash().attribute("etapaEncerradaErro", "Esta etapa já foi encerrada."))
				.andExpect(flash().attribute("redirecionarPara", "/LIC/minha-ideia.jsp"));
		verify(povService, never()).atualizarComPersonas(any());
	}

	@Test
	public void atualizar_povNaoPertenceAIdeia_naoAtualiza() throws Exception { // SEC-24
		when(povService.povPertenceAIdeia(7, 5L)).thenReturn(false);

		mvc.perform(post("/point-of-view/atualizar")
				.cookie(new Cookie("ideiaId", "5"), new Cookie("ideiaSig", AssinaturaCaixa.assinar("5")))
				.param("id", "7")
				.param("userText", "texto atualizado")
				.param("personasId", "1"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/point-of-view/lista"));
		verify(povService, never()).atualizarComPersonas(any());
	}

	@Test
	public void atualizar_semTextoValido_naoAtualiza() throws Exception { // TK-VAL
		mvc.perform(post("/point-of-view/atualizar")
				.cookie(new Cookie("ideiaId", "5"), new Cookie("ideiaSig", AssinaturaCaixa.assinar("5")))
				.param("personasId", "1"))
				.andExpect(status().is3xxRedirection());
		verify(povService, never()).atualizarComPersonas(any());
	}

	@Test
	public void deletar_comCookieEEtapaCF_deleta() throws Exception {
		mvc.perform(post("/point-of-view/deletar")
				.cookie(new Cookie("ideiaId", "5"), new Cookie("ideiaSig", AssinaturaCaixa.assinar("5")))
				.param("povId", "7"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/point-of-view/lista"));
		verify(povService).deletePointOfView(7, 5L);
	}

	@Test
	public void deletar_semCookie_naoDeleta() throws Exception { // TK-03
		mvc.perform(post("/point-of-view/deletar").param("povId", "7"))
				.andExpect(status().is3xxRedirection());
		verify(povService, never()).deletePointOfView(org.mockito.ArgumentMatchers.anyInt(), any());
	}

	@Test
	public void deletar_ideiaForaDaEtapaCF_naoDeleta() throws Exception { // UX-TOOLKIT-STATUS-GUARD
		Ideia ideiaCanvas = new Ideia();
		ideiaCanvas.setStatus("CV");
		when(ideiaService.getIdeia(any())).thenReturn(ideiaCanvas);

		mvc.perform(post("/point-of-view/deletar")
				.cookie(new Cookie("ideiaId", "5"), new Cookie("ideiaSig", AssinaturaCaixa.assinar("5")))
				.param("povId", "7"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/aviso-etapa-encerrada")) // UX-PADRAO-ETAPA-FINALIZADA
				.andExpect(flash().attribute("etapaEncerradaErro", "Esta etapa já foi encerrada."))
				.andExpect(flash().attribute("redirecionarPara", "/LIC/minha-ideia.jsp"));
		verify(povService, never()).deletePointOfView(org.mockito.ArgumentMatchers.anyInt(), any());
	}
}
