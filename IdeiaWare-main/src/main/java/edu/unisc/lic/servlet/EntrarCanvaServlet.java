package edu.unisc.lic.servlet;

import edu.unisc.lic.dao.CanvaDAO;
import edu.unisc.lic.dao.IdeiaDAO;
import edu.unisc.lic.dao.IdeiaUsuarioDAO;
import edu.unisc.lic.dao.UsuarioDAO;
import edu.unisc.lic.domain.Canva;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.IdeiaUsuario;
import edu.unisc.lic.domain.Usuario;

import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class EntrarCanvaServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        
        IdeiaDAO ideiaDAO = new IdeiaDAO();

        // CAN-09: ideiaId do parametro ou da sessao; se nenhum existir, redireciona (evita 500).
        Long ideiaId = null;
        String paramIdeia = request.getParameter("ideiaId");
        if (paramIdeia != null) {
            try {
                ideiaId = Long.parseLong(paramIdeia);
            } catch (NumberFormatException e) {
                ideiaId = null;
            }
        } else {
            ideiaId = (Long) request.getSession().getAttribute("ideiaId");
        }
        if (ideiaId == null) {
            response.sendRedirect(request.getContextPath() + File.separator + "lista-canvas.jsp");
            return;
        }

        Ideia ideia = ideiaDAO.buscar(ideiaId);
        if (ideia == null) {
            response.sendRedirect(request.getContextPath() + File.separator + "lista-canvas.jsp");
            return;
        }

        // TEST-04: faltava checagem de login (codigoUsuario nulo dava NPE no unboxing).
        Object codigoUsuarioObj = request.getSession().getAttribute("codigoUsuario");
        if (codigoUsuarioObj == null) {
            response.sendRedirect(request.getContextPath() + File.separator + "login.jsp");
            return;
        }

        UsuarioDAO uDAO = new UsuarioDAO();
        Usuario u = uDAO.buscar((Long) codigoUsuarioObj);
        
        IdeiaUsuarioDAO iuDAO = new IdeiaUsuarioDAO();
        IdeiaUsuario iu = new IdeiaUsuario();
        iu.setIdeia(ideia);
        iu.setUsuario(u);
        
        List<IdeiaUsuario> lista = iuDAO.listarParametro(iu);
        if (lista.size() > 0) {
        	iu = lista.get(0);
        } else {
        	iu = null;
        }

        // CAN-ACESSO-V2: exige participacao (antes so calculava flLider, nunca barrava acesso).
        if (iu == null) {
        	response.sendRedirect(request.getContextPath() + File.separator + "lista-canvas.jsp");
        	return;
        }

        HttpSession session = request.getSession(true);

        String retencao = request.getParameter("retencao");
        if (retencao == null || retencao.isEmpty()) {
            session.setAttribute("isRetencao", false);
        } else {
            session.setAttribute("isRetencao", retencao);
        }

        session.setAttribute("ideiaId", ideia.getCodigo());
        session.setAttribute("ideiaTitulo", ideia.getTitulo());
        session.setAttribute("ideiaDesc", ideia.getDescricao());
        session.setAttribute("lider", iu != null ? iu.getFlLider() : "N");
        
        CanvaDAO canvaDAO = new CanvaDAO();
        Canva canva = new Canva();
        canva.setIdeia(ideia);
        
        List<Canva> receita = canvaDAO.listarCanvaElement(canva, "receita");
        List<Canva> atividade = canvaDAO.listarCanvaElement(canva, "atividade");
        List<Canva> canal = canvaDAO.listarCanvaElement(canva, "canal");
        List<Canva> parceria = canvaDAO.listarCanvaElement(canva, "parceria");
        List<Canva> proposta = canvaDAO.listarCanvaElement(canva, "proposta");
        List<Canva> segmento = canvaDAO.listarCanvaElement(canva, "segmento");
        List<Canva> recurso = canvaDAO.listarCanvaElement(canva, "recurso");
        List<Canva> relacionamento = canvaDAO.listarCanvaElement(canva, "relacionamento");
        List<Canva> custo = canvaDAO.listarCanvaElement(canva, "custo");
        
        session.setAttribute("receita", receita);
        session.setAttribute("atividade", atividade);
        session.setAttribute("canal", canal);
        session.setAttribute("parceria", parceria);
        session.setAttribute("proposta", proposta);
        session.setAttribute("segmento", segmento);
        session.setAttribute("recurso", recurso);
        session.setAttribute("relacionamento", relacionamento);
        session.setAttribute("custo", custo);

        response.sendRedirect(request.getContextPath() + File.separator + "canva-mapa.jsp");

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
