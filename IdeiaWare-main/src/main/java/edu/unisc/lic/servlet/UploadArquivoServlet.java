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

public class UploadArquivoServlet extends HttpServlet {

    // STR-06: limites de upload de imagem/audio do storytelling
    private static final int MAX_MEMORY_SIZE = 1024 * 1024 * 16;
    private static final int MAX_REQUEST_SIZE = 1024 * 1024 * 10;

    // SEC-12: allowlist de extensoes de imagem
    private static final java.util.Set<String> EXTENSOES_IMAGEM =
            new java.util.HashSet<>(java.util.Arrays.asList("png", "jpg", "jpeg", "gif", "webp", "bmp"));

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

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
                    // SEC-12: so aceita extensao de imagem
                    String original = new File(item.getName()).getName();
                    String ext = "";
                    int ponto = original.lastIndexOf('.');
                    if (ponto >= 0) {
                        ext = original.substring(ponto + 1).toLowerCase();
                    }
                    if (!EXTENSOES_IMAGEM.contains(ext)) {
                        continue;
                    }

                    // SEC-12: nome aleatorio, nao confia no nome enviado
                    fileName = System.currentTimeMillis() + "_"
                            + java.util.UUID.randomUUID().toString().replace("-", "") + "." + ext;
                    File uploadedFile = new File(uploadFolder + File.separator + fileName);
                    item.write(uploadedFile);

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
