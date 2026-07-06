package edu.unisc.lic.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import edu.unisc.lic.classes.StatusIdeia;
import edu.unisc.lic.domain.Canva;
import edu.unisc.lic.domain.Ideia;
import edu.unisc.lic.domain.Usuario;

/**
 * TEST-04: CanvaDAO nunca teve nenhum arquivo de teste (nem @Ignore). E o DAO do
 * modulo mais complexo do LIC (Canvas de 9 blocos), sem rede de seguranca nenhuma
 * antes deste teste.
 */
public class CanvaDAOTest {

	private static final String COR_AMARELA = "ffeb3b";
	private static final String ATTR_RECURSO = "recurso";

	private final CanvaDAO canvaDAO = new CanvaDAO();
	private final IdeiaDAO ideiaDAO = new IdeiaDAO();
	private final UsuarioDAO usuarioDAO = new UsuarioDAO();

	private Ideia novaIdeiaSalva() {
		Usuario autor = new Usuario("Autor Canva", "autor_canva_" + System.nanoTime(), "s", "usr", "autor_canva_" + System.nanoTime() + "@x.com");
		usuarioDAO.salvar(autor);

		Ideia ideia = new Ideia(autor, "Ideia Canva", "Descricao", StatusIdeia.CANVAS, StatusIdeia.GRUPO_ABERTO);
		ideia.setDtCriacao();
		ideiaDAO.salvar(ideia);
		return ideia;
	}

	@Test
	public void salvarEBuscar_preservaTextoCorEAttribute() {
		Ideia ideia = novaIdeiaSalva();
		Canva c = new Canva(ideia, "Parceiro chave X", COR_AMARELA, "parceria");
		canvaDAO.salvar(c);

		assertNotNull("codigo gerado ao salvar", c.getCodigo());

		Canva carregado = canvaDAO.buscar(c.getCodigo());
		assertNotNull(carregado);
		assertEquals("Parceiro chave X", carregado.getText());
		assertEquals(COR_AMARELA, carregado.getColor());
		assertEquals("parceria", carregado.getAttribute());
		assertEquals(ideia.getCodigo(), carregado.getIdeia().getCodigo());
	}

	@Test
	public void listarParametro_filtraSoPelaIdeiaDaSessao() {
		Ideia ideiaA = novaIdeiaSalva();
		Ideia ideiaB = novaIdeiaSalva();

		canvaDAO.salvar(new Canva(ideiaA, "Post-it A1", COR_AMARELA, ATTR_RECURSO));
		canvaDAO.salvar(new Canva(ideiaA, "Post-it A2", COR_AMARELA, ATTR_RECURSO));
		canvaDAO.salvar(new Canva(ideiaB, "Post-it B1", COR_AMARELA, ATTR_RECURSO));

		Canva filtro = new Canva();
		filtro.setIdeia(ideiaA);
		List<Canva> resultado = canvaDAO.listarParametro(filtro);

		assertEquals(2, resultado.size());
		for (Canva c : resultado) {
			assertEquals(ideiaA.getCodigo(), c.getIdeia().getCodigo());
		}
	}

	@Test
	public void listarCanvaElement_filtraPorIdeiaEAttributeJuntos() {
		Ideia ideia = novaIdeiaSalva();

		canvaDAO.salvar(new Canva(ideia, "Recurso 1", COR_AMARELA, ATTR_RECURSO));
		canvaDAO.salvar(new Canva(ideia, "Atividade 1", COR_AMARELA, "atividade"));

		Canva filtro = new Canva();
		filtro.setIdeia(ideia);
		List<Canva> resultado = canvaDAO.listarCanvaElement(filtro, ATTR_RECURSO);

		assertEquals(1, resultado.size());
		assertTrue(resultado.get(0).getText().equals("Recurso 1"));
		assertEquals(ATTR_RECURSO, resultado.get(0).getAttribute());
	}

	@Test
	public void excluir_removeORegistro() {
		Ideia ideia = novaIdeiaSalva();
		Canva c = new Canva(ideia, "Vai ser apagado", COR_AMARELA, ATTR_RECURSO);
		canvaDAO.salvar(c);
		Long codigo = c.getCodigo();

		canvaDAO.excluir(c);

		assertEquals(null, canvaDAO.buscar(codigo));
	}
}
