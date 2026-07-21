document.write(unescape("%3Cscript src='js/Bibliotecas/jspdf.js' type='text/javascript'%3E%3C/script%3E"));

function geraPDF(stage) {
  // UX-STORYTELLING-HEADER-ICONE: confirm(), finalizar o Storytelling gera o PDF e encerra a edição (irreversível).
  var confirmation = confirm("Finalizar o Storytelling gera o PDF final e encerra a edição. Esta ação não pode ser desfeita. Confirmar?");

  if (!confirmation) return;

  $("#overlay").attr('style', 'display: block !important');

  // STM-11: Stage.toDataURL() é assíncrono no Konva 1.6.5.
  stage.toDataURL({
    pixelRatio: 1,
    callback: function (dataURL) {
      // STM-24: reexporta em JPEG sobre canvas branco, PNG estourava o pacote do MySQL.
      var img = new Image();
      img.onload = function () {
        var canvas = document.createElement('canvas');
        canvas.width  = img.width;
        canvas.height = img.height;
        var ctx = canvas.getContext('2d');
        ctx.fillStyle = '#ffffff';
        ctx.fillRect(0, 0, canvas.width, canvas.height);
        ctx.drawImage(img, 0, 0);

        var jpeg = canvas.toDataURL('image/jpeg', 0.8);

        var pdf = new jsPDF("landscape", "pt", "a4");
        // CAN-16: este jsPDF só tem .width/.height, não getWidth().
        var pageWidth  = pdf.internal.pageSize.getWidth  ? pdf.internal.pageSize.getWidth()  : pdf.internal.pageSize.width;
        var pageHeight = pdf.internal.pageSize.getHeight ? pdf.internal.pageSize.getHeight() : pdf.internal.pageSize.height;
        var imgW = pageWidth;
        var imgH = canvas.height * (pageWidth / canvas.width);

        if (imgH <= pageHeight) {
          pdf.addImage(jpeg, 'JPEG', 0, 0, imgW, imgH);
        } else {
          var leftHeight = imgH;
          var position = 0;
          while (leftHeight > 0) {
            pdf.addImage(jpeg, 'JPEG', 0, position, imgW, imgH);
            leftHeight -= pageHeight;
            position  -= pageHeight;
            if (leftHeight > 0) {
              pdf.addPage();
            }
          }
        }

        var blob = pdf.output("blob");
        var reader = new window.FileReader();
        reader.readAsDataURL(blob);
        // TK-38: checa reader.error antes de usar reader.result.
        reader.onloadend = function () {
          if (reader.error) {
            $("#overlay").attr('style', 'display: none !important');
            alert('Erro ao gerar o PDF :(');
            return;
          }
          salvaPDF(reader.result);
        };
      };
      // TK-38: img.onerror, senão falha deixava o overlay travado.
      img.onerror = function () {
        $("#overlay").attr('style', 'display: none !important');
        alert('Erro ao gerar o PDF :(');
      };
      img.src = dataURL;
    }
  });
}

function salvaPDF(base64data) {
  $.ajax({
    type: 'POST',
    url: "ExportaStoryServlet",
    contentType: false,
    processData: false,
    data: base64data,
    success: function () {
      // NAV-FINALIZE/STR-12: URL relativa, padronizado com Canvas/Caixa.
      window.location.href = "storytelling-finalizado.jsp";
    },
    error: function (xhr) {
      $("#overlay").attr('style', 'display: none !important');
      tratarErroEtapaFinalizada(xhr, 'Erro ao exportar o PDF :(');
    }
  });
}

// UX-STORYTELLING-SALVAR-AUDIO-JUNTO: "silencioso" pula o alert quando chamado junto do Salvar do cabecalho.
// UX-STORYTELLING-AUDIO-RECARREGA: "onSucesso" roda apos salvar (recarrega a lista do servidor).
function salvaAudio(blob, silencioso, onSucesso) {
  if (!blob) return;
  $("#overlay").attr('style', 'display: block !important');
  var reader = new window.FileReader();
  reader.readAsDataURL(blob);
  // TK-38: checa reader.error antes de usar reader.result.
  reader.onloadend = function () {
    if (reader.error) {
      $("#overlay").attr('style', 'display: none !important');
      alert('Erro ao salvar o áudio :(');
      return;
    }
    $.ajax({
      type: 'POST',
      url: "SalvarAudioServlet",
      contentType: false,
      processData: false,
      data: reader.result,
      success: function () {
        $("#overlay").attr('style', 'display: none !important');
        if (!silencioso) {
          alert('Áudio salvo com sucesso!');
        }
        if (typeof onSucesso === 'function') {
          onSucesso();
        }
      },
      error: function (xhr) {
        $("#overlay").attr('style', 'display: none !important');
        tratarErroEtapaFinalizada(xhr, 'Erro ao salvar o áudio :(');
      }
    });
  };
}
