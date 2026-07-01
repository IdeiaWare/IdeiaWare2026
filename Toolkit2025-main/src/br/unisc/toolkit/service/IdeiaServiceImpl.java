package br.unisc.toolkit.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.unisc.toolkit.entity.Ideia;
import br.unisc.toolkit.entity.Persona;
import br.unisc.toolkit.repository.IdeiaDAO;

@Service
public class IdeiaServiceImpl implements IdeiaService {

	@Autowired
	private IdeiaDAO ideiaDAO;
	
	@Override
	@Transactional
	public void finalize(Ideia theIdeia) {
		ideiaDAO.finalize(theIdeia);
	}
	
	@Override
	@Transactional
	public Ideia getIdeia(Long ideiaCodigo) {
		return ideiaDAO.getIdeia(ideiaCodigo);
	}
}
