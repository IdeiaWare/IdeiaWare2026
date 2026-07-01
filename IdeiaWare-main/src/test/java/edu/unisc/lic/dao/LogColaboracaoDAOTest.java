package edu.unisc.lic.dao;

import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.LogColaboracao;
import java.util.List;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author viniciussdsilva
 */
public class LogColaboracaoDAOTest {
    @Test
    @Ignore
    public void listarParametros() {
        LogColaboracao lc = new LogColaboracao();
        Ideia i = new IdeiaDAO().buscar(13L);
        lc.setIdeia(i);
        
        List<LogColaboracao> resultado = new LogColaboracaoDAO().listarParametro(lc);
        
        for(LogColaboracao logC : resultado) {
            System.out.println(logC);
        }
    }
}
