package br.unisc.toolkit.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import br.unisc.toolkit.entity.Persona;
import br.unisc.toolkit.entity.PersonaPointOfView;
import br.unisc.toolkit.entity.PointOfView;
import br.unisc.toolkit.repository.PointOfViewDAO;

// TEST-0X: PointOfViewServiceImpl -- trava o TK-TXN e o TK-23.
public class PointOfViewServiceImplTest {

	private PointOfViewServiceImpl service;
	private PointOfViewDAO povDAO;
	private PersonaPointOfViewService personaPointOfViewService;
	private PersonaService personaService;

	@Before
	public void setup() {
		service = new PointOfViewServiceImpl();
		povDAO = mock(PointOfViewDAO.class);
		personaPointOfViewService = mock(PersonaPointOfViewService.class);
		personaService = mock(PersonaService.class);
		ReflectionTestUtils.setField(service, "povDAO", povDAO);
		ReflectionTestUtils.setField(service, "personaPointOfViewService", personaPointOfViewService);
		ReflectionTestUtils.setField(service, "personaService", personaService);
	}

	private PointOfView povCom(Integer... personasId) {
		PointOfView pov = new PointOfView();
		pov.setId(10);
		pov.setIdeiaCodigo(1L);
		Integer[] ids = personasId;
		pov.setPersonasId(ids);
		return pov;
	}

	@Test
	public void criarComPersonas_salvaPOVEAssociaTodasAsPersonasValidas() {
		when(personaService.getPersona(1, 1L)).thenReturn(new Persona("Joao", 30));
		when(personaService.getPersona(2, 1L)).thenReturn(new Persona("Maria", 25));

		service.criarComPersonas(povCom(1, 2));

		verify(povDAO).savePOV(any(PointOfView.class));
		verify(personaPointOfViewService, times(2)).savePersonaPOV(any(PersonaPointOfView.class));
	}

	@Test
	public void criarComPersonas_pulaPersonaQueNaoPertenceAIdeia() { // TK-23
		when(personaService.getPersona(1, 1L)).thenReturn(new Persona("Joao", 30));
		when(personaService.getPersona(2, 1L)).thenReturn(null); // persona 2 nao e desta ideia (ou nao existe)

		service.criarComPersonas(povCom(1, 2));

		verify(personaPointOfViewService, times(1)).savePersonaPOV(any(PersonaPointOfView.class));
	}

	@Test
	public void atualizarComPersonas_removeVinculosAntigosAntesDeSalvarEReassociar() { // TK-TXN
		when(personaService.getPersona(1, 1L)).thenReturn(new Persona("Joao", 30));

		service.atualizarComPersonas(povCom(1));

		InOrder ordem = Mockito.inOrder(personaPointOfViewService, povDAO);
		ordem.verify(personaPointOfViewService).removePOVIdFromAuxiliarTable(eq(10), eq(1L));
		ordem.verify(povDAO).savePOV(any(PointOfView.class));
		ordem.verify(personaPointOfViewService).savePersonaPOV(any(PersonaPointOfView.class));
	}

	@Test
	public void atualizarComPersonas_pulaPersonaQueNaoPertenceAIdeia() { // TK-23
		when(personaService.getPersona(1, 1L)).thenReturn(null);

		service.atualizarComPersonas(povCom(1));

		verify(personaPointOfViewService, never()).savePersonaPOV(any());
	}
}
