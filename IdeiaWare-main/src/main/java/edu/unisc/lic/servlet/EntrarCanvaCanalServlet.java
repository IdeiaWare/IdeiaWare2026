package edu.unisc.lic.servlet;

/** MNT-01: subclasse fina de EntrarCanvaBaseServlet (logica comum la). */
public class EntrarCanvaCanalServlet extends EntrarCanvaBaseServlet {

    @Override
    protected String getTipoCanva() {
        return "canal";
    }

    @Override
    protected String getPaginaDestino() {
        return "canva-canais.jsp";
    }
}
