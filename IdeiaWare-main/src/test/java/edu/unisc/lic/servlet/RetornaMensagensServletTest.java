package edu.unisc.lic.servlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import edu.unisc.lic.classes.StatusIdeia;
import edu.unisc.lic.dao.ColaboracaoIdeiaDAO;
import edu.unisc.lic.dao.IdeiaDAO;
import edu.unisc.lic.dao.UsuarioDAO;
import edu.unisc.lic.domain.ColaboracaoIdeia;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.Usuario;

// RetornaMensagensServlet: polling do colaboracao.jsp -- M.6 mudou pra protocolo por ULTIMO CODIGO visto (nao perde nem duplica na corrida envio-vs-polling).
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

	private ColaboracaoIdeia novaColab(Ideia ideia, Usuario autor, String texto) {
		ColaboracaoIdeia c = new ColaboracaoIdeia(ideia, autor, new Timestamp(System.currentTimeMillis()), texto);
		colaboracaoIdeiaDAO.salvar(c);
		return c;
	}

	private HttpServletRequest mockRequest(Long ideiaId, String ultimoCodigo) {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpSession session = mock(HttpSession.class);
		when(request.getSession()).thenReturn(session);
		when(session.getAttribute("ideiaId")).thenReturn(ideiaId);
		when(request.getParameter("ultimoCodigo")).thenReturn(ultimoCodigo);
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
		// TEST-04 (2026-07-06): ideiaDAO.buscar() pode retornar null -- guard evita NPE.
		HttpServletRequest request = mockRequest(999999L, "0");
		HttpServletResponse response = mock(HttpServletResponse.class);
		StringWriter sw = mockResponseWriter(response);

		new RetornaMensagensServlet().doGet(request, response);

		assertEquals("não", sw.toString());
	}

	@Test
	public void ultimoCodigoInvalido_tratadoComoZero_retornaTodasEmArray() throws Exception {
		// M.6: valor invalido nao gera 500 -- vira 0, entao devolve TODAS as colaboracoes.
		Usuario autor = novoUsuario("Autor");
		Ideia ideia = novaIdeia(autor);
		novaColab(ideia, autor, "unica");

		HttpServletRequest request = mockRequest(ideia.getCodigo(), "abc");
		HttpServletResponse response = mock(HttpServletResponse.class);
		StringWriter sw = mockResponseWriter(response);

		new RetornaMensagensServlet().doGet(request, response);

		JsonArray arr = JsonParser.parseString(sw.toString()).getAsJsonArray();
		assertEquals(1, arr.size());
		assertEquals("unica", arr.get(0).getAsJsonObject().get("descricaoIdeiaAtual").getAsString());
	}

	@Test
	public void ultimoCodigoMenorQueMax_retornaSoAsNovasEmOrdem() throws Exception {
		// M.6: cliente ja viu c1; deve receber SO c2 e c3 (as com codigo maior), em ordem.
		Usuario autor = novoUsuario("Autor2");
		Ideia ideia = novaIdeia(autor);
		ColaboracaoIdeia c1 = novaColab(ideia, autor, "primeira");
		novaColab(ideia, autor, "segunda");
		novaColab(ideia, autor, "terceira");

		HttpServletRequest request = mockRequest(ideia.getCodigo(), c1.getCodigo().toString());
		HttpServletResponse response = mock(HttpServletResponse.class);
		StringWriter sw = mockResponseWriter(response);

		new RetornaMensagensServlet().doGet(request, response);

		JsonArray arr = JsonParser.parseString(sw.toString()).getAsJsonArray();
		assertEquals(2, arr.size());
		assertEquals("segunda", arr.get(0).getAsJsonObject().get("descricaoIdeiaAtual").getAsString());
		assertEquals("terceira", arr.get(1).getAsJsonObject().get("descricaoIdeiaAtual").getAsString());
	}

	@Test
	public void ultimoCodigoIgualAoMax_retornaArrayVazio() throws Exception {
		// M.6: cliente ja esta em dia -> array vazio (nada novo pra renderizar).
		Usuario autor = novoUsuario("Autor3");
		Ideia ideia = novaIdeia(autor);
		ColaboracaoIdeia c1 = novaColab(ideia, autor, "unica");

		HttpServletRequest request = mockRequest(ideia.getCodigo(), c1.getCodigo().toString());
		HttpServletResponse response = mock(HttpServletResponse.class);
		StringWriter sw = mockResponseWriter(response);

		new RetornaMensagensServlet().doGet(request, response);

		JsonArray arr = JsonParser.parseString(sw.toString()).getAsJsonArray();
		assertTrue("array deve vir vazio", arr.isEmpty());
	}
}
