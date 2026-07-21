package edu.unisc.lic.servlet;
import edu.unisc.lic.classes.StatusIdeia;

import edu.unisc.lic.dao.IdeiaDAO;
import edu.unisc.lic.dao.UsuarioDAO;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.IdeiaUsuario;
import edu.unisc.lic.domain.Usuario;
import java.io.File;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class CadastroIdeiaServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // GT-05: exige login
        HttpSession session = request.getSession(true);
        Object codigoUsuarioObj = session.getAttribute("codigoUsuario");
        if (codigoUsuarioObj == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // COL-15: validacao server-side
        String titulo    = request.getParameter("titulo");
        String descricao = request.getParameter("descricao");

        if (titulo == null || titulo.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cadastro-ideia.jsp?erro=titulo_obrigatorio");
            return;
        }
        // UX-CADASTRO-TITULO-CURTO: minlength=4 so' era checado no client (cadastro-ideia.jsp);
        // um POST direto passava titulo de 1 caractere sem checagem nenhuma no servidor.
        if (titulo.trim().length() < 4) {
            response.sendRedirect(request.getContextPath() + "/cadastro-ideia.jsp?erro=titulo_curto");
            return;
        }
        if (titulo.trim().length() > 50) {
            response.sendRedirect(request.getContextPath() + "/cadastro-ideia.jsp?erro=titulo_longo");
            return;
        }
        if (descricao == null || descricao.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cadastro-ideia.jsp?erro=descricao_obrigatoria");
            return;
        }
        if (descricao.trim().length() > 200) {
            response.sendRedirect(request.getContextPath() + "/cadastro-ideia.jsp?erro=descricao_longa");
            return;
        }

        Usuario usuario = new UsuarioDAO().buscar((Long) codigoUsuarioObj);

        Ideia ideia = new Ideia();
        ideia.setTitulo(titulo.trim());
        ideia.setDescricao(descricao.trim());
        ideia.setDtCriacao();
        ideia.setUsuario(usuario);
        ideia.setStatus(StatusIdeia.PENDENTE);
        ideia.setStatusGrupo(StatusIdeia.GRUPO_ABERTO);

        IdeiaUsuario ideiaUsuario = new IdeiaUsuario(usuario, ideia, "S");
        ideiaUsuario.setFlStatusVinculo(StatusIdeia.VINCULO_APROVADO);
        ideiaUsuario.setDtInscricao();

        // K.8 #5: Ideia + vinculo de lideranca numa so transacao
        new IdeiaDAO().criarComLider(ideia, ideiaUsuario);

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
