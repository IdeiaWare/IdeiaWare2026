package edu.unisc.lic.dao;

import edu.unisc.lic.domain.ExportFile;
import java.util.List;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Gustavo Armborst Guedes de Azevedo
 */
public class ExportFileDAOTest {
    
    @Test
    @Ignore
    public void listarParametro() {
        ExportFile ef = new ExportFile();
        ef.setFileTypeIdentification("pov");
        
        List<ExportFile> resultado = new ExportFileDAO().listarParametro(ef);
        System.out.println("tamanho: " + resultado.size());
        
        for(ExportFile e : resultado) {
            System.out.println(e);
        }
        
    }
}
