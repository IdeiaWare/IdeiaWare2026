package br.unisc.toolkit.service;

import java.util.List;

import br.unisc.toolkit.entity.PointOfView;

public interface PointOfViewService {

	public List<Object> getPointOfViews(Long ideiaCodigo);
	public List<Object> getSpecificPointOfView(int theId, Long ideiaCodigo);
	public boolean povPertenceAIdeia(int povId, Long ideiaCodigo);
	public void savePOV(PointOfView thePOV);
	public void deletePointOfView(int theId, Long ideiaCodigo);

	// TK-TXN: save+reassociar personas NUMA UNICA transacao (antes, varias @Transactional separadas).
	public void criarComPersonas(PointOfView thePOV);
	public void atualizarComPersonas(PointOfView thePOV);
}
