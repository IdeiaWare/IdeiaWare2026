package edu.unisc.lic.servlet;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import edu.unisc.lic.classes.StatusIdeia;
import edu.unisc.lic.dao.ColaboracaoIdeiaDAO;
import edu.unisc.lic.dao.IdeiaDAO;
import edu.unisc.lic.dao.UsuarioDAO;
import edu.unisc.lic.domain.ColaboracaoIdeia;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.Usuario;

/**
 * RetornaMensagensServlet: polling (a cada 2s, ver COLM-05/COLM-04) que
 * devolve a ultima colaboracao quando ha mais mensagens do que o cliente ja
 * tem. Sem ideiaId na sessao ou numMensagens invalido -> corpo "nao", nunca
 * 500.
 */
public class RetornaMensagensServletTest {

	private final UsuarioDAO usuarioDAO = new UsuarioDAO();
	private final IdeiaDAO ideiaDAO = new IdeiaDAO();
	private final ColaboracaoIdeiaDAO colaboracaoIdeiaDAO = new ColaboracaoIdeiaDAO();

	private Usuario novoUsuario(String nome) {
		Usuario u = new Usuario(nome, nome + "_" + System.nanoTime(), "s", "usr", nome + "_" + System.nanoTime() + "@x.com");
		usuarioDAO.salvar(u);
		return u;
	}

	private Ideia novaIdeia(Usuario autor) {
		Ideia ideia = new Ideia(autor, "Ideia RetornaMensagens", "desc", StatusIdeia.EM_DESENVOLVIMENTO, StatusIdeia.GRUPO_ABERTO);
		ideia.setDtCriacao();
		ideiaDAO.salvar(ideia);
		return ideia;
	}

	private HttpServletRequest mockRequest(Long ideiaId, String numMensagens) {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpSession session = mock(HttpSession.class);
		when(request.getSession()).thenReturn(session);
		when(session.getAttribute("ideiaId")).thenReturn(ideiaId);
		when(request.getParameter("numMensagens")).thenReturn(numMensagens);
		return request;
	}

	private StringWriter mockResponseWriter(HttpServletResponse response) throws Exception {
		StringWriter sw = new StringWriter();
		when(response.getWriter()).thenReturn(new PrintWriter(sw));
		return sw;
	}

	@Test
	public void semIdeiaIdNaSessao_retornaNao() throws Exception {
		HttpServletRequest request = mockRequest(null, "0");
		HttpServletResponse response = mock(HttpServletResponse.class);
		StringWriter sw = mockResponseWriter(response);

		new RetornaMensagensServlet().doGet(request, response);

		assertEquals("não", sw.toString());
	}

	@Test
	public void ideiaIdApontaParaIdeiaInexistente_retornaNaoSemQuebrar() throws Exception {
		// TEST-04 (2026-07-06): achado na reconferencia dos DAOs -- ideiaDAO.buscar()
		// pode retornar null, e sem guard isso estourava NPE dentro de
		// ColaboracaoIdeiaDAO.listarParametro (ci.getIdeia().getCodigo()).
		HttpServletRequest request = mockRequest(999999L, "0");
		HttpServletResponse response = mock(HttpServletResponse.class);
		StringWriter sw = mockResponseWriter(response);

		new RetornaMensagensServlet().doGet(request, response);

		assertEquals("não", sw.toString());
	}

	@Test
	public void numMensagensInvalido_retornaNao() throws Exception {
		Usuario autor = novoUsuario("Autor");
		Ideia ideia = novaIdeia(autor);

		HttpServletRequest request = mockRequest(ideia.getCodigo(), "abc");
		HttpServletResponse response = mock(HttpServletResponse.class);
		StringWriter sw = mockResponseWriter(response);

		new RetornaMensagensServlet().doGet(request, response);

		assertEquals("não", sw.toString());
	}

	@Test
	public void numMensagensMenorQueTotal_retornaUltimaColaboracaoEmJson() throws Exception {
		Usuario autor = novoUsuario("Autor2");
		Ideia ideia = novaIdeia(autor);

		ColaboracaoIdeia c1 = new ColaboracaoIdeia(ideia, autor, new Timestamp(System.currentTimeMillis()), "primeira colaboracao");
		colaboracaoIdeiaDAO.salvar(c1);
		ColaboracaoIdeia c2 = new ColaboracaoIdeia(ideia, autor, new Timestamp(System.currentTimeMillis() + 5000), "ultima colaboracao");
		colaboracaoIdeiaDAO.salvar(c2);

		HttpServletRequest request = mockRequest(ideia.getCodigo(), "1");
		HttpServletResponse response = mock(HttpServletResponse.class);
		StringWriter sw = mockResponseWriter(response);

		new RetornaMensagensServlet().doGet(request, response);

		JsonObject resposta = JsonParser.parseString(sw.toString()).getAsJsonObject();
		assertEquals("ultima colaboracao", resposta.get("descricaoIdeiaAtual").getAsString());
	}

	@Test
	public void numMensagensMaiorOuIgualQueTotal_retornaNao() throws Exception {
		Usuario autor = novoUsuario("Autor3");
		Ideia ideia = novaIdeia(autor);

		ColaboracaoIdeia c1 = new ColaboracaoIdeia(ideia, autor, new Timestamp(System.currentTimeMillis()), "unica colaboracao");
		colaboracaoIdeiaDAO.salvar(c1);

		HttpServletRequest request = mockRequest(ideia.getCodigo(), "1");
		HttpServletResponse response = mock(HttpServletResponse.class);
		StringWriter sw = mockResponseWriter(response);

		new RetornaMensagensServlet().doGet(request, response);

		assertEquals("não", sw.toString());
	}
}
