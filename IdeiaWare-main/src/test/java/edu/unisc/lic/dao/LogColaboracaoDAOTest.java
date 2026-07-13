package edu.unisc.lic.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Date;
import java.util.List;

import org.junit.Test;

import edu.unisc.lic.classes.StatusIdeia;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.LogColaboracao;
import edu.unisc.lic.domain.Usuario;

// TEST-04: reativado a partir do scratch @Ignore original.
public class LogColaboracaoDAOTest {

	private final LogColaboracaoDAO logColaboracaoDAO = new LogColaboracaoDAO();
	private final IdeiaDAO ideiaDAO = new IdeiaDAO();
	private final UsuarioDAO usuarioDAO = new UsuarioDAO();

	private Ideia novaIdeiaSalva() {
		Usuario autor = new Usuario("Autor Log", "autor_log_" + System.nanoTime(), "s", "usr", "autor_log_" + System.nanoTime() + "@x.com");
		usuarioDAO.salvar(autor);

		Ideia ideia = new Ideia(autor, "Ideia Log", "Descricao", StatusIdeia.EM_DESENVOLVIMENTO, StatusIdeia.GRUPO_ABERTO);
		ideia.setDtCriacao();
		ideiaDAO.salvar(ideia);
		return ideia;
	}

	@Test
	public void salvarEListarParametro_filtraPorIdeia() {
		Ideia ideiaA = novaIdeiaSalva();
		Ideia ideiaB = novaIdeiaSalva();

		logColaboracaoDAO.salvar(new LogColaboracao(ideiaA, ideiaA.getUsuario(), new Date(1000), "Log A"));
		logColaboracaoDAO.salvar(new LogColaboracao(ideiaB, ideiaB.getUsuario(), new Date(1000), "Log B"));

		LogColaboracao filtro = new LogColaboracao();
		filtro.setIdeia(ideiaA);

		List<LogColaboracao> resultado = logColaboracaoDAO.listarParametro(filtro);

		assertEquals(1, resultado.size());
		assertEquals("Log A", resultado.get(0).getDescricao());
	}

	@Test
	public void buscarDescricaoFinal_retornaAMaisRecentePorCodigo() {
		Ideia ideia = novaIdeiaSalva();
		Usuario autor = ideia.getUsuario();

		logColaboracaoDAO.salvar(new LogColaboracao(ideia, autor, new Date(1000), "Versao 1"));
		logColaboracaoDAO.salvar(new LogColaboracao(ideia, autor, new Date(2000), "Versao final"));

		LogColaboracao filtro = new LogColaboracao();
		filtro.setIdeia(ideia);

		LogColaboracao resultado = logColaboracaoDAO.buscarDescricaoFinal(filtro);

		assertNotNull(resultado);
		assertEquals("Versao final", resultado.getDescricao());
	}

	@Test
	public void buscarDescricaoFinal_semLogs_retornaNull() {
		Ideia ideia = novaIdeiaSalva();

		LogColaboracao filtro = new LogColaboracao();
		filtro.setIdeia(ideia);

		assertNull(logColaboracaoDAO.buscarDescricaoFinal(filtro));
	}
}
