package edu.unisc.lic.dao;

import edu.unisc.lic.domain.ColaboracaoIdeia;
import edu.unisc.lic.util.HibernateUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.hibernate.Session;

public class ColaboracaoIdeiaDAO extends GenericDAO<ColaboracaoIdeia> {

    // DAO-FAIL-CLOSED: sem ideia definida nao ha o que listar (antes voltava a tabela inteira).
    public List<ColaboracaoIdeia> listarParametro(ColaboracaoIdeia ci) {
        if (ci.getIdeia().getCodigo() == null) {
            return Collections.emptyList();
        }
        Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();

        try {
            CriteriaBuilder builder = sessao.getCriteriaBuilder();
            CriteriaQuery<ColaboracaoIdeia> consulta = builder.createQuery(ColaboracaoIdeia.class);
            Root<ColaboracaoIdeia> raiz = consulta.from(ColaboracaoIdeia.class);

            consulta.select(raiz)
                    .where(builder.equal(raiz.get("ideia"), ci.getIdeia()))
                    .orderBy(builder.asc(raiz.get("codigo")));

            return sessao.createQuery(consulta).getResultList();

        } finally {
            sessao.close();
        }
    }

    // M.6: colaboracoes com codigo > ultimoCodigo (antes, protocolo por contagem perdia/duplicava).
    public List<ColaboracaoIdeia> listarAposCodigo(ColaboracaoIdeia ci, Long ultimoCodigo) {
        if (ci.getIdeia().getCodigo() == null) {
            return Collections.emptyList();
        }
        Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();

        try {
            CriteriaBuilder builder = sessao.getCriteriaBuilder();
            CriteriaQuery<ColaboracaoIdeia> consulta = builder.createQuery(ColaboracaoIdeia.class);
            Root<ColaboracaoIdeia> raiz = consulta.from(ColaboracaoIdeia.class);

            List<Predicate> predicados = new ArrayList<>();
            predicados.add(builder.equal(raiz.get("ideia"), ci.getIdeia()));
            Path<Long> codigoPath = raiz.get("codigo");
            predicados.add(builder.greaterThan(codigoPath, ultimoCodigo == null ? 0L : ultimoCodigo));

            consulta.select(raiz)
                    .where(predicados.toArray(new Predicate[0]))
                    .orderBy(builder.asc(raiz.get("codigo")));

            return sessao.createQuery(consulta).getResultList();

        } finally {
            sessao.close();
        }
    }

    public ColaboracaoIdeia ultimaColab(ColaboracaoIdeia ci) {
        if (ci.getIdeia().getCodigo() == null) {
            return null;
        }
        Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();

        try {
            CriteriaBuilder builder = sessao.getCriteriaBuilder();
            CriteriaQuery<ColaboracaoIdeia> consulta = builder.createQuery(ColaboracaoIdeia.class);
            Root<ColaboracaoIdeia> raiz = consulta.from(ColaboracaoIdeia.class);

            consulta.select(raiz)
                    .where(builder.equal(raiz.get("ideia"), ci.getIdeia()))
                    .orderBy(builder.desc(raiz.get("dtModificacao")));

            // RET-14: retorna null em vez de estourar se nao houver colaboracoes.
            List<ColaboracaoIdeia> resultado = sessao.createQuery(consulta).setMaxResults(1).getResultList();
            return resultado.isEmpty() ? null : resultado.get(0);

        } finally {
            sessao.close();
        }
    }

    // GT-12: renomeado de quantidadeMes (nunca filtrou por mes) + COUNT no banco em vez de carregar tudo.
    public int quantidadeTotal(ColaboracaoIdeia ci) {
        if (ci.getIdeia().getCodigo() == null) {
            return 0;
        }
        Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();

        try {
            CriteriaBuilder builder = sessao.getCriteriaBuilder();
            CriteriaQuery<Long> consulta = builder.createQuery(Long.class);
            Root<ColaboracaoIdeia> raiz = consulta.from(ColaboracaoIdeia.class);

            consulta.select(builder.count(raiz)).where(builder.equal(raiz.get("ideia"), ci.getIdeia()));

            return sessao.createQuery(consulta).getSingleResult().intValue();

        } finally {
            sessao.close();
        }
    }
}
