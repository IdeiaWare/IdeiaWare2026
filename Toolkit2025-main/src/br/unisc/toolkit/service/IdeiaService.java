package br.unisc.toolkit.service;

import br.unisc.toolkit.entity.Ideia;

public interface IdeiaService {

	public void finalize(Ideia theIdeia);
	
	public Ideia getIdeia(Long ideiaCodigo);

}
