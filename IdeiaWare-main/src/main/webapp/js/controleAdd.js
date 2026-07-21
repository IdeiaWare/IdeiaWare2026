/* global Konva */

function drawImage(imageObj, stage, caracteristicas) {

  // BRAVE: Konva.Image direto, sem workaround de Group/Rect/hitFunc.
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

  // KONVA-UPGRADE-FASE2: duplo-clique anexa o Transformer em vez de abrir o modal antigo.
  elemento.on('dblclick', function () {
    var y = document.getElementById("deletarAlgo");
    if (y.style.display == "") {
      caracteristicas.transformer.nodes([elemento]);
      // KONVA-UPGRADE-FASE2-ZORDER: figura nova entra por cima, escondia as alcas atras dela.
      caracteristicas.transformer.moveToTop();
      caracteristicas.layer.draw();
    }
  });

  // KONVA-UPGRADE-FASE2: converte scaleX/scaleY do Transformer pra width/height absolutos.
  elemento.on('transformend', function () {
    var node = this;
    var novaLargura = Math.round(node.width() * node.scaleX());
    var novaAltura = Math.round(node.height() * node.scaleY());
    node.scaleX(1);
    node.scaleY(1);
    node.width(novaLargura);
    node.height(novaAltura);
    stage.draw();
    var a = node.attrs;
    $.ajax({
      type: 'POST',
      url: 'AutoSalvarStoryServlet',
      contentType: 'application/json',
      data: JSON.stringify([{ tipo: 'outro', codigo: a.id, x: a.x, y: a.y, height: novaAltura, width: novaLargura }]),
      error: function () {
        alert("Erro ao salvar o novo tamanho. Recarregue a página para conferir se ficou salvo.");
      }
    });
  });

  elemento.on('click.deletar', function () {
    var y = document.getElementById("deletarAlgo");
    if (y.style.display == "block") {
      // KONVA-UPGRADE-FASE2: desanexa o Transformer antes de destruir (senao aponta pro no morto).
      if (caracteristicas.transformer.nodes().indexOf(this) !== -1) {
        caracteristicas.transformer.nodes([]);
      }
      this.hide();
      this.destroy();
      stage.draw();
      $.ajax({
        type: 'POST',
        url: "DeletarObjServlet",
        contentType: 'application/json',
        data: JSON.stringify(this.attrs.id),
        // TK-29: avisa se o delete falhar no servidor após sumir da tela.
        error: function () {
          alert("Erro ao excluir a figura no servidor. Recarregue a página -- ela pode reaparecer.");
        }
      });
    }
  });

  // STM-22: salva posição automaticamente ao soltar a figura.
  elemento.on('dragend', function () {
    var a = this.attrs;
    $.ajax({
      type: 'POST',
      url: 'AutoSalvarStoryServlet',
      contentType: 'application/json',
      data: JSON.stringify([{ tipo: 'outro', codigo: a.id, x: a.x, y: a.y, height: a.height, width: a.width }]),
      // TK-29: avisa se o auto-save da posição falhar.
      error: function () {
        alert("Erro ao salvar a nova posição. Recarregue a página para conferir se a figura ficou no lugar certo.");
      }
    });
  });

  caracteristicas.layer.add(elemento);
  caracteristicas.layer.draw();

}


function drawText(stage, caracteristicas) {

  // STM-09: usa a mesma layer das imagens, evita cobrir ícones e bloquear arraste.
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

  // STM-22: salva posição do texto automaticamente ao soltar.
  textNode.on('dragend', function () {
    var a = this.attrs;
    $.ajax({
      type: 'POST',
      url: 'AutoSalvarStoryServlet',
      contentType: 'application/json',
      data: JSON.stringify([{ tipo: 'texto', codigo: a.id, x: a.x, y: a.y, conteudo: a.text, fonte: a.fontFamily, tamanhoFonte: a.fontSize, cor: a.fill }]),
      // TK-29: mesmo motivo do dragend de imagem.
      error: function () {
        alert("Erro ao salvar a nova posição. Recarregue a página para conferir se o texto ficou no lugar certo.");
      }
    });
  });

  // TK-32d: namespace .deletar, consistente com o delete de imagem.
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
        // TK-29: mesmo motivo do delete de imagem.
        error: function () {
          alert("Erro ao excluir o texto no servidor. Recarregue a página -- ele pode reaparecer.");
        }
      });
    }
  });

  layer.add(textNode);
  layer.draw();

  textNode.on('dblclick', () => {
    // AUDITORIA-2026-07-16: guard da borracha, mesmo padrao que a imagem ja usava pro resize.
    var eraserAtivo = document.getElementById("deletarAlgo").style.display === "block";
    if (eraserAtivo) return;

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
    // AUDITORIA-2026-07-16: faltava 'px' -- numero puro em style.width e' CSS invalido.
    textarea.style.width = textNode.width() + 'px';
    // AUDITORIA-2026-07-16: fundo branco -- materialize.css deixa todo textarea transparente por padrao.
    textarea.style.backgroundColor = '#fff';
    textarea.style.border = '1px solid #9e9e9e';
    textarea.style.zIndex = '1000';

    textarea.focus();

    // AUDITORIA-2026-07-16: Enter/Esc/clique-fora fecham o textarea (antes so' Enter fechava).
    function salvarEFechar() {
      if (!document.body.contains(textarea)) return;
      document.removeEventListener('mousedown', aoClicarFora, true);
      textNode.text(textarea.value);
      layer.draw();
      document.body.removeChild(textarea);
      // UX-STORYTELLING-TEXTO-SAVE: salva só este texto, não o quadro inteiro.
      var a = textNode.attrs;
      $.ajax({
        type: 'POST',
        url: 'AutoSalvarStoryServlet',
        contentType: 'application/json',
        data: JSON.stringify([{ tipo: 'texto', codigo: a.id, x: a.x, y: a.y, conteudo: a.text, fonte: a.fontFamily, tamanhoFonte: a.fontSize, cor: a.fill }]),
        // TK-29: avisa se o auto-save falhar após o texto já mudar na tela.
        error: function () {
          alert("Erro ao salvar o texto editado. Recarregue a página para conferir se a edição ficou salva.");
        }
      });
    }

    function cancelarEFechar() {
      if (!document.body.contains(textarea)) return;
      document.removeEventListener('mousedown', aoClicarFora, true);
      document.body.removeChild(textarea);
    }

    // AUDITORIA-2026-07-16: mousedown em fase de captura -- 'blur' sozinho nao fechava clicando no quadro (Konva intercepta).
    function aoClicarFora(e) {
      if (e.target !== textarea) {
        salvarEFechar();
      }
    }
    document.addEventListener('mousedown', aoClicarFora, true);

    textarea.addEventListener('keydown', function (e) {
      if (e.keyCode === 13) {
        e.preventDefault();
        salvarEFechar();
      } else if (e.keyCode === 27) {
        cancelarEFechar();
      }
    });
    // AUDITORIA-2026-07-16: blur mantido como reforco (Tab, modal) -- guards evitam processar 2x.
    textarea.addEventListener('blur', salvarEFechar);
  });

}

// TK-32c: drawFreeLine() removida, código morto nunca chamado.
