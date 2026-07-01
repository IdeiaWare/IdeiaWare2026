package edu.unisc.lic.servlet;

/** MNT-01: subclasse fina de EntrarCanvaBaseServlet (logica comum la). */
public class EntrarCanvaParceriaServlet extends EntrarCanvaBaseServlet {

    @Override
    protected String getTipoCanva() {
        return "parceria";
    }

    @Override
    protected String getPaginaDestino() {
        return "canva-parcerias-principais.jsp";
    }
}
