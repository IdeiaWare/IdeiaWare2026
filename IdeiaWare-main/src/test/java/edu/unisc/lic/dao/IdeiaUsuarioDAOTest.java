package edu.unisc.lic.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import edu.unisc.lic.classes.StatusIdeia;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.IdeiaUsuario;
import edu.unisc.lic.domain.Usuario;

// TEST-04: reativado do scratch @Ignore original; cobre tambem os 3 metodos do PERF-01, que nunca tiveram teste.
public class IdeiaUsuarioDAOTest {

	private final IdeiaUsuarioDAO ideiaUsuarioDAO = new IdeiaUsuarioDAO();
	private final IdeiaDAO ideiaDAO = new IdeiaDAO();
	private final UsuarioDAO usuarioDAO = new UsuarioDAO();

	private Usuario novoUsuarioSalvo(String nome) {
		Usuario u = new Usuario(nome, "login_iu_" + System.nanoTime(), "s", "usr", nome + "@x.com");
		usuarioDAO.salvar(u);
		return u;
	}

	private Ideia novaIdeiaSalva(Usuario autor, String status) {
		Ideia ideia = new Ideia(autor, "Ideia IU", "Descricao", status, StatusIdeia.GRUPO_ABERTO);
		ideia.setDtCriacao();
		ideiaDAO.salvar(ideia);
		return ideia;
	}

	@Test
	public void salvarEListarParametro_filtraPorUsuarioIdeiaEFlLider() {
		Usuario u = novoUsuarioSalvo("Membro IU");
		Ideia ideia = novaIdeiaSalva(u, StatusIdeia.PENDENTE);

		ideiaUsuarioDAO.salvar(new IdeiaUsuario(u, ideia, "S"));

		IdeiaUsuario filtro = new IdeiaUsuario(u, null, "S");
		List<IdeiaUsuario> resultado = ideiaUsuarioDAO.listarParametro(filtro);

		assertEquals(1, resultado.size());
		assertEquals(u.getCodigo(), resultado.get(0).getUsuario().getCodigo());
		assertEquals("S", resultado.get(0).getFlLider());
	}

	@Test
	public void listarParametro_flLiderN_naoRetornaOsLideres() {
		Usuario u = novoUsuarioSalvo("Membro Nao Lider");
		Ideia ideia = novaIdeiaSalva(u, StatusIdeia.PENDENTE);

		ideiaUsuarioDAO.salvar(new IdeiaUsuario(u, ideia, "S"));

		IdeiaUsuario filtro = new IdeiaUsuario(u, null, "N");
		assertTrue(ideiaUsuarioDAO.listarParametro(filtro).isEmpty());
	}

	@Test
	public void listarTodasIdeiasStorytelling_retornaSoIdeiasNesseStatus() {
		Usuario u = novoUsuarioSalvo("User Story");
		Ideia ideiaStory = novaIdeiaSalva(u, StatusIdeia.STORYTELLING);
		Ideia ideiaPendente = novaIdeiaSalva(u, StatusIdeia.PENDENTE);

		ideiaUsuarioDAO.salvar(new IdeiaUsuario(u, ideiaStory, "S"));
		ideiaUsuarioDAO.salvar(new IdeiaUsuario(u, ideiaPendente, "S"));

		IdeiaUsuario filtro = new IdeiaUsuario(u, null, null);
		List<IdeiaUsuario> resultado = ideiaUsuarioDAO.listarTodasIdeiasStorytelling(filtro);

		assertEquals(1, resultado.size());
		assertEquals(StatusIdeia.STORYTELLING, resultado.get(0).getIdeia().getStatus());
	}

