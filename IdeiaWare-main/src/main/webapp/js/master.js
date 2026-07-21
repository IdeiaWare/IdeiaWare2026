function deleteFile(id){
    var confirmDelete = confirm("Você tem certeza que deseja deletar este item?");
    
    if (confirmDelete){
        $.ajax({
          type: 'POST',
          url: "DeletarExportedFileServlet",
          contentType: 'application/json',
          data: id.toString(),
          success: function () {
            // TK-32: seletor com "]" faltando, linha não sumia da lista.
            $('.modal.open i[data-id="'+ id +'"]').closest('tr').remove();

            if ($('.modal.open i').length === 0)
                $('.modal.open #corpo').html("Não há arquivos exportados.");
          },
          error: function () {
            alert("Erro ao deletar o arquivo!");
          }
        });
    }
}

function deleteCanvaExport(codigo){
    var confirmDelete = confirm("Você tem certeza que deseja deletar este item?");

    if (confirmDelete){
        $.ajax({
          type: 'POST',
          url: "DeletarCanvaexportServlet",
          contentType: 'application/json',
          data: codigo.toString(),
          success: function () {
            // TK-32: mesmo bug de copy-paste do deleteFile().
            $('.modal.open i[data-id="'+ codigo +'"]').closest('tr').remove();

            if ($('.modal.open i').length === 0)
                $('.modal.open #corpo').html("Nenhum Canva foi exportado.");
          },
          error: function () {
            alert("Erro ao deletar o Canva exportado!");
          }
        });
    }
}
