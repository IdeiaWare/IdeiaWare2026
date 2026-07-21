package edu.unisc.lic.servlet;
import edu.unisc.lic.classes.StatusIdeia;

import edu.unisc.lic.classes.Constantes;
import edu.unisc.lic.classes.Data;
import edu.unisc.lic.dao.IdeiaDAO;
import edu.unisc.lic.dao.IdeiaUsuarioDAO;
import edu.unisc.lic.dao.LogColaboracaoDAO;
import edu.unisc.lic.dao.StorytellingDAO;
import edu.unisc.lic.dao.UsuarioDAO;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.IdeiaUsuario;
import edu.unisc.lic.domain.LogColaboracao;
import edu.unisc.lic.domain.Storytelling;
import edu.unisc.lic.domain.Usuario;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class FinalizarColaboracaoServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        Object codigoUsuario = session == null ? null : session.getAttribute("codigoUsuario");
        if (codigoUsuario == null) {
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

        // RET-12: nao re-finaliza ideia ja avancada
        // UX-PADRAO-ETAPA-FINALIZADA: mensagem via flash de sessao (lida por headerCookies.jsp).
        String statusAtual = ideia.getStatus();
        if (StatusIdeia.STORYTELLING.equals(statusAtual) || StatusIdeia.CAIXA_FERRAMENTAS.equals(statusAtual)
                || StatusIdeia.CANVAS.equals(statusAtual) || StatusIdeia.FINALIZADO.equals(statusAtual)) {
            session.setAttribute("mensagemErroEtapa", "A colaboração desta ideia já foi encerrada.");
            response.sendRedirect(request.getContextPath() + File.separator + "minha-ideia.jsp");
            return;
        }

        // SEC-14: so o LIDER finaliza
        Usuario sessionUser = new Usuario();
        sessionUser.setCodigo((Long) codigoUsuario);
        List<IdeiaUsuario> souLider = new IdeiaUsuarioDAO()
                .listarParametro(new IdeiaUsuario(sessionUser, ideia, "S"));
        if (souLider == null || souLider.isEmpty()) {
            response.sendRedirect(request.getContextPath() + File.separator + "minha-ideia.jsp");
            return;
        }

        LogColaboracao lg = new LogColaboracao();
        lg.setIdeia(ideia);

        // COL-02: null check — buscarDescricaoFinal pode retornar null
        LogColaboracao logCol = new LogColaboracaoDAO().buscarDescricaoFinal(lg);
        if (logCol != null && logCol.getDescricao() != null) {
            ideia.setDescricao(logCol.getDescricao());
        }

        ideia.setStatus(StatusIdeia.STORYTELLING);
        ideia.setDtFimDesenv(Data.horaAtual());

        criaDiretorios(ideia);
        criaStorytelling(ideia);   // COL-11: verifica duplicata antes de criar

        new IdeiaDAO().editar(ideia);

        // UX-SUCESSO-CONSISTENCIA: tela de confirmacao, mesmo padrao de Canvas/Storytelling.
        response.sendRedirect(request.getContextPath() + File.separator + "colaboracao-finalizada.jsp");
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

    private void criaDiretorios(Ideia i) {
        File diretorio = new File(getServletContext().getRealPath("") + File.separator
                + Constantes.CAMINHO_IMAGENS_STORYTELLING + i.getCodigo().toString());
        diretorio.mkdirs();
    }

    private void criaStorytelling(Ideia i) {
        StorytellingDAO stDAO = new StorytellingDAO();
        // COL-11: só cria se não existir — evita duplicatas por duplo-clique
        List<Storytelling> existentes = stDAO.listarParametro(
                new Storytelling(new Usuario(), i, null, null));
        if (existentes != null && !existentes.isEmpty()) {
            return;
        }
        Usuario u = new UsuarioDAO().buscar(i.getUsuario().getCodigo());
        try {
            stDAO.salvar(new Storytelling(u, i, Data.horaAtual(), "DE"));
        } catch (org.hibernate.exception.ConstraintViolationException ex) {
            // K.8 #3: UNIQUE do banco barra o 2o insert
        }
    }
}
