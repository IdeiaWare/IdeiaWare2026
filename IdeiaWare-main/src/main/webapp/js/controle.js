/* global Konva */

document.write(unescape("%3Cscript src='js/Bibliotecas/jquery-3.2.1.js' type='text/javascript'%3E%3C/script%3E"));
document.write(unescape("%3Cscript src='js/controleAdd.js' type='text/javascript'%3E%3C/script%3E"));
document.write(unescape("%3Cscript src='js/controleAudio.js' type='text/javascript'%3E%3C/script%3E"));
document.write(unescape("%3Cscript src='js/master.js' type='text/javascript'%3E%3C/script%3E"));
document.write(unescape("%3Cscript src='js/Bibliotecas/Konva.min.js' type='text/javascript'%3E%3C/script%3E"));

window.onload = function () {

  // STR-13: URL relativa — não depende mais do contexto /LIC
  if ($('#storytelling-data').val() === "FN") {
    $("#salvarBotao, #exporta").addClass("disabled");
    alert("Este item já foi finalizado. Você será redirecionado para a lista de Storytellings.");
    window.location.href = "lista-storytelling.jsp";
    return;
  }

  var width = $('#container').innerWidth();
  var height = 850;

  var stage = new Konva.Stage({
    container: 'container',
    width: width,
    height: height
  });

  var layer = new Konva.Layer({
    name: "camadaImagens"
  });

  // STM-09: adiciona a layer ao stage UMA vez aqui (antes era adicionada dentro
  // de cada drawImage/drawText, com getStage() que nao e confiavel em todas as
  // versoes do Konva). Garante estado consistente para o drag e para a
  // exportacao (stage.toDataURL).
  stage.add(layer);

  // STM-16: cada figura nova nasce numa celula de uma GRADE (em vez de todas no
  // centro, empilhadas). O contador idxPos e sincrono, entao cliques rapidos nao
  // caem na mesma posicao. posOcupadas guarda as posicoes ja usadas (carregadas
  // do banco + adicionadas) para a proxima figura desviar e nao nascer por cima.
  var idxPos = 0;
  var posOcupadas = [];
  function proximaPosicao() {
    var n = idxPos++;
    var espac = 145;
    var margem = 20;
    var cols = Math.max(1, Math.floor((stage.getWidth() - margem) / espac));
    var maxRows = Math.max(1, Math.floor((stage.getHeight() - margem) / espac));
    var slot = n % (cols * maxRows);
    return { x: margem + (slot % cols) * espac, y: margem + Math.floor(slot / cols) * espac };
  }
  function colideComOcupadas(px, py) {
    return posOcupadas.some(function (p) {
      return Math.abs(p.x - px) < 130 && Math.abs(p.y - py) < 130;
    });
  }
  // STM-21: pega a proxima celula da grade que esteja realmente livre.
  function proximaPosicaoLivre() {
    var p, tentativas = 0;
    do {
      p = proximaPosicao();
      tentativas++;
    } while (colideComOcupadas(p.x, p.y) && tentativas < 300);
    posOcupadas.push({ x: p.x, y: p.y });
    return p;
  }

  // STR-10: removido o hack de reload com #loaded que causava double-load na cada acesso

  // Carrega imagens e textos salvos no banco
  $.get("RetornaElementos", function (responseJson) {
    $.each(responseJson, function (index, elemento) {
      if (elemento.tipo === "IMG") {
        var imageObj = new Image();
        imageObj.onload = function () {
          // STM-19: usa a posicao salva diretamente (nao reorganiza o layout).
          // STM-21: registra a posicao ocupada para que figuras NOVAS adicionadas
          // depois desviem dela (proximaPosicaoLivre) em vez de nascer por cima.
          posOcupadas.push({ x: elemento.x, y: elemento.y });
          drawImage(this, stage, {
            layer: layer,
            codigo: elemento.codigo,
            x: elemento.x,
            y: elemento.y,
            altura: elemento.altura,
            largura: elemento.largura
          });
        };
        imageObj.src = elemento.caminho;
      } else if (elemento.tipo === "TXT") {
        drawText(stage, {
          layer: layer,
          codigo: elemento.codigo,
          x: elemento.x,
          y: elemento.y,
          informacaoTexto: elemento.informacaoTexto,
          fonte: elemento.fonte,
          tamanho: elemento.tamanhoFonte,
          cor: elemento.corFonte
        });
      }
    });
  });

  $.get("RetornaAudio", function (responseJson) {
    $.each(responseJson, function (index, elemento) {
      var b64 = b64toBlob(elemento.caminho, 'audio/wav');
      var url = URL.createObjectURL(b64);
      var li  = document.createElement('li');
      var au  = document.createElement('audio');
      var hf  = document.createElement('a');

      au.controls = true;
      au.src = url;
      hf.href = url;
      hf.download = new Date().toISOString() + '.wav';
      hf.innerHTML = hf.download;
      li.appendChild(au);
      li.appendChild(hf);
      recordingslist.appendChild(li);
    });
  });

  // -------SALVAR--------
  function salvarProgresso() {
    $("#overlay").attr('style', 'display: block !important');
    var lista = [];

    for (var i = 0; i < stage.children.length; i++) {
      for (var j = 0; j < stage.children[i].children.length; j++) {
        var attrs = stage.children[i].children[j].attrs;
        if (attrs.name === 'texto') {
          lista.push({
            tipo:        'texto',
            x:           attrs.x,
            y:           attrs.y,
            conteudo:    attrs.text,
            codigo:      attrs.id,
            fonte:       attrs.fontFamily,
            tamanhoFonte: attrs.fontSize,
            cor:         attrs.fill
          });
        } else {
          lista.push({
            tipo:   'outro',
            codigo: attrs.id,
            x:      attrs.x,
            y:      attrs.y,
            height: attrs.height,
            width:  attrs.width
          });
        }
      }
    }

    $.ajax({
      type: 'POST',
      url: "AutoSalvarStoryServlet",
      contentType: "application/json",
      data: JSON.stringify(lista),
      success: function () {
        $("#overlay").attr('style', 'display: none !important');
        alert("Salvo com sucesso!");
      },
      error: function () {
        $("#overlay").attr('style', 'display: none !important');
        alert("Erro ao salvar!");
      }
    });
  }

  // STR-11: expõe salvarProgresso globalmente para que controleAdd.js possa chamá-la
  window.salvarProgresso = salvarProgresso;

  // (proximaPosicao foi movida para o topo do onload — STM-16 — com contador
  //  sincrono, para nao depender de layer.getChildren().length que tinha race.)

  // -------FORMAS--------
  function adicionaForma(forma, corForma) {
    // STM-21: pega a proxima celula livre (desviando das figuras ja existentes),
    // para a nova forma nao nascer por cima das que ja estao no quadro.
    var pos = proximaPosicaoLivre();
    var dadosCompletos = {
      tipo: forma + corForma,
      x: pos.x,
      y: pos.y
    };

    $.ajax({
      type: "POST",
      url: "InserirForma",
      contentType: "application/json",
      data: JSON.stringify(dadosCompletos),
      success: function (response) {
        var imageObj = new Image();
        imageObj.onload = function () {
          drawImage(this, stage, {
            layer:   layer,
            codigo:  response.codigo,
            x:       response.x,
            y:       response.y,
            altura:  response.altura,
            largura: response.largura
          });
        };
        imageObj.src = 'imagens/' + dadosCompletos.tipo + '.png';
      }
    });
  }

  // -------BORRACHA------
  $('#deletarAlgo').click(function () {
    var x = document.getElementById("deletarAlgo");
    if (x.style.display === "") {
      x.style.display = "block";
      x.innerHTML = "Desativar borracha";
      stage.container().style.cursor = 'crosshair';
      stage.draw();
    } else {
      x.style.display = "";
      x.innerHTML = "Ativar borracha";
      stage.container().style.cursor = 'default';
    }
  });

  // -------PDF-----------
  $('#exporta').on('click', function () {
    geraPDF(stage);
  });

  // -------RESIZE--------
  $('#btnModificar').click(function () {
    var id = $('#imgaemIdResize').val();
    for (var i = 0; i < stage.children.length; i++) {
      for (var j = 0; j < stage.children[i].children.length; j++) {
        var node = stage.children[i].children[j];
        if (node.attrs.id == id) {
          // STM-23: usa os setters width()/height() com Number (antes atribuia a
          // string do input direto em attrs, que nao redimensionava de fato).
          node.width(Number($('#LarguraId').val()));
          node.height(Number($('#AlturaId').val()));
          stage.draw();
          $('#modalResize').modal('close');
          // persiste o novo tamanho na hora
          $.ajax({
            type: 'POST',
            url: 'AutoSalvarStoryServlet',
            contentType: 'application/json',
            data: JSON.stringify([{ tipo: 'outro', codigo: node.attrs.id, x: node.attrs.x, y: node.attrs.y, height: node.attrs.height, width: node.attrs.width }])
          });
          return;
        }
      }
    }
  });

  // -------SALVAR--------
  $("#salvarBotao").on("click", function () {
    salvarProgresso();
  });

  // Salva antes de fazer upload de imagem
  $('#b1_1').on('click', function () {
    salvarProgresso();
  });

  // -------FORMAS (botões)--------
  // STR-14: removido handler morto #b2_00 (id não existe no JSP)
  $('#b2_1').on('click', function () { adicionaForma("quadrado",    $('#corForma :selected').text()); });
  $('#b2_2').on('click', function () { adicionaForma("circulo",     $('#corForma :selected').text()); });
  $('#b2_3').on('click', function () { adicionaForma("flecha",      $('#corForma :selected').text()); });
  $('#b2_4').on('click', function () { adicionaForma("triangulo",   $('#corForma :selected').text()); });
  $('#b2_5').on('click', function () { adicionaForma("dinheiro",    $('#corForma :selected').text()); });
  $('#b2_6').on('click', function () { adicionaForma("caveira",     $('#corForma :selected').text()); });
  $('#b2_7').on('click', function () { adicionaForma("nuvem",       $('#corForma :selected').text()); });
  $('#b2_8').on('click', function () { adicionaForma("nota",        $('#corForma :selected').text()); });
  $('#b2_9').on('click', function () { adicionaForma("monitor",     $('#corForma :selected').text()); });
  $('#b2_10').on('click', function () { adicionaForma("localizacao",$('#corForma :selected').text()); });
  $('#b2_11').on('click', function () { adicionaForma("lampada",    $('#corForma :selected').text()); });
  $('#b2_12').on('click', function () { adicionaForma("cadeado",    $('#corForma :selected').text()); });
  $('#b2_13').on('click', function () { adicionaForma("banco",      $('#corForma :selected').text()); });
  $('#b2_14').on('click', function () { adicionaForma("balao",      $('#corForma :selected').text()); });
  $('#b2_15').on('click', function () { adicionaForma("linha",      $('#corForma :selected').text()); });

  // -------TEXTO---------
  $('#b3').on('click', function () {
    var font  = $('#fontFamily :selected').val();
    var size  = $('#fontSize :selected').val();
    var color = document.getElementById("corTexto").value;

    // STM-21: proxima celula livre, para o texto novo nao nascer por cima.
    var pos = proximaPosicaoLivre();
    var dadosCompletos = {
      x: pos.x,
      y: pos.y,
      tamanho:         size,
      fonte:           font,
      cor:             color,
      informacaoTexto: 'Clique 2x para editar'
    };

    $.ajax({
      type: "POST",
      url: "InserirTexto",
      contentType: "application/json",
      data: JSON.stringify(dadosCompletos),
      success: function (response) {
        drawText(stage, {
          layer:           layer,
          codigo:          response.codigo,
          x:               response.x,
          y:               response.y,
          informacaoTexto: response.informacaoTexto,
          fonte:           font,
          tamanho:         size,
          cor:             color
        });
      }
    });
  });

  // -------AUDIO----------
  document.getElementById("start-btn").addEventListener("click", function () {
    startRecording();
  }, false);

  var blobAudio;

  document.getElementById("stop-btn").addEventListener("click", function () {
    stopRecording(function (AudioBLOB) {
      var url = URL.createObjectURL(AudioBLOB);
      var li  = document.createElement('li');
      var au  = document.createElement('audio');
      var hf  = document.createElement('a');

      blobAudio = AudioBLOB;
      au.controls = true;
      au.src = url;
      hf.href = url;
      hf.download = new Date().toISOString() + '.wav';
      hf.innerHTML = hf.download;
      li.appendChild(au);
      li.appendChild(hf);
      recordingslist.appendChild(li);
    }, "audio/wav");
  }, false);

  $('#save-btn').on('click', function () {
    salvaAudio(blobAudio);
  });

  // -------ÁUDIO INIT----
  var audio_context;
  var recorder;
  var audio_stream;
  Initialize();
};
