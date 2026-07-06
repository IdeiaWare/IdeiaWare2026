package edu.unisc.lic.classes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;

/**
 * TEST-04: o scratch @Ignore original batia num IdeiaDAO real (buscar(2L)) so pra
 * chamar diferencaDatas() e dar println -- nao testava nada e dependia de banco.
 * diferencaDatas() e logica pura (2 Date -> String); reescrito sem nenhuma
 * dependencia de banco, igual ao espirito do DataFormatacaoTest (TEST-01).
 */
public class DataTest {

	@Test
	public void diferencaDatas_diasHorasEMinutos_formataOsTres() {
		Date dt1 = new Date(0);
		Date dt2 = new Date(dt1.getTime() + ((2L * 24 + 3) * 60 + 15) * 60_000L); // +2d 3h 15min

		assertEquals("2 dias, 3 horas e 15 minutos.", Data.diferencaDatas(dt1, dt2));
	}

	@Test
	public void diferencaDatas_semDias_omiteDias() {
		Date dt1 = new Date(0);
		Date dt2 = new Date(dt1.getTime() + (5 * 60 + 30) * 60_000L); // +5h 30min

		assertEquals("5 horas e 30 minutos.", Data.diferencaDatas(dt1, dt2));
	}

	@Test
	public void diferencaDatas_semDiasNemHoras_soMinutos() {
		Date dt1 = new Date(0);
		Date dt2 = new Date(dt1.getTime() + 45 * 60_000L); // +45min

		assertEquals("45 minutos.", Data.diferencaDatas(dt1, dt2));
	}

	@Test
	public void diferencaDatas_ambosNulos_naoQuebraUsaHoraAtualNosDois() {
		String resultado = Data.diferencaDatas(null, null);

		assertNotNull(resultado);
		assertTrue("diff entre 2 horaAtual() sucessivas fica em minutos", resultado.endsWith("minutos."));
	}

	@Test
	public void diferencaDatas_dt1Nulo_usaHoraAtualComoDt1() {
		Date dt2 = new Date(System.currentTimeMillis() + 10 * 60_000L); // +10min no futuro

		String resultado = Data.diferencaDatas(null, dt2);

		assertNotNull(resultado);
		assertTrue(resultado.endsWith("minutos.") || resultado.contains("horas"));
	}
}
