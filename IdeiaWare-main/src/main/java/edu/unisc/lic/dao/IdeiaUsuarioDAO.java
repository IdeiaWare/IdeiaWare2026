package edu.unisc.lic.dao;
import edu.unisc.lic.classes.StatusIdeia;

import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.IdeiaUsuario;
import edu.unisc.lic.domain.LogColaboracao;
import edu.unisc.lic.util.HibernateUtil;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class IdeiaUsuarioDAO extends GenericDAO<IdeiaUsuario> {

    public List<IdeiaUsuario> listarParametro(IdeiaUsuario iu) {
        Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();

        try {
            CriteriaBuilder builder = sessao.getCriteriaBuilder();
            CriteriaQuery<IdeiaUsuario> consulta = builder.createQuery(IdeiaUsuario.class);
            Root<IdeiaUsuario> raiz = consulta.from(IdeiaUsuario.class);

            List<Predicate> predicados = new ArrayList<>();
            if (iu.getUsuario().getCodigo() != null) {
                predicados.add(builder.equal(raiz.get("usuario"), iu.getUsuario()));
            }
            if (iu.getIdeia().getCodigo() != null) {
                predicados.add(builder.equal(raiz.get("ideia"), iu.getIdeia()));
            }
            if (iu.getFlLider() != null) {
                predicados.add(builder.equal(raiz.get("flLider"), iu.getFlLider()));
            }

            // Ordena "Minhas Ideias" do mais NOVO pro mais antigo.
            consulta.select(raiz)
                    .where(predicados.toArray(new Predicate[0]))
                    .orderBy(builder.desc(raiz.get("ideia")));

            return sessao.createQuery(consulta).getResultList();

        } finally {
            sessao.close();
        }
    }

    // STR-09/PERF-01: lider+participantes, com filtro de status na propria query (nao em Java).
    public List<IdeiaUsuario> listarTodasIdeiasStorytelling(IdeiaUsuario iu) {
        Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();

        try {
            CriteriaBuilder builder = sessao.getCriteriaBuilder();
            CriteriaQuery<IdeiaUsuario> consulta = builder.createQuery(IdeiaUsuario.class);
            Root<IdeiaUsuario> raiz = consulta.from(IdeiaUsuario.class);
            Join<IdeiaUsuario, Ideia> i = raiz.join("ideia");

            Predicate porUsuario = builder.equal(raiz.get("usuario"), iu.getUsuario());
            Predicate porStatus = builder.equal(i.get("status"), StatusIdeia.STORYTELLING);

            consulta.select(raiz)
                    .where(builder.and(porUsuario, porStatus))
                    .orderBy(builder.desc(raiz.get("ideia"))); // listagem com as ideias mais novas em cima

            return sessao.createQuery(consulta).getResultList();

        } finally {
            sessao.close();
        }
    }

    public List<IdeiaUsuario> listarCaixa(IdeiaUsuario iu) {
        Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();

        try {
            CriteriaBuilder builder = sessao.getCriteriaBuilder();
            CriteriaQuery<IdeiaUsuario> consulta = builder.createQuery(IdeiaUsuario.class);
            Root<IdeiaUsuario> raiz = consulta.from(IdeiaUsuario.class);
            Join<IdeiaUsuario, Ideia> i = raiz.join("ideia");

            Predicate porUsuario = builder.equal(raiz.get("usuario"), iu.getUsuario());
            // TK-LIST: a Caixa aparece p/ TODOS os participantes, nao so o lider.
            Predicate porStatus = builder.equal(i.get("status"), StatusIdeia.CAIXA_FERRAMENTAS);

            consulta.select(raiz)
                    .where(builder.and(porUsuario, porStatus))
                    .orderBy(builder.desc(raiz.get("ideia"))); // listagem com as ideias mais novas em cima

            return sessao.createQuery(consulta).getResultList();

        } finally {
            sessao.close();
        }
    }

    public List<IdeiaUsuario> listarIdeiasCanva(IdeiaUsuario iu) {
        Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();

        try {
            CriteriaBuilder builder = sessao.getCriteriaBuilder();
            CriteriaQuery<IdeiaUsuario> consulta = builder.createQuery(IdeiaUsuario.class);
            Root<IdeiaUsuario> raiz = consulta.from(IdeiaUsuario.class);
            Join<IdeiaUsuario, Ideia> i = raiz.join("ideia");

            // CAN-ACESSO-V2: antes so o LIDER via a ideia na listagem do Canvas, agora todos.
            Predicate porUsuario = builder.equal(raiz.get("usuario"), iu.getUsuario());
            // Status "CV" é definido pelo projeto Toolkit2025 (Caixa de Ferramentas) no
            // método IdeiaDAOImpl.finalize() quando a ideia sai da caixa. É o status
            // correto para listar no Canvas — NÃO alterar.
            Predicate porStatus = builder.equal(i.get("status"), StatusIdeia.CANVAS);

            consulta.select(raiz)
                    .where(builder.and(porUsuario, porStatus))
                    .orderBy(builder.desc(raiz.get("ideia"))); // listagem com as ideias mais novas em cima

            return sessao.createQuery(consulta).getResultList();

        } finally {
            sessao.close();
        }
    }

    // K.8 #1: fecha grupo + lideranca + log NUMA UNICA transacao (antes, falha no meio deixava sem lider).
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
            // M.2/GT-03: re-busca cada vinculo FRESCO na transacao e so apaga se ainda P/R (fecha a corrida com AprovarMembroServlet).
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
