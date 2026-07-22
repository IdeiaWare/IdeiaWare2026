package edu.unisc.lic.classes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Test;

// TEST-01: formatacao de data (logica pura) -- cobre o comportamento null-safe (null -> "", nao lanca NPE).
public class DataFormatacaoTest {

	// TEST-TZ: fuso fixo (nao o default da JVM), Data.java sempre formata em America/Sao_Paulo.
	private Date data(int ano, int mes, int dia, int hora, int min) {
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("America/Sao_Paulo"));
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

	// INFRA-09: null nao mostra mais "hoje" (data que nunca aconteceu) -- mostra vazio.
	@Test
	public void formatarData_nullRetornaVazio() {
		assertEquals("", Data.formatarData(null));
	}

	@Test
	public void formatarHora_nullRetornaVazio() {
		assertEquals("", Data.formatarHora(null));
	}

	@Test
	public void formatarDataHoraCompleta_nullRetornaVazio() {
		assertEquals("", Data.formatarDataHoraCompleta(null));
	}

	@Test
	public void formatarDataHoraCompleta_ddMmYyyyHhMm() {
		assertEquals("25/12/2024 10:30", Data.formatarDataHoraCompleta(data(2024, 12, 25, 10, 30)));
	}
}
