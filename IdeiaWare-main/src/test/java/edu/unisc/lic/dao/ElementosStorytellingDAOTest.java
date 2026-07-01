package edu.unisc.lic.dao;

import edu.unisc.lic.domain.ElementosStorytelling;
import edu.unisc.lic.domain.Storytelling;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author viniciussdsilva
 */
public class ElementosStorytellingDAOTest {

    @Test
    @Ignore
    public void inserir() {
        Storytelling s = new StorytellingDAO().buscar(1L);

        ElementosStorytelling es = new ElementosStorytelling(s, "IMAGEM", s.getCodigo() + "", 0, 0, 0, 0);
    }

}
