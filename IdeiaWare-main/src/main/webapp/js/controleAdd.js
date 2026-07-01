/* global Konva */
var indice = 0;
//var layer = new Konva.Layer();

function drawImage(imageObj, stage, caracteristicas) {

  // Abordagem original limpa: a figura e uma Konva.Image arrastavel direto.
  // (As tentativas de Group/Rect/fill/hitFunc foram removidas — existiam so para
  //  contornar o canvas do navegador Brave, que bloqueia a leitura de pixels do
  //  canvas; no Chrome/Edge/Firefox isso nao e necessario. Aquele "fundo" tambem
  //  era o que quebrava o redimensionamento.)
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
        data: JSON.stringify(this.attrs.id)
      });
    }
  });

  // STM-22: salva a posicao automaticamente ao soltar a figura, para o layout
  // montado arrastando persistir sem precisar clicar em "Salvar".
  elemento.on('dragend', function () {
    var a = this.attrs;
    $.ajax({
      type: 'POST',
      url: 'AutoSalvarStoryServlet',
      contentType: 'application/json',
      data: JSON.stringify([{ tipo: 'outro', codigo: a.id, x: a.x, y: a.y, height: a.height, width: a.width }])
    });
  });

  caracteristicas.layer.add(elemento);
  caracteristicas.layer.draw();

}


function drawText(stage, caracteristicas) {

  // STM-09: usa a MESMA layer das imagens em vez de criar uma layer nova a cada
  // texto. As layers de texto empilhadas ficavam por cima dos ícones e
  // interceptavam os eventos de mouse, impedindo arrastar as formas.
  // A layer ja foi adicionada ao stage no controle.js (onload).
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
      data: JSON.stringify([{ tipo: 'texto', codigo: a.id, x: a.x, y: a.y, conteudo: a.text, fonte: a.fontFamily, tamanhoFonte: a.fontSize, cor: a.fill }])
    });
  });

  textNode.on('click', function () {
    var y = document.getElementById("deletarAlgo");
    if (y.style.display == "block") {
      this.hide();
      this.destroy();
      stage.draw();
      $.ajax({
        type: 'POST',
        url: "DeletarObjServlet",
        contentType: 'application/json',
        data: JSON.stringify(this.attrs.id)
      });
    }
  });

  //textNode.fontSize = size; //document.getElementById('fontSize').value;
  //textNode.fontFamily = font; //document.getElementById('fontFamily').value;
  //textNode.fill = color; //document.getElementById('fill').value;
  layer.add(textNode);
  layer.draw();
//  caracteristicas.layer.add(textNode);
//  caracteristicas.layer.draw();

  textNode.on('dblclick', () => {
    // create textarea over canvas with absolute position

    // first we need to find its positon
    var textPosition = textNode.getAbsolutePosition();
    var stageBox = stage.getContainer().getBoundingClientRect();



    var areaPosition = {
      x: textPosition.x + stageBox.left,
      y: textPosition.y + stageBox.top
    };


    // create textarea and style it
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
        // STR-11: salva automaticamente ao confirmar edição de texto
        if (typeof window.salvarProgresso === 'function') {
          window.salvarProgresso();
        }
      }
    });
  });

}


function drawFreeLine(stage, layer) {
  layer = new Konva.Layer();
  stage.add(layer);

  //#######
  var canvas = document.createElement('canvas');
  canvas.width = stage.width() / 1;
  canvas.height = stage.height() / 1;

  // creted canvas we can add to layer as "Konva.Image" element

  var image = new Konva.Image({
    image: canvas,
    x: stage.width() / 1000,
    y: stage.height() / 1000,
    draggable: false
  });

  layer.add(image);
  stage.draw();

  // Good. Now we need to get access to context element
  var context = canvas.getContext('2d');
  context.strokeStyle = "#df4b26";
  context.lineJoin = "round";
  context.lineWidth = 6;
  var isPaint = false;
  var lastPointerPosition;
  var mode = 'brush';
  // now we need to bind some events
  // we need to start drawing on mousedown
  // and stop drawing on mouseup
  stage.on('contentMousedown.proto', function () {
    isPaint = true;
    lastPointerPosition = stage.getPointerPosition();
  });
  stage.on('contentMouseup.proto', function () {
    isPaint = false;
  });
  // and core function - drawing
  stage.on('contentMousemove.proto', function () {
    if (!isPaint) {
      return;
    }
    if (mode === 'brush') {
      context.globalCompositeOperation = 'source-over';
    }
    if (mode === 'eraser') {
      context.globalCompositeOperation = 'destination-out';
    }
    context.beginPath();
    var localPos = {
      x: lastPointerPosition.x - image.x(),
      y: lastPointerPosition.y - image.y()
    };
    context.moveTo(localPos.x, localPos.y);
    var pos = stage.getPointerPosition();
    localPos = {
      x: pos.x - image.x(),
      y: pos.y - image.y()
    };
    context.lineTo(localPos.x, localPos.y);
    context.closePath();
    context.stroke();
    lastPointerPosition = pos;
    layer.draw();
  });
  var select = document.getElementById('tool');
  select.addEventListener('change', function () {
    mode = select.value;
  });
}
