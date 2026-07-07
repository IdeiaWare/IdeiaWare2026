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

        // AUTORIZACAO: exige login. Antes o servlet nao checava sessao nenhuma -- um
        // POST sem login dava NPE no unboxing de codigoUsuario, e a ideia ficava sem
        // usuario (FK nullable=false), virando erro 500 cru em vez de redirecionar.
        HttpSession session = request.getSession(true);
        Object codigoUsuarioObj = session.getAttribute("codigoUsuario");
        if (codigoUsuarioObj == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // COL-15: validação server-side — não depende apenas do JavaScript do cliente
        String titulo    = request.getParameter("titulo");
        String descricao = request.getParameter("descricao");

        if (titulo == null || titulo.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cadastro-ideia.jsp?erro=titulo_obrigatorio");
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
        // M.2 (2026-07-06): o criador ja e lider e ja entra APROVADO (nao passa por lista
        // de espera). So os que clicam "Participar" depois e que entram como pendentes.
        ideiaUsuario.setFlStatusVinculo(StatusIdeia.VINCULO_APROVADO);
        ideiaUsuario.setDtInscricao();

        // K.8 #5: salva a Ideia e o vinculo de lideranca do autor NUMA SO transacao (antes
        // eram 2 DAOs/transacoes separadas -- falha na 2a deixava a Ideia orfa, sem lider).
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
