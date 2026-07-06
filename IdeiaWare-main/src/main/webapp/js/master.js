function openFileData(data){
	var blob = b64toBlob(data, 'application/pdf');
	var filePath = window.open(URL.createObjectURL(blob));
}

function b64toBlob(b64Data, contentType) {
	contentType = contentType || '';
	var sliceSize = 512;
	b64Data = b64Data.replace(/^[^,]+,/, '');
	b64Data = b64Data.replace(/\s/g, '');
	var byteCharacters = window.atob(b64Data);
	var byteArrays = [];

	for (var offset = 0; offset < byteCharacters.length; offset += sliceSize) {
	    var slice = byteCharacters.slice(offset, offset + sliceSize);

	    var byteNumbers = new Array(slice.length);
	    for (var i = 0; i < slice.length; i++) {
	        byteNumbers[i] = slice.charCodeAt(i);
	    }

	    var byteArray = new Uint8Array(byteNumbers);

	    byteArrays.push(byteArray);
	}

	var blob = new Blob(byteArrays, {type: contentType});
	return blob;
}

function deleteFile(id){
    var confirmDelete = confirm("Você tem certeza que deseja deletar este item?");
    
    if (confirmDelete){
        $.ajax({
          type: 'POST',
          url: "DeletarExportedFileServlet",
          contentType: 'application/json',
          data: id.toString(),
          success: function () {
            $('.modal.open i[data-id="'+ id +'"').closest('tr').remove();

            if ($('.modal.open i').length === 0)
                $('.modal.open #corpo').html("Não há arquivos exportados.");
          },
          error: function () {
            alert("Erro ao deletar o arquivo!");
          }
        });
    }
}

// Exclui um Canva exportado (Canvaexport) na Retencao. Espelha deleteFile(), mas
// aponta para o DeletarCanvaexportServlet (entidade diferente do ExportFile de
// persona/POV). POST via $.ajax -> o csrf.js injeta o header X-CSRF-Token.
function deleteCanvaExport(codigo){
    var confirmDelete = confirm("Você tem certeza que deseja deletar este item?");

    if (confirmDelete){
        $.ajax({
          type: 'POST',
          url: "DeletarCanvaexportServlet",
          contentType: 'application/json',
          data: codigo.toString(),
          success: function () {
            $('.modal.open i[data-id="'+ codigo +'"').closest('tr').remove();

            if ($('.modal.open i').length === 0)
                $('.modal.open #corpo').html("Nenhum Canva foi exportado.");
          },
          error: function () {
            alert("Erro ao deletar o Canva exportado!");
          }
        });
    }
}
