package edu.unisc.lic.servlet;

import edu.unisc.lic.classes.AssinaturaCaixa;
import edu.unisc.lic.dao.IdeiaUsuarioDAO;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.IdeiaUsuario;
import edu.unisc.lic.domain.Usuario;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class EntrarCaixaServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // SEC-23: exige login + participacao, assina o ideiaId (HMAC)
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("codigoUsuario") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        Long ideiaId;
        try {
            ideiaId = Long.parseLong(request.getParameter("ideiaId"));
        } catch (NumberFormatException e) {
            ideiaId = null;
        }
        if (ideiaId == null) {
            response.sendRedirect(request.getContextPath() + "/lista-caixa-de-ferramentas.jsp");
            return;
        }

        Usuario u = new Usuario();
        u.setCodigo((Long) session.getAttribute("codigoUsuario"));
        Ideia i = new Ideia();
        i.setCodigo(ideiaId);
        List<IdeiaUsuario> vinculo = new IdeiaUsuarioDAO()
                .listarParametro(new IdeiaUsuario(u, i, null));
        if (vinculo == null || vinculo.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/lista-caixa-de-ferramentas.jsp");
            return;
        }

        String host = request.getServerName();
        int port = request.getServerPort();

        Cookie ck = new Cookie("ideiaId", String.valueOf(ideiaId));
        ck.setMaxAge(-1);
        ck.setPath("/");
        response.addCookie(ck);

        Cookie ckSig = new Cookie("ideiaSig", AssinaturaCaixa.assinar(String.valueOf(ideiaId)));
        ckSig.setMaxAge(-1);
        ckSig.setPath("/");
        response.addCookie(ckSig);

        String nomeParam = request.getParameter("usuarioNome");
        Cookie ck2 = new Cookie("usuarioNome", URLEncoder.encode(nomeParam == null ? "" : nomeParam, "UTF-8"));
        ck2.setMaxAge(-1);
        ck2.setPath("/");
        response.addCookie(ck2);

        // INFRA-11/ROUTE-404-01: scheme da requisicao + rota real direto
        response.sendRedirect(request.getScheme() + "://" + host + ":" + port + "/toolkit/persona/lista");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }

}
