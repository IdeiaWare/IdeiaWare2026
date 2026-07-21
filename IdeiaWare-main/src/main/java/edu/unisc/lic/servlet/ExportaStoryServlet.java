package edu.unisc.lic.servlet;
import edu.unisc.lic.classes.ArquivoExport;
import edu.unisc.lic.classes.Constantes;
import edu.unisc.lic.classes.StatusIdeia;

import edu.unisc.lic.dao.IdeiaDAO;
import edu.unisc.lic.dao.StorytellingDAO;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.Storytelling;
import java.io.BufferedReader;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ExportaStoryServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        // ENCODING-01: sem isso o write de erro sai com acentuacao quebrada (charset default do container).
        response.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(true);

        BufferedReader reader = request.getReader();
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        String fileData = sb.toString();

        if (fileData == null || fileData.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // BLINDA-01: sessao sem storytellingId ou id invalido
        Object storyIdAttr = session.getAttribute("storytellingId");
        if (storyIdAttr == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        StorytellingDAO storytellingDAO = new StorytellingDAO();
        Storytelling storytelling;
        try {
            storytelling = storytellingDAO.buscar(Long.parseLong(storyIdAttr.toString()));
        } catch (NumberFormatException ex) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if (storytelling == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // UX-STORY-EXPORT-DUPLO: bloqueia re-finalizacao -- sem isso, uma 2a aba (outro
        // participante) conseguia exportar de novo e SOBRESCREVIA o PDF da 1a exportacao no
        // mesmo caminho deterministico (Constantes.CAMINHO_EXPORT_STORYTELLING + ideiaCodigo),
        // perdendo silenciosamente a versao original na Retenção do Conhecimento.
        if (StatusIdeia.FINALIZADO.equals(storytelling.getStatus())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Este Storytelling já foi finalizado.");
            return;
        }

        try {
            // PDF-DISCO
            String caminhoRelativo = Constantes.CAMINHO_EXPORT_STORYTELLING + storytelling.getIdeia().getCodigo() + ".pdf";
            ArquivoExport.salvar(fileData, caminhoRelativo);

            storytelling.setCaminhoFinalizado(caminhoRelativo);
            storytelling.setDtFinalizacao();
            storytelling.setStatus(StatusIdeia.FINALIZADO);
            storytellingDAO.editar(storytelling);

            Ideia ideia = storytelling.getIdeia();
            ideia.setStatus(StatusIdeia.CAIXA_FERRAMENTAS);
            new IdeiaDAO().editar(ideia);
        } catch (RuntimeException | IOException e) {
            // CAN-10: falha ao salvar retorna 500 explicito
            System.out.println("Erro ao exportar storytelling: " + e.getMessage());
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
