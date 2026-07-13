package edu.unisc.lic.servlet;

import edu.unisc.lic.dao.ColaboracaoIdeiaDAO;
import edu.unisc.lic.dao.IdeiaDAO;
import edu.unisc.lic.domain.ColaboracaoIdeia;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.util.JsonUtil;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RetornaMensagensServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");

        // COLM-05: sem ideiaId na sessão não há o que retornar.
        Object ideiaIdObj = request.getSession().getAttribute("ideiaId");
        if (ideiaIdObj == null) {
            response.setContentType("text/plain");
            response.getWriter().write("não");
            return;
        }

        IdeiaDAO ideiaDAO = new IdeiaDAO();
        Ideia ideia = ideiaDAO.buscar((Long) ideiaIdObj);

        // SRV-NPE-01: ideiaId na sessao pode apontar pra ideia que nao existe mais.
        if (ideia == null) {
            response.setContentType("text/plain");
            response.getWriter().write("não");
            return;
        }

        // M.6: protocolo por ULTIMO CODIGO visto (nao mais por contagem) -- nunca perde colaboracao do meio.
        long ultimoCodigo;
        try {
            ultimoCodigo = Long.parseLong(request.getParameter("ultimoCodigo"));
        } catch (NumberFormatException e) {
            ultimoCodigo = 0L;
        }

        ColaboracaoIdeiaDAO colaboracaoIdeiaDAO = new ColaboracaoIdeiaDAO();

        ColaboracaoIdeia colaboracaoIdeia = new ColaboracaoIdeia();
        colaboracaoIdeia.setIdeia(ideia);

        List<ColaboracaoIdeia> novas = colaboracaoIdeiaDAO.listarAposCodigo(colaboracaoIdeia, ultimoCodigo);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        // GT-01: GSON_SEM_SENHA -- senao vaza o hash bcrypt de cada autor a cada poll de 2s.
        response.getWriter().write(JsonUtil.GSON_SEM_SENHA.toJson(novas));
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
