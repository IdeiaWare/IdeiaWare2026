package edu.unisc.lic.servlet;

import edu.unisc.lic.classes.Data;
import edu.unisc.lic.dao.ColaboracaoIdeiaDAO;
import edu.unisc.lic.dao.IdeiaUsuarioDAO;
import edu.unisc.lic.dao.LogColaboracaoDAO;
import edu.unisc.lic.domain.ColaboracaoIdeia;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.IdeiaUsuario;
import edu.unisc.lic.domain.LogColaboracao;
import edu.unisc.lic.domain.Usuario;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AddDescricaoServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        ColaboracaoIdeiaDAO colaboracaoIdeiaDAO = new ColaboracaoIdeiaDAO();
        // RET-14: protege o parse do parametro (evita 500 com valor invalido/nulo).
        ColaboracaoIdeia colaboracaoIdeia = null;
        try {
            colaboracaoIdeia = colaboracaoIdeiaDAO.buscar(
                    Long.parseLong(request.getParameter("colaboracao")));
        } catch (NumberFormatException e) {
            colaboracaoIdeia = null;
        }

        if (colaboracaoIdeia == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Colaboração não encontrada.");
            return;
        }

        colaboracaoIdeia.setFlSalvado("ad");
        colaboracaoIdeia.setDtModificacao();
        colaboracaoIdeiaDAO.editar(colaboracaoIdeia);

        Ideia ideia = colaboracaoIdeia.getIdeia();

        List<IdeiaUsuario> liderList = new IdeiaUsuarioDAO()
                .listarParametro(new IdeiaUsuario(new Usuario(), ideia, "S"));

        if (liderList == null || liderList.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Líder da ideia não encontrado.");
            return;
        }

        LogColaboracaoDAO logColaboracaoDAO = new LogColaboracaoDAO();
        LogColaboracao descricaoAtual = logColaboracaoDAO
                .buscarDescricaoFinal(new LogColaboracao(ideia, new Usuario(), null, null));

        String descricaoBase = "";
        if (descricaoAtual != null && descricaoAtual.getDescricao() != null) {
            descricaoBase = descricaoAtual.getDescricao().trim();
        } else if (ideia.getDescricao() != null) {
            descricaoBase = ideia.getDescricao().trim();
        }

        String novaDescricao = descricaoBase
                + (descricaoBase.isEmpty() ? "" : " ")
                + colaboracaoIdeia.getDescricaoIdeiaAtual().trim();

        // SEC-20: a coluna descricao (LogColaboracao/Ideia) e varchar(1500). Cada
        // "adicionar a descricao" concatenava sem limite -> em colaboracao longa
        // estourava a coluna (trunca silencioso no MySQL nao-strict / erro no strict).
        // Limita em 1500 p/ caber sempre.
        if (novaDescricao.length() > 1500) {
            novaDescricao = novaDescricao.substring(0, 1500);
        }

        LogColaboracao logColaboracao = new LogColaboracao(
                ideia,
                liderList.get(0).getUsuario(),
                Data.horaAtual(),
                novaDescricao);

        logColaboracaoDAO.salvar(logColaboracao);

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(novaDescricao);
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
