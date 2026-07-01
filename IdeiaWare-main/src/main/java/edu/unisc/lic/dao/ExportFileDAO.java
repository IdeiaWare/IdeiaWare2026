package edu.unisc.lic.dao;

import edu.unisc.lic.domain.ExportFile;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.util.HibernateUtil;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author viniciussdsilva
 */
public class ExportFileDAO extends GenericDAO<ExportFile> {

    private Session sessao;
    private Transaction transacao;

    public List<ExportFile> listarParametro(ExportFile ef) {
        this.sessao = HibernateUtil.getFabricaDeSessoes().openSession();
        this.transacao = sessao.beginTransaction();

        List<ExportFile> resultado = null;

        try {

            Criteria filtro = sessao.createCriteria(ExportFile.class);

            if (ef.getIdeia().getCodigo() != null) {
                filtro.add(Restrictions.eq("ideia", ef.getIdeia()));
            }
            if (ef.getFileTypeIdentification() != null) {
                filtro.add(Restrictions.eq("fileTypeIdentification", ef.getFileTypeIdentification()));
            }

            resultado = filtro.list();

        } catch (HibernateException e) {
            if (this.transacao.isActive()) {
                this.transacao.rollback();
            }
        } finally {
            try {
                if (sessao.isOpen()) {
                    sessao.close();
                }
            } catch (HibernateException e) {
                System.out.println("Erro ao fechar a operação. Mensagem:" + e.getMessage());
            }
        }

        return resultado;
    }
}
