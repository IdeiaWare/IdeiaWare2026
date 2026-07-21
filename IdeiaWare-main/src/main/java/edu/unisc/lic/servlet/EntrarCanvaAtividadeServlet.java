package edu.unisc.lic.servlet;

// MNT-01: subclasse fina de EntrarCanvaBaseServlet
public class EntrarCanvaAtividadeServlet extends EntrarCanvaBaseServlet {

    @Override
    protected String getTipoCanva() {
        return "atividade";
    }

    @Override
    protected String getPaginaDestino() {
        return "canva-atividades-principais.jsp";
    }
}
