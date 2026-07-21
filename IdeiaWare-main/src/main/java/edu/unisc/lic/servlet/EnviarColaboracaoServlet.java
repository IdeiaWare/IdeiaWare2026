package edu.unisc.lic.servlet;

import edu.unisc.lic.classes.Data;
import edu.unisc.lic.classes.StatusIdeia;
import edu.unisc.lic.dao.ColaboracaoIdeiaDAO;
import edu.unisc.lic.dao.IdeiaDAO;
import edu.unisc.lic.dao.UsuarioDAO;
import edu.unisc.lic.domain.ColaboracaoIdeia;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.Usuario;
import edu.unisc.lic.util.JsonUtil;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class EnviarColaboracaoServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        // ENCODING-01: sem isso os writes de erro saem com acentuacao quebrada (charset default do container).
        response.setCharacterEncoding("UTF-8");

        // COLM-03: exige sessao valida com ideiaId e usuario
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("ideiaId") == null
                || session.getAttribute("codigoUsuario") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        IdeiaDAO ideiaDAO = new IdeiaDAO();
        Ideia ideia = ideiaDAO.buscar((Long) session.getAttribute("ideiaId"));

        // UX-COLAB-ETAPA-TRAVADA: bloqueia escrita se a Colaboração ja foi finalizada (o
        // dono avancou a ideia pra Storytelling, mas uma aba antiga de outro participante
        // continuava conseguindo enviar colaboracoes -- nenhum guard de status existia aqui).
        if (ideia == null || !StatusIdeia.EM_DESENVOLVIMENTO.equals(ideia.getStatus())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("A colaboração desta ideia já foi encerrada.");
            return;
        }

        UsuarioDAO usuarioDAO = new UsuarioDAO();
        Usuario usuario = usuarioDAO.buscar((Long) session.getAttribute("codigoUsuario"));

        ColaboracaoIdeiaDAO colaboracaoIdeiaDAO = new ColaboracaoIdeiaDAO();

        ColaboracaoIdeia colaboracaoIdeia = new ColaboracaoIdeia(ideia, usuario, Data.horaAtual(), request.getParameter("descricao"));

        colaboracaoIdeiaDAO.salvar(colaboracaoIdeia);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        // GT-01: GSON_SEM_SENHA evita vazar o hash bcrypt
        response.getWriter().write(JsonUtil.GSON_SEM_SENHA.toJson(colaboracaoIdeia));
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
