package edu.unisc.lic.servlet;

import edu.unisc.lic.classes.StatusIdeia;
import edu.unisc.lic.dao.IdeiaUsuarioDAO;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.IdeiaUsuario;
import edu.unisc.lic.domain.Usuario;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * M.2/M.3 (2026-07-06): o LIDER rejeita a entrada de alguem no grupo (vinculo P -> R) COM
 * MOTIVO obrigatorio (mesmo padrao de rejeitar uma ideia). So o lider da ideia rejeita, e
 * so vinculos PENDENTES. O rejeitado passa a ver o motivo (em minha-ideia.jsp).
 */
@WebServlet(name = "RejeitarMembroServlet", urlPatterns = {"/RejeitarMembroServlet"})
public class RejeitarMembroServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        // AUTORIZACAO: exige login.
        HttpSession session = request.getSession(false);
        Object codigoUsuario = session == null ? null : session.getAttribute("codigoUsuario");
        if (codigoUsuario == null) {
            response.sendRedirect(request.getContextPath() + File.separator + "login.jsp");
            return;
        }

        IdeiaUsuarioDAO ideiaUsuarioDAO = new IdeiaUsuarioDAO();

        IdeiaUsuario vinculo = null;
        try {
            vinculo = ideiaUsuarioDAO.buscar(Long.parseLong(request.getParameter("vinculo")));
        } catch (NumberFormatException e) {
            vinculo = null;
        }
        if (vinculo == null) {
            response.sendRedirect(request.getContextPath() + File.separator + "detalhes-ideia.jsp");
            return;
        }

        Ideia ideia = vinculo.getIdeia();

        // AUTORIZACAO: so o LIDER da ideia rejeita.
        Usuario sessionUser = new Usuario();
        sessionUser.setCodigo((Long) codigoUsuario);
        List<IdeiaUsuario> souLider = ideiaUsuarioDAO
                .listarParametro(new IdeiaUsuario(sessionUser, ideia, "S"));
        if (souLider == null || souLider.isEmpty()) {
            response.sendRedirect(request.getContextPath() + File.separator + "detalhes-ideia.jsp");
            return;
        }

        // REVISAO 2026-07-07: motivo era exigido so no client (onsubmit do modal em
        // detalhes-ideia.jsp) -- um POST forjado sem "motivo" gravava motivoRejeicaoMembro
        // vazio, contrariando a garantia que minha-ideia.jsp depende (mostrar o motivo ao
        // rejeitado). Exige tambem no servidor, mesmo padrao do EditarColaboracaoServlet
        // pra descricao vazia.
        String motivo = request.getParameter("motivo");
        if (motivo == null || motivo.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + File.separator + "detalhes-ideia.jsp");
            return;
        }

        // So rejeita quem esta PENDENTE.
        if (StatusIdeia.VINCULO_PENDENTE.equals(vinculo.getFlStatusVinculo())) {
            vinculo.setFlStatusVinculo(StatusIdeia.VINCULO_REJEITADO);
            vinculo.setMotivoRejeicaoMembro(motivo.trim());
            ideiaUsuarioDAO.editar(vinculo);
        }

        response.sendRedirect(request.getContextPath() + File.separator + "detalhes-ideia.jsp");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // POST-only.
        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "M.2/M.3: lider rejeita a entrada de um membro no grupo (P -> R) com motivo.";
    }
}
