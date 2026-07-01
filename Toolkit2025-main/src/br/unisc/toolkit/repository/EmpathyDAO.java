package br.unisc.toolkit.repository;

import java.util.List;

import br.unisc.toolkit.entity.Empathy;

public interface EmpathyDAO {

	public void saveEmpathyAttribute(Empathy theEmpathy);

	public List<Empathy> getAttributes(int theId, String attribute, Long ideiaCodigo);

	public void deleteAttribute(int attributeId, Long ideiaCodigo);

}
