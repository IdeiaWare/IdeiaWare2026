package edu.unisc.lic.servlet;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Test;

// FUT-02: EnviarFeedbackServlet -- exige sessao, exige texto nao vazio, e nao explode sem FEEDBACK_EMAIL configurado.
public class EnviarFeedbackServletTest {

	@Test
	public void semSessao_bloqueiaCom401ENaoForward() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getSession(false)).thenReturn(null);
		HttpServletResponse response = mock(HttpServletResponse.class);

		new EnviarFeedbackServlet().doPost(request, response);

		verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		verify(request, never()).getRequestDispatcher(anyString());
	}

	@Test
	public void sugestaoVazia_marcaErroEForward() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpSession session = mock(HttpSession.class);
		when(request.getSession(false)).thenReturn(session);
		when(session.getAttribute("codigoUsuario")).thenReturn(1L);
		when(request.getParameter("sugestao")).thenReturn("   ");
		RequestDispatcher dispatcher = mock(RequestDispatcher.class);
		when(request.getRequestDispatcher("index.jsp")).thenReturn(dispatcher);
		HttpServletResponse response = mock(HttpServletResponse.class);

		new EnviarFeedbackServlet().doPost(request, response);

		verify(request).setAttribute("feedbackErro", true);
		verify(dispatcher).forward(request, response);
	}

	@Test
	public void semFeedbackEmailConfigurado_marcaErroENaoExplode() throws Exception {
		// FEEDBACK: sem FEEDBACK_EMAIL no ambiente, servlet degrada gracioso em vez de estourar.
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpSession session = mock(HttpSession.class);
		when(request.getSession(false)).thenReturn(session);
		when(session.getAttribute("codigoUsuario")).thenReturn(1L);
		when(request.getParameter("sugestao")).thenReturn("Uma sugestao valida de teste.");
		RequestDispatcher dispatcher = mock(RequestDispatcher.class);
		when(request.getRequestDispatcher("index.jsp")).thenReturn(dispatcher);
		HttpServletResponse response = mock(HttpServletResponse.class);

		new EnviarFeedbackServlet().doPost(request, response);

		verify(request).setAttribute("feedbackErro", true);
		verify(dispatcher).forward(request, response);
	}

	@Test
	public void get_redirecionaParaIndex() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getContextPath()).thenReturn("");
		HttpServletResponse response = mock(HttpServletResponse.class);

		new EnviarFeedbackServlet().doGet(request, response);

		verify(response).sendRedirect("/index.jsp");
	}
}
