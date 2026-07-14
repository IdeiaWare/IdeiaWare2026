package edu.unisc.lic.servlet;
import edu.unisc.lic.classes.ArquivoExport;
import edu.unisc.lic.classes.Constantes;
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

public class ExportCanvaServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // CAN-10/CANM-06: le o body inteiro ANTES de validar (senao, ERR_CONNECTION_RESET).
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

            // PDF-DISCO
            String caminhoRelativo = Constantes.CAMINHO_EXPORT_CANVA + ideia.getCodigo() + ".pdf";
            ArquivoExport.salvar(fileData, caminhoRelativo);

            CanvaexportDAO canvaDAO = new CanvaexportDAO();
            Canvaexport canva = new Canvaexport();
            canva.setIdeia(ideia);

            // CANM-07: consulta uma única vez em vez de duas.
            List<Canvaexport> existentes = canvaDAO.listarParametro(canva);
            if (existentes != null && !existentes.isEmpty()) {
                canva = existentes.get(0);
                canva.setFile(caminhoRelativo);
                canva.setDate();
                canvaDAO.editar(canva);
            } else {
                canva.setFile(caminhoRelativo);
                canva.setDate();
                try {
                    canvaDAO.salvar(canva);
                } catch (ConstraintViolationException ex) {
                    // K.8 #4: outro export quase-simultaneo ja inseriu a linha -- trata como update.
                    List<Canvaexport> agora = canvaDAO.listarParametro(canva);
                    if (agora != null && !agora.isEmpty()) {
                        Canvaexport existente = agora.get(0);
                        existente.setFile(caminhoRelativo);
                        existente.setDate();
                        canvaDAO.editar(existente);
                    }
                }
            }
            ideia.setStatus(StatusIdeia.FINALIZADO);
            new IdeiaDAO().editar(ideia);
        } catch (RuntimeException | IOException e) {
            // CAN-10: falha ao salvar (ex.: base64 invalido, disco cheio) retorna 500 explicito.
            System.out.println("Erro ao exportar canva: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
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
