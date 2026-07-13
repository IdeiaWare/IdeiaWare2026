/* global Konva */

function drawImage(imageObj, stage, caracteristicas) {

  // [BRAVE] figura e uma Konva.Image arrastavel direto (Group/Rect/fill/hitFunc tentados
  // antes eram workaround pro Brave bloquear leitura de pixel do canvas -- ver relatorio).
  var elemento = new Konva.Image({
    image: imageObj,
    draggable: true,
    x: caracteristicas.x,
    y: caracteristicas.y,
    width: caracteristicas.largura,
    height: caracteristicas.altura,
    id: caracteristicas.codigo,
    name: 'outro'
  });

  // Resize via DUPLO-CLIQUE (com a borracha desligada).
  elemento.on('dblclick', function () {
    var y = document.getElementById("deletarAlgo");
    if (y.style.display == "") {
      $('#AlturaId').val(this.attrs.height);
      $('#LarguraId').val(this.attrs.width);
      $('#imgaemIdResize').val(this.attrs.id);
      $('#modalResize').modal('open');
    }
  });

  // Borracha: apaga ao clicar com a borracha ligada.
  elemento.on('click.deletar', function () {
    var y = document.getElementById("deletarAlgo");
    if (y.style.display == "block") {
      this.hide();
      this.destroy();
      stage.draw();
      $.ajax({
        type: 'POST',
        url: "DeletarObjServlet",
        contentType: 'application/json',
        data: JSON.stringify(this.attrs.id),
        // TK-29: figura ja some da tela antes do POST -- avisa se o delete falhar no servidor.
        error: function () {
          alert("Erro ao excluir a figura no servidor. Recarregue a página -- ela pode reaparecer.");
        }
      });
    }
  });

  // STM-22: salva a posicao automaticamente ao soltar a figura (sem precisar clicar em "Salvar").
  elemento.on('dragend', function () {
    var a = this.attrs;
    $.ajax({
      type: 'POST',
      url: 'AutoSalvarStoryServlet',
      contentType: 'application/json',
      data: JSON.stringify([{ tipo: 'outro', codigo: a.id, x: a.x, y: a.y, height: a.height, width: a.width }]),
      // TK-29: avisa se o auto-save da posicao falhar (a figura ja mudou na tela).
      error: function () {
        alert("Erro ao salvar a nova posição. Recarregue a página para conferir se a figura ficou no lugar certo.");
      }
    });
  });

  caracteristicas.layer.add(elemento);
  caracteristicas.layer.draw();

}


function drawText(stage, caracteristicas) {

  // STM-09: usa a MESMA layer das imagens (layers de texto empilhadas cobriam os icones e bloqueavam o arrastar).
  var layer = caracteristicas.layer;

  var textNode = new Konva.Text({
    text: caracteristicas.informacaoTexto,
    x: caracteristicas.x,
    y: caracteristicas.y,
    fontSize: caracteristicas.tamanho,
    fontFamily: caracteristicas.fonte,
    fill: caracteristicas.cor,
    draggable: true,
    name: 'texto',
    id: caracteristicas.codigo
  });

  // STM-22: salva a posicao do texto automaticamente ao soltar (igual as figuras).
  textNode.on('dragend', function () {
    var a = this.attrs;
    $.ajax({
      type: 'POST',
      url: 'AutoSalvarStoryServlet',
      contentType: 'application/json',
      data: JSON.stringify([{ tipo: 'texto', codigo: a.id, x: a.x, y: a.y, conteudo: a.text, fonte: a.fontFamily, tamanhoFonte: a.fontSize, cor: a.fill }]),
      // TK-29: mesmo motivo do dragend de imagem acima.
      error: function () {
        alert("Erro ao salvar a nova posição. Recarregue a página para conferir se o texto ficou no lugar certo.");
      }
    });
  });

  // TK-32d: namespace .deletar (era so 'click', inconsistente com o delete de imagem).
  textNode.on('click.deletar', function () {
    var y = document.getElementById("deletarAlgo");
    if (y.style.display == "block") {
      this.hide();
      this.destroy();
      stage.draw();
      $.ajax({
        type: 'POST',
        url: "DeletarObjServlet",
        contentType: 'application/json',
        data: JSON.stringify(this.attrs.id),
        // TK-29: mesmo motivo do delete de imagem acima.
        error: function () {
          alert("Erro ao excluir o texto no servidor. Recarregue a página -- ele pode reaparecer.");
        }
      });
    }
  });

  layer.add(textNode);
  layer.draw();

  textNode.on('dblclick', () => {
    var textPosition = textNode.getAbsolutePosition();
    var stageBox = stage.getContainer().getBoundingClientRect();

    var areaPosition = {
      x: textPosition.x + stageBox.left,
      y: textPosition.y + stageBox.top
    };

    var textarea = document.createElement('textarea');
    document.body.appendChild(textarea);

    textarea.value = textNode.text();
    textarea.style.position = 'absolute';
    textarea.style.top = areaPosition.y + 'px';
    textarea.style.left = areaPosition.x + 'px';
    textarea.style.width = textNode.width();

    textarea.focus();


    textarea.addEventListener('keydown', function (e) {
      // hide on enter
      if (e.keyCode === 13) {
        textNode.text(textarea.value);
        layer.draw();
        document.body.removeChild(textarea);
        // UX-STORYTELLING-TEXTO-SAVE: salva SO este texto (nao o quadro inteiro via salvarProgresso()).
        var a = textNode.attrs;
        $.ajax({
          type: 'POST',
          url: 'AutoSalvarStoryServlet',
          contentType: 'application/json',
          data: JSON.stringify([{ tipo: 'texto', codigo: a.id, x: a.x, y: a.y, conteudo: a.text, fonte: a.fontFamily, tamanhoFonte: a.fontSize, cor: a.fill }]),
          // TK-29: texto ja mudou na tela antes do POST -- avisa se o auto-save falhar.
          error: function () {
            alert("Erro ao salvar o texto editado. Recarregue a página para conferir se a edição ficou salva.");
          }
        });
      }
    });
  });

}

// TK-32c: drawFreeLine() removida (codigo morto, nunca chamada; dependia de um id que nao existe em nenhum JSP).
