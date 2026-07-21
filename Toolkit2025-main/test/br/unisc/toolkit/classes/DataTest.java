package br.unisc.toolkit.classes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

// TEST-0X: Data nunca teve teste -- formatacao usada em toda tela que mostra prazo/data, e
// diferencaDatas tem 3 ramos (dias/horas/minutos) que decidem o texto mostrado ao usuario.
public class DataTest {

	private Date em(int ano, int mes, int dia, int hora, int minuto) {
		Calendar cal = Calendar.getInstance();
		cal.set(ano, mes - 1, dia, hora, minuto, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	@Test
	public void horaAtual_retornaTimestampNaoNulo() {
		assertNotNull(Data.horaAtual());
	}

	@Test
	public void dataAtualFormatada_seguePadraoDDMMYYYY() {
		assertTrue(Data.dataAtualFormatada().matches("\\d{2}/\\d{2}/\\d{4}"));
	}

	@Test
	public void formatarData_dataNula_retornaVazio() {
		assertEquals("", Data.formatarData(null));
	}

	@Test
	public void formatarData_dataValida_formataDDMMYYYY() {
		assertEquals("15/03/2026", Data.formatarData(em(2026, 3, 15, 10, 30)));
	}

	@Test
	public void formatarHora_dataNula_retornaVazio() {
		assertEquals("", Data.formatarHora(null));
	}

	@Test
	public void formatarHora_dataValida_formataHHmm() {
		assertEquals("14:05", Data.formatarHora(em(2026, 3, 15, 14, 5)));
	}

	@Test
	public void formatarDataHoraCompleta_dataNula_retornaVazio() {
		assertEquals("", Data.formatarDataHoraCompleta(null));
	}

	@Test
	public void formatarDataHoraCompleta_dataValida_formataHoraEData() {
		assertEquals("14:05 15/03/2026", Data.formatarDataHoraCompleta(em(2026, 3, 15, 14, 5)));
	}

	@Test
	public void diferencaDatas_menosDeUmaHora_mostraSoMinutos() {
		Date inicio = em(2026, 3, 15, 10, 0);
		Date fim = em(2026, 3, 15, 10, 25);
		assertEquals("25 minutos.", Data.diferencaDatas(inicio, fim));
	}

	@Test
	public void diferencaDatas_menosDeUmDia_mostraHorasEMinutos() {
		Date inicio = em(2026, 3, 15, 10, 0);
		Date fim = em(2026, 3, 15, 13, 20);
		assertEquals("3 horas e 20 minutos.", Data.diferencaDatas(inicio, fim));
	}

	@Test
	public void diferencaDatas_maisDeUmDia_mostraDiasHorasEMinutos() {
		Date inicio = em(2026, 3, 15, 10, 0);
		Date fim = em(2026, 3, 17, 12, 30);
		assertEquals("2 dias, 2 horas e 30 minutos.", Data.diferencaDatas(inicio, fim));
	}

	@Test
	public void diferencaDatas_primeiraDataNula_usaAgoraComoInicio() {
		// so garante que nao quebra e devolve uma mensagem valida no formato esperado.
		String resultado = Data.diferencaDatas(null, Data.horaAtual());
		assertTrue(resultado.endsWith("."));
	}

	@Test
	public void diferencaDatas_segundaDataNula_usaAgoraComoFim() {
		String resultado = Data.diferencaDatas(Data.horaAtual(), null);
		assertTrue(resultado.endsWith("."));
	}
}
