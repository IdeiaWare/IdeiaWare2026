package edu.unisc.lic.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Date;
import java.util.List;

import org.junit.Test;

import edu.unisc.lic.classes.StatusIdeia;
import edu.unisc.lic.domain.ColaboracaoIdeia;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.Usuario;

// TEST-04: reativado do scratch @Ignore original (sem assert*, hits em IDs fixos de MySQL real) -- agora roda contra H2.
public class ColaboracaoIdeiaDAOTest {

	private final ColaboracaoIdeiaDAO colaboracaoIdeiaDAO = new ColaboracaoIdeiaDAO();
	private final IdeiaDAO ideiaDAO = new IdeiaDAO();
	private final UsuarioDAO usuarioDAO = new UsuarioDAO();

	private Ideia novaIdeiaSalva() {
		Usuario autor = new Usuario("Autor Colab", "autor_colab_" + System.nanoTime(), "s", "usr", "autor_colab_" + System.nanoTime() + "@x.com");
		usuarioDAO.salvar(autor);

		Ideia ideia = new Ideia(autor, "Ideia Colab", "Descricao", StatusIdeia.PENDENTE, StatusIdeia.GRUPO_ABERTO);
		ideia.setDtCriacao();
		ideiaDAO.salvar(ideia);
		return ideia;
	}

	@Test
	public void salvarEListarParametro_retornaOrdenadoPorCodigoAsc() {
		Ideia ideia = novaIdeiaSalva();
		Usuario colaborador = ideia.getUsuario();

		ColaboracaoIdeia c1 = new ColaboracaoIdeia(ideia, colaborador, new Date(1000), "Primeira versao");
		ColaboracaoIdeia c2 = new ColaboracaoIdeia(ideia, colaborador, new Date(2000), "Segunda versao");
		colaboracaoIdeiaDAO.salvar(c1);
		colaboracaoIdeiaDAO.salvar(c2);

		ColaboracaoIdeia filtro = new ColaboracaoIdeia();
		filtro.setIdeia(ideia);
		List<ColaboracaoIdeia> resultado = colaboracaoIdeiaDAO.listarParametro(filtro);

		assertEquals(2, resultado.size());
		assertEquals("Primeira versao", resultado.get(0).getDescricaoIdeiaAtual());
		assertEquals("Segunda versao", resultado.get(1).getDescricaoIdeiaAtual());
	}

	@Test
	public void ultimaColab_retornaAMaisRecentePorData() {
		Ideia ideia = novaIdeiaSalva();
		Usuario colaborador = ideia.getUsuario();

		colaboracaoIdeiaDAO.salvar(new ColaboracaoIdeia(ideia, colaborador, new Date(1000), "Antiga"));
		colaboracaoIdeiaDAO.salvar(new ColaboracaoIdeia(ideia, colaborador, new Date(9000), "Mais recente"));
		colaboracaoIdeiaDAO.salvar(new ColaboracaoIdeia(ideia, colaborador, new Date(5000), "Meio termo"));

		ColaboracaoIdeia filtro = new ColaboracaoIdeia();
		filtro.setIdeia(ideia);
		ColaboracaoIdeia ultima = colaboracaoIdeiaDAO.ultimaColab(filtro);

		assertNotNull(ultima);
		assertEquals("Mais recente", ultima.getDescricaoIdeiaAtual());
	}

	@Test
	public void ultimaColab_semColaboracoes_retornaNull() {
		Ideia ideia = novaIdeiaSalva();

		ColaboracaoIdeia filtro = new ColaboracaoIdeia();
		filtro.setIdeia(ideia);

		assertNull(colaboracaoIdeiaDAO.ultimaColab(filtro));
	}

	@Test
	public void quantidadeTotal_contaSoAsColaboracoesDaIdeiaFiltrada() {
		// GT-12: renomeado de quantidadeMes -- nunca filtrou por data, so por ideia.
		Ideia ideiaA = novaIdeiaSalva();
		Ideia ideiaB = novaIdeiaSalva();

		colaboracaoIdeiaDAO.salvar(new ColaboracaoIdeia(ideiaA, ideiaA.getUsuario(), new Date(1000), "A1"));
		colaboracaoIdeiaDAO.salvar(new ColaboracaoIdeia(ideiaA, ideiaA.getUsuario(), new Date(2000), "A2"));
		colaboracaoIdeiaDAO.salvar(new ColaboracaoIdeia(ideiaB, ideiaB.getUsuario(), new Date(1000), "B1"));

		ColaboracaoIdeia filtro = new ColaboracaoIdeia();
		filtro.setIdeia(ideiaA);

		assertEquals(2, colaboracaoIdeiaDAO.quantidadeTotal(filtro));
	}
}
