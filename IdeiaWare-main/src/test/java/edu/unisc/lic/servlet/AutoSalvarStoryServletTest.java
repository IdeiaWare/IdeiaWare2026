package edu.unisc.lic.servlet;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.StringReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Test;

import edu.unisc.lic.classes.Data;
import edu.unisc.lic.classes.StatusIdeia;
import edu.unisc.lic.dao.ElementosStorytellingDAO;
import edu.unisc.lic.dao.IdeiaDAO;
import edu.unisc.lic.dao.StorytellingDAO;
import edu.unisc.lic.dao.UsuarioDAO;
import edu.unisc.lic.domain.ElementosStorytelling;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.Storytelling;
import edu.unisc.lic.domain.Usuario;

/**
 * AutoSalvarStoryServlet: autosave do quadro de Storytelling. So pode
 * editar elementos do storytelling ATIVO na sessao (IDOR de escrita - ver
 * comentario "AUTORIZACAO" no servlet).
 */
public class AutoSalvarStoryServletTest {

	private final UsuarioDAO usuarioDAO = new UsuarioDAO();
	private final IdeiaDAO ideiaDAO = new IdeiaDAO();
	private final StorytellingDAO storytellingDAO = new StorytellingDAO();
	private final ElementosStorytellingDAO elementosStorytellingDAO = new ElementosStorytellingDAO();

	private Usuario novoUsuario(String nome) {
		Usuario u = new Usuario(nome, nome + "_" + System.nanoTime(), "s", "usr", nome + "_" + System.nanoTime() + "@x.com");
		usuarioDAO.salvar(u);
		return u;
	}

	private Ideia novaIdeia(Usuario autor) {
		Ideia ideia = new Ideia(autor, "Ideia AutoSalvar", "desc", StatusIdeia.STORYTELLING, StatusIdeia.GRUPO_ABERTO);
		ideia.setDtCriacao();
		ideiaDAO.salvar(ideia);
		return ideia;
	}

	private Storytelling novoStorytelling(Usuario autor, Ideia ideia) {
		Storytelling st = new Storytelling(autor, ideia, Data.horaAtual(), "AB");
		storytellingDAO.salvar(st);
		return st;
	}

	private HttpServletRequest mockRequestSemSessao(String json) throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getSession(false)).thenReturn(null);
		when(request.getReader()).thenReturn(new BufferedReader(new StringReader(json)));
		return request;
	}

	private HttpServletRequest mockRequest(Long storytellingId, String json) throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpSession session = mock(HttpSession.class);
		when(request.getSession(false)).thenReturn(session);
		when(session.getAttribute("storytellingId")).thenReturn(storytellingId);
		when(request.getReader()).thenReturn(new BufferedReader(new StringReader(json)));
		return request;
	}

	@Test
	public void semSessao_retorna401() throws Exception {
		HttpServletRequest request = mockRequestSemSessao("[]");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new AutoSalvarStoryServlet().doPost(request, response);

		verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}

	@Test
	public void semStorytellingIdNaSessao_retorna401() throws Exception {
		HttpServletRequest request = mockRequest(null, "[]");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new AutoSalvarStoryServlet().doPost(request, response);

		verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}

	@Test
	public void corpoVazio_naoFazNadaNemErro() throws Exception {
		HttpServletRequest request = mockRequest(1L, "");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new AutoSalvarStoryServlet().doPost(request, response);

		verify(response, never()).setStatus(anyInt());
	}

	@Test
	public void elementoNaoTexto_atualizaPosicaoETamanho() throws Exception {
		Usuario autor = novoUsuario("Autor1");
		Ideia ideia = novaIdeia(autor);
		Storytelling st = novoStorytelling(autor, ideia);

		ElementosStorytelling est = new ElementosStorytelling(st, "IMG", "", 1, 1, 10, 10);
		elementosStorytellingDAO.salvar(est);

		String json = "[{\"codigo\":" + est.getCodigo()
				+ ",\"tipo\":\"forma\",\"x\":50.5,\"y\":60.5,\"height\":100.0,\"width\":200.0}]";
		HttpServletRequest request = mockRequest(st.getCodigo(), json);
		HttpServletResponse response = mock(HttpServletResponse.class);

		new AutoSalvarStoryServlet().doPost(request, response);

		ElementosStorytelling atualizado = elementosStorytellingDAO.buscar(est.getCodigo());
		assertEquals(50.5, atualizado.getX(), 0.001);
		assertEquals(60.5, atualizado.getY(), 0.001);
		assertEquals(100.0, atualizado.getAltura(), 0.001);
		assertEquals(200.0, atualizado.getLargura(), 0.001);
	}

	@Test
	public void elementoTexto_atualizaConteudoEEstilo() throws Exception {
		Usuario autor = novoUsuario("Autor2");
		Ideia ideia = novaIdeia(autor);
		Storytelling st = novoStorytelling(autor, ideia);

		ElementosStorytelling est = new ElementosStorytelling();
		est.setStorytelling(st);
		est.setTipo("TXT");
		est.setCaminho("");
		est.setInformacaoTexto("original");
		est.setFonte("Arial");
		est.setTamanhoFonte(10);
		est.setCorFonte("#000000");
		elementosStorytellingDAO.salvar(est);

		String json = "[{\"codigo\":" + est.getCodigo() + ",\"tipo\":\"texto\",\"x\":5.0,\"y\":6.0,"
				+ "\"conteudo\":\"novo texto\",\"tamanhoFonte\":24,\"fonte\":\"Verdana\",\"cor\":\"#ffffff\"}]";
		HttpServletRequest request = mockRequest(st.getCodigo(), json);
		HttpServletResponse response = mock(HttpServletResponse.class);

		new AutoSalvarStoryServlet().doPost(request, response);

		ElementosStorytelling atualizado = elementosStorytellingDAO.buscar(est.getCodigo());
		assertEquals("novo texto", atualizado.getInformacaoTexto());
		assertEquals(24, atualizado.getTamanhoFonte());
		assertEquals("Verdana", atualizado.getFonte());
		assertEquals("#ffffff", atualizado.getCorFonte());
		assertEquals(5.0, atualizado.getX(), 0.001);
		assertEquals(6.0, atualizado.getY(), 0.001);
	}

	@Test
	public void elementoDeOutroStorytelling_naoEAtualizado() throws Exception {
		Usuario autor1 = novoUsuario("AutorA");
		Ideia ideiaA = novaIdeia(autor1);
		Storytelling stA = novoStorytelling(autor1, ideiaA);

		Usuario autor2 = novoUsuario("AutorB");
		Ideia ideiaB = novaIdeia(autor2);
		Storytelling stB = novoStorytelling(autor2, ideiaB);

		ElementosStorytelling est = new ElementosStorytelling(stA, "IMG", "", 1, 1, 10, 10);
		elementosStorytellingDAO.salvar(est);

		// sessao diz storytellingId = stB, mas o elemento pertence ao stA (IDOR)
		String json = "[{\"codigo\":" + est.getCodigo()
				+ ",\"tipo\":\"forma\",\"x\":999.0,\"y\":999.0,\"height\":999.0,\"width\":999.0}]";
		HttpServletRequest request = mockRequest(stB.getCodigo(), json);
		HttpServletResponse response = mock(HttpServletResponse.class);

		new AutoSalvarStoryServlet().doPost(request, response);

		ElementosStorytelling naoAlterado = elementosStorytellingDAO.buscar(est.getCodigo());
		assertEquals(1.0, naoAlterado.getX(), 0.001);
		assertEquals(1.0, naoAlterado.getY(), 0.001);
	}
}
