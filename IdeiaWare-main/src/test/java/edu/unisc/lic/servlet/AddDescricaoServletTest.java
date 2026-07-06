package edu.unisc.lic.servlet;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Test;

import edu.unisc.lic.classes.Data;
import edu.unisc.lic.classes.StatusIdeia;
import edu.unisc.lic.dao.ColaboracaoIdeiaDAO;
import edu.unisc.lic.dao.IdeiaDAO;
import edu.unisc.lic.dao.IdeiaUsuarioDAO;
import edu.unisc.lic.domain.ColaboracaoIdeia;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.IdeiaUsuario;
import edu.unisc.lic.domain.Usuario;
import edu.unisc.lic.dao.UsuarioDAO;

/**
 * TEST-04 (2026-07-03), Tier 1: AddDescricaoServlet -- so o LIDER da ideia pode
 * "adicionar a descricao"; antes qualquer logado conseguia.
 */
public class AddDescricaoServletTest {

	private final IdeiaDAO ideiaDAO = new IdeiaDAO();
	private final UsuarioDAO usuarioDAO = new UsuarioDAO();
	private final IdeiaUsuarioDAO ideiaUsuarioDAO = new IdeiaUsuarioDAO();
	private final ColaboracaoIdeiaDAO colaboracaoIdeiaDAO = new ColaboracaoIdeiaDAO();

	private Usuario novoUsuario(String nome) {
		Usuario u = new Usuario(nome, nome + "_" + System.nanoTime(), "s", "usr", nome + "_" + System.nanoTime() + "@x.com");
		usuarioDAO.salvar(u);
		return u;
	}

	private Ideia novaIdeia(Usuario autor) {
		Ideia ideia = new Ideia(autor, "Ideia AddDescricao", "desc", StatusIdeia.EM_DESENVOLVIMENTO, StatusIdeia.GRUPO_ABERTO);
		ideia.setDtCriacao();
		ideiaDAO.salvar(ideia);
		return ideia;
	}

	private HttpServletRequest mockRequest(Long codigoUsuario, String colaboracaoParam) {
		HttpServletRequest request = mock(HttpServletRequest.class);
		if (codigoUsuario == null) {
			when(request.getSession(false)).thenReturn(null);
		} else {
			HttpSession session = mock(HttpSession.class);
			when(request.getSession(false)).thenReturn(session);
			when(session.getAttribute("codigoUsuario")).thenReturn(codigoUsuario);
		}
		when(request.getParameter("colaboracao")).thenReturn(colaboracaoParam);
		return request;
	}

	private HttpServletResponse mockResponse() throws Exception {
		HttpServletResponse response = mock(HttpServletResponse.class);
		when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));
		return response;
	}

	private HttpServletResponse mockResponseCapturando(StringWriter saida) throws Exception {
		HttpServletResponse response = mock(HttpServletResponse.class);
		when(response.getWriter()).thenReturn(new PrintWriter(saida));
		return response;
	}

	@Test
	public void semLogin_retorna401() throws Exception {
		HttpServletRequest request = mockRequest(null, "1");
		HttpServletResponse response = mockResponse();

		new AddDescricaoServlet().doPost(request, response);

		verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}

	@Test
	public void colaboracaoInexistente_retorna400() throws Exception {
		Usuario u = novoUsuario("Solo");
		HttpServletRequest request = mockRequest(u.getCodigo(), "999999");
		HttpServletResponse response = mockResponse();

		new AddDescricaoServlet().doPost(request, response);

		verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
	}

	@Test
	public void usuarioNaoLider_retorna403() throws Exception {
		Usuario autor = novoUsuario("Autor");
		Ideia ideia = novaIdeia(autor);
		ColaboracaoIdeia colab = new ColaboracaoIdeia(ideia, autor, Data.horaAtual(), "sugestao do participante");
		colaboracaoIdeiaDAO.salvar(colab);

		Usuario naoLider = novoUsuario("NaoLider"); // logado, mas nao e lider desta ideia
		HttpServletRequest request = mockRequest(naoLider.getCodigo(), colab.getCodigo().toString());
		HttpServletResponse response = mockResponse();

		new AddDescricaoServlet().doPost(request, response);

		verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
	}

	@Test
	public void lider_adicionaDescricaoComSucesso() throws Exception {
		Usuario autor = novoUsuario("Autor2");
		Ideia ideia = novaIdeia(autor);
		IdeiaUsuario vinculo = new IdeiaUsuario(autor, ideia, "S");
		vinculo.setDtInscricao();
		ideiaUsuarioDAO.salvar(vinculo);

		ColaboracaoIdeia colab = new ColaboracaoIdeia(ideia, autor, Data.horaAtual(), "trecho novo pra ideia");
		colaboracaoIdeiaDAO.salvar(colab);

		HttpServletRequest request = mockRequest(autor.getCodigo(), colab.getCodigo().toString());
		HttpServletResponse response = mockResponse();

		new AddDescricaoServlet().doPost(request, response);

		verify(response, org.mockito.Mockito.never()).setStatus(anyInt());
		assertEquals("ad", colaboracaoIdeiaDAO.buscar(colab.getCodigo()).getFlSalvado());
	}

	/**
	 * K.8 #8 (2026-07-06): duplo-POST (duplo-clique, retry de rede) na MESMA colaboracao
	 * nao deve reaplicar o texto 2x na descricao oficial da ideia. "ad" (flSalvado) serve
	 * de marcador de idempotencia -- na 2a chamada, o servlet devolve a descricao ja
	 * calculada em vez de acrescentar de novo.
	 */
	@Test
	public void duploPostNaMesmaColaboracao_naoDuplicaTextoNaDescricao() throws Exception {
		Usuario autor = novoUsuario("Autor3");
		Ideia ideia = novaIdeia(autor);
		IdeiaUsuario vinculo = new IdeiaUsuario(autor, ideia, "S");
		vinculo.setDtInscricao();
		ideiaUsuarioDAO.salvar(vinculo);

		ColaboracaoIdeia colab = new ColaboracaoIdeia(ideia, autor, Data.horaAtual(), "trecho unico");
		colaboracaoIdeiaDAO.salvar(colab);

		HttpServletRequest request1 = mockRequest(autor.getCodigo(), colab.getCodigo().toString());
		StringWriter saida1 = new StringWriter();
		new AddDescricaoServlet().doPost(request1, mockResponseCapturando(saida1));

		HttpServletRequest request2 = mockRequest(autor.getCodigo(), colab.getCodigo().toString());
		StringWriter saida2 = new StringWriter();
		new AddDescricaoServlet().doPost(request2, mockResponseCapturando(saida2));

		String descricaoFinal = saida2.toString();
		int ocorrencias = descricaoFinal.split("trecho unico", -1).length - 1;
		assertEquals("o trecho nao deve aparecer duplicado na descricao apos o 2o POST", 1, ocorrencias);
		assertEquals("as 2 respostas devem ser identicas (idempotente)", saida1.toString(), descricaoFinal);
	}

	private static int anyInt() {
		return org.mockito.ArgumentMatchers.anyInt();
	}
}
