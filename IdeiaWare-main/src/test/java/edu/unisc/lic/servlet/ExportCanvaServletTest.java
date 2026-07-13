package edu.unisc.lic.servlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Test;

import edu.unisc.lic.classes.Data;
import edu.unisc.lic.classes.StatusIdeia;
import edu.unisc.lic.dao.CanvaexportDAO;
import edu.unisc.lic.dao.IdeiaDAO;
import edu.unisc.lic.dao.UsuarioDAO;
import edu.unisc.lic.domain.Canvaexport;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.Usuario;

// TEST-04/K.8 #4: ExportCanvaServlet (CAN-10/CANM-03/CANM-06/CANM-07) -- UNIQUE(ideia_codigo) em Canvaexport + catch trata a corrida como update.
public class ExportCanvaServletTest {

	private final IdeiaDAO ideiaDAO = new IdeiaDAO();
	private final UsuarioDAO usuarioDAO = new UsuarioDAO();
	private final CanvaexportDAO canvaexportDAO = new CanvaexportDAO();

	private Usuario novoUsuario(String nome) {
		Usuario u = new Usuario(nome, nome + "_" + System.nanoTime(), "s", "usr", nome + "_" + System.nanoTime() + "@x.com");
		usuarioDAO.salvar(u);
		return u;
	}

	private Ideia novaIdeia(Usuario autor) {
		Ideia ideia = new Ideia(autor, "Ideia Export Canva", "desc", StatusIdeia.CANVAS, StatusIdeia.GRUPO_ABERTO);
		ideia.setDtCriacao();
		ideiaDAO.salvar(ideia);
		return ideia;
	}

	private HttpServletRequest mockRequest(Long ideiaIdSessao, String body) throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getReader()).thenReturn(new BufferedReader(new StringReader(body == null ? "" : body)));
		if (ideiaIdSessao == null) {
			when(request.getSession(false)).thenReturn(null);
		} else {
			HttpSession session = mock(HttpSession.class);
			when(request.getSession(false)).thenReturn(session);
			when(session.getAttribute("ideiaId")).thenReturn(ideiaIdSessao);
		}
		return request;
	}

	@Test
	public void semSessaoOuSemIdeiaId_retorna400() throws Exception {
		HttpServletRequest request = mockRequest(null, "conteudoBase64Fake");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new ExportCanvaServlet().doPost(request, response);

		verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
	}

	@Test
	public void bodyVazio_retorna400() throws Exception {
		HttpServletRequest request = mockRequest(1L, "");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new ExportCanvaServlet().doPost(request, response);

		verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
	}

	@Test
	public void ideiaInexistenteNaSessao_retorna400() throws Exception {
		HttpServletRequest request = mockRequest(999999L, "conteudoBase64Fake");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new ExportCanvaServlet().doPost(request, response);

		verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
	}

	@Test
	public void novoExport_salvaCanvaexportEFinalizaIdeia() throws Exception {
		Usuario autor = novoUsuario("Autor");
		Ideia ideia = novaIdeia(autor);

		HttpServletRequest request = mockRequest(ideia.getCodigo(), "conteudoPdfBase64");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new ExportCanvaServlet().doPost(request, response);

		verify(response, never()).setStatus(org.mockito.ArgumentMatchers.anyInt());

		Canvaexport filtro = new Canvaexport();
		filtro.setIdeia(ideia);
		List<Canvaexport> exports = canvaexportDAO.listarParametro(filtro);
		assertEquals(1, exports.size());
		assertEquals("conteudoPdfBase64", exports.get(0).getFile());

		Ideia ideiaAtualizada = ideiaDAO.buscar(ideia.getCodigo());
		assertEquals(StatusIdeia.FINALIZADO, ideiaAtualizada.getStatus());
	}

	@Test
	public void exportExistente_atualizaEmVezDeCriarNovo() throws Exception {
		Usuario autor = novoUsuario("Autor2");
		Ideia ideia = novaIdeia(autor);
		Canvaexport existente = new Canvaexport(ideia, "conteudoAntigo", Data.horaAtual());
		canvaexportDAO.salvar(existente);

		HttpServletRequest request = mockRequest(ideia.getCodigo(), "conteudoNovo");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new ExportCanvaServlet().doPost(request, response);

		Canvaexport filtro = new Canvaexport();
		filtro.setIdeia(ideia);
		List<Canvaexport> exports = canvaexportDAO.listarParametro(filtro);
		assertEquals("deve atualizar o existente, nao criar um segundo", 1, exports.size());
		assertEquals("conteudoNovo", exports.get(0).getFile());
	}

	@Test
	public void doisExportsSimultaneos_soUmCanvaexportEhCriado() throws Exception {
		Usuario autor = novoUsuario("AutC");
		Ideia ideia = novaIdeia(autor);

		CyclicBarrier largada = new CyclicBarrier(2);
		CountDownLatch fim = new CountDownLatch(2);
		AtomicReference<Throwable> escapouThread1 = new AtomicReference<>();
		AtomicReference<Throwable> escapouThread2 = new AtomicReference<>();

		new Thread(criarTarefa(ideia, largada, fim, escapouThread1)).start();
		new Thread(criarTarefa(ideia, largada, fim, escapouThread2)).start();

		assertTrue("as 2 threads devem terminar", fim.await(10, TimeUnit.SECONDS));
		assertNull("doPost nao deve deixar excecao escapar (thread 1)", escapouThread1.get());
		assertNull("doPost nao deve deixar excecao escapar (thread 2)", escapouThread2.get());

		Canvaexport filtro = new Canvaexport();
		filtro.setIdeia(ideia);
		List<Canvaexport> exports = canvaexportDAO.listarParametro(filtro);
		assertEquals("so 1 canvaexport deve existir, mesmo com a corrida", 1, exports.size());
	}

	private Runnable criarTarefa(Ideia ideia, CyclicBarrier largada, CountDownLatch fim,
			AtomicReference<Throwable> escapou) {
		return () -> {
			try {
				HttpServletRequest request = mockRequest(ideia.getCodigo(), "conteudoPdfBase64");
				HttpServletResponse response = mock(HttpServletResponse.class);
				largada.await(5, TimeUnit.SECONDS);
				new ExportCanvaServlet().doPost(request, response);
			} catch (Exception t) {
				escapou.set(t);
			} finally {
				fim.countDown();
			}
		};
	}
}
