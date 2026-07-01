/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unisc.lic.servlet;

import edu.unisc.lic.classes.Constantes;
import edu.unisc.lic.dao.ElementosStorytellingDAO;
import edu.unisc.lic.dao.StorytellingDAO;
import edu.unisc.lic.domain.ElementosStorytelling;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.Storytelling;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 *
 * @author Vinicius Santiago
 */
public class UploadArquivoServlet extends HttpServlet {

    // STR-06: limites de upload de imagem/áudio do storytelling.
    private static final int MAX_MEMORY_SIZE = 1024 * 1024 * 16;
    private static final int MAX_REQUEST_SIZE = 1024 * 1024 * 10; // 10 MB

    // SEGURANCA (upload): allowlist de extensoes de imagem. Bloqueia .jsp/.html/etc.
    // que, gravados na pasta do webapp servida pelo Tomcat, seriam EXECUTADOS (RCE).
    private static final java.util.Set<String> EXTENSOES_IMAGEM =
            new java.util.HashSet<>(java.util.Arrays.asList("png", "jpg", "jpeg", "gif", "webp", "bmp"));

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

        /**
         * Pessoa que estiver lendo esse código e se perguntando o que se passa
         * nele, aqui vai a minha resposta: eu não faço a menor fucking ideia.
         * Simplesmente copiei do post
         * https://stackoverflow.com/questions/19510656/how-to-upload-files-on-server-folder-using-jsp
         */
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        // Exige um storytelling ativo na sessao (sem isso, buscar((long) null) dava NPE/500).
        Object storyId = session == null ? null : session.getAttribute("storytellingId");
        if (storyId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        Storytelling st = new StorytellingDAO().buscar((long) storyId);
        if (st == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        criaDiretorios(st.getIdeia());

        boolean isMultipart = ServletFileUpload.isMultipartContent(request);

        if (!isMultipart) {
            return;
        }

        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold(MAX_MEMORY_SIZE);

        String uploadFolder = getServletContext().getRealPath("") + File.separator + Constantes.CAMINHO_IMAGENS_STORYTELLING + st.getIdeia().getCodigo();

        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setSizeMax(MAX_REQUEST_SIZE);

        int largura = 0;
        int altura = 0;
        
        try {

            List items = upload.parseRequest(request);
            Iterator iter = items.iterator();
            String fileName = null;
            while (iter.hasNext()) {
                FileItem item = (FileItem) iter.next();

                if (!item.isFormField()) {
                    // So aceita extensoes de IMAGEM (allowlist) -> impede gravar .jsp/.html
                    // executavel na pasta servida pelo Tomcat (RCE).
                    String original = new File(item.getName()).getName();
                    String ext = "";
                    int ponto = original.lastIndexOf('.');
                    if (ponto >= 0) {
                        ext = original.substring(ponto + 1).toLowerCase();
                    }
                    if (!EXTENSOES_IMAGEM.contains(ext)) {
                        continue; // ignora qualquer coisa que nao seja imagem
                    }

                    // Nome aleatorio: nao confia no nome enviado (evita sobrescrita e
                    // caracteres perigosos). A extensao ja foi validada acima.
                    fileName = System.currentTimeMillis() + "_"
                            + java.util.UUID.randomUUID().toString().replace("-", "") + "." + ext;
                    File uploadedFile = new File(uploadFolder + File.separator + fileName);
                    item.write(uploadedFile);

                    // Confirma que e MESMO uma imagem decodificavel; senao apaga e ignora.
                    BufferedImage bimg = ImageIO.read(uploadedFile);
                    if (bimg == null) {
                        uploadedFile.delete();
                        fileName = null;
                        continue;
                    }
                    largura = bimg.getWidth();
                    altura = bimg.getHeight();
                }

            }

            // Nenhuma imagem valida -> volta sem gravar lixo no banco.
            if (fileName == null) {
                response.sendRedirect(request.getContextPath() + File.separator + "storytelling.jsp");
                return;
            }

            ElementosStorytelling est = new ElementosStorytelling(st, "IMG", Constantes.CAMINHO_IMAGENS_STORYTELLING + st.getIdeia().getCodigo() + File.separator + fileName, 0, 0, altura, largura);
            new ElementosStorytellingDAO().salvar(est);

            response.sendRedirect(request.getContextPath() + File.separator + "storytelling.jsp");

        } catch (FileUploadException ex) {
            throw new ServletException(ex);
        } catch (Exception ex) {
            throw new ServletException(ex);
        }

    }

    private void criaDiretorios(Ideia i) {
        File diretorio = new File(getServletContext().getRealPath("") + File.separator + Constantes.CAMINHO_IMAGENS_STORYTELLING + i.getCodigo().toString());
        diretorio.mkdirs();
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
