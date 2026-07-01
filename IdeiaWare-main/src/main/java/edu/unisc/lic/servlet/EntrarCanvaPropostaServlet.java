package edu.unisc.lic.servlet;

/** MNT-01: subclasse fina de EntrarCanvaBaseServlet (logica comum la). */
public class EntrarCanvaPropostaServlet extends EntrarCanvaBaseServlet {

    @Override
    protected String getTipoCanva() {
        return "proposta";
    }

    @Override
    protected String getPaginaDestino() {
        return "canva-proposta-de-valor.jsp";
    }
}
