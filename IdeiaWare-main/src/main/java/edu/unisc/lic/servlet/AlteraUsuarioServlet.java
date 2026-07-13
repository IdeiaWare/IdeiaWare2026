package edu.unisc.lic.servlet;

import edu.unisc.lic.dao.UsuarioDAO;
import edu.unisc.lic.domain.Usuario;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.hibernate.exception.ConstraintViolationException;

@WebServlet(name = "AlteraUsuarioServlet", urlPatterns = {"/AlteraUsuarioServlet"})
public class AlteraUsuarioServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            request.setCharacterEncoding("UTF-8");
            
            HttpSession session = request.getSession(true);
            
            Usuario usuario = new Usuario();
            usuario.setCodigo((Long) session.getAttribute("codigoUsuario"));

            UsuarioDAO usuarioDAO = new UsuarioDAO();
            usuario = usuarioDAO.buscar((Long) session.getAttribute("codigoUsuario"));

            if (request.getParameterMap().containsKey("nome")) {
                // RKM-04: null-safe (POST com "nome" mas sem "email" gerava NPE).
                String nomeParam = request.getParameter("nome");
                String emailParam = request.getParameter("email");
                if (nomeParam == null || nomeParam.isEmpty() || emailParam == null || emailParam.isEmpty()){
                    request.setAttribute("respostaNomeEmail", true);
                    request.getRequestDispatcher("index-perfil.jsp").forward(request, response);

                    return;
                }
            }
            
            Boolean Alterar = false;
            
            List<Usuario> lista;
            
            //Se mudou e-mail não pode ter outro igual
            if (request.getParameterMap().containsKey("email")) {
                if (!request.getParameter("email").equals(usuario.getEmail())) {
                    Usuario usuarioE = new Usuario();
                    usuarioE.setEmail(request.getParameter("email"));
                    lista = usuarioDAO.listarParametro(usuarioE, false);

                    if (lista.size() > 0) {
                        request.setAttribute("respostaEmailCadastrado", true);
                        request.getRequestDispatcher("index-perfil.jsp").forward(request, response);

                        return;
                    }
                    usuario.setEmail(request.getParameter("email"));
                    Alterar = true;
                }
            }
            
            if (request.getParameterMap().containsKey("senhaAtual")) {
                // BLINDA-05: null-safe (POST com senhaAtual sem senhaNova gerava NPE); so troca se as 2 vierem.
                String senhaAtualParam = request.getParameter("senhaAtual");
                String senhaNovaParam  = request.getParameter("senhaNova");
                if (senhaAtualParam != null && !senhaAtualParam.isEmpty()
                        && senhaNovaParam != null && !senhaNovaParam.isEmpty()){
                    // SEC-22: verifica a senha atual com bcrypt (checaSenha), nao mais por "WHERE senha=hash".
                    if (!usuario.checaSenha(senhaAtualParam)) {
                        request.setAttribute("respostaSenhaInvalida", true);
                        request.getRequestDispatcher("index-perfil.jsp").forward(request, response);

                        return;
                    }

                    if (!request.getParameter("senhaNova").equals(request.getParameter("senha2"))){
                        request.setAttribute("respostaSenhasDiferentes", true);
                        request.getRequestDispatcher("index-perfil.jsp").forward(request, response);

                        return;
                    }

                    usuario.setSenha(request.getParameter("senhaNova"), true);
                    Alterar = true;
                }
            }
            
            if (request.getParameterMap().containsKey("nome")) {
                if (!request.getParameter("nome").equals("") && (!request.getParameter("nome").equals(usuario.getNome()))) {
                    usuario.setNome(request.getParameter("nome"));
                    Alterar = true;
                }
            }
            
            if (Alterar){
                try {
                    usuarioDAO.editar(usuario);
                } catch (RuntimeException ex) {
                    // K.8 #6: mesma corrida do RACE-01 -- UNIQUE do banco protege, catch da a mensagem amigavel.
                    if (!isConstraintViolation(ex)) {
                        throw ex;
                    }
                    request.setAttribute("respostaEmailCadastrado", true);
                    request.getRequestDispatcher("index-perfil.jsp").forward(request, response);
                    return;
                }
                // TEST-04: atualiza a sessao junto (senao o header so mostrava o nome novo apos relogar).
                session.setAttribute("nomeUsuario", usuario.getNome());
                // UX: confirma o sucesso na propria tela (antes nao dava feedback nenhum).
                request.setAttribute("respostaSucesso", true);
            }
            request.getRequestDispatcher("index-perfil.jsp").forward(request, response);
        }
    }

@Override
protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // SEC-18: POST-only. GET nao altera dados/senha (evita CSRF via GET).
        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }

    @Override
protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    // K.8 #6: percorre a cadeia de causas (Hibernate as vezes envolve a excecao, as vezes nao).
    private static boolean isConstraintViolation(Throwable t) {
        while (t != null) {
            if (t instanceof ConstraintViolationException) {
                return true;
            }
            t = t.getCause();
        }
        return false;
    }

    @Override
public String getServletInfo() {
        return "Short description";
    }

}
