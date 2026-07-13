package edu.unisc.lic.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Test;

// TEST-04: reativado do scratch @Ignore -- a fabrica de sessoes e static final compartilhada pela JVM de teste, NAO pode ser fechada aqui.
public class HibernateUtilTest {

	@Test
	public void getFabricaDeSessoes_retornaFabricaAbertaEReutilizavel() {
		SessionFactory fabrica = HibernateUtil.getFabricaDeSessoes();

		assertNotNull(fabrica);
		assertTrue("a fabrica compartilhada nao pode estar fechada", fabrica.isOpen());
	}

	@Test
	public void getFabricaDeSessoes_abreEFechaSessaoSemErro() {
		Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();
		try {
			assertTrue(sessao.isOpen());
		} finally {
			sessao.close();
		}
		assertTrue("sessao fechada, mas a fabrica continua aberta p/ os proximos testes",
				HibernateUtil.getFabricaDeSessoes().isOpen());
	}

	@Test
	public void getFabricaDeSessoes_sempreRetornaAMesmaInstancia() {
		assertTrue(HibernateUtil.getFabricaDeSessoes() == HibernateUtil.getFabricaDeSessoes());
	}
}
