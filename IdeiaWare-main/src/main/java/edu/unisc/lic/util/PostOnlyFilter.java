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

// SEC-25: endpoints de escrita que so aceitam POST, allowlist mantida a mao
public class PostOnlyFilter implements Filter {

    private static final Set<String> POST_ONLY = new HashSet<String>(Arrays.asList(
            "/InserirForma", "/InserirTexto", "/DeletarObjServlet", "/EditarTextoServlet",
            "/DeleteCanvaServlet", "/EnviarCanvaServlet", "/ExportCanvaServlet",
            "/SalvarTextoServlet", "/AddDescricaoServlet", "/FecharGrupoServlet",
            "/FinalizarColaboracaoServlet", "/EnviarColaboracaoServlet",
            "/AutoSalvarStoryServlet", "/ExportaStoryServlet", "/SalvarAudioServlet",
            "/EditarColaboracaoServlet", "/CadastroIdeiaServlet", "/EntrarIdeiaServlet",
            "/AprovarMembroServlet", "/RejeitarMembroServlet",
            "/UploadArquivoServlet", "/DeletarExportedFileServlet", "/DeletarCanvaexportServlet",
            "/EnviarFeedbackServlet"
    ));
    // GT-05/GT-06/FUT-02: allowlist inclui doGet sem guard e feedback

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
    }

    @Override
    public void destroy() {
    }
}
