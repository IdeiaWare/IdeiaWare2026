package edu.unisc.lic.servlet;

import edu.unisc.lic.classes.StatusIdeia;
import edu.unisc.lic.dao.ColaboracaoIdeiaDAO;
import edu.unisc.lic.domain.ColaboracaoIdeia;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.util.JsonUtil;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class EditarColaboracaoServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        // ENCODING-01: sem isso os writes de erro saem com acentuacao quebrada (charset default do container).
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        Object codigoUsuarioObj = session == null ? null : session.getAttribute("codigoUsuario");
        if (codigoUsuarioObj == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        ColaboracaoIdeiaDAO colaboracaoIdeiaDAO = new ColaboracaoIdeiaDAO();

        // RET-14: protege o parse do parametro, evita 500
        ColaboracaoIdeia colaboracaoIdeia = null;
        try {
            colaboracaoIdeia = colaboracaoIdeiaDAO.buscar(
                    Long.parseLong(request.getParameter("colaboracao")));
        } catch (NumberFormatException e) {
            colaboracaoIdeia = null;
        }

        if (colaboracaoIdeia == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Colaboração não encontrada.");
            return;
        }

        Long codigoUsuario = (Long) codigoUsuarioObj;
        if (colaboracaoIdeia.getUsuario() == null
                || !codigoUsuario.equals(colaboracaoIdeia.getUsuario().getCodigo())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Apenas o autor pode editar a própria colaboração.");
            return;
        }

        if ("ad".equals(colaboracaoIdeia.getFlSalvado())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Esta colaboração já foi adicionada à descrição e não pode mais ser editada.");
            return;
        }

        // UX-COLAB-ETAPA-TRAVADA: mesmo guard de EnviarColaboracaoServlet.
        Ideia ideiaDaColab = colaboracaoIdeia.getIdeia();
        if (ideiaDaColab == null || !StatusIdeia.EM_DESENVOLVIMENTO.equals(ideiaDaColab.getStatus())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("A colaboração desta ideia já foi encerrada.");
            return;
        }

        String novoTexto = request.getParameter("descricao");
        if (novoTexto == null || novoTexto.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("O texto da colaboração não pode ser vazio.");
            return;
        }
        novoTexto = novoTexto.trim();
        if (novoTexto.length() > 1500) {
            novoTexto = novoTexto.substring(0, 1500);
        }

        colaboracaoIdeia.setDescricaoIdeiaAnterior(colaboracaoIdeia.getDescricaoIdeiaAtual());
        colaboracaoIdeia.setDescricaoIdeiaAtual(novoTexto);
        colaboracaoIdeia.setDtModificacao();
        colaboracaoIdeiaDAO.editar(colaboracaoIdeia);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        // GT-01: GSON_SEM_SENHA, senao vaza o hash bcrypt
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
        return "Edita uma colaboracao (M.10) -- so autor, so antes de adicionada a descricao.";
    }
}
