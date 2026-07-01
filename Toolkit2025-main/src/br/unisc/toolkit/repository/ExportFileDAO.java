package br.unisc.toolkit.repository;

import java.util.List;

import br.unisc.toolkit.entity.ExportFile;

public interface ExportFileDAO {

	public void saveExportedFile(ExportFile file);

	public List<ExportFile> getFiles(Long ideiaCodigo);

}
