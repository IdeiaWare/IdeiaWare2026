package edu.unisc.lic.servlet;

/** MNT-01: subclasse fina de EntrarCanvaBaseServlet (logica comum la). */
public class EntrarCanvaRelacionamentoServlet extends EntrarCanvaBaseServlet {

    @Override
    protected String getTipoCanva() {
        return "relacionamento";
    }

    @Override
    protected String getPaginaDestino() {
        return "canva-relacionamento-com-clientes.jsp";
    }
}
