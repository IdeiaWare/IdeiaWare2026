package br.unisc.toolkit.repository;

import java.util.List;

import br.unisc.toolkit.entity.Persona;

public interface PersonaDAO {

	public List<Persona> getPersonas(Long ideiaCodigo);

	public void savePersona(Persona thePersona);

	public void deletePersona(int theId, Long ideiaCodigo);

	public Persona getPersona(int theId, Long ideiaCodigo);

}
