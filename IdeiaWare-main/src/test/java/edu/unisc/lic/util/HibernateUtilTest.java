package edu.unisc.lic.util;

import org.hibernate.Session;
import org.junit.Ignore;
import org.junit.Test;

public class HibernateUtilTest {
	@Test
        @Ignore
	public void conectar() {
		Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();
		sessao.close();
		HibernateUtil.getFabricaDeSessoes().close();
	}
}
