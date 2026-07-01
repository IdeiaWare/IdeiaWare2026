package edu.unisc.lic.dao;

import edu.unisc.lic.classes.Data;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.Storytelling;
import edu.unisc.lic.domain.Usuario;
import java.util.List;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author viniciussdsilva
 */
public class StorytellingDAOTest {
    
    @Test
    @Ignore
    public void inserir() {
        Usuario u = new UsuarioDAO().buscar(1L);
        Ideia i = new IdeiaDAO().buscar(6L);
        Storytelling s = new Storytelling(u, i, Data.horaAtual(), "AB");
        System.out.println(s);
        new StorytellingDAO().salvar(s);
    }
    
    @Test
    @Ignore
    public void listarParametro() {
        Storytelling st = new Storytelling();
//        Usuario u = new UsuarioDAO().buscar(2L);
//        st.setUsuario(u);
        
        Ideia i = new IdeiaDAO().buscar(4L);
        st.setIdeia(i);

        System.out.println(st);
        
        List<Storytelling> lista = new StorytellingDAO().listarParametro(st);

        for (Storytelling elemento : lista) {
//            System.out.println(elemento.getIdeia().getTitulo());
            System.out.println(elemento.getUsuario().getNome());
        }
        
    }
    
    
}
