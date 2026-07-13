package edu.unisc.lic.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import edu.unisc.lic.classes.StatusIdeia;
import edu.unisc.lic.domain.ExportFile;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.Usuario;

// TEST-04: reativado a partir do scratch @Ignore original.
public class ExportFileDAOTest {

	private static final String TIPO_POV = "pov";
	private static final String TIPO_PERSONA = "persona";

	private final ExportFileDAO exportFileDAO = new ExportFileDAO();
	private final IdeiaDAO ideiaDAO = new IdeiaDAO();
	private final UsuarioDAO usuarioDAO = new UsuarioDAO();

	private Ideia novaIdeiaSalva() {
		Usuario autor = new Usuario("Autor Export", "autor_ef_" + System.nanoTime(), "s", "usr", "autor_ef_" + System.nanoTime() + "@x.com");
		usuarioDAO.salvar(autor);

		Ideia ideia = new Ideia(autor, "Ideia Export", "Descricao", StatusIdeia.CAIXA_FERRAMENTAS, StatusIdeia.GRUPO_ABERTO);
		ideia.setDtCriacao();
		ideiaDAO.salvar(ideia);
		return ideia;
	}

	@Test
	public void salvarEBuscar_preservaCaminhoETipo() {
		Ideia ideia = novaIdeiaSalva();
		ExportFile ef = new ExportFile(ideia, "base64-fake-pdf");
		ef.setFileTypeIdentification(TIPO_POV);
		exportFileDAO.salvar(ef);

		assertNotNull("id gerado ao salvar", ef.getId());

		ExportFile carregado = exportFileDAO.buscar(ef.getId());
		assertNotNull(carregado);
		assertEquals("base64-fake-pdf", carregado.getFileLocation());
		assertEquals(TIPO_POV, carregado.getFileTypeIdentification());
		assertEquals(ideia.getCodigo(), carregado.getIdeia().getCodigo());
	}

	@Test
	public void listarParametro_filtraPorIdeiaETipo() {
		Ideia ideia = novaIdeiaSalva();

		ExportFile pov = new ExportFile(ideia, "conteudo-pov");
		pov.setFileTypeIdentification(TIPO_POV);
		exportFileDAO.salvar(pov);

		ExportFile persona = new ExportFile(ideia, "conteudo-persona");
		persona.setFileTypeIdentification(TIPO_PERSONA);
		exportFileDAO.salvar(persona);

		ExportFile filtro = new ExportFile();
		filtro.setIdeia(ideia);
		filtro.setFileTypeIdentification(TIPO_POV);

		List<ExportFile> resultado = exportFileDAO.listarParametro(filtro);

		assertEquals(1, resultado.size());
		assertEquals(TIPO_POV, resultado.get(0).getFileTypeIdentification());
	}

	@Test
	public void listarParametro_semTipoInformado_retornaTodosDaIdeia() {
		Ideia ideia = novaIdeiaSalva();

		ExportFile pov = new ExportFile(ideia, "conteudo-pov");
		pov.setFileTypeIdentification(TIPO_POV);
		exportFileDAO.salvar(pov);

		ExportFile persona = new ExportFile(ideia, "conteudo-persona");
		persona.setFileTypeIdentification(TIPO_PERSONA);
		exportFileDAO.salvar(persona);

		ExportFile filtro = new ExportFile();
		filtro.setIdeia(ideia);

		List<ExportFile> resultado = exportFileDAO.listarParametro(filtro);

		assertEquals(2, resultado.size());
		assertTrue(resultado.stream().allMatch(e -> e.getIdeia().getCodigo().equals(ideia.getCodigo())));
	}
}
