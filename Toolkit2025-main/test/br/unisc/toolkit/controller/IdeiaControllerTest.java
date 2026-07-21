package br.unisc.toolkit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import br.unisc.toolkit.classes.AssinaturaCaixa;
import br.unisc.toolkit.entity.Ideia;
import br.unisc.toolkit.service.IdeiaService;

// TEST-0X: IdeiaController -- trava o TK-26 (finalize e POST-only, o mais grave dos 9 -- link
// fica no header presente em TODA pagina do modulo), TK-02 (ideia sumida do cookie nao quebra
// com NPE) e TK-11 (tela de confirmacao em vez de redirect seco).
public class IdeiaControllerTest {

	private MockMvc mvc;
	private IdeiaService service;

	@Before
	public void setup() {
		IdeiaController controller = new IdeiaController();
		service = mock(IdeiaService.class);
		ReflectionTestUtils.setField(controller, "ideiaService", service);
		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
		resolver.setPrefix("/WEB-INF/view/");
		resolver.setSuffix(".jsp");
		mvc = MockMvcBuilders.standaloneSetup(controller).setViewResolvers(resolver).build();
	}

	@Test
	public void semCookie_naoFinalizaERedireciona() throws Exception {
		mvc.perform(post("/ideia/finalize"))
				.andExpect(view().name("redirect"));
		verify(service, never()).finalize(any());
	}

	@Test
	public void ideiaDoCookieNaoExisteMais_naoFinalizaERedireciona() throws Exception { // TK-02
		when(service.getIdeia(5L)).thenReturn(null);

		mvc.perform(post("/ideia/finalize")
				.cookie(new Cookie("ideiaId", "5"), new Cookie("ideiaSig", AssinaturaCaixa.assinar("5"))))
				.andExpect(view().name("redirect"));
		verify(service, never()).finalize(any());
	}

	@Test
	public void ideiaValida_finalizaEMostraTelaDeConfirmacao() throws Exception { // TK-11
		// UX-TOOLKIT-FINALIZE-TRAVADO: status CF libera o guard novo (StatusGuard.podeEscrever).
		Ideia ideia = new Ideia();
		ideia.setStatus("CF");
		when(service.getIdeia(5L)).thenReturn(ideia);

		mvc.perform(post("/ideia/finalize")
				.cookie(new Cookie("ideiaId", "5"), new Cookie("ideiaSig", AssinaturaCaixa.assinar("5"))))
				.andExpect(status().isOk())
				.andExpect(view().name("finalize"));
		verify(service).finalize(ideia);
	}

	@Test
	public void ideiaForaDaEtapaCF_naoFinaliza() throws Exception { // UX-TOOLKIT-FINALIZE-TRAVADO
		Ideia ideia = new Ideia();
		ideia.setStatus("CV"); // ja passou da Caixa de Ferramentas
		when(service.getIdeia(5L)).thenReturn(ideia);

		mvc.perform(post("/ideia/finalize")
				.cookie(new Cookie("ideiaId", "5"), new Cookie("ideiaSig", AssinaturaCaixa.assinar("5"))))
				.andExpect(status().is3xxRedirection())
				// UX-PADRAO-ETAPA-FINALIZADA: antes caia na view "redirect", que terminava no login do
				// LIC; agora passa pelo hop /aviso-etapa-encerrada (alert() e SO' DEPOIS redireciona).
				.andExpect(redirectedUrl("/aviso-etapa-encerrada"))
				.andExpect(flash().attribute("etapaEncerradaErro", "Esta etapa já foi encerrada."))
				.andExpect(flash().attribute("redirecionarPara", "/LIC/minha-ideia.jsp"));
		verify(service, never()).finalize(any());
	}
}
