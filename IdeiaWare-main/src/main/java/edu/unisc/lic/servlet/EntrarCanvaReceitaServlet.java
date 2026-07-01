package edu.unisc.lic.servlet;

/** MNT-01: subclasse fina de EntrarCanvaBaseServlet (logica comum la). */
public class EntrarCanvaReceitaServlet extends EntrarCanvaBaseServlet {

    @Override
    protected String getTipoCanva() {
        return "receita";
    }

    @Override
    protected String getPaginaDestino() {
        return "canva-receita.jsp";
    }
}
