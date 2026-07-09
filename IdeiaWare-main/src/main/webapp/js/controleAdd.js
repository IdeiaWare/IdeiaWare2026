/* global Konva */
// REVISAO 2026-07-08 (varredura JS, achado BAIXA): "indice" nunca era referenciada em
// lugar nenhum -- codigo morto, removido.
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
        data: JSON.stringify(this.attrs.id),
        // REVISAO 2026-07-08 (varredura JS, achado ALTA): a figura ja e escondida e
        // destruida no CLIENTE antes do POST -- se a requisicao falhar (rede/sessao
        // expirada/500/CSRF), o servidor nunca recebe o delete, mas a tela ja mostra
        // como se tivesse sumido, sem nenhum aviso. Mesmo padrao de error() ja usado
        // no save MANUAL (controle.js).
        error: function () {
          alert("Erro ao excluir a figura no servidor. Recarregue a página -- ela pode reaparecer.");
        }
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
      data: JSON.stringify([{ tipo: 'outro', codigo: a.id, x: a.x, y: a.y, height: a.height, width: a.width }]),
      // REVISAO 2026-07-08 (varredura JS, achado ALTA): a figura ja esta visualmente
      // na posicao nova (o usuario acabou de soltar) -- se o auto-save falhar, a
      // posicao real no servidor fica desatualizada sem nenhum aviso na hora do erro.
      error: function () {
        alert("Erro ao salvar a nova posição. Recarregue a página para conferir se a figura ficou no lugar certo.");
      }
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
      data: JSON.stringify([{ tipo: 'texto', codigo: a.id, x: a.x, y: a.y, conteudo: a.text, fonte: a.fontFamily, tamanhoFonte: a.fontSize, cor: a.fill }]),
      // REVISAO 2026-07-08 (varredura JS, achado ALTA): mesmo motivo do dragend de
      // imagem acima -- posicao ja mudou na tela, sem aviso se o auto-save falhar.
      error: function () {
        alert("Erro ao salvar a nova posição. Recarregue a página para conferir se o texto ficou no lugar certo.");
      }
    });
  });

  // REVISAO 2026-07-08 (varredura JS, achado BAIXA): namespace .deletar adicionado
  // (era so 'click', sem namespace -- inconsistente com o delete de imagem, que ja
  // usa 'click.deletar'). Sem efeito pratico hoje (nada mais liga 'click' neste
  // textNode), padroniza pra facilitar manutencao futura.
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
        // REVISAO 2026-07-08 (varredura JS, achado ALTA): mesmo motivo do delete de
        // imagem acima -- texto ja escondido/destruido no cliente antes do POST.
        error: function () {
          alert("Erro ao excluir o texto no servidor. Recarregue a página -- ele pode reaparecer.");
        }
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
        // UX: salva SO este texto (mesmo padrao do dragend), nao o quadro inteiro.
        // Antes chamava salvarProgresso() -- salvava TODOS os elementos do quadro
        // com overlay bloqueante + alert só por causa de uma edição de texto.
        var a = textNode.attrs;
        $.ajax({
          type: 'POST',
          url: 'AutoSalvarStoryServlet',
          contentType: 'application/json',
          data: JSON.stringify([{ tipo: 'texto', codigo: a.id, x: a.x, y: a.y, conteudo: a.text, fonte: a.fontFamily, tamanhoFonte: a.fontSize, cor: a.fill }]),
          // REVISAO 2026-07-08 (varredura JS, achado ALTA): o texto ja foi trocado na
          // tela (textNode.text(textarea.value) acima) antes do POST -- se o auto-save
          // falhar, o conteudo editado nao chega no servidor sem nenhum aviso.
          error: function () {
            alert("Erro ao salvar o texto editado. Recarregue a página para conferir se a edição ficou salva.");
          }
        });
      }
    });
  });

}

// REVISAO 2026-07-08 (varredura JS, achado BAIXA): drawFreeLine() foi removida --
// codigo morto confirmado (nunca chamada em lugar nenhum; dependia de
// document.getElementById('tool'), um id que nao existe em nenhum JSP do projeto).
// Alem de morta, ela empilhava listeners `stage.on('....proto', ...)` sem nunca dar
// `stage.off('.proto')` -- se um dia for reaproveitada, precisa desse cuidado.
