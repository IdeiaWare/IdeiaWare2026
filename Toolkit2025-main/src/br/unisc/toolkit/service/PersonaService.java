package br.unisc.toolkit.service;

import java.util.List;

import br.unisc.toolkit.entity.Persona;

public interface PersonaService {

	public List<Persona> getPersonas(Long ideiaCodigo);

	public void savePersona(Persona thePersona);

	public void deletePersona(int theId, Long ideiaCodigo);

	public Persona getPersona(int theId, Long ideiaCodigo);

}
