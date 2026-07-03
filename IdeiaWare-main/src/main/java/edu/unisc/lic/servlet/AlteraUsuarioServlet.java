/*7
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
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

/**
 *
 * @author lucas
 */
@WebServlet(name = "AlteraUsuarioServlet", urlPatterns = {"/AlteraUsuarioServlet"})
public class AlteraUsuarioServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
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
                // RKM-04: null-safe. A linha usava getParameter("email") mas só
                // checava containsKey("nome"); um POST sem o campo email gerava NPE.
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
                // Antes a mesma condição (senhaAtual != "") era checada duas vezes.
                // O correto é exigir que a senha atual E a nova estejam preenchidas
                // antes de tentar a troca, evitando salvar uma senha em branco.
                // BLINDA-05: null-safe. Um POST com senhaAtual mas SEM senhaNova
                // gerava NPE em getParameter("senhaNova").equals(""). Prossegue so
                // se as duas vierem preenchidas.
                String senhaAtualParam = request.getParameter("senhaAtual");
                String senhaNovaParam  = request.getParameter("senhaNova");
                if (senhaAtualParam != null && !senhaAtualParam.isEmpty()
                        && senhaNovaParam != null && !senhaNovaParam.isEmpty()){
                    // SEC-22: verifica a senha atual com bcrypt (checaSenha), nao mais
                    // por "WHERE senha=hash".
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
                usuarioDAO.editar(usuario);
                // UX: confirma o sucesso na propria tela de perfil (antes ia para
                // wait.jsp -> index.jsp e o usuario nao recebia nenhum retorno).
                request.setAttribute("respostaSucesso", true);
            }
            request.getRequestDispatcher("index-perfil.jsp").forward(request, response);
        }
    }

// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
/**
 * Handles the HTTP <code>GET</code> method.
 *
 * @param request servlet request
 * @param response servlet response
 * @throws ServletException if a servlet-specific error occurs
 * @throws IOException if an I/O error occurs
 */
@Override
protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // SEC-18: POST-only. GET nao altera dados/senha (evita CSRF via GET).
        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
