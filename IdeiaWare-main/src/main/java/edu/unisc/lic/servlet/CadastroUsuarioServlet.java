/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unisc.lic.servlet;

import edu.unisc.lic.domain.Usuario;
import edu.unisc.lic.dao.UsuarioDAO;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.exception.ConstraintViolationException;

/**
 *
 * @author Rafael
 */
public class CadastroUsuarioServlet extends HttpServlet {

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
        
        request.setCharacterEncoding("UTF-8");

        Usuario usuario = new Usuario();
        usuario.setUsuario(request.getParameter("usuario"));

        UsuarioDAO usuarioDAO = new UsuarioDAO();
        List<Usuario> lista = usuarioDAO.listarParametro(usuario, false);
        
        // LucasFreitag 2024 :: email
        // INFRA-07: usa "".equals(...) (null-safe) — um POST sem algum dos campos
        // gerava NullPointerException em getParameter(...).equals("").
        if ("".equals(request.getParameter("nome")) || "".equals(request.getParameter("senha")) ||
            "".equals(request.getParameter("usuario")) || "".equals(request.getParameter("senha2")) ||
            "".equals(request.getParameter("email")) ||
            request.getParameter("nome") == null || request.getParameter("senha") == null ||
            request.getParameter("usuario") == null || request.getParameter("senha2") == null ||
            request.getParameter("email") == null) {
            Boolean resp = true;

            request.setAttribute("respostaCadastro3", resp);
            request.getRequestDispatcher("login.jsp").forward(request, response);
            
            return;
        }
            
        if (!request.getParameter("senha").equals(request.getParameter("senha2"))){
            Boolean resp = true;

            request.setAttribute("respostaCadastro2", resp);
            request.getRequestDispatcher("login.jsp").forward(request, response);
            
            return;
        }
        
        // LucasFreitag 2024 :: email já cadastrado
        Usuario usuarioE = new Usuario();
        usuarioE.setEmail(request.getParameter("email"));
        List<Usuario> listaE = usuarioDAO.listarParametro(usuarioE, false);
        
        if (listaE.size() == 1) {
            Boolean resp = true;

            request.setAttribute("respostaCadastro4", resp);
            request.getRequestDispatcher("login.jsp").forward(request, response);
            
            return;
        }      
        
        if (lista.size() == 1) {
            Boolean resp = true;

            request.setAttribute("respostaCadastro", resp);
            request.getRequestDispatcher("login.jsp").forward(request, response);
            
            return;
        }
        
        usuario.setNome(request.getParameter("nome"));
        // LucasFreitag 2024
        //usuario.setSenha(request.getParameter("senha"));
        usuario.setSenha(request.getParameter("senha"),true);
        usuario.setPermissao("col");
        usuario.setEmail(request.getParameter("email")); // LucasFreitag 2024
        usuario.setAnonimizado("N");

        // RACE-01: as checagens acima (lista/listaE) tem uma janela de corrida -- 2
        // cadastros simultaneos com o mesmo login/email podem passar os 2 pela checagem.
        // A trava de verdade agora e a UNIQUE do banco (Usuario.usuario/email); se a
        // corrida acontecer, o PERDEDOR cai aqui em vez de criar uma 2a conta travada.
        // Mesma resposta amigavel de "ja existe" que a checagem em Java ja mostrava.
        try {
            usuarioDAO.salvar(usuario);
        } catch (ConstraintViolationException ex) {
            request.setAttribute("respostaCadastro", true);
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }
        request.getRequestDispatcher("LogInServlet").forward(request, response);
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
        // SEC-18: POST-only. GET nao cadastra (evita CSRF via GET).
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
