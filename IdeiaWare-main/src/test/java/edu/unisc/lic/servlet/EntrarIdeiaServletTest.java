package edu.unisc.lic.servlet;

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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Test;

import edu.unisc.lic.classes.StatusIdeia;
import edu.unisc.lic.dao.IdeiaDAO;
import edu.unisc.lic.dao.IdeiaUsuarioDAO;
import edu.unisc.lic.dao.UsuarioDAO;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.IdeiaUsuario;
import edu.unisc.lic.domain.Usuario;

// TEST-04/K.8 #2: EntrarIdeiaServlet (COLM-05/COL-DUP) -- UNIQUE(usuario_codigo, ideia_codigo) + catch; teste de corrida prova que 2 cliques simultaneos nao duplicam o vinculo.
public class EntrarIdeiaServletTest {

	private final IdeiaDAO ideiaDAO = new IdeiaDAO();
	private final UsuarioDAO usuarioDAO = new UsuarioDAO();
	private final IdeiaUsuarioDAO ideiaUsuarioDAO = new IdeiaUsuarioDAO();

	private Usuario novoUsuario(String nome) {
		Usuario u = new Usuario(nome, nome + "_" + System.nanoTime(), "s", "usr", nome + "_" + System.nanoTime() + "@x.com");
		usuarioDAO.salvar(u);
		return u;
	}

	private Ideia novaIdeia(Usuario autor) {
		Ideia ideia = new Ideia(autor, "Ideia", "desc", StatusIdeia.EM_DESENVOLVIMENTO, StatusIdeia.GRUPO_ABERTO);
		ideia.setDtCriacao();
		ideiaDAO.salvar(ideia);
		return ideia;
	}

	private HttpServletRequest mockRequest(Long codigoUsuario, String codigoIdeia) {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpSession session = mock(HttpSession.class);
		when(request.getSession()).thenReturn(session);
		when(session.getAttribute("codigoUsuario")).thenReturn(codigoUsuario);
		when(request.getParameter("codigo")).thenReturn(codigoIdeia);
		when(request.getContextPath()).thenReturn("");
		return request;
	}

	@Test
	public void semLogin_redirecionaParaLogin() throws Exception {
		HttpServletRequest request = mockRequest(null, "1");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new EntrarIdeiaServlet().doGet(request, response);

		verify(response).sendRedirect(contains("login.jsp"));
	}

	@Test
	public void codigoInvalido_redirecionaParaListaIdeia() throws Exception {
		Usuario u = novoUsuario("Solo");
		HttpServletRequest request = mockRequest(u.getCodigo(), "abc");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new EntrarIdeiaServlet().doGet(request, response);

		verify(response).sendRedirect(contains("lista-ideia.jsp"));
	}

	@Test
	public void primeiraVez_criaVinculoNaoLider() throws Exception {
		Usuario autor = novoUsuario("Autor");
		Ideia ideia = novaIdeia(autor);
		Usuario novoParticipante = novoUsuario("Participante");

		HttpServletRequest request = mockRequest(novoParticipante.getCodigo(), ideia.getCodigo().toString());
		HttpServletResponse response = mock(HttpServletResponse.class);

		new EntrarIdeiaServlet().doGet(request, response);

		List<IdeiaUsuario> vinculos = ideiaUsuarioDAO.listarParametro(new IdeiaUsuario(novoParticipante, ideia, null));
		org.junit.Assert.assertEquals(1, vinculos.size());
		org.junit.Assert.assertEquals("N", vinculos.get(0).getFlLider());
		// M.2: "Participar" entra na LISTA DE ESPERA (pendente), nao mais como membro efetivo direto.
		org.junit.Assert.assertEquals(StatusIdeia.VINCULO_PENDENTE, vinculos.get(0).getFlStatusVinculo());
		verify(response).sendRedirect(contains("minha-ideia.jsp"));
	}

	@Test
	public void jaVinculado_naoDuplicaEApenasRedireciona() throws Exception {
		Usuario autor = novoUsuario("Autor2");
		Ideia ideia = novaIdeia(autor);
		Usuario participante = novoUsuario("Participante2");

		HttpServletRequest request = mockRequest(participante.getCodigo(), ideia.getCodigo().toString());
		HttpServletResponse response = mock(HttpServletResponse.class);

		new EntrarIdeiaServlet().doGet(request, response);
		new EntrarIdeiaServlet().doGet(request, response);

		List<IdeiaUsuario> vinculos = ideiaUsuarioDAO.listarParametro(new IdeiaUsuario(participante, ideia, null));
		org.junit.Assert.assertEquals("2a chamada sequencial nao deve duplicar o vinculo", 1, vinculos.size());
	}

	@Test
	public void doisCliquesSimultaneosEmEntrar_soUmVinculoEhCriado() throws Exception {
		Usuario autor = novoUsuario("AutC");
		Ideia ideia = novaIdeia(autor);
		Usuario participante = novoUsuario("PartC");

		CyclicBarrier largada = new CyclicBarrier(2);
		CountDownLatch fim = new CountDownLatch(2);
		AtomicReference<Throwable> escapouThread1 = new AtomicReference<>();
		AtomicReference<Throwable> escapouThread2 = new AtomicReference<>();

		new Thread(criarTarefa(participante, ideia, largada, fim, escapouThread1)).start();
		new Thread(criarTarefa(participante, ideia, largada, fim, escapouThread2)).start();

		assertTrue("as 2 threads devem terminar", fim.await(10, TimeUnit.SECONDS));
		assertNull("doGet nao deve deixar excecao escapar (thread 1)", escapouThread1.get());
		assertNull("doGet nao deve deixar excecao escapar (thread 2)", escapouThread2.get());

		List<IdeiaUsuario> vinculos = ideiaUsuarioDAO.listarParametro(new IdeiaUsuario(participante, ideia, null));
		org.junit.Assert.assertEquals("so 1 vinculo deve existir, mesmo com a corrida", 1, vinculos.size());
	}

	private Runnable criarTarefa(Usuario participante, Ideia ideia, CyclicBarrier largada, CountDownLatch fim,
			AtomicReference<Throwable> escapou) {
		return () -> {
			try {
				HttpServletRequest request = mockRequest(participante.getCodigo(), ideia.getCodigo().toString());
				HttpServletResponse response = mock(HttpServletResponse.class);
				largada.await(5, TimeUnit.SECONDS);
				new EntrarIdeiaServlet().doGet(request, response);
			} catch (Exception t) {
				escapou.set(t);
			} finally {
				fim.countDown();
			}
		};
	}
}
