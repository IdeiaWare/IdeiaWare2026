package edu.unisc.lic.servlet;
import edu.unisc.lic.classes.StatusIdeia;

import edu.unisc.lic.classes.Data;
import edu.unisc.lic.dao.IdeiaDAO;
import edu.unisc.lic.dao.IdeiaUsuarioDAO;
import edu.unisc.lic.dao.LogColaboracaoDAO;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.IdeiaUsuario;
import edu.unisc.lic.domain.LogColaboracao;
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

@WebServlet(name = "FecharGrupoServlet", urlPatterns = {"/FecharGrupoServlet"})
public class FecharGrupoServlet extends HttpServlet {

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

        // RET-14: valida parametro/ideia antes de usar (evita 500/NPE).
        String codigoParam = request.getParameter("codigo");
        Ideia ideia = null;
        if (codigoParam != null) {
            try {
                ideia = new IdeiaDAO().buscar(Long.parseLong(codigoParam));
            } catch (NumberFormatException e) {
                ideia = null;
            }
        }
        if (ideia == null) {
            response.sendRedirect(request.getContextPath() + File.separator + "minha-ideia.jsp");
            return;
        }

        // AUTORIZACAO: so o LIDER da ideia pode fechar o grupo / transferir a lideranca.
        // Antes, qualquer um (ate sem login) regredia o status com um POST do codigo.
        Usuario sessionUser = new Usuario();
        sessionUser.setCodigo((Long) codigoUsuario);
        List<IdeiaUsuario> souLider = new IdeiaUsuarioDAO()
                .listarParametro(new IdeiaUsuario(sessionUser, ideia, "S"));
        if (souLider == null || souLider.isEmpty()) {
            response.sendRedirect(request.getContextPath() + File.separator + "minha-ideia.jsp");
            return;
        }

        IdeiaDAO ideiaDAO = new IdeiaDAO();

        ideia.setStatusGrupo(StatusIdeia.GRUPO_FECHADO);
        ideia.setStatus(StatusIdeia.EM_DESENVOLVIMENTO);
        ideia.setDtInicioDesenv();
        ideiaDAO.editar(ideia);

        IdeiaUsuarioDAO ideiaUsuarioDAO = new IdeiaUsuarioDAO();
        List<IdeiaUsuario> list = ideiaUsuarioDAO.listarParametro(new IdeiaUsuario(null, ideia, "S"));

        if (list == null || list.isEmpty()) {
            response.sendRedirect(request.getContextPath() + File.separator + "minha-ideia.jsp");
            return;
        }

        String radioParam = request.getParameter("radio");
        if (radioParam != null && !radioParam.isEmpty()) {
            long radioId;
            try {
                radioId = Long.parseLong(radioParam);
            } catch (NumberFormatException ex) {
                response.sendRedirect(request.getContextPath() + File.separator + "minha-ideia.jsp");
                return;
            }
            if (list.get(0).getUsuario().getCodigo() != radioId) {
                list.get(0).setFlLider("N");
                ideiaUsuarioDAO.editar(list.get(0));

                List<IdeiaUsuario> todos = ideiaUsuarioDAO.listarParametro(new IdeiaUsuario(null, ideia, null));
                if (todos != null) {
                    for (IdeiaUsuario iU : todos) {
                        if (iU.getUsuario().getCodigo() == radioId) {
                            iU.setFlLider("S");
                            ideiaUsuarioDAO.editar(iU);
                        }
                    }
                }
            }
        }

        List<IdeiaUsuario> lideres = new IdeiaUsuarioDAO().listarParametro(new IdeiaUsuario(new Usuario(), ideia, "S"));
        if (lideres == null || lideres.isEmpty()) {
            response.sendRedirect(request.getContextPath() + File.separator + "minha-ideia.jsp");
            return;
        }

        String descricaoInicial = ideia.getDescricao() != null ? ideia.getDescricao() : "";
        LogColaboracao logColaboracao = new LogColaboracao(
                ideia,
                lideres.get(0).getUsuario(),
                Data.horaAtual(),
                descricaoInicial);

        new LogColaboracaoDAO().salvar(logColaboracao);

        response.sendRedirect(request.getContextPath() + File.separator + "minha-ideia.jsp");
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
