package edu.unisc.lic.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import edu.unisc.lic.classes.Data;
import edu.unisc.lic.classes.StatusIdeia;
import edu.unisc.lic.domain.Canvaexport;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.Usuario;

// TEST-04: CanvaexportDAO nunca teve teste; Canvaexport.ideia e @OneToOne, cada teste usa Ideia nova.
public class CanvaexportDAOTest {

	private final CanvaexportDAO canvaexportDAO = new CanvaexportDAO();
	private final IdeiaDAO ideiaDAO = new IdeiaDAO();
	private final UsuarioDAO usuarioDAO = new UsuarioDAO();

	private Ideia novaIdeiaSalva() {
		Usuario autor = new Usuario("Autor Export", "autor_exp_" + System.nanoTime(), "s", "usr", "autor_exp_" + System.nanoTime() + "@x.com");
		usuarioDAO.salvar(autor);

		Ideia ideia = new Ideia(autor, "Ideia com Export", "Descricao", StatusIdeia.CANVAS, StatusIdeia.GRUPO_ABERTO);
		ideia.setDtCriacao();
		ideiaDAO.salvar(ideia);
		return ideia;
	}

	@Test
	public void salvarEBuscar_preservaArquivoEData() {
		Ideia ideia = novaIdeiaSalva();
		Canvaexport ce = new Canvaexport(ideia, "base64-fake-pdf-content", Data.horaAtual());
		canvaexportDAO.salvar(ce);

		assertNotNull("codigo gerado ao salvar", ce.getCodigo());

		Canvaexport carregado = canvaexportDAO.buscar(ce.getCodigo());
		assertNotNull(carregado);
		assertEquals("base64-fake-pdf-content", carregado.getFile());
		assertNotNull(carregado.getDate());
		assertEquals(ideia.getCodigo(), carregado.getIdeia().getCodigo());
	}

	@Test
	public void listarParametro_filtraPelaIdeia() {
		Ideia ideiaComExport = novaIdeiaSalva();
		Ideia ideiaSemExport = novaIdeiaSalva();

		canvaexportDAO.salvar(new Canvaexport(ideiaComExport, "conteudo", Data.horaAtual()));

		Canvaexport filtro = new Canvaexport();
		filtro.setIdeia(ideiaComExport);
		List<Canvaexport> resultado = canvaexportDAO.listarParametro(filtro);

		assertEquals(1, resultado.size());
		assertEquals(ideiaComExport.getCodigo(), resultado.get(0).getIdeia().getCodigo());

		Canvaexport filtroVazio = new Canvaexport();
		filtroVazio.setIdeia(ideiaSemExport);
		assertTrue(canvaexportDAO.listarParametro(filtroVazio).isEmpty());
	}

	@Test
	public void excluir_removeORegistro() {
		Ideia ideia = novaIdeiaSalva();
		Canvaexport ce = new Canvaexport(ideia, "conteudo", Data.horaAtual());
		canvaexportDAO.salvar(ce);
		Long codigo = ce.getCodigo();

		canvaexportDAO.excluir(ce);

		assertEquals(null, canvaexportDAO.buscar(codigo));
	}
}
