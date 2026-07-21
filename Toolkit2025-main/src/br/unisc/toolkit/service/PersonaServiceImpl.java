package br.unisc.toolkit.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.unisc.toolkit.entity.Persona;

import br.unisc.toolkit.repository.PersonaDAO;

@Service
public class PersonaServiceImpl implements PersonaService {

	@Autowired
	private PersonaDAO personaDAO;
	
	@Override
	@Transactional
	public List<Persona> getPersonas(Long ideiaCodigo) {
		return personaDAO.getPersonas(ideiaCodigo);
	}
	
	@Override
	@Transactional
	public void savePersona(Persona thePersona) {
		
		personaDAO.savePersona(thePersona);
	}

	@Override
	@Transactional
	public void deletePersona(int theId, Long ideiaCodigo) {
		personaDAO.deletePersona(theId, ideiaCodigo);

	}

	@Override
	@Transactional
	public Persona getPersona(int theId, Long ideiaCodigo) {
		return personaDAO.getPersona(theId, ideiaCodigo);
	}
}
