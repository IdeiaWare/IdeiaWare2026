package br.unisc.toolkit.service;

import java.util.List;

import br.unisc.toolkit.entity.PointOfView;

public interface PointOfViewService {

	public List<Object> getPointOfViews(Long ideiaCodigo);
	public List<Object> getSpecificPointOfView(int theId, Long ideiaCodigo);
	public boolean povPertenceAIdeia(int povId, Long ideiaCodigo);
	public void savePOV(PointOfView thePOV);
	public void deletePointOfView(int theId, Long ideiaCodigo);

	// REVISAO 2026-07-08 (varredura Toolkit, achado ALTA): savePOV()+reassociar personas
	// eram chamados pelo controller como varias transacoes SEPARADAS (cada metodo
	// @Transactional isolado) -- falha no meio deixava POV com associacoes parciais/zero,
	// que SOME SILENCIOSAMENTE da listagem (INNER JOIN persona_pov). Os 2 metodos abaixo
	// consolidam save+reassociar (e, na atualizacao, o remove antigo) numa UNICA
	// transacao -- ou tudo commita, ou nada commita.
	public void criarComPersonas(PointOfView thePOV);
	public void atualizarComPersonas(PointOfView thePOV);
}
