document.write(unescape("%3Cscript src='js/Bibliotecas/jspdf.js' type='text/javascript'%3E%3C/script%3E"));

function geraPDF(stage) {
  var confirmation = confirm("Ao exportar o PDF, seu Storytelling não poderá mais ser editado. Tem certeza disso?");

  if (!confirmation) return;

  $("#overlay").attr('style', 'display: block !important');

  // STM-11: Stage.toDataURL() e ASSINCRONO no Konva 1.6.5 (uso sincrono retornava undefined).
  stage.toDataURL({
    pixelRatio: 1,
    callback: function (dataURL) {
      // STM-24: desenha sobre canvas BRANCO + reexporta em JPEG (PNG cru estourava o max_allowed_packet do MySQL).
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
        // este jsPDF (IdeiaWare) so tem .width/.height, nao getWidth() (ver CAN-16)
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
        reader.onloadend = function () {
          salvaPDF(reader.result);
        };
      };
      // TK-38: img.onerror (antes so tratava sucesso -- falha deixava o overlay travado pra sempre).
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
      // NAV-FINALIZE/STR-12: pagina de sucesso (URL relativa), padronizado com Canvas/Caixa.
      window.location.href = "storytelling-finalizado.jsp";
    },
    error: function () {
      $("#overlay").attr('style', 'display: none !important');
      alert('Erro ao exportar o PDF :(');
    }
  });
}

function salvaAudio(blob) {
  if (!blob) return;
  $("#overlay").attr('style', 'display: block !important');
  var reader = new window.FileReader();
  reader.readAsDataURL(blob);
  reader.onloadend = function () {
    $.ajax({
      type: 'POST',
      url: "SalvarAudioServlet",
      contentType: false,
      processData: false,
      data: reader.result,
      success: function () {
        $("#overlay").attr('style', 'display: none !important');
        alert('Áudio salvo com sucesso!');
      },
      error: function () {
        $("#overlay").attr('style', 'display: none !important');
        alert('Erro ao salvar o áudio :(');
      }
    });
  };
}
