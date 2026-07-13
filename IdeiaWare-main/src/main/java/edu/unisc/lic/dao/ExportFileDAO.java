package edu.unisc.lic.dao;

import edu.unisc.lic.domain.ExportFile;
import edu.unisc.lic.util.HibernateUtil;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

public class ExportFileDAO extends GenericDAO<ExportFile> {

    public List<ExportFile> listarParametro(ExportFile ef) {
        Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();

        try {
            Criteria filtro = sessao.createCriteria(ExportFile.class);

            if (ef.getIdeia().getCodigo() != null) {
                filtro.add(Restrictions.eq("ideia", ef.getIdeia()));
            }
            if (ef.getFileTypeIdentification() != null) {
                filtro.add(Restrictions.eq("fileTypeIdentification", ef.getFileTypeIdentification()));
            }

            return filtro.list();

        } finally {
            sessao.close();
        }
    }
}
