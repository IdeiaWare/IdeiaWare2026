package edu.unisc.lic.servlet;
import edu.unisc.lic.classes.StatusIdeia;

import edu.unisc.lic.classes.Data;
import edu.unisc.lic.dao.IdeiaDAO;
import edu.unisc.lic.dao.IdeiaUsuarioDAO;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.IdeiaUsuario;
import edu.unisc.lic.domain.LogColaboracao;
import edu.unisc.lic.domain.Usuario;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "FecharGrupoServlet", urlPatterns = {"/FecharGrupoServlet"})
public class FecharGrupoServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // AUTORIZACAO: exige login.
        HttpSession session = request.getSession(false);
        Object codigoUsuario = session == null ? null : session.getAttribute("codigoUsuario");
        if (codigoUsuario == null) {
            response.sendRedirect(request.getContextPath() + File.separator + "login.jsp");
            return;
        }

        // RET-14: valida parametro/ideia antes de usar (evita 500/NPE).
        String codigoParam = request.getParameter("codigo");
        Ideia ideia = null;
        if (codigoParam != null) {
            try {
                ideia = new IdeiaDAO().buscar(Long.parseLong(codigoParam));
            } catch (NumberFormatException e) {
                ideia = null;
            }
        }
        if (ideia == null) {
            response.sendRedirect(request.getContextPath() + File.separator + "minha-ideia.jsp");
            return;
        }

        // AUTORIZACAO: so o LIDER da ideia pode fechar o grupo / transferir a lideranca.
        // Antes, qualquer um (ate sem login) regredia o status com um POST do codigo.
        Usuario sessionUser = new Usuario();
        sessionUser.setCodigo((Long) codigoUsuario);
        List<IdeiaUsuario> souLider = new IdeiaUsuarioDAO()
                .listarParametro(new IdeiaUsuario(sessionUser, ideia, "S"));
        if (souLider == null || souLider.isEmpty()) {
            response.sendRedirect(request.getContextPath() + File.separator + "minha-ideia.jsp");
            return;
        }

        ideia.setStatusGrupo(StatusIdeia.GRUPO_FECHADO);
        ideia.setStatus(StatusIdeia.EM_DESENVOLVIMENTO);
        ideia.setDtInicioDesenv();

        IdeiaUsuarioDAO ideiaUsuarioDAO = new IdeiaUsuarioDAO();
        List<IdeiaUsuario> list = ideiaUsuarioDAO.listarParametro(new IdeiaUsuario(null, ideia, "S"));

        if (list == null || list.isEmpty()) {
            response.sendRedirect(request.getContextPath() + File.separator + "minha-ideia.jsp");
            return;
        }

        // K.8 #1: as escritas (ideia, vinculos de lideranca, log) sao decididas aqui em
        // Java a partir de leituras, mas so sao PERSISTIDAS no final, todas juntas, via
        // fecharGrupoAtomico -- antes cada editar()/salvar() abria sua propria transacao.
        List<IdeiaUsuario> vinculosParaAtualizar = new ArrayList<>();
        Usuario liderFinal = list.get(0).getUsuario();

        // M.2 (2026-07-06): todos os vinculos da ideia. Ao fechar, os PENDENTES/REJEITADOS
        // (quem nao foi aprovado) sao REMOVIDOS -- sobram so os aprovados, e as checagens de
        // participacao ja existentes (Canva/Caixa/Storytelling/etc.) seguem valendo sem
        // filtro de status. So um membro aprovado pode virar lider.
        List<IdeiaUsuario> todos = ideiaUsuarioDAO.listarParametro(new IdeiaUsuario(null, ideia, null));
        List<IdeiaUsuario> vinculosParaRemover = new ArrayList<>();
        if (todos != null) {
            for (IdeiaUsuario iU : todos) {
                String st = iU.getFlStatusVinculo();
                if (StatusIdeia.VINCULO_PENDENTE.equals(st) || StatusIdeia.VINCULO_REJEITADO.equals(st)) {
                    vinculosParaRemover.add(iU);
                }
            }
        }

        String radioParam = request.getParameter("radio");
        if (radioParam != null && !radioParam.isEmpty()) {
            long radioId;
            try {
                radioId = Long.parseLong(radioParam);
            } catch (NumberFormatException ex) {
                response.sendRedirect(request.getContextPath() + File.separator + "minha-ideia.jsp");
                return;
            }
            if (list.get(0).getUsuario().getCodigo() != radioId && todos != null) {
                // M.2 (2026-07-06): acha o novo lider entre os APROVADOS ANTES de mexer em
                // nada. So promove um membro aprovado (o radio na tela ja so mostra
                // aprovados; reforca aqui contra POST forjado com id de pendente/rejeitado).
                // Antes o codigo rebaixava o lider antigo PRIMEIRO e so depois procurava o
                // novo -- se o radio apontasse pra um invalido, a ideia ficava SEM LIDER.
                IdeiaUsuario novoLider = null;
                for (IdeiaUsuario iU : todos) {
                    String st = iU.getFlStatusVinculo();
                    boolean aprovado = !StatusIdeia.VINCULO_PENDENTE.equals(st)
                            && !StatusIdeia.VINCULO_REJEITADO.equals(st);
                    if (iU.getUsuario().getCodigo() == radioId && aprovado) {
                        novoLider = iU;
                        break;
                    }
                }
                if (novoLider != null) {
                    IdeiaUsuario liderAntigo = list.get(0);
                    liderAntigo.setFlLider("N");
                    vinculosParaAtualizar.add(liderAntigo);
                    novoLider.setFlLider("S");
                    vinculosParaAtualizar.add(novoLider);
                    liderFinal = novoLider.getUsuario();
                }
            }
        }

        String descricaoInicial = ideia.getDescricao() != null ? ideia.getDescricao() : "";
        LogColaboracao logColaboracao = new LogColaboracao(
                ideia,
                liderFinal,
                Data.horaAtual(),
                descricaoInicial);

        ideiaUsuarioDAO.fecharGrupoAtomico(ideia, vinculosParaAtualizar, vinculosParaRemover, logColaboracao);

        response.sendRedirect(request.getContextPath() + File.separator + "minha-ideia.jsp");
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
