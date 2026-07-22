package br.unisc.toolkit.controller;

import static org.mockito.ArgumentMatchers.any;
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
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import br.unisc.toolkit.classes.AssinaturaCaixa;
import br.unisc.toolkit.entity.Empathy;
import br.unisc.toolkit.entity.Ideia;
import br.unisc.toolkit.service.IdeiaService;
import br.unisc.toolkit.service.EmpathyService;

// TEST-02: SayDoQuestionController -- valida o backstop server-side (TK-VAL) e o TK-ATTR via MockMvc.
public class SayDoQuestionControllerTest {

	private MockMvc mvc;
	private EmpathyService service;
	private IdeiaService ideiaService;

	@Before
	public void setup() {
		SayDoQuestionController controller = new SayDoQuestionController();
		service = mock(EmpathyService.class);
		ReflectionTestUtils.setField(controller, "empathyService", service);
		ideiaService = mock(IdeiaService.class);
		Ideia ideiaCF = new Ideia();
		ideiaCF.setStatus("CF");
		when(ideiaService.getIdeia(any())).thenReturn(ideiaCF);
		ReflectionTestUtils.setField(controller, "ideiaService", ideiaService);
		mvc = MockMvcBuilders.standaloneSetup(controller).build();
	}

	@Test
	public void mostrarPergunta_semCookie_redireciona() throws Exception {
		mvc.perform(get("/persona/empatia/o-que-diz-e-faz").param("personaId", "1"))
				.andExpect(view().name("redirect"));
	}

	@Test
	public void mostrarPergunta_comCookie_mostraView() throws Exception {
		when(service.getAttributes(1, "say_do", 5L)).thenReturn(java.util.Collections.<Empathy>emptyList());

		mvc.perform(get("/persona/empatia/o-que-diz-e-faz")
				.cookie(new Cookie("ideiaId", "5"), new Cookie("ideiaSig", AssinaturaCaixa.assinar("5")))
				.param("personaId", "1"))
				.andExpect(status().isOk())
				.andExpect(view().name("say-and-do"));
	}

	@Test
	public void ideiaForaDaEtapaCF_naoSalva() throws Exception { // UX-TOOLKIT-STATUS-GUARD
		Ideia ideiaCanvas = new Ideia();
		ideiaCanvas.setStatus("CV");
		when(ideiaService.getIdeia(any())).thenReturn(ideiaCanvas);

		mvc.perform(post("/persona/empatia/o-que-diz-e-faz/save-attribute")
				.cookie(new Cookie("ideiaId", "5"), new Cookie("ideiaSig", AssinaturaCaixa.assinar("5")))
				.param("attributeText", "Fala com os amigos")
				.param("personaId", "1"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/aviso-etapa-encerrada")) // UX-PADRAO-ETAPA-FINALIZADA
				.andExpect(flash().attribute("etapaEncerradaErro", "Esta etapa já foi encerrada."))
				.andExpect(flash().attribute("redirecionarPara", "/LIC/minha-ideia.jsp"));
		verify(service, never()).saveEmpathyAttribute(any());
	}

	@Test
	public void deletar_comCookieEEtapaCF_deleta() throws Exception {
		mvc.perform(post("/persona/empatia/o-que-diz-e-faz/delete")
				.cookie(new Cookie("ideiaId", "5"), new Cookie("ideiaSig", AssinaturaCaixa.assinar("5")))
				.param("personaId", "1")
				.param("attributeId", "9"))
				.andExpect(status().is3xxRedirection());
		verify(service).deleteAttribute(9, 5L);
	}

	@Test
	public void deletar_semCookie_naoDeleta() throws Exception { // TK-03
		mvc.perform(post("/persona/empatia/o-que-diz-e-faz/delete")
				.param("personaId", "1")
				.param("attributeId", "9"))
				.andExpect(status().is3xxRedirection());
		verify(service, never()).deleteAttribute(org.mockito.ArgumentMatchers.anyInt(), any());
	}

	@Test
	public void deletar_ideiaForaDaEtapaCF_naoDeleta() throws Exception { // UX-TOOLKIT-STATUS-GUARD
		Ideia ideiaCanvas = new Ideia();
		ideiaCanvas.setStatus("CV");
		when(ideiaService.getIdeia(any())).thenReturn(ideiaCanvas);

		mvc.perform(post("/persona/empatia/o-que-diz-e-faz/delete")
				.cookie(new Cookie("ideiaId", "5"), new Cookie("ideiaSig", AssinaturaCaixa.assinar("5")))
				.param("personaId", "1")
				.param("attributeId", "9"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/aviso-etapa-encerrada")) // UX-PADRAO-ETAPA-FINALIZADA
				.andExpect(flash().attribute("etapaEncerradaErro", "Esta etapa já foi encerrada."))
				.andExpect(flash().attribute("redirecionarPara", "/LIC/minha-ideia.jsp"));
		verify(service, never()).deleteAttribute(org.mockito.ArgumentMatchers.anyInt(), any());
	}

	@Test
	public void atributoValido_salva() throws Exception {
		mvc.perform(post("/persona/empatia/o-que-diz-e-faz/save-attribute")
				.cookie(new Cookie("ideiaId", "5"), new Cookie("ideiaSig", AssinaturaCaixa.assinar("5")))
				.param("attributeText", "Fala com os amigos")
				.param("personaId", "1"))
				.andExpect(status().is3xxRedirection());
		verify(service).saveEmpathyAttribute(any(Empathy.class));
	}

	@Test
	public void atributoVazio_naoSalva() throws Exception {
		mvc.perform(post("/persona/empatia/o-que-diz-e-faz/save-attribute")
				.cookie(new Cookie("ideiaId", "5"), new Cookie("ideiaSig", AssinaturaCaixa.assinar("5")))
				.param("attributeText", "   ")
				.param("personaId", "1"))
				.andExpect(status().is3xxRedirection());
		verify(service, never()).saveEmpathyAttribute(any());
	}

	@Test
	public void semCookie_naoSalva() throws Exception {
		mvc.perform(post("/persona/empatia/o-que-diz-e-faz/save-attribute")
				.param("attributeText", "x").param("personaId", "1"))
				.andExpect(view().name("redirect"));
		verify(service, never()).saveEmpathyAttribute(any());
	}

	@Test
	public void attributeForcadoNoServidor_ignoraValorDoCliente() throws Exception { // TK-ATTR
		mvc.perform(post("/persona/empatia/o-que-diz-e-faz/save-attribute")
				.cookie(new Cookie("ideiaId", "5"), new Cookie("ideiaSig", AssinaturaCaixa.assinar("5")))
				.param("attributeText", "Texto valido")
				.param("personaId", "1")
				.param("attribute", "hacked"))
				.andExpect(status().is3xxRedirection());

		ArgumentCaptor<Empathy> captor = ArgumentCaptor.forClass(Empathy.class);
		verify(service).saveEmpathyAttribute(captor.capture());
		org.junit.Assert.assertEquals("say_do", captor.getValue().getAttribute());
	}
}
