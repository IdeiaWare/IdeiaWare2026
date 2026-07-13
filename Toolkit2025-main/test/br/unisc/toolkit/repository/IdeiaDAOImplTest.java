package br.unisc.toolkit.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.hibernate.SessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import br.unisc.toolkit.entity.Ideia;
import br.unisc.toolkit.entity.Usuario;

// TEST-03/04: IdeiaDAOImpl contra H2 -- trava TK-02 (uniqueResult null-safe) e o fluxo de finalize().
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext-test.xml")
@Transactional
public class IdeiaDAOImplTest {

	@Autowired
	private IdeiaDAO ideiaDAO;

	@Autowired
	private SessionFactory sessionFactory;

	private Usuario novoUsuario() {
		Usuario u = new Usuario("Autor", "autor" + System.nanoTime(), "hash", "usr");
		sessionFactory.getCurrentSession().save(u);
		return u;
	}

	private Ideia novaIdeia(String status) {
		Ideia ideia = new Ideia();
		ideia.setUsuario(novoUsuario());
		ideia.setTitulo("Ideia de teste");
		ideia.setDescricao("Descricao");
		ideia.setStatus(status);
		ideia.setStatusGrupo("AB");
		ideia.setDtCriacao();
		sessionFactory.getCurrentSession().save(ideia);
		return ideia;
	}

	@Test
	public void getIdeia_idExistente_retornaIdeia() {
		Ideia salva = novaIdeia("CF");

		Ideia recarregada = ideiaDAO.getIdeia(salva.getCodigo());

		assertEquals("Ideia de teste", recarregada.getTitulo());
		assertEquals("CF", recarregada.getStatus());
	}

	@Test
	public void getIdeia_idInexistente_retornaNull() { // TK-02: uniqueResult, nao getSingleResult
		assertNull(ideiaDAO.getIdeia(999999L));
	}

	@Test
	public void finalize_avancaStatusParaCV() {
		Ideia salva = novaIdeia("CF");

		ideiaDAO.finalize(salva);

		assertEquals("CV", ideiaDAO.getIdeia(salva.getCodigo()).getStatus());
	}
}
