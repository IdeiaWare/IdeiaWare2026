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

// SEC-25: endpoints de escrita que so aceitam POST (GET nesses paths driblaria o CSRF). K.2: allowlist mantida a mao -- todo servlet de escrita novo precisa entrar na lista.
public class PostOnlyFilter implements Filter {

    private static final Set<String> POST_ONLY = new HashSet<String>(Arrays.asList(
            // Canvas
            "/InserirForma", "/InserirTexto", "/DeletarObjServlet", "/EditarTextoServlet",
            "/DeleteCanvaServlet", "/EnviarCanvaServlet", "/ExportCanvaServlet",
            // Colaboracao
            "/SalvarTextoServlet", "/AddDescricaoServlet", "/FecharGrupoServlet",
            "/FinalizarColaboracaoServlet", "/EnviarColaboracaoServlet",
            // Storytelling
            "/AutoSalvarStoryServlet", "/ExportaStoryServlet", "/SalvarAudioServlet",
            // GT-05: doGet chamava processRequest direto (boilerplate), sem guard.
            "/EditarColaboracaoServlet", "/CadastroIdeiaServlet", "/EntrarIdeiaServlet",
            "/AprovarMembroServlet", "/RejeitarMembroServlet",
            // GT-06: risco baixo (multipart/admin-only), adicionados por consistencia.
            "/UploadArquivoServlet", "/DeletarExportedFileServlet", "/DeletarCanvaexportServlet",
            // FUT-02: feedback/sugestao.
            "/EnviarFeedbackServlet"
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
        // no-op
    }

    @Override
    public void destroy() {
        // no-op
    }
}
