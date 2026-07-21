package edu.unisc.lic.dao;

import edu.unisc.lic.domain.ExportFile;
import edu.unisc.lic.util.HibernateUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.hibernate.Session;

public class ExportFileDAO extends GenericDAO<ExportFile> {

    // DAO-FAIL-CLOSED: sem ideia definida, nao lista tudo
    public List<ExportFile> listarParametro(ExportFile ef) {
        if (ef.getIdeia().getCodigo() == null) {
            return Collections.emptyList();
        }
        Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();

        try {
            CriteriaBuilder builder = sessao.getCriteriaBuilder();
            CriteriaQuery<ExportFile> consulta = builder.createQuery(ExportFile.class);
            Root<ExportFile> raiz = consulta.from(ExportFile.class);

            List<Predicate> predicados = new ArrayList<>();
            predicados.add(builder.equal(raiz.get("ideia"), ef.getIdeia()));
            if (ef.getFileTypeIdentification() != null) {
                predicados.add(builder.equal(raiz.get("fileTypeIdentification"), ef.getFileTypeIdentification()));
            }

            consulta.select(raiz).where(predicados.toArray(new Predicate[0]));

            return sessao.createQuery(consulta).getResultList();

        } finally {
            sessao.close();
        }
    }
}
