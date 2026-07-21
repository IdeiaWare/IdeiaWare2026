package br.unisc.toolkit.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.unisc.toolkit.entity.Empathy;
import br.unisc.toolkit.repository.EmpathyDAO;

@Service
public class EmpathyServiceImpl implements EmpathyService {
	
	@Autowired
	private EmpathyDAO empathyDAO;
	
	@Override
	@Transactional
	public List<Empathy> getAttributes(int theId, String attribute, Long ideiaCodigo) {
		return empathyDAO.getAttributes(theId, attribute, ideiaCodigo);
	}
	
	@Override
	@Transactional
	public void saveEmpathyAttribute(Empathy theEmpathy) {
		
		empathyDAO.saveEmpathyAttribute(theEmpathy);
	}

	@Override
	@Transactional
	public void deleteAttribute(int attributeId, Long ideiaCodigo) {
		empathyDAO.deleteAttribute(attributeId, ideiaCodigo);
	}
}
