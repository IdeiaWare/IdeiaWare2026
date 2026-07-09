package br.unisc.toolkit.repository;

import java.util.List;

import br.unisc.toolkit.entity.PersonaPointOfView;

public interface PersonaPointOfViewDAO {

	public void savePersonaPOV(PersonaPointOfView personaPOV);

	public void removePOVIdFromAuxiliarTable(int povID, Long ideiaCodigo);

}
