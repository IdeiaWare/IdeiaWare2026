package edu.unisc.lic.classes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

// TEST-01: formatacao de data (logica pura) -- cobre o comportamento null-safe (null -> "hoje", nao lanca NPE).
public class DataFormatacaoTest {

	private Date data(int ano, int mes, int dia, int hora, int min) {
		Calendar c = Calendar.getInstance();
		c.set(ano, mes - 1, dia, hora, min, 0);
		return c.getTime();
	}

	@Test
	public void formatarData_ddMmYyyy() {
		assertEquals("25/12/2024", Data.formatarData(data(2024, 12, 25, 10, 30)));
	}

	@Test
	public void formatarHora_hhMm() {
		assertEquals("14:05", Data.formatarHora(data(2024, 1, 1, 14, 5)));
	}

	@Test
	public void formatarData_nullNaoQuebra() {
		String hoje = Data.formatarData(null);
		assertNotNull(hoje);
		assertEquals(10, hoje.length()); // dd/MM/yyyy = 10 chars
	}

	@Test
	public void formatarHora_nullNaoQuebra() {
		String agora = Data.formatarHora(null);
		assertNotNull(agora);
		assertEquals(5, agora.length()); // HH:mm = 5 chars
	}
}
