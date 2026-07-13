package edu.unisc.lic.util;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

    private static final SessionFactory fabricaDeSessoes = criarFabricaDeSessoes();

    public static SessionFactory getFabricaDeSessoes() {
        return fabricaDeSessoes;
    }

    private static SessionFactory criarFabricaDeSessoes() {

        try {
            Configuration configuracao = new Configuration().configure();

            // CFGBANCO-REMOVIDO: mecanismo de override via cfgbanco.txt (AES ECB) era codigo morto, removido.
            SessionFactory fabrica = configuracao.buildSessionFactory();
            return fabrica;

        } catch (HibernateException ex) {
            System.err.println("A fábrica de sessões não pode ser criada." + ex);
            throw new ExceptionInInitializerError(ex);
        } catch (Exception ex) {
            Logger.getLogger(HibernateUtil.class.getName()).log(Level.SEVERE, null, ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

}
