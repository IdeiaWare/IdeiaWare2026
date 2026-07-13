package edu.unisc.lic.servlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;

import edu.unisc.lic.dao.UsuarioDAO;
import edu.unisc.lic.domain.Usuario;

// TEST-04/RACE-01: prova que 2 cadastros simultaneos com o mesmo login nao criam 2 contas -- UNIQUE do banco fecha a corrida, catch devolve a resposta amigavel.
public class CadastroUsuarioServletTest {

	private HttpServletRequest mockRequest(String login, String email) throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getParameter("nome")).thenReturn("Usuario Teste");
		when(request.getParameter("usuario")).thenReturn(login);
		when(request.getParameter("senha")).thenReturn("senha123");
		when(request.getParameter("senha2")).thenReturn("senha123");
		when(request.getParameter("email")).thenReturn(email);

		RequestDispatcher dispatcher = mock(RequestDispatcher.class);
		when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
		return request;
	}

	@Test
	public void doisCadastrosSimultaneosComMesmoLogin_soUmaContaEhCriada() throws Exception {
		String login = "corrida_" + System.nanoTime();
		CyclicBarrier largada = new CyclicBarrier(2);
		CountDownLatch fim = new CountDownLatch(2);
		AtomicReference<Throwable> escapouThread1 = new AtomicReference<>();
		AtomicReference<Throwable> escapouThread2 = new AtomicReference<>();

		Runnable tarefa1 = criarTarefa(login, "email1_" + System.nanoTime() + "@x.com", largada, fim, escapouThread1);
		Runnable tarefa2 = criarTarefa(login, "email2_" + System.nanoTime() + "@x.com", largada, fim, escapouThread2);

		new Thread(tarefa1).start();
		new Thread(tarefa2).start();

		boolean terminouATempo = fim.await(10, TimeUnit.SECONDS);

		assertTrue("as 2 threads devem terminar", terminouATempo);
		assertNull("processRequest nao deve deixar excecao escapar (thread 1)", escapouThread1.get());
		assertNull("processRequest nao deve deixar excecao escapar (thread 2)", escapouThread2.get());

		Usuario filtro = new Usuario();
		filtro.setUsuario(login);
		List<Usuario> resultado = new UsuarioDAO().listarParametro(filtro, false);
		assertEquals("so 1 conta com esse login deve existir, mesmo com a corrida", 1, resultado.size());
	}

	private Runnable criarTarefa(String login, String email, CyclicBarrier largada, CountDownLatch fim,
			AtomicReference<Throwable> escapou) {
		return () -> {
			try {
				HttpServletRequest request = mockRequest(login, email);
				HttpServletResponse response = mock(HttpServletResponse.class);
				CadastroUsuarioServlet servlet = new CadastroUsuarioServlet();

				largada.await(5, TimeUnit.SECONDS); // sincroniza as 2 threads pra maximizar a corrida
				servlet.doPost(request, response);
			} catch (Throwable t) {
				escapou.set(t);
			} finally {
				fim.countDown();
			}
		};
	}
}
