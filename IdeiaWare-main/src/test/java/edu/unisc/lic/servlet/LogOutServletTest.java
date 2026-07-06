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

/**
 * TEST-04, Tier 2 (2026-07-05): LogOutServlet -- infra de auth core, nunca teve teste.
 */
public class LogOutServletTest {

	@Test
	public void comSessaoAtiva_invalidaSessaoEForwardParaLogin() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpSession session = mock(HttpSession.class);
		when(request.getSession(false)).thenReturn(session);
		RequestDispatcher dispatcher = mock(RequestDispatcher.class);
		when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
		HttpServletResponse response = mock(HttpServletResponse.class);

		new LogOutServlet().doGet(request, response);

		verify(session).invalidate();
		verify(dispatcher).forward(request, response);
	}

	@Test
	public void semSessaoAtiva_naoQuebraEForwardParaLogin() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getSession(false)).thenReturn(null);
		RequestDispatcher dispatcher = mock(RequestDispatcher.class);
		when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
		HttpServletResponse response = mock(HttpServletResponse.class);

		new LogOutServlet().doGet(request, response);

		verify(dispatcher).forward(request, response);
	}

	@Test
	public void post_tambemFuncionaIgualAoGet() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpSession session = mock(HttpSession.class);
		when(request.getSession(false)).thenReturn(session);
		RequestDispatcher dispatcher = mock(RequestDispatcher.class);
		when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
		HttpServletResponse response = mock(HttpServletResponse.class);

		new LogOutServlet().doPost(request, response);

		verify(session).invalidate();
	}
}
