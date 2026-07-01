package edu.unisc.lic.dao;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.unisc.lic.util.HibernateUtil;

public class GenericDAO<Entidade> {

    Class<Entidade> classe;

    @SuppressWarnings("unchecked")
    public GenericDAO() {
        this.classe = (Class<Entidade>) ((ParameterizedType) getClass().getGenericSuperclass())
                .getActualTypeArguments()[0];
    }

    /**
     * Método genérico que vai inserir no banco qualquer objeto que for passado
     * para ele. A classe deve ser igual a tabela.
     *
     * @param entidade (qualquer objeto)
     */
    public void salvar(Entidade entidade) {
        Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();
        Transaction transacao = null;

        try {
            transacao = sessao.beginTransaction();
            sessao.save(entidade);

            transacao.commit(); // vê se deu tudo certo na inserção

        } catch (RuntimeException erro) {
            if (transacao != null) { // se algo deu errado, desfaz
                transacao.rollback();
            }

            System.err.println(erro);

            throw erro;
        } finally {
            sessao.close(); // finaliza a sessão (TEM QUE COLOCAR)
        }

    }

    /**
     * Método genérico que vai listar todos os registros do banco de determinada
     * tabela.
     *
     * @return lista de objetos
     */
    public List<Entidade> listar() {
        Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();

        try {
            CriteriaBuilder builder = sessao.getCriteriaBuilder();
            CriteriaQuery<Entidade> consulta = builder.createQuery(classe);
            consulta.from(classe);

            List<Entidade> resultado = sessao.createQuery(consulta).getResultList();

            return resultado;

        } catch (RuntimeException erro) {
            throw erro;
        } finally {
            sessao.close(); // finaliza a sessão (TEM QUE COLOCAR)
        }

    }

    /**
     * Método genérico que vai retornar um único objeto de uma determinada
     * tabela.
     *
     * @param codigo
     * @return
     */
    public Entidade buscar(Long codigo) {
        Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();
        Entidade resultado = null;

        try {
            resultado = sessao.find(classe, codigo);

            return resultado;

        } catch (RuntimeException erro) {
            throw erro;
        } finally {
            sessao.close(); // finaliza a sessão (TEM QUE COLOCAR)
        }

    }

    /**
     * Método genérico que vai excluir um registro da tabela do banco assim como
     * ele foi passado por parâmetro.
     *
     * @param entidade (objeto)
     */
    public void excluir(Entidade entidade) {
        Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();
        Transaction transacao = null;

        try {
            transacao = sessao.beginTransaction();
            sessao.delete(entidade);

            transacao.commit(); // vê se deu tudo certo na inserção

        } catch (RuntimeException erro) {
            if (transacao != null) { // se algo deu errado, desfaz
                transacao.rollback();
            }

            throw erro;
        } finally {
            sessao.close(); // finaliza a sessão (TEM QUE COLOCAR)
        }

    }

    /**
     * Método genérico que vai editar tudo em um registro do banco de dados.
     * @param entidade 
     */
    public void editar(Entidade entidade) {
        Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();
        Transaction transacao = null;

        try {
            transacao = sessao.beginTransaction();
            sessao.update(entidade);

            transacao.commit(); // vê se deu tudo certo na inserção

        } catch (RuntimeException erro) {
            if (transacao != null) { // se algo deu errado, desfaz
                transacao.rollback();
            }

            throw erro;
        } finally {
            sessao.close(); // finaliza a sessão (TEM QUE COLOCAR)
        }

    }

}
