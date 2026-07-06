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

            // LEGADO-CFGBANCO removido: existia um mecanismo de override via
            // cfgbanco.txt (ManipulaTxt + CriptografaDados, AES em modo ECB) que
            // nunca chegou a ser usado em nenhum deploy real (Docker ou manual) --
            // confirmado ausente em todos os scripts/config do projeto. A config
            // do banco hoje vem so do hibernate.cfg.xml (patchado por variavel de
            // ambiente no entrypoint.sh do Docker, ou editado direto no deploy
            // manual). Codigo morto removido em vez de corrigir o modo AES fraco
            // de algo que nunca era exercitado.
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
