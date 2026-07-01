package edu.unisc.lic.servlet;

/** MNT-01: subclasse fina de EntrarCanvaBaseServlet (logica comum la). */
public class EntrarCanvaSegmentoServlet extends EntrarCanvaBaseServlet {

    @Override
    protected String getTipoCanva() {
        return "segmento";
    }

    @Override
    protected String getPaginaDestino() {
        return "canva-segmento-de-clientes.jsp";
    }
}
