package br.unisc.toolkit.classes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;

import org.junit.Before;
import org.junit.Test;

// AUDITORIA-2026-07-16: PDF-DISCO criou este helper sem teste (irmao LIC ArquivoExport ja tinha).
public class ArquivoExportTest {

	@Before
	public void redirecionaExportsParaTmpdir() {
		System.setProperty("ideiaware.exports.dir", System.getProperty("java.io.tmpdir"));
	}

	@Test
	public void salvar_base64Puro_decodificaEGravaOsBytes() throws Exception {
		String base64 = Base64.getEncoder().encodeToString("conteudo-do-pdf".getBytes(StandardCharsets.UTF_8));

		String caminhoRelativo = ArquivoExport.salvar(base64, 123L);

		assertTrue(caminhoRelativo.startsWith(Constantes.CAMINHO_EXPORT_CONHECIMENTO + "123" + File.separator));
		assertTrue(caminhoRelativo.endsWith(".pdf"));
		byte[] lido = Files.readAllBytes(new File(Constantes.caminhoExports() + caminhoRelativo).toPath());
		assertEquals("conteudo-do-pdf", new String(lido, StandardCharsets.UTF_8));
	}

	@Test
	public void salvar_dataUri_decodificaSoAParteBase64() throws Exception {
		String base64 = Base64.getEncoder().encodeToString("conteudo-do-pdf".getBytes(StandardCharsets.UTF_8));

		String caminhoRelativo = ArquivoExport.salvar("data:application/pdf;base64," + base64, 124L);

		byte[] lido = Files.readAllBytes(new File(Constantes.caminhoExports() + caminhoRelativo).toPath());
		assertEquals("conteudo-do-pdf", new String(lido, StandardCharsets.UTF_8));
	}

	@Test(expected = IllegalArgumentException.class)
	public void salvar_base64Invalido_lancaExcecao() throws Exception {
		ArquivoExport.salvar("isso nao e base64 valido!!!", 125L);
	}

	@Test
	public void salvar_duasVezesMesmaIdeia_geraNomesDiferentes() throws Exception {
		// CONC-01: nome do arquivo e' um UUID, nunca colide.
		String base64 = Base64.getEncoder().encodeToString("x".getBytes(StandardCharsets.UTF_8));

		String caminho1 = ArquivoExport.salvar(base64, 126L);
		String caminho2 = ArquivoExport.salvar(base64, 126L);

		assertNotEquals(caminho1, caminho2);
	}
}
