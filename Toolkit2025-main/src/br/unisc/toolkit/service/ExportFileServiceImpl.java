package br.unisc.toolkit.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.unisc.toolkit.entity.ExportFile;
import br.unisc.toolkit.repository.ExportFileDAO;

@Service
public class ExportFileServiceImpl implements ExportFileService {

	@Autowired
	private ExportFileDAO exportFileDAO;
	
	@Override
	@Transactional
	public void saveFile(ExportFile file) {
		exportFileDAO.saveExportedFile(file);		
	}

	@Override
	@Transactional
	public List<ExportFile> getFiles(Long ideiaCodigo) {
		return exportFileDAO.getFiles(ideiaCodigo);
	}

}
