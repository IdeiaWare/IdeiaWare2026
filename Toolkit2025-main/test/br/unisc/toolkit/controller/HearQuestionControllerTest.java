package br.unisc.toolkit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import br.unisc.toolkit.service.EmpathyService;

// TEST-02: HearQuestionController -- valida o backstop server-side (TK-VAL) e o TK-ATTR
// (atributo "hear" forcado no servidor) via MockMvc.
public class HearQuestionControllerTest {

	private MockMvc mvc;
	private EmpathyService service;

	@Before
	public void setup() {
		HearQuestionController controller = new HearQuestionController();
		service = mock(EmpathyService.class);
		ReflectionTestUtils.setField(controller, "empathyService", service);
		mvc = MockMvcBuilders.standaloneSetup(controller).build();
	}

	@Test
	public void atributoValido_salva() throws Exception {
		mvc.perform(post("/persona/empatia/o-que-escuta/save-attribute")
				.cookie(new Cookie("ideiaId", "5"), new Cookie("ideiaSig", AssinaturaCaixa.assinar("5")))
				.param("attributeText", "Ouve a familia")
				.param("personaId", "1"))
				.andExpect(status().is3xxRedirection());
		verify(service).saveEmpathyAttribute(any(Empathy.class));
	}

	@Test
	public void atributoVazio_naoSalva() throws Exception {
		mvc.perform(post("/persona/empatia/o-que-escuta/save-attribute")
				.cookie(new Cookie("ideiaId", "5"), new Cookie("ideiaSig", AssinaturaCaixa.assinar("5")))
				.param("attributeText", "   ")
				.param("personaId", "1"))
				.andExpect(status().is3xxRedirection());
		verify(service, never()).saveEmpathyAttribute(any());
	}

	@Test
	public void semCookie_naoSalva() throws Exception {
		mvc.perform(post("/persona/empatia/o-que-escuta/save-attribute")
				.param("attributeText", "x").param("personaId", "1"))
				.andExpect(view().name("redirect"));
		verify(service, never()).saveEmpathyAttribute(any());
	}

	@Test
	public void attributeForcadoNoServidor_ignoraValorDoCliente() throws Exception { // TK-ATTR
		mvc.perform(post("/persona/empatia/o-que-escuta/save-attribute")
				.cookie(new Cookie("ideiaId", "5"), new Cookie("ideiaSig", AssinaturaCaixa.assinar("5")))
				.param("attributeText", "Texto valido")
				.param("personaId", "1")
				.param("attribute", "hacked"))
				.andExpect(status().is3xxRedirection());

		ArgumentCaptor<Empathy> captor = ArgumentCaptor.forClass(Empathy.class);
		verify(service).saveEmpathyAttribute(captor.capture());
		org.junit.Assert.assertEquals("hear", captor.getValue().getAttribute());
	}
}
