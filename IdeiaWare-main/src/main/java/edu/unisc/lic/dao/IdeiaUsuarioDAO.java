package edu.unisc.lic.dao;
import edu.unisc.lic.classes.StatusIdeia;

import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.IdeiaUsuario;
import edu.unisc.lic.domain.LogColaboracao;
import edu.unisc.lic.util.HibernateUtil;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author viniciussdsilva
 */
public class IdeiaUsuarioDAO extends GenericDAO<IdeiaUsuario> {

    /**
     * Esse método busca e retorna
     *
     * @param iu - IdeiaUsuario
     * @return
     */
    public List<IdeiaUsuario> listarParametro(IdeiaUsuario iu) {
        Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();

        try {
            Criteria filtro = sessao.createCriteria(IdeiaUsuario.class);

            if (iu.getUsuario().getCodigo() != null) {
                filtro.add(Restrictions.eq("usuario", iu.getUsuario()));
            }
            if (iu.getIdeia().getCodigo() != null) {
                filtro.add(Restrictions.eq("ideia", iu.getIdeia()));
            }
            if (iu.getFlLider() != null) {
                filtro.add(Restrictions.eq("flLider", iu.getFlLider()));
            }

            // Ordena a listagem das "Minhas Ideias" do mais NOVO para o mais antigo
            // (ideia recem-cadastrada aparece em cima). So afeta este caso (filtro por
            // usuario, varias ideias); os callers que filtram por uma unica ideia
            // (membros de um grupo) nao mudam, pois todos tem o mesmo valor de "ideia".
            filtro.addOrder(Order.desc("ideia"));

            return filtro.list();

        } finally {
            sessao.close();
        }
    }

    /**
     * Retorna todas as ideias em status ST para o usuário — líderes e participantes.
     * Corrige STR-09: lista-storytelling.jsp só mostrava para líderes.
     */
    // PERF-01: as 3 listagens abaixo (Storytelling/Caixa/Canva) carregavam TODAS as
    // ideias do usuario e filtravam por status DEPOIS, em Java (loop + lista auxiliar).
    // O filtro de status agora vai pra dentro da propria query via createAlias("ideia",
    // "i") -- uma unica consulta ao banco, sem trazer linhas que vao ser descartadas.
    // Mesmo padrao ja usado em IdeiaDAO.listarIdeiasDisponiveis (HQL com filtro no banco).

    public List<IdeiaUsuario> listarTodasIdeiasStorytelling(IdeiaUsuario iu) {
        Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();

        try {
            Criteria filtro = sessao.createCriteria(IdeiaUsuario.class);
            filtro.createAlias("ideia", "i");
            filtro.add(Restrictions.eq("usuario", iu.getUsuario()));
            filtro.add(Restrictions.eq("i.status", StatusIdeia.STORYTELLING));
            filtro.addOrder(Order.desc("ideia")); // listagem com as ideias mais novas em cima

            return filtro.list();

        } finally {
            sessao.close();
        }
    }

    public List<IdeiaUsuario> listarCaixa(IdeiaUsuario iu) {
        Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();

        try {
            Criteria filtro = sessao.createCriteria(IdeiaUsuario.class);
            filtro.createAlias("ideia", "i");

            filtro.add(Restrictions.eq("usuario", iu.getUsuario()));
            // TK-LIST: a Caixa aparece p/ TODOS os participantes da ideia (nao so o lider).
            // Se da p/ entrar pelo "Minhas Ideias", tem que aparecer nesta listagem tambem.
            filtro.add(Restrictions.eq("i.status", StatusIdeia.CAIXA_FERRAMENTAS));
            filtro.addOrder(Order.desc("ideia")); // listagem com as ideias mais novas em cima

            return filtro.list();

        } finally {
            sessao.close();
        }
    }

