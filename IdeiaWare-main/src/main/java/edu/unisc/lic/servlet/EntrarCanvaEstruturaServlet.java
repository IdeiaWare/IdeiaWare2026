package edu.unisc.lic.servlet;

/** MNT-01: subclasse fina de EntrarCanvaBaseServlet (logica comum la). */
public class EntrarCanvaEstruturaServlet extends EntrarCanvaBaseServlet {

    @Override
    protected String getTipoCanva() {
        return "custo";
    }

    @Override
    protected String getPaginaDestino() {
        return "canva-estrutura-de-custos.jsp";
    }
}
