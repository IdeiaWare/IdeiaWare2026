package edu.unisc.lic.servlet;

import edu.unisc.lic.dao.ColaboracaoIdeiaDAO;
import edu.unisc.lic.domain.ColaboracaoIdeia;
import edu.unisc.lic.util.JsonUtil;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * M.10 (2026-07-06): edicao de uma colaboracao ja enviada. Regras decididas com o usuario:
 *  - SO o AUTOR da colaboracao pode editar (nao o lider, nao outro participante);
 *  - SO enquanto a colaboracao ainda NAO foi adicionada a descricao oficial da ideia
 *    (flSalvado != "ad"). Depois de mesclada, o texto ja foi pra descricao (LogColaboracao)
 *    e editar aqui criaria inconsistencia -- por isso e bloqueado (na UI o botao some, e
 *    aqui no servidor tambem, pra nao depender so do front).
 * Ambas as checagens sao no SERVIDOR (nao so escondendo o botao na UI).
 */
public class EditarColaboracaoServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        // AUTORIZACAO: exige login.
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

        // AUTORIZACAO: so o AUTOR edita.
        Long codigoUsuario = (Long) codigoUsuarioObj;
        if (colaboracaoIdeia.getUsuario() == null
                || !codigoUsuario.equals(colaboracaoIdeia.getUsuario().getCodigo())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Apenas o autor pode editar a própria colaboração.");
            return;
        }

        // REGRA: nao edita depois de adicionada a descricao (flSalvado == "ad").
        if ("ad".equals(colaboracaoIdeia.getFlSalvado())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Esta colaboração já foi adicionada à descrição e não pode mais ser editada.");
            return;
        }

        String novoTexto = request.getParameter("descricao");
        if (novoTexto == null || novoTexto.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("O texto da colaboração não pode ser vazio.");
            return;
        }
        novoTexto = novoTexto.trim();
        // A coluna descricaoIdeiaAtual e varchar(1500) -- limita p/ caber sempre.
        if (novoTexto.length() > 1500) {
            novoTexto = novoTexto.substring(0, 1500);
        }

        // Guarda o texto anterior (a coluna descricaoIdeiaAnterior ja existe no dominio,
        // sem uso ate agora) como historico simples da edicao, e marca dtModificacao.
        colaboracaoIdeia.setDescricaoIdeiaAnterior(colaboracaoIdeia.getDescricaoIdeiaAtual());
        colaboracaoIdeia.setDescricaoIdeiaAtual(novoTexto);
        colaboracaoIdeia.setDtModificacao();
        colaboracaoIdeiaDAO.editar(colaboracaoIdeia);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        // REVISAO 2026-07-07: JsonUtil.GSON_SEM_SENHA (nao new Gson()) -- colaboracaoIdeia
        // carrega .usuario, que tem o hash bcrypt da senha; Gson padrao serializa TODOS os
        // campos por reflection e vazaria o hash pra qualquer participante do grupo.
        response.getWriter().write(JsonUtil.GSON_SEM_SENHA.toJson(colaboracaoIdeia));
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
        return "Edita uma colaboracao (M.10) -- so autor, so antes de adicionada a descricao.";
    }
}
