package br.unisc.toolkit.repository;

import java.util.List;

import br.unisc.toolkit.entity.PointOfView;

public interface PointOfViewDAO {

	public void savePOV(PointOfView thePOV);

	public List<Object> getPointOfViews(Long ideiaCodigo);

	public List<Object> getSpecificPointOfView(int theId, Long ideiaCodigo);

	public boolean povPertenceAIdeia(int povId, Long ideiaCodigo);

	public void deletePointOfView(int theId, Long ideiaCodigo);

}
