package br.unisc.toolkit.repository;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import br.unisc.toolkit.entity.ExportFile;

// TEST-03/04: ExportFileDAOImpl contra H2 -- trava TK-HQL (ideiaCodigo por propriedade) e a ordenacao por nome.
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext-test.xml")
@Transactional
public class ExportFileDAOImplTest {

	@Autowired
	private ExportFileDAO exportFileDAO;

	private void novoArquivo(String nome, long ideiaCodigo) {
		ExportFile f = new ExportFile();
		f.setFileName(nome);
		f.setFileLocation("data:application/pdf;base64,AAAA");
		f.setFileTypeIdentification("persona");
		f.setIdeiaCodigo(ideiaCodigo);
		f.setCreated(new Date());
		exportFileDAO.saveExportedFile(f);
	}

	@Test
	public void getFiles_retornaSoOsDaIdeiaEEmOrdemAlfabetica() { // TK-HQL
		novoArquivo("Zebra.pdf", 1L);
		novoArquivo("Abelha.pdf", 1L);
		novoArquivo("Outra Ideia.pdf", 2L);

		List<ExportFile> resultado = exportFileDAO.getFiles(1L);

		assertEquals(2, resultado.size());
		assertEquals("Abelha.pdf", resultado.get(0).getFileName());
		assertEquals("Zebra.pdf", resultado.get(1).getFileName());
	}

	@Test
	public void getFiles_ideiaSemArquivos_retornaVazio() {
		novoArquivo("Arquivo.pdf", 1L);

		assertEquals(0, exportFileDAO.getFiles(2L).size());
	}

	@Test
	public void saveExportedFile_persisteConteudoLongo() { // E1: columnDefinition=longtext
		StringBuilder base64Longo = new StringBuilder("data:application/pdf;base64,");
		for (int i = 0; i < 5000; i++) {
			base64Longo.append('A');
		}
		ExportFile f = new ExportFile();
		f.setFileName("Grande.pdf");
		f.setFileLocation(base64Longo.toString());
		f.setFileTypeIdentification("persona");
		f.setIdeiaCodigo(1L);
		f.setCreated(new Date());

		exportFileDAO.saveExportedFile(f);

		ExportFile recarregado = exportFileDAO.getFiles(1L).get(0);
		assertEquals(base64Longo.length(), recarregado.getFileLocation().length());
	}
}
