package br.unisc.toolkit.service;

import java.util.List;

import br.unisc.toolkit.entity.PersonaPointOfView;

public interface PersonaPointOfViewService {

	public void savePersonaPOV(PersonaPointOfView personaPOV);

	public void removePOVIdFromAuxiliarTable(int povID);

}
