package edu.unisc.lic.util;

import edu.unisc.lic.classes.CriptografaDados;
import edu.unisc.lic.classes.ManipulaTxt;
import java.io.IOException;
import java.util.ArrayList;
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
            ManipulaTxt escritor = new ManipulaTxt();

            if (escritor.verificarSeArquivoExiste()) {

                ArrayList<String> dadosBanco = escritor.lerTxt();

                // INFRA-06: só sobrescreve a config se o arquivo trouxe as 5 linhas
                // esperadas; senão um txt vazio/malformado derrubaria a aplicação
                // inteira com IndexOutOfBounds na inicialização.
                if (dadosBanco.size() >= 5) {
                configuracao.setProperty("hibernate.connection.url", "jdbc:mysql://"
                        + CriptografaDados.descriptografar((String) dadosBanco.get(0)) + ":"
                        + CriptografaDados.descriptografar((String) dadosBanco.get(1)) + "/"
                        + CriptografaDados.descriptografar((String) dadosBanco.get(2)));

                configuracao.setProperty("hibernate.connection.username",
                        CriptografaDados.descriptografar((String) dadosBanco.get(3)));

                configuracao.setProperty("hibernate.connection.password",
                        CriptografaDados.descriptografar((String) dadosBanco.get(4)));
                }
            }

            SessionFactory fabrica = configuracao.buildSessionFactory();
            return fabrica;

        } catch (IOException | HibernateException ex) {
            System.err.println("A fábrica de sessões não pode ser criada." + ex);
            throw new ExceptionInInitializerError(ex);
        } catch (Exception ex) {
            Logger.getLogger(HibernateUtil.class.getName()).log(Level.SEVERE, null, ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

}
