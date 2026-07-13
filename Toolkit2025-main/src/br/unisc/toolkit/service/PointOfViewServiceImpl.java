package br.unisc.toolkit.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.unisc.toolkit.entity.PersonaPointOfView;
import br.unisc.toolkit.entity.PointOfView;
import br.unisc.toolkit.entity.PointOfViewInfo;
import br.unisc.toolkit.repository.PointOfViewDAO;

@Service
public class PointOfViewServiceImpl implements PointOfViewService {

	@Autowired
	private PointOfViewDAO povDAO;

	@Autowired
	private PersonaPointOfViewService personaPointOfViewService;

	@Autowired
	private PersonaService personaService;

	@Override
	@Transactional
	public List<Object> getPointOfViews(Long ideiaCodigo) {
		return povDAO.getPointOfViews(ideiaCodigo);
	}
	
	@Override
	@Transactional
	public List<Object> getSpecificPointOfView(int theId, Long ideiaCodigo) {
		return povDAO.getSpecificPointOfView(theId, ideiaCodigo);
	}
	
	@Override
	@Transactional
	public boolean povPertenceAIdeia(int povId, Long ideiaCodigo) {
		return povDAO.povPertenceAIdeia(povId, ideiaCodigo);
	}

	@Override
	@Transactional
	public void savePOV(PointOfView thePOV) {
		povDAO.savePOV(thePOV);

	}

	@Override
	@Transactional
	public void deletePointOfView(int theId, Long ideiaCodigo) {
		povDAO.deletePointOfView(theId, ideiaCodigo);

	}

	@Override
	@Transactional
	public void criarComPersonas(PointOfView thePOV) {
		povDAO.savePOV(thePOV);
		associarPersonas(thePOV);
	}

	@Override
	@Transactional
	public void atualizarComPersonas(PointOfView thePOV) {
		personaPointOfViewService.removePOVIdFromAuxiliarTable(thePOV.getId(), thePOV.getIdeiaCodigo());
		povDAO.savePOV(thePOV);
		associarPersonas(thePOV);
	}

	// TK-23: so associa personas que realmente pertencem a esta ideia (persona_id e auto-increment GLOBAL).
	private void associarPersonas(PointOfView thePOV) {
		for (int id : thePOV.getPersonasId()) {
			if (personaService.getPersona(id, thePOV.getIdeiaCodigo()) == null) {
				continue;
			}
			PersonaPointOfView personaPOV = new PersonaPointOfView();
			personaPOV.setPersonaID(id);
			personaPOV.setPointOfViewID(thePOV.getId());
			personaPOV.setIdeiaCodigo(thePOV.getIdeiaCodigo());
			personaPointOfViewService.savePersonaPOV(personaPOV);
		}
	}
}
