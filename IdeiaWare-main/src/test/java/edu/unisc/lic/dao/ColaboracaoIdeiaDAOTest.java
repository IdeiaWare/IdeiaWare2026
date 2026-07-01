package edu.unisc.lic.dao;

import edu.unisc.lic.classes.Data;
import edu.unisc.lic.domain.ColaboracaoIdeia;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.Usuario;
import java.util.List;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author viniciussdsilva
 */
public class ColaboracaoIdeiaDAOTest {
    
    @Test
    @Ignore
    public void inserir() {
        IdeiaDAO iDAO = new IdeiaDAO();
        UsuarioDAO uDAO = new UsuarioDAO();
        ColaboracaoIdeiaDAO ciDAO = new ColaboracaoIdeiaDAO();
        
        Ideia i = iDAO.buscar(3L);
        Usuario u = uDAO.buscar(3L);
        ColaboracaoIdeia ci = new ColaboracaoIdeia(i, u, Data.horaAtual(), "O bebedor deve ser aqueles de encher");
        
        ciDAO.salvar(ci);
        
    }
    
    @Test
    @Ignore
    public void listarParametro() {
        IdeiaDAO iDAO = new IdeiaDAO();
        
        ColaboracaoIdeia ci = new ColaboracaoIdeia();
        ci.setIdeia(iDAO.buscar(2L));
        
        ColaboracaoIdeiaDAO ciDAO = new ColaboracaoIdeiaDAO();
        
        List<ColaboracaoIdeia> listaCI = ciDAO.listarParametro(ci);
        
        for (ColaboracaoIdeia colabIdeia : listaCI) {
            System.out.println(colabIdeia.getDescricaoIdeiaAtual());
        }
        
    }
    
}
