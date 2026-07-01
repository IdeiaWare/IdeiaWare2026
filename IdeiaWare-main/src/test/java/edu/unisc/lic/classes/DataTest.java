package edu.unisc.lic.classes;

import edu.unisc.lic.dao.IdeiaDAO;
import edu.unisc.lic.domain.Ideia;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author viniciussdsilva
 */
public class DataTest {
    
    @Test
    @Ignore
    public void diferencaDatas() {
        
        IdeiaDAO iDAO = new IdeiaDAO();
        Ideia i = iDAO.buscar(2L);
        
        System.out.println(i.getDtCriacao());
        System.out.println(i.getDtValidacao());
        System.out.println(Data.diferencaDatas(i.getDtCriacao(), i.getDtValidacao()));
    }
}
