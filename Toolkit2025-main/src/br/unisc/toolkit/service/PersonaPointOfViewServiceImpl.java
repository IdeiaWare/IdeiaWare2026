package br.unisc.toolkit.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.unisc.toolkit.entity.Persona;
import br.unisc.toolkit.entity.PersonaPointOfView;
import br.unisc.toolkit.repository.PersonaPointOfViewDAO;

@Service
public class PersonaPointOfViewServiceImpl implements PersonaPointOfViewService {

	@Autowired
	private PersonaPointOfViewDAO personaPointOfViewDAO;
	
	@Override
	@Transactional
	public void savePersonaPOV(PersonaPointOfView personaPOV) {
		
		personaPointOfViewDAO.savePersonaPOV(personaPOV);
		
	}

	@Override
	@Transactional
	public void removePOVIdFromAuxiliarTable(int povID) {
		personaPointOfViewDAO.removePOVIdFromAuxiliarTable(povID);
		
	}

}
