package edu.unisc.lic.util;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * SEC-25: endpoints de ESCRITA (colaboracao / canvas / storytelling) que so devem aceitar
 * POST. Todos os chamadores legitimos ja usam POST (forms method="post" e $.ajax type:'POST'),
 * entao um GET nesses paths so serviria p/ MUDAR ESTADO driblando o CSRF (ex.: vitima clica num
 * link). Aqui o GET nesses paths e rejeitado com 405 -> fecha o vetor de "CSRF por GET".
 *
 * Centralizado num filtro (em vez de mexer no doGet de 15 servlets) p/ a lista ficar num lugar
 * so e facil de revisar. Complementa o CsrfFilter (que cobre os POST) e o SameSite=Lax.
 */
public class PostOnlyFilter implements Filter {

    private static final Set<String> POST_ONLY = new HashSet<String>(Arrays.asList(
            // Canvas
            "/InserirForma", "/InserirTexto", "/DeletarObjServlet", "/EditarTextoServlet",
            "/DeleteCanvaServlet", "/EnviarCanvaServlet", "/ExportCanvaServlet",
            // Colaboracao
            "/SalvarTextoServlet", "/AddDescricaoServlet", "/FecharGrupoServlet",
            "/FinalizarColaboracaoServlet", "/EnviarColaboracaoServlet",
            // Storytelling
            "/AutoSalvarStoryServlet", "/ExportaStoryServlet", "/SalvarAudioServlet"
    ));

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        if ("GET".equalsIgnoreCase(request.getMethod())) {
            String path = request.getServletPath();
            if (path != null && POST_ONLY.contains(path)) {
                response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                        "Este recurso so aceita POST.");
                return;
            }
        }

        chain.doFilter(req, resp);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // sem configuracao
    }

    @Override
    public void destroy() {
        // nada a liberar
    }
}
