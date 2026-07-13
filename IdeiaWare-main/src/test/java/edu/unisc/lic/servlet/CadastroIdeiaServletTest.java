package edu.unisc.lic.servlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

// TEST-04, Tier 1: CadastroIdeiaServlet -- exige login (RET-14/SRV-500-01) e valida titulo/descricao no servidor (COL-15).
public class CadastroIdeiaServletTest {

	private final IdeiaDAO ideiaDAO = new IdeiaDAO();
	private final UsuarioDAO usuarioDAO = new UsuarioDAO();
	private final IdeiaUsuarioDAO ideiaUsuarioDAO = new IdeiaUsuarioDAO();

	private Usuario novoUsuario(String nome) {
		Usuario u = new Usuario(nome, nome + "_" + System.nanoTime(), "s", "usr", nome + "_" + System.nanoTime() + "@x.com");
		usuarioDAO.salvar(u);
		return u;
	}

	private HttpServletRequest mockRequest(Long codigoUsuario, String titulo, String descricao) {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpSession session = mock(HttpSession.class);
		when(request.getSession(true)).thenReturn(session);
		when(session.getAttribute("codigoUsuario")).thenReturn(codigoUsuario);
		when(request.getParameter("titulo")).thenReturn(titulo);
		when(request.getParameter("descricao")).thenReturn(descricao);
		when(request.getContextPath()).thenReturn("");
		return request;
	}

	@Test
	public void semLogin_redirecionaParaLogin() throws Exception {
		HttpServletRequest request = mockRequest(null, "Titulo valido", "Descricao valida");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new CadastroIdeiaServlet().doPost(request, response);

		verify(response).sendRedirect(contains("login.jsp"));
	}

	@Test
	public void tituloVazio_redirecionaComErroENaoSalva() throws Exception {
		Usuario u = novoUsuario("Autor");
		HttpServletRequest request = mockRequest(u.getCodigo(), "", "Descricao valida");
		HttpServletResponse response = mock(HttpServletResponse.class);
		int antes = ideiaDAO.listar().size();

		new CadastroIdeiaServlet().doPost(request, response);

		verify(response).sendRedirect(contains("erro=titulo_obrigatorio"));
		assertEquals(antes, ideiaDAO.listar().size());
	}

	@Test
	public void tituloMaiorQue50_redirecionaComErro() throws Exception {
		Usuario u = novoUsuario("Autor2");
		String tituloGigante = "T".repeat(51);
		HttpServletRequest request = mockRequest(u.getCodigo(), tituloGigante, "Descricao valida");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new CadastroIdeiaServlet().doPost(request, response);

		verify(response).sendRedirect(contains("erro=titulo_longo"));
	}

	@Test
	public void descricaoVazia_redirecionaComErro() throws Exception {
		Usuario u = novoUsuario("Autor3");
		HttpServletRequest request = mockRequest(u.getCodigo(), "Titulo ok", "");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new CadastroIdeiaServlet().doPost(request, response);

		verify(response).sendRedirect(contains("erro=descricao_obrigatoria"));
	}

	@Test
	public void descricaoMaiorQue200_redirecionaComErro() throws Exception {
		Usuario u = novoUsuario("Autor4");
		String descricaoGigante = "D".repeat(201);
		HttpServletRequest request = mockRequest(u.getCodigo(), "Titulo ok", descricaoGigante);
		HttpServletResponse response = mock(HttpServletResponse.class);

		new CadastroIdeiaServlet().doPost(request, response);

		verify(response).sendRedirect(contains("erro=descricao_longa"));
	}

	@Test
	public void dadosValidos_criaIdeiaEDeixaAutorComoLider() throws Exception {
		Usuario autor = novoUsuario("Autor5");
		HttpServletRequest request = mockRequest(autor.getCodigo(), "Ideia nova valida", "Descricao da ideia nova");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new CadastroIdeiaServlet().doPost(request, response);

		verify(response).sendRedirect(contains("minha-ideia.jsp"));

		Ideia filtro = new Ideia();
		filtro.setUsuario(autor);
		java.util.List<Ideia> criadas = ideiaDAO.listarParametro(filtro);
		assertEquals(1, criadas.size());
		assertEquals(StatusIdeia.PENDENTE, criadas.get(0).getStatus());

		IdeiaUsuario filtroVinculo = new IdeiaUsuario(autor, criadas.get(0), "S");
		assertTrue("autor deve ser lider da propria ideia",
				!ideiaUsuarioDAO.listarParametro(filtroVinculo).isEmpty());
	}
}
