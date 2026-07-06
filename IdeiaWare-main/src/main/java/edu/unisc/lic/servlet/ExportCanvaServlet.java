/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unisc.lic.servlet;
import edu.unisc.lic.classes.StatusIdeia;

import edu.unisc.lic.dao.IdeiaDAO;
import edu.unisc.lic.dao.CanvaexportDAO;
import edu.unisc.lic.domain.Canvaexport;
import edu.unisc.lic.domain.Ideia;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import org.hibernate.exception.ConstraintViolationException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author yanrodrigues
 */
public class ExportCanvaServlet extends HttpServlet {

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

        // CAN-10: le o corpo (PDF base64) ANTES de qualquer validacao/retorno. Se o
        // servlet retornasse sem consumir o body grande, o Tomcat resetava a conexao
        // (ERR_CONNECTION_RESET) ao tentar descartar mais que maxSwallowSize (~2MB).
        // CANM-06: o base64 pode ter mais de uma linha, entao acumula tudo.
        BufferedReader reader = request.getReader();
        StringBuilder sb = new StringBuilder();
        String linha;
        while ((linha = reader.readLine()) != null) {
            sb.append(linha);
        }
        String fileData = sb.toString();

        // CANM-03: so prossegue com sessao valida e ideiaId presente.
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("ideiaId") == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        if (fileData.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            Ideia ideia = new IdeiaDAO().buscar((Long) session.getAttribute("ideiaId"));
            if (ideia == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            CanvaexportDAO canvaDAO = new CanvaexportDAO();
            Canvaexport canva = new Canvaexport();
            canva.setIdeia(ideia);

            // CANM-07: consulta uma única vez em vez de duas.
            List<Canvaexport> existentes = canvaDAO.listarParametro(canva);
            if (existentes != null && !existentes.isEmpty()) {
                canva = existentes.get(0);
                canva.setFile(fileData);
                canva.setDate();
                canvaDAO.editar(canva);
            } else {
                canva.setFile(fileData);
                canva.setDate();
                try {
                    canvaDAO.salvar(canva);
                } catch (ConstraintViolationException ex) {
                    // K.8 #4: outro export quase-simultaneo ja inseriu a linha entre a
                    // checagem "existentes" acima e este insert -- a UNIQUE do banco
                    // (uk_canvaexport_ideia) barra o 2o insert. Trata como update em cima
                    // da linha que a corrida acabou de criar.
                    List<Canvaexport> agora = canvaDAO.listarParametro(canva);
                    if (agora != null && !agora.isEmpty()) {
                        Canvaexport existente = agora.get(0);
                        existente.setFile(fileData);
                        existente.setDate();
                        canvaDAO.editar(existente);
                    }
                }
            }
            ideia.setStatus(StatusIdeia.FINALIZADO);
            new IdeiaDAO().editar(ideia);
        } catch (RuntimeException e) {
            // CAN-10: falha ao salvar (ex.: PDF maior que o max_allowed_packet do
            // MySQL) retorna um 500 explicito em vez de deixar a conexao resetar.
            System.out.println("Erro ao exportar canva: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
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
        processRequest(request, response);
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
