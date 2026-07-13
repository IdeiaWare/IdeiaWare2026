package edu.unisc.lic.servlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Test;

import edu.unisc.lic.classes.StatusIdeia;
import edu.unisc.lic.dao.IdeiaDAO;
import edu.unisc.lic.dao.IdeiaUsuarioDAO;
import edu.unisc.lic.dao.StorytellingDAO;
import edu.unisc.lic.dao.UsuarioDAO;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.IdeiaUsuario;
import edu.unisc.lic.domain.Storytelling;
import edu.unisc.lic.domain.Usuario;

// TEST-04, Tier 3: FinalizarColaboracaoServlet (RET-12/RET-14) -- exige lideranca, nao re-finaliza, cria Storytelling sem duplicar (COL-11).
public class FinalizarColaboracaoServletTest {

	private final IdeiaDAO ideiaDAO = new IdeiaDAO();
	private final UsuarioDAO usuarioDAO = new UsuarioDAO();
	private final IdeiaUsuarioDAO ideiaUsuarioDAO = new IdeiaUsuarioDAO();
	private final StorytellingDAO storytellingDAO = new StorytellingDAO();

	private Usuario novoUsuario(String nome) {
		Usuario u = new Usuario(nome, nome + "_" + System.nanoTime(), "s", "usr", nome + "_" + System.nanoTime() + "@x.com");
		usuarioDAO.salvar(u);
		return u;
	}

	private Ideia novaIdeia(Usuario autor, String status) {
		Ideia ideia = new Ideia(autor, "Ideia Finalizar", "desc", status, StatusIdeia.GRUPO_ABERTO);
		ideia.setDtCriacao();
		ideiaDAO.salvar(ideia);
		return ideia;
	}

	private HttpServletRequest mockRequest(Long codigoUsuario, boolean comSessao, String ideiaId) {
		HttpServletRequest request = mock(HttpServletRequest.class);
		if (!comSessao) {
			when(request.getSession(false)).thenReturn(null);
		} else {
			HttpSession session = mock(HttpSession.class);
			when(request.getSession(false)).thenReturn(session);
			when(session.getAttribute("codigoUsuario")).thenReturn(codigoUsuario);
		}
		when(request.getParameter("ideiaId")).thenReturn(ideiaId);
		when(request.getContextPath()).thenReturn("");
		return request;
	}

	// getRealPath("") pra criar diretorios de imagens da NPE sem init() -- so o fluxo feliz precisa disso.
	private FinalizarColaboracaoServlet novoServletComContexto() throws Exception {
		FinalizarColaboracaoServlet servlet = new FinalizarColaboracaoServlet();
		ServletConfig config = mock(ServletConfig.class);
		ServletContext context = mock(ServletContext.class);
		when(config.getServletContext()).thenReturn(context);
		when(context.getRealPath("")).thenReturn(System.getProperty("java.io.tmpdir"));
		servlet.init(config);
		return servlet;
	}

	@Test
	public void semLogin_redirecionaParaLogin() throws Exception {
		HttpServletRequest request = mockRequest(null, false, "1");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new FinalizarColaboracaoServlet().doPost(request, response);

		verify(response).sendRedirect(contains("login.jsp"));
	}

	@Test
	public void ideiaInexistente_redirecionaParaMinhaIdeia() throws Exception {
		Usuario u = novoUsuario("Solo");
		HttpServletRequest request = mockRequest(u.getCodigo(), true, "abc");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new FinalizarColaboracaoServlet().doPost(request, response);

		verify(response).sendRedirect(contains("minha-ideia.jsp"));
	}

	@Test
	public void ideiaJaAvancadaParaStorytelling_naoRegrideERedirecionaParaMinhaIdeia() throws Exception {
		Usuario autor = novoUsuario("Autor");
		Ideia ideia = novaIdeia(autor, StatusIdeia.STORYTELLING);

		HttpServletRequest request = mockRequest(autor.getCodigo(), true, ideia.getCodigo().toString());
		HttpServletResponse response = mock(HttpServletResponse.class);

		new FinalizarColaboracaoServlet().doPost(request, response);

		verify(response).sendRedirect(contains("minha-ideia.jsp"));
		assertEquals(StatusIdeia.STORYTELLING, ideiaDAO.buscar(ideia.getCodigo()).getStatus());
	}

