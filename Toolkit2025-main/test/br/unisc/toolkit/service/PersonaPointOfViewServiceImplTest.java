package br.unisc.toolkit.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import br.unisc.toolkit.entity.PersonaPointOfView;
import br.unisc.toolkit.repository.PersonaPointOfViewDAO;

// TEST-0X: PersonaPointOfViewServiceImpl -- trava o contrato de delegacao pro DAO (TK-23).
public class PersonaPointOfViewServiceImplTest {

	private PersonaPointOfViewServiceImpl service;
	private PersonaPointOfViewDAO dao;

	@Before
	public void setup() {
		service = new PersonaPointOfViewServiceImpl();
		dao = mock(PersonaPointOfViewDAO.class);
		ReflectionTestUtils.setField(service, "personaPointOfViewDAO", dao);
	}

	@Test
	public void savePersonaPOV_delegaParaODAO() {
		PersonaPointOfView vinculo = new PersonaPointOfView();

		service.savePersonaPOV(vinculo);

		verify(dao).savePersonaPOV(vinculo);
	}

	@Test
	public void removePOVIdFromAuxiliarTable_delegaComOsMesmosParametros() { // TK-23
		service.removePOVIdFromAuxiliarTable(10, 1L);

		verify(dao).removePOVIdFromAuxiliarTable(10, 1L);
	}
}
