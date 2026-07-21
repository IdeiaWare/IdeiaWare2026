package edu.unisc.lic.servlet;

import edu.unisc.lic.dao.IdeiaDAO;
import edu.unisc.lic.dao.IdeiaUsuarioDAO;
import edu.unisc.lic.dao.UsuarioDAO;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.IdeiaUsuario;
import edu.unisc.lic.domain.Usuario;

import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class EntrarColaboracaoServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // SRV-IDOR-01: exige login
        HttpSession session = request.getSession(true);
        Object codigoUsuarioObj = session.getAttribute("codigoUsuario");
        if (codigoUsuarioObj == null) {
            response.sendRedirect(request.getContextPath() + File.separator + "login.jsp");
            return;
        }

        // RET-14: valida parametro/ideia antes de usar
        String ideiaIdParam = request.getParameter("ideiaId");
        Ideia ideia = null;
        if (ideiaIdParam != null) {
            try {
                ideia = new IdeiaDAO().buscar(Long.parseLong(ideiaIdParam));
            } catch (NumberFormatException e) {
                ideia = null;
            }
        }
        if (ideia == null) {
            response.sendRedirect(request.getContextPath() + File.separator + "minha-ideia.jsp");
            return;
        }

        UsuarioDAO uDAO = new UsuarioDAO();
        Usuario u = uDAO.buscar((Long) codigoUsuarioObj);

        IdeiaUsuarioDAO iuDAO = new IdeiaUsuarioDAO();
        IdeiaUsuario iu = new IdeiaUsuario();
        iu.setIdeia(ideia);
        iu.setUsuario(u);

        // COL-08: protege .get(0) de lista vazia
        List<IdeiaUsuario> lista = iuDAO.listarParametro(iu);
        if (lista.size() > 0) {
        	iu = lista.get(0);
        } else {
        	iu = null;
        }

        // SRV-IDOR-01/GT-02: exige vinculo APROVADO ou admin
        boolean vinculoAprovado = iu != null && (iu.getFlStatusVinculo() == null
                || edu.unisc.lic.classes.StatusIdeia.VINCULO_APROVADO.equals(iu.getFlStatusVinculo()));
        if (u == null || (!vinculoAprovado && !"adm".equals(u.getPermissao()))) {
            response.sendRedirect(request.getContextPath() + File.separator + "minha-ideia.jsp");
            return;
        }

        // ISRETENCAO-TIPO: sempre Boolean (antes o branch com parametro guardava String cru).
        String retencao = request.getParameter("retencao");
        session.setAttribute("isRetencao", retencao != null && !retencao.isEmpty());

        session.setAttribute("ideiaId", ideia.getCodigo());
        session.setAttribute("ideiaTitulo", ideia.getTitulo());
        session.setAttribute("ideiaDesc", ideia.getDescricao());
        session.setAttribute("lider", iu != null ? iu.getFlLider() : "N");

        response.sendRedirect(request.getContextPath() + File.separator + "colaboracao.jsp");

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
