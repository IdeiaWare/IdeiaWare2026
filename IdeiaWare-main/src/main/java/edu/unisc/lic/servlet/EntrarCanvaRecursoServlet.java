package edu.unisc.lic.servlet;

/** MNT-01: subclasse fina de EntrarCanvaBaseServlet (logica comum la). */
public class EntrarCanvaRecursoServlet extends EntrarCanvaBaseServlet {

    @Override
    protected String getTipoCanva() {
        return "recurso";
    }

    @Override
    protected String getPaginaDestino() {
        return "canva-recursos-principais.jsp";
    }
}
