package edu.unisc.lic.util;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyString;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;

// TEST-04: PostOnlyFilter (SEC-25) roda em toda rota, barra GET nos endpoints de escrita -- cobre bloqueio/liberacao/POST sempre livre.
public class PostOnlyFilterTest {

	private final PostOnlyFilter filtro = new PostOnlyFilter();

	@Test
	public void get_emPathDeEscrita_bloqueiaCom405ENaoChamaChain() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);

		when(request.getMethod()).thenReturn("GET");
		when(request.getServletPath()).thenReturn("/InserirForma");

		filtro.doFilter(request, response, chain);

		verify(response, times(1)).sendError(
				org.mockito.ArgumentMatchers.eq(HttpServletResponse.SC_METHOD_NOT_ALLOWED), anyString());
		verify(chain, never()).doFilter(request, response);
	}

	@Test
	public void get_forDaListaDeEscrita_naoBloqueia() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);

		when(request.getMethod()).thenReturn("GET");
		// GT-05: EntrarIdeiaServlet entrou pra lista de escrita; GerenciarIdeiaServlet e so-leitura, continua fora.
		when(request.getServletPath()).thenReturn("/GerenciarIdeiaServlet");

		filtro.doFilter(request, response, chain);

		verify(chain, times(1)).doFilter(request, response);
		verify(response, never()).sendError(org.mockito.ArgumentMatchers.anyInt(), anyString());
	}

	@Test
	public void post_emPathDeEscrita_nuncaBloqueia() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);

		when(request.getMethod()).thenReturn("POST");
		when(request.getServletPath()).thenReturn("/InserirForma");

		filtro.doFilter(request, response, chain);

		verify(chain, times(1)).doFilter(request, response);
		verify(response, never()).sendError(org.mockito.ArgumentMatchers.anyInt(), anyString());
	}

	@Test
	public void get_todosOsPathsDaLista_saoBloqueados() throws Exception {
		String[] paths = {
			"/InserirForma", "/InserirTexto", "/DeletarObjServlet", "/EditarTextoServlet",
			"/DeleteCanvaServlet", "/EnviarCanvaServlet", "/ExportCanvaServlet",
			"/SalvarTextoServlet", "/AddDescricaoServlet", "/FecharGrupoServlet",
			"/FinalizarColaboracaoServlet", "/EnviarColaboracaoServlet",
			"/AutoSalvarStoryServlet", "/ExportaStoryServlet", "/SalvarAudioServlet",
			// GT-05/GT-06: mesmo padrao "doGet chama processRequest direto" achado nestes 8.
			"/EditarColaboracaoServlet", "/CadastroIdeiaServlet", "/EntrarIdeiaServlet",
			"/AprovarMembroServlet", "/RejeitarMembroServlet",
			"/UploadArquivoServlet", "/DeletarExportedFileServlet", "/DeletarCanvaexportServlet",
			"/EnviarFeedbackServlet"
		};

		for (String path : paths) {
			HttpServletRequest request = mock(HttpServletRequest.class);
			HttpServletResponse response = mock(HttpServletResponse.class);
			FilterChain chain = mock(FilterChain.class);

			when(request.getMethod()).thenReturn("GET");
			when(request.getServletPath()).thenReturn(path);

			filtro.doFilter(request, response, chain);

			verify(chain, never()).doFilter(request, response);
			verify(response, times(1)).sendError(
					org.mockito.ArgumentMatchers.eq(HttpServletResponse.SC_METHOD_NOT_ALLOWED), anyString());
		}
	}
}
