package edu.unisc.lic.servlet;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Test;

import edu.unisc.lic.classes.StatusIdeia;
import edu.unisc.lic.dao.ColaboracaoIdeiaDAO;
import edu.unisc.lic.dao.IdeiaDAO;
import edu.unisc.lic.dao.UsuarioDAO;
import edu.unisc.lic.domain.ColaboracaoIdeia;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.Usuario;

// M.10: EditarColaboracaoServlet -- so o AUTOR edita, so enquanto nao foi adicionada a descricao (flSalvado != "ad"), checado no servidor.
public class EditarColaboracaoServletTest {

	private final UsuarioDAO usuarioDAO = new UsuarioDAO();
	private final IdeiaDAO ideiaDAO = new IdeiaDAO();
	private final ColaboracaoIdeiaDAO colaboracaoIdeiaDAO = new ColaboracaoIdeiaDAO();

	private Usuario novoUsuario(String nome) {
		Usuario u = new Usuario(nome, nome + "_" + System.nanoTime(), "s", "usr", nome + "_" + System.nanoTime() + "@x.com");
		usuarioDAO.salvar(u);
		return u;
	}

	private Ideia novaIdeia(Usuario autor) {
		Ideia ideia = new Ideia(autor, "Ideia EditarColab", "desc", StatusIdeia.EM_DESENVOLVIMENTO, StatusIdeia.GRUPO_ABERTO);
		ideia.setDtCriacao();
		ideiaDAO.salvar(ideia);
		return ideia;
	}

	private ColaboracaoIdeia novaColab(Ideia ideia, Usuario autor, String texto) {
		ColaboracaoIdeia c = new ColaboracaoIdeia(ideia, autor, new Timestamp(System.currentTimeMillis()), texto);
		colaboracaoIdeiaDAO.salvar(c);
		return c;
	}

	private HttpServletRequest mockRequest(Long codigoUsuario, String colaboracao, String descricao) {
		HttpServletRequest request = mock(HttpServletRequest.class);
		if (codigoUsuario == null) {
			when(request.getSession(false)).thenReturn(null);
		} else {
			HttpSession session = mock(HttpSession.class);
			when(request.getSession(false)).thenReturn(session);
			when(session.getAttribute("codigoUsuario")).thenReturn(codigoUsuario);
		}
		when(request.getParameter("colaboracao")).thenReturn(colaboracao);
		when(request.getParameter("descricao")).thenReturn(descricao);
		return request;
	}

	private HttpServletResponse mockResponse() throws Exception {
		HttpServletResponse response = mock(HttpServletResponse.class);
		when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));
		return response;
	}

	@Test
	public void autorEditaColaboracaoNaoSalvada_atualizaTexto() throws Exception {
		Usuario autor = novoUsuario("Autor");
		Ideia ideia = novaIdeia(autor);
		ColaboracaoIdeia c = novaColab(ideia, autor, "texto original");

		HttpServletRequest request = mockRequest(autor.getCodigo(), c.getCodigo().toString(), "texto corrigido");
		new EditarColaboracaoServlet().doPost(request, mockResponse());

		ColaboracaoIdeia recarregada = colaboracaoIdeiaDAO.buscar(c.getCodigo());
		assertEquals("texto corrigido", recarregada.getDescricaoIdeiaAtual());
		assertEquals("texto original", recarregada.getDescricaoIdeiaAnterior());
	}

	@Test
	public void naoAutor_naoEdita_retorna403() throws Exception {
		Usuario autor = novoUsuario("Autor2");
		Usuario outro = novoUsuario("Outro");
		Ideia ideia = novaIdeia(autor);
		ColaboracaoIdeia c = novaColab(ideia, autor, "texto original");

		HttpServletRequest request = mockRequest(outro.getCodigo(), c.getCodigo().toString(), "hackeado");
		HttpServletResponse response = mockResponse();
		new EditarColaboracaoServlet().doPost(request, response);

		verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
		assertEquals("texto original", colaboracaoIdeiaDAO.buscar(c.getCodigo()).getDescricaoIdeiaAtual());
	}

	@Test
	public void colaboracaoJaAdicionadaADescricao_naoEdita_retorna403() throws Exception {
		Usuario autor = novoUsuario("Autor3");
		Ideia ideia = novaIdeia(autor);
		ColaboracaoIdeia c = novaColab(ideia, autor, "texto original");
		c.setFlSalvado("ad"); // ja mesclada na descricao
		colaboracaoIdeiaDAO.editar(c);

		HttpServletRequest request = mockRequest(autor.getCodigo(), c.getCodigo().toString(), "tentando editar");
		HttpServletResponse response = mockResponse();
		new EditarColaboracaoServlet().doPost(request, response);

		verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
		assertEquals("texto original", colaboracaoIdeiaDAO.buscar(c.getCodigo()).getDescricaoIdeiaAtual());
	}

	@Test
	public void textoVazio_retorna400() throws Exception {
		Usuario autor = novoUsuario("Autor4");
		Ideia ideia = novaIdeia(autor);
		ColaboracaoIdeia c = novaColab(ideia, autor, "texto original");

		HttpServletRequest request = mockRequest(autor.getCodigo(), c.getCodigo().toString(), "   ");
		HttpServletResponse response = mockResponse();
		new EditarColaboracaoServlet().doPost(request, response);

		verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
		assertEquals("texto original", colaboracaoIdeiaDAO.buscar(c.getCodigo()).getDescricaoIdeiaAtual());
	}

	@Test
	public void semLogin_retorna401() throws Exception {
		HttpServletRequest request = mockRequest(null, "1", "x");
		HttpServletResponse response = mockResponse();
		new EditarColaboracaoServlet().doPost(request, response);

		verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}
}