    public List<IdeiaUsuario> listarIdeiasCanva(IdeiaUsuario iu) {
        Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();

        try {
            Criteria filtro = sessao.createCriteria(IdeiaUsuario.class);
            filtro.createAlias("ideia", "i");

            // CAN-PARTICIPANTE: antes so o LIDER (flLider='S') via a ideia na listagem
            // do Canvas. Agora TODOS os participantes veem -- o acesso real e reforcado
            // no EntrarCanvaServlet (que agora exige participacao), nao mais so aqui.
            filtro.add(Restrictions.eq("usuario", iu.getUsuario()));
            // Status "CV" é definido pelo projeto Toolkit2025 (Caixa de Ferramentas) no
            // método IdeiaDAOImpl.finalize() quando a ideia sai da caixa. É o status
            // correto para listar no Canvas — NÃO alterar.
            filtro.add(Restrictions.eq("i.status", StatusIdeia.CANVAS));
            filtro.addOrder(Order.desc("ideia")); // listagem com as ideias mais novas em cima

            return filtro.list();

        } finally {
            sessao.close();
        }
    }

    /**
     * K.8 #1 (2026-07-06): fecha o grupo, atualiza a lideranca (se houve transferencia) e
     * grava o log inicial de colaboracao NUMA UNICA Session/Transaction. Antes, FecharGrupoServlet
     * fazia isso em 2 a 4 transacoes separadas (ideia.editar, ate 2x ideiaUsuario.editar, log.salvar)
     * -- uma falha exatamente entre rebaixar o lider antigo e promover o novo podia deixar a
     * ideia SEM NENHUM lider. Agora tudo commita junto ou nada commita (rollback).
     */
    public void fecharGrupoAtomico(Ideia ideia, List<IdeiaUsuario> vinculosParaAtualizar,
            List<IdeiaUsuario> vinculosParaRemover, LogColaboracao logInicial) {
        Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();
        Transaction transacao = null;

        try {
            transacao = sessao.beginTransaction();
            sessao.update(ideia);
            if (vinculosParaAtualizar != null) {
                for (IdeiaUsuario iu : vinculosParaAtualizar) {
                    sessao.update(iu);
                }
            }
            // M.2 (2026-07-06): ao fechar o grupo, os vinculos PENDENTES/REJEITADOS (quem
            // nao foi aprovado) sao removidos na MESMA transacao -- assim sobram so os
            // aprovados e todas as checagens de participacao ja existentes (Canva/Caixa/
            // Storytelling/etc.) continuam valendo sem precisar filtrar por status.
            //
            // REVISAO 2026-07-07: os objetos em vinculosParaRemover foram lidos numa
            // Session ANTERIOR (fora desta transacao) -- se AprovarMembroServlet aprovar
            // (P->A) e commitar EXATAMENTE entre essa leitura antiga e este delete, o
            // objeto em memoria ainda diz "P", e o codigo antigo apagava o vinculo mesmo
            // assim (um membro recem-aprovado sumia do grupo sem nunca ter sido rejeitado).
            // Fix: re-busca cada um FRESCO dentro desta transacao e SO apaga se o status
            // ainda for P/R no exato momento do commit -- fecha a corrida.
            if (vinculosParaRemover != null) {
                for (IdeiaUsuario iuAntigo : vinculosParaRemover) {
                    IdeiaUsuario iuFresco = sessao.get(IdeiaUsuario.class, iuAntigo.getCodigo());
                    if (iuFresco != null) {
                        String statusAtual = iuFresco.getFlStatusVinculo();
                        boolean aindaNaoAprovado = StatusIdeia.VINCULO_PENDENTE.equals(statusAtual)
                                || StatusIdeia.VINCULO_REJEITADO.equals(statusAtual);
                        if (aindaNaoAprovado) {
                            sessao.delete(iuFresco);
                        }
                    }
                }
            }
            sessao.save(logInicial);
            transacao.commit();

        } catch (RuntimeException erro) {
            if (transacao != null) {
                transacao.rollback();
            }
            throw erro;
        } finally {
            sessao.close();
        }
    }

}
