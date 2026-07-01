package br.unisc.toolkit.service;

import java.util.List;

import br.unisc.toolkit.entity.ExportFile;

public interface ExportFileService {

	public void saveFile(ExportFile file);

	public List<ExportFile> getFiles(Long ideiaCodigo);

}
