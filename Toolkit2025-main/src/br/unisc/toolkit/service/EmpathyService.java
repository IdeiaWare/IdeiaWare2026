package br.unisc.toolkit.service;

import java.util.List;

import br.unisc.toolkit.entity.Empathy;

public interface EmpathyService {

	public void saveEmpathyAttribute(Empathy theEmpathy);

	public List<Empathy> getAttributes(int theId, String attribute, Long ideiaCodigo);

	public void deleteAttribute(int attributeId, Long ideiaCodigo);

}