	@Test
	public void listarCaixa_retornaSoIdeiasEmCaixaDeFerramentas() {
		Usuario u = novoUsuarioSalvo("User Caixa");
		Ideia ideiaCaixa = novaIdeiaSalva(u, StatusIdeia.CAIXA_FERRAMENTAS);
		Ideia ideiaValidada = novaIdeiaSalva(u, StatusIdeia.VALIDADA);

		ideiaUsuarioDAO.salvar(new IdeiaUsuario(u, ideiaCaixa, "N"));
		ideiaUsuarioDAO.salvar(new IdeiaUsuario(u, ideiaValidada, "N"));

		IdeiaUsuario filtro = new IdeiaUsuario(u, null, null);
		List<IdeiaUsuario> resultado = ideiaUsuarioDAO.listarCaixa(filtro);

		assertEquals(1, resultado.size());
		assertEquals(StatusIdeia.CAIXA_FERRAMENTAS, resultado.get(0).getIdeia().getStatus());
	}

	@Test
	public void listarIdeiasCanva_retornaSoIdeiasEmCanvasParaTodosOsParticipantes() {
		Usuario lider = novoUsuarioSalvo("Lider Canva");
		Usuario membro = novoUsuarioSalvo("Membro Canva");
		Ideia ideiaCanva = novaIdeiaSalva(lider, StatusIdeia.CANVAS);

		ideiaUsuarioDAO.salvar(new IdeiaUsuario(lider, ideiaCanva, "S"));
		ideiaUsuarioDAO.salvar(new IdeiaUsuario(membro, ideiaCanva, "N"));

		IdeiaUsuario filtroMembro = new IdeiaUsuario(membro, null, null);
		List<IdeiaUsuario> resultado = ideiaUsuarioDAO.listarIdeiasCanva(filtroMembro);

		assertEquals("CAN-PARTICIPANTE: membro nao-lider tambem deve ver a ideia no Canvas", 1, resultado.size());
		assertEquals(StatusIdeia.CANVAS, resultado.get(0).getIdeia().getStatus());
	}

	@Test
	public void fecharGrupoAtomico_naoApagaVinculoQueFoiAprovadoDepoisDaLeitura() {
		// GT-03: simula a corrida sem threads -- snapshot ANTIGO (P) na lista de remocao, banco ja reflete o estado NOVO (A).
		Usuario lider = novoUsuarioSalvo("Lider Race");
		Ideia ideia = novaIdeiaSalva(lider, StatusIdeia.VALIDADA);
		IdeiaUsuario vinculoLider = new IdeiaUsuario(lider, ideia, "S");
		vinculoLider.setFlStatusVinculo(StatusIdeia.VINCULO_APROVADO);
		ideiaUsuarioDAO.salvar(vinculoLider);

		Usuario candidato = novoUsuarioSalvo("Candidato Race");
		IdeiaUsuario vinculoCandidato = new IdeiaUsuario(candidato, ideia, "N");
		vinculoCandidato.setFlStatusVinculo(StatusIdeia.VINCULO_PENDENTE);
		ideiaUsuarioDAO.salvar(vinculoCandidato);

		IdeiaUsuario snapshotAntigo = ideiaUsuarioDAO.buscar(vinculoCandidato.getCodigo());
		assertEquals(StatusIdeia.VINCULO_PENDENTE, snapshotAntigo.getFlStatusVinculo());

		vinculoCandidato.setFlStatusVinculo(StatusIdeia.VINCULO_APROVADO);
		ideiaUsuarioDAO.editar(vinculoCandidato);

		java.util.List<IdeiaUsuario> paraRemover = java.util.Collections.singletonList(snapshotAntigo);
		edu.unisc.lic.domain.LogColaboracao log = new edu.unisc.lic.domain.LogColaboracao(
				ideia, lider, edu.unisc.lic.classes.Data.horaAtual(), "desc inicial");

		ideiaUsuarioDAO.fecharGrupoAtomico(ideia, new java.util.ArrayList<>(), paraRemover, log);

		IdeiaUsuario recarregado = ideiaUsuarioDAO.buscar(vinculoCandidato.getCodigo());
		org.junit.Assert.assertNotNull("vinculo recem-aprovado NAO deve ser apagado pela corrida", recarregado);
		assertEquals(StatusIdeia.VINCULO_APROVADO, recarregado.getFlStatusVinculo());
	}
}