	@Test
	public void usuarioNaoLider_bloqueadoRedirecionaParaMinhaIdeia() throws Exception {
		Usuario autor = novoUsuario("Autor2");
		Ideia ideia = novaIdeia(autor, StatusIdeia.EM_DESENVOLVIMENTO);
		Usuario naoLider = novoUsuario("NaoLider");
		IdeiaUsuario vinculo = new IdeiaUsuario(naoLider, ideia, "N");
		vinculo.setDtInscricao();
		ideiaUsuarioDAO.salvar(vinculo);

		HttpServletRequest request = mockRequest(naoLider.getCodigo(), true, ideia.getCodigo().toString());
		HttpServletResponse response = mock(HttpServletResponse.class);

		new FinalizarColaboracaoServlet().doPost(request, response);

		verify(response).sendRedirect(contains("minha-ideia.jsp"));
		assertEquals(StatusIdeia.EM_DESENVOLVIMENTO, ideiaDAO.buscar(ideia.getCodigo()).getStatus());
	}

	@Test
	public void lider_finalizaColaboracaoAvancaParaStorytellingECriaStorytelling() throws Exception {
		Usuario autor = novoUsuario("Autor3");
		Ideia ideia = novaIdeia(autor, StatusIdeia.EM_DESENVOLVIMENTO);
		IdeiaUsuario vinculo = new IdeiaUsuario(autor, ideia, "S");
		vinculo.setDtInscricao();
		ideiaUsuarioDAO.salvar(vinculo);

		HttpServletRequest request = mockRequest(autor.getCodigo(), true, ideia.getCodigo().toString());
		HttpServletResponse response = mock(HttpServletResponse.class);

		novoServletComContexto().doPost(request, response);

		verify(response).sendRedirect(contains("minha-ideia.jsp"));
		assertEquals(StatusIdeia.STORYTELLING, ideiaDAO.buscar(ideia.getCodigo()).getStatus());

		Storytelling filtro = new Storytelling();
		filtro.setIdeia(ideia);
		List<Storytelling> criados = storytellingDAO.listarParametro(filtro);
		assertEquals(1, criados.size());
	}

	// K.8 #3: UNIQUE(ideia_codigo) em Storytelling + catch no servlet -- prova que 2 submits simultaneos nao duplicam.
	@Test
	public void doisSubmitsSimultaneos_soUmStorytellingEhCriado() throws Exception {
		Usuario autor = novoUsuario("AutC");
		Ideia ideia = novaIdeia(autor, StatusIdeia.EM_DESENVOLVIMENTO);
		IdeiaUsuario vinculo = new IdeiaUsuario(autor, ideia, "S");
		vinculo.setDtInscricao();
		ideiaUsuarioDAO.salvar(vinculo);

		CyclicBarrier largada = new CyclicBarrier(2);
		CountDownLatch fim = new CountDownLatch(2);
		AtomicReference<Throwable> escapouThread1 = new AtomicReference<>();
		AtomicReference<Throwable> escapouThread2 = new AtomicReference<>();

		new Thread(criarTarefa(autor, ideia, largada, fim, escapouThread1)).start();
		new Thread(criarTarefa(autor, ideia, largada, fim, escapouThread2)).start();

		assertTrue("as 2 threads devem terminar", fim.await(10, TimeUnit.SECONDS));
		assertNull("doPost nao deve deixar excecao escapar (thread 1)", escapouThread1.get());
		assertNull("doPost nao deve deixar excecao escapar (thread 2)", escapouThread2.get());

		Storytelling filtro = new Storytelling();
		filtro.setIdeia(ideia);
		List<Storytelling> criados = storytellingDAO.listarParametro(filtro);
		assertEquals("so 1 storytelling deve existir, mesmo com a corrida", 1, criados.size());
	}

	private Runnable criarTarefa(Usuario autor, Ideia ideia, CyclicBarrier largada, CountDownLatch fim,
			AtomicReference<Throwable> escapou) {
		return () -> {
			try {
				HttpServletRequest request = mockRequest(autor.getCodigo(), true, ideia.getCodigo().toString());
				HttpServletResponse response = mock(HttpServletResponse.class);
				FinalizarColaboracaoServlet servlet = novoServletComContexto();
				largada.await(5, TimeUnit.SECONDS);
				servlet.doPost(request, response);
			} catch (Exception t) {
				escapou.set(t);
			} finally {
				fim.countDown();
			}
		};
	}
}
