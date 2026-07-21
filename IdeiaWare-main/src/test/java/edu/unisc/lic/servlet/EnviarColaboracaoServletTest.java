package edu.unisc.lic.servlet;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

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

// TEST-04, Tier 3: EnviarColaboracaoServlet (COLM-02/COLM-03) -- exige sessao antes de salvar, retorna o objeto salvo como JSON.
public class EnviarColaboracaoServletTest {

	private final IdeiaDAO ideiaDAO = new IdeiaDAO();
	private final UsuarioDAO usuarioDAO = new UsuarioDAO();
	private final ColaboracaoIdeiaDAO colaboracaoIdeiaDAO = new ColaboracaoIdeiaDAO();

	private Usuario novoUsuario(String nome) {
		Usuario u = new Usuario(nome, nome + "_" + System.nanoTime(), "s", "usr", nome + "_" + System.nanoTime() + "@x.com");
		usuarioDAO.salvar(u);
		return u;
	}

	private Ideia novaIdeia(Usuario autor) {
		Ideia ideia = new Ideia(autor, "Ideia ColabEnvio", "desc", StatusIdeia.EM_DESENVOLVIMENTO, StatusIdeia.GRUPO_ABERTO);
		ideia.setDtCriacao();
		ideiaDAO.salvar(ideia);
		return ideia;
	}

	private HttpServletRequest mockRequest(Long codigoUsuario, Long ideiaId, boolean comSessao, String descricao) {
		HttpServletRequest request = mock(HttpServletRequest.class);
		if (!comSessao) {
			when(request.getSession(false)).thenReturn(null);
		} else {
			HttpSession session = mock(HttpSession.class);
			when(request.getSession(false)).thenReturn(session);
			when(session.getAttribute("ideiaId")).thenReturn(ideiaId);
			when(session.getAttribute("codigoUsuario")).thenReturn(codigoUsuario);
		}
		when(request.getParameter("descricao")).thenReturn(descricao);
		return request;
	}

	private HttpServletResponse mockResponse() throws Exception {
		HttpServletResponse response = mock(HttpServletResponse.class);
		when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));
		return response;
	}

	@Test
	public void semSessao_retorna401() throws Exception {
		HttpServletRequest request = mockRequest(null, null, false, "Colaboracao");
		HttpServletResponse response = mockResponse();

		new EnviarColaboracaoServlet().doPost(request, response);

		verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}

	@Test
	public void sessaoSemIdeiaIdOuUsuario_retorna401() throws Exception {
		Usuario u = novoUsuario("Solo");
		HttpServletRequest request = mockRequest(u.getCodigo(), null, true, "Colaboracao");
		HttpServletResponse response = mockResponse();

		new EnviarColaboracaoServlet().doPost(request, response);

		verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}

	@Test
	public void dadosValidos_salvaColaboracaoERetornaJson() throws Exception {
		Usuario autor = novoUsuario("Autor");
		Ideia ideia = novaIdeia(autor);

		HttpServletRequest request = mockRequest(autor.getCodigo(), ideia.getCodigo(), true, "Descricao da colaboracao");
		HttpServletResponse response = mockResponse();

		new EnviarColaboracaoServlet().doPost(request, response);

		ColaboracaoIdeia filtro = new ColaboracaoIdeia();
		filtro.setIdeia(ideia);
		List<ColaboracaoIdeia> salvas = colaboracaoIdeiaDAO.listarParametro(filtro);
		assertEquals(1, salvas.size());
		assertEquals("Descricao da colaboracao", salvas.get(0).getDescricaoIdeiaAtual());

		verify(response).setContentType("application/json");
	}

	@Test
	public void colaboracaoJaFinalizada_bloqueiaEnvio() throws Exception { // UX-COLAB-ETAPA-TRAVADA
		Usuario autor = novoUsuario("AutorFinalizada");
		Ideia ideia = new Ideia(autor, "Ideia Ja Finalizada", "desc", StatusIdeia.STORYTELLING, StatusIdeia.GRUPO_FECHADO);
		ideia.setDtCriacao();
		ideiaDAO.salvar(ideia);

		HttpServletRequest request = mockRequest(autor.getCodigo(), ideia.getCodigo(), true, "Colaboracao tardia");
		HttpServletResponse response = mockResponse();

		new EnviarColaboracaoServlet().doPost(request, response);

		verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
		ColaboracaoIdeia filtro = new ColaboracaoIdeia();
		filtro.setIdeia(ideia);
		assertEquals(0, colaboracaoIdeiaDAO.listarParametro(filtro).size());
	}
}
