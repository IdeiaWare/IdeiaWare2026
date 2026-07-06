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
import javax.servlet.http.HttpSession;

public class AddDescricaoServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        // AUTORIZACAO: exige login. Antes o servlet nao checava sessao nenhuma.
        HttpSession session = request.getSession(false);
        Object codigoUsuarioObj = session == null ? null : session.getAttribute("codigoUsuario");
        if (codigoUsuarioObj == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

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

        Ideia ideiaDaColab = colaboracaoIdeia.getIdeia();

        // AUTORIZACAO: so o LIDER da ideia pode "adicionar a descricao" -- essa
        // restricao so existia na UI (colaboracao.jsp escondia o botao pra quem nao
        // era lider); o servlet aceitava de qualquer usuario logado, mesmo sem
        // nenhum vinculo com a ideia.
        Usuario sessionUser = new Usuario();
        sessionUser.setCodigo((Long) codigoUsuarioObj);
        List<IdeiaUsuario> souLider = new IdeiaUsuarioDAO()
                .listarParametro(new IdeiaUsuario(sessionUser, ideiaDaColab, "S"));
        if (souLider == null || souLider.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Apenas o líder pode adicionar à descrição.");
            return;
        }

        Ideia ideia = colaboracaoIdeia.getIdeia();
        LogColaboracaoDAO logColaboracaoDAO = new LogColaboracaoDAO();

        // K.8 #8: "ad" so e setado por ESTE servlet e nunca e lido em nenhum outro lugar
        // do sistema -- serve como marcador de idempotencia. Sem esta checagem, um
        // duplo-POST (duplo-clique, retry de rede) na MESMA colaboracao reaplicava o
        // texto 2x na descricao oficial da ideia. Se ja processada, devolve a descricao
        // atual (calculada na 1a chamada) em vez de acrescentar de novo.
        if ("ad".equals(colaboracaoIdeia.getFlSalvado())) {
            LogColaboracao jaProcessada = logColaboracaoDAO
                    .buscarDescricaoFinal(new LogColaboracao(ideia, new Usuario(), null, null));
            response.setContentType("text/plain");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(jaProcessada != null && jaProcessada.getDescricao() != null
                    ? jaProcessada.getDescricao() : "");
            return;
        }

        colaboracaoIdeia.setFlSalvado("ad");
        colaboracaoIdeia.setDtModificacao();
        colaboracaoIdeiaDAO.editar(colaboracaoIdeia);

        List<IdeiaUsuario> liderList = new IdeiaUsuarioDAO()
                .listarParametro(new IdeiaUsuario(new Usuario(), ideia, "S"));

        if (liderList == null || liderList.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Líder da ideia não encontrado.");
            return;
        }

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
