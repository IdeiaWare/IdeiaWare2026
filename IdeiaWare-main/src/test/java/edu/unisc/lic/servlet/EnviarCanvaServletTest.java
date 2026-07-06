package edu.unisc.lic.servlet;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Test;

import edu.unisc.lic.classes.StatusIdeia;
import edu.unisc.lic.dao.CanvaDAO;
import edu.unisc.lic.dao.IdeiaDAO;
import edu.unisc.lic.dao.UsuarioDAO;
import edu.unisc.lic.domain.Canva;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.Usuario;

/**
 * TEST-04, Tier 3: EnviarCanvaServlet (CANM-01/CANM-02/CANM-09) -- cria ou edita um
 * post-it do Canvas; a edicao tem guard IDOR (o post-it deve pertencer ao canva ativo
 * na sessao).
 */
public class EnviarCanvaServletTest {

	private final IdeiaDAO ideiaDAO = new IdeiaDAO();
	private final UsuarioDAO usuarioDAO = new UsuarioDAO();
	private final CanvaDAO canvaDAO = new CanvaDAO();

	private Usuario novoUsuario(String nome) {
		Usuario u = new Usuario(nome, nome + "_" + System.nanoTime(), "s", "usr", nome + "_" + System.nanoTime() + "@x.com");
		usuarioDAO.salvar(u);
		return u;
	}

	private Ideia novaIdeia(Usuario autor) {
		Ideia ideia = new Ideia(autor, "Ideia Enviar Canva", "desc", StatusIdeia.CANVAS, StatusIdeia.GRUPO_ABERTO);
		ideia.setDtCriacao();
		ideiaDAO.salvar(ideia);
		return ideia;
	}

	private HttpServletRequest mockRequest(Long codigoUsuario, Long ideiaIdSessao, String attribute, String idCanva,
			String text, String color) {
		HttpServletRequest request = mock(HttpServletRequest.class);
		if (codigoUsuario == null) {
			when(request.getSession(false)).thenReturn(null);
		} else {
			HttpSession session = mock(HttpSession.class);
			when(request.getSession(false)).thenReturn(session);
			when(session.getAttribute("codigoUsuario")).thenReturn(codigoUsuario);
			when(session.getAttribute("ideiaId")).thenReturn(ideiaIdSessao);
		}
		when(request.getParameter("attribute")).thenReturn(attribute);
		when(request.getParameter("idCanva")).thenReturn(idCanva);
		when(request.getParameter("text")).thenReturn(text);
		when(request.getParameter("color")).thenReturn(color);
		when(request.getContextPath()).thenReturn("");
		return request;
	}

	@Test
	public void semSessao_redirecionaLogin() throws Exception {
		HttpServletRequest request = mockRequest(null, null, "receita", null, "Texto", "ffeb3b");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new EnviarCanvaServlet().doPost(request, response);

		verify(response).sendRedirect(contains("login.jsp"));
	}

	@Test
	public void attributeAusente_redirecionaEntrarCanva() throws Exception {
		Usuario autor = novoUsuario("Autor");
		Ideia ideia = novaIdeia(autor);
		HttpServletRequest request = mockRequest(autor.getCodigo(), ideia.getCodigo(), null, null, "Texto", "ffeb3b");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new EnviarCanvaServlet().doPost(request, response);

		verify(response).sendRedirect(contains("EntrarCanvaServlet"));
	}

	@Test
	public void idCanvaNaoNumerico_redirecionaEntrarCanva() throws Exception {
		Usuario autor = novoUsuario("Autor2");
		Ideia ideia = novaIdeia(autor);
		HttpServletRequest request = mockRequest(autor.getCodigo(), ideia.getCodigo(), "receita", "abc", "Texto", "ffeb3b");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new EnviarCanvaServlet().doPost(request, response);

		verify(response).sendRedirect(contains("EntrarCanvaServlet"));
	}

	@Test
	public void idCanvaDeOutraIdeia_bloqueadoIDOR() throws Exception {
		Usuario autorA = novoUsuario("AutorA");
		Ideia ideiaA = novaIdeia(autorA);
		Usuario autorB = novoUsuario("AutorB");
		Ideia ideiaB = novaIdeia(autorB);
		Canva canvaDeB = new Canva(ideiaB, "Texto original", "ffeb3b", "receita");
		canvaDAO.salvar(canvaDeB);

		HttpServletRequest request = mockRequest(autorA.getCodigo(), ideiaA.getCodigo(), "receita",
				canvaDeB.getCodigo().toString(), "Texto malicioso", "000000");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new EnviarCanvaServlet().doPost(request, response);

		verify(response).sendRedirect(contains("EntrarCanvaServlet"));
		Canva aposChamada = canvaDAO.buscar(canvaDeB.getCodigo());
		assertEquals("nao deve editar post-it de outra ideia", "Texto original", aposChamada.getText());
	}

	@Test
	public void novoPostIt_criaCanvaERedirecionaParaTelaCorreta() throws Exception {
		Usuario autor = novoUsuario("Autor3");
		Ideia ideia = novaIdeia(autor);

		HttpServletRequest request = mockRequest(autor.getCodigo(), ideia.getCodigo(), "receita", null,
				"Texto novo post-it", "ffeb3b");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new EnviarCanvaServlet().doPost(request, response);

		verify(response).sendRedirect(contains("EntrarCanvaReceitaServlet"));

		Canva filtro = new Canva();
		filtro.setIdeia(ideia);
		List<Canva> receita = canvaDAO.listarCanvaElement(filtro, "receita");
		assertEquals(1, receita.size());
		assertEquals("Texto novo post-it", receita.get(0).getText());
	}

	@Test
	public void editaPostItExistente_atualizaCamposERedirecionaParaTelaCorreta() throws Exception {
		Usuario autor = novoUsuario("Autor4");
		Ideia ideia = novaIdeia(autor);
		Canva canva = new Canva(ideia, "Texto antigo", "ffeb3b", "receita");
		canvaDAO.salvar(canva);

		HttpServletRequest request = mockRequest(autor.getCodigo(), ideia.getCodigo(), "custo",
				canva.getCodigo().toString(), "Texto atualizado", "123456");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new EnviarCanvaServlet().doPost(request, response);

		verify(response).sendRedirect(contains("EntrarCanvaEstruturaServlet"));

		Canva atualizado = canvaDAO.buscar(canva.getCodigo());
		assertEquals("Texto atualizado", atualizado.getText());
		assertEquals("123456", atualizado.getColor());
		assertEquals("custo", atualizado.getAttribute());
	}
}
