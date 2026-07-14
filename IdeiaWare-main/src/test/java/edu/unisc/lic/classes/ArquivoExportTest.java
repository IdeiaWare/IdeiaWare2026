package edu.unisc.lic.classes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;

import org.junit.Before;
import org.junit.Test;

public class ArquivoExportTest {

	@Before
	public void redirecionaExportsParaTmpdir() {
		System.setProperty("ideiaware.exports.dir", System.getProperty("java.io.tmpdir"));
	}

	private String caminhoDeTeste() {
		return "arquivo-export-test" + File.separator + System.nanoTime() + ".pdf";
	}

	@Test
	public void salvar_base64Puro_decodificaEGravaOsBytes() throws Exception {
		String base64 = Base64.getEncoder().encodeToString("conteudo-do-pdf".getBytes(StandardCharsets.UTF_8));
		String caminhoRelativo = caminhoDeTeste();

		String retorno = ArquivoExport.salvar(base64, caminhoRelativo);

		assertEquals(caminhoRelativo, retorno);
		byte[] lido = Files.readAllBytes(new File(Constantes.caminhoExports() + caminhoRelativo).toPath());
		assertEquals("conteudo-do-pdf", new String(lido, StandardCharsets.UTF_8));
	}

	@Test
	public void salvar_dataUri_decodificaSoAParteBase64() throws Exception {
		String base64 = Base64.getEncoder().encodeToString("conteudo-do-pdf".getBytes(StandardCharsets.UTF_8));
		String caminhoRelativo = caminhoDeTeste();

		ArquivoExport.salvar("data:application/pdf;base64," + base64, caminhoRelativo);

		byte[] lido = Files.readAllBytes(new File(Constantes.caminhoExports() + caminhoRelativo).toPath());
		assertEquals("conteudo-do-pdf", new String(lido, StandardCharsets.UTF_8));
	}

	@Test(expected = IllegalArgumentException.class)
	public void salvar_base64Invalido_lancaExcecao() throws Exception {
		ArquivoExport.salvar("isso nao e base64 valido!!!", caminhoDeTeste());
	}

	@Test
	public void excluir_arquivoExistente_apagaERetornaTrue() throws Exception {
		String base64 = Base64.getEncoder().encodeToString("x".getBytes(StandardCharsets.UTF_8));
		String caminhoRelativo = caminhoDeTeste();
		ArquivoExport.salvar(base64, caminhoRelativo);

		boolean apagou = ArquivoExport.excluir(caminhoRelativo);

		assertTrue(apagou);
		assertFalse(new File(Constantes.caminhoExports() + caminhoRelativo).exists());
	}

	@Test
	public void excluir_caminhoNulo_retornaFalseSemLancar() {
		assertFalse(ArquivoExport.excluir(null));
	}
}
