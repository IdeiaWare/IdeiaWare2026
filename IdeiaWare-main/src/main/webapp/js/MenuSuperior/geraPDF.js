document.write(unescape("%3Cscript src='js/Bibliotecas/jspdf.js' type='text/javascript'%3E%3C/script%3E"));

function geraPDF(stage) {
  var confirmation = confirm("Ao exportar o PDF, seu Storytelling não poderá mais ser editado. Tem certeza disso?");

  if (!confirmation) return;

  $("#overlay").attr('style', 'display: block !important');

  // STM-11: no Konva 1.6.5 o Stage.toDataURL() e ASSINCRONO (compoe as varias
  // layers via callback). O uso sincrono retornava undefined e o PDF saia
  // vazio/com erro. Usar o callback funciona tanto no caso sincrono quanto no
  // assincrono.
  // STM-24: pixelRatio:1 evita que telas retina (devicePixelRatio 2) gerem uma
  // imagem com o dobro/quadruplo de pixels, inflando o base64.
  stage.toDataURL({
    pixelRatio: 1,
    callback: function (dataURL) {
      // STM-24: ANTES o quadro era exportado como PNG sem compressao e jogado
      // direto no PDF (addImage(...,'PNG',0,0)). O base64 do PNG de um quadro
      // inteiro chega a varios MB e estourava o max_allowed_packet do MySQL ->
      // o ExportaStoryServlet dava 500 e o usuario via "Erro ao exportar o PDF".
      // Aqui desenhamos o PNG (transparente) sobre um canvas BRANCO e
      // reexportamos como JPEG comprimido:
      //  - some a transparencia (JPEG nao tem alpha, evita fundo preto);
      //  - o base64 fica MUITO menor, cabendo no pacote do banco;
      //  - a imagem e escalada/paginada para caber na pagina A4 (antes saia
      //    cortada quando o quadro era maior que a pagina).
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
      // UX: exportar ENCERRA o modulo -> vai para a pagina de sucesso (que leva a
      // "Minhas Ideias"), padronizando com Canvas (canvas-finalizado) e Caixa
      // (finalize.jsp). Antes: alert() bloqueante + corte seco para minha-ideia.
      // STR-12: URL relativa — funciona em qualquer contexto de deploy
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
