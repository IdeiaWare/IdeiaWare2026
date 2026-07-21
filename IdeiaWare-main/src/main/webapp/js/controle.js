/* global Konva */

// AUDITORIA-2026-07-16: jquery/controleAdd.js ja carregados pelo header, redundante aqui.
document.write(unescape("%3Cscript src='js/controleAudio.js' type='text/javascript'%3E%3C/script%3E"));
document.write(unescape("%3Cscript src='js/master.js' type='text/javascript'%3E%3C/script%3E"));
document.write(unescape("%3Cscript src='js/Bibliotecas/Konva.min.js' type='text/javascript'%3E%3C/script%3E"));

// AUDITORIA-2026-07-16: converte base64 (elemento.caminho vindo do RetornaAudio) em Blob.
function b64toBlob(b64Data, contentType, sliceSize) {
  contentType = contentType || '';
  sliceSize = sliceSize || 512;

  // AUDIO-DATAURI: tolera receber a data URI inteira, nao so' o base64 puro (registros antigos).
  var comma = b64Data.indexOf(',');
  if (b64Data.indexOf('data:') === 0 && comma >= 0) {
    b64Data = b64Data.substring(comma + 1);
  }

  var byteCharacters = atob(b64Data);
  var byteArrays = [];

  for (var offset = 0; offset < byteCharacters.length; offset += sliceSize) {
    var slice = byteCharacters.slice(offset, offset + sliceSize);
    var byteNumbers = new Array(slice.length);
    for (var i = 0; i < slice.length; i++) {
      byteNumbers[i] = slice.charCodeAt(i);
    }
    byteArrays.push(new Uint8Array(byteNumbers));
  }

  return new Blob(byteArrays, { type: contentType });
}

// UX-PADRAO-ETAPA-FINALIZADA: cobre o caso de outro participante finalizar com a aba ja aberta.
function tratarErroEtapaFinalizada(xhr, mensagemPadrao) {
  if (xhr.status === 403) {
    alert((xhr.responseText || "Este item já foi finalizado.") + " Você será redirecionado para a lista de Storytellings.");
    window.location.href = "lista-storytelling.jsp";
    return true;
  }
  alert(mensagemPadrao);
  return false;
}

window.onload = function () {

  // AUDITORIA-2026-07-16: sem Konva o quadro nao tem como renderizar -- antes falhava em silencio.
  if (typeof Konva === 'undefined') {
    alert('Não foi possível carregar os componentes do quadro. Recarregue a página; se o problema continuar, verifique sua conexão ou desative bloqueadores de conteúdo.');
    return;
  }

  // STR-13: URL relativa, não depende do contexto /LIC
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

  // STM-09: layer adicionada ao stage uma vez, getStage() era instável.
  stage.add(layer);

  // KONVA-UPGRADE-FASE2: Transformer nativo substitui o modal de digitar largura/altura.
  var transformer = new Konva.Transformer({
    keepRatio: false,
    boundBoxFunc: function (oldBox, newBox) {
      // TK-33: mesmo limite minimo que o modal antigo garantia (min="10").
      if (newBox.width < 10 || newBox.height < 10) {
        return oldBox;
      }
      return newBox;
    }
  });
  layer.add(transformer);

  // KONVA-UPGRADE-FASE2: clique no vazio do quadro desanexa o Transformer.
  stage.on('click tap', function (e) {
    if (e.target === stage) {
      transformer.nodes([]);
      layer.draw();
    }
  });

  // STM-16: figuras nascem em grade, idxPos síncrono evita cliques na mesma célula.
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
  // STM-21: pega a próxima célula livre, desvia das já ocupadas.
  function proximaPosicaoLivre() {
    var p, tentativas = 0;
    do {
      p = proximaPosicao();
      tentativas++;
    } while (colideComOcupadas(p.x, p.y) && tentativas < 300);
    posOcupadas.push({ x: p.x, y: p.y });
    return p;
  }

  // STR-10: removido hack de reload com #loaded que causava double-load.
  $.get("RetornaElementos", function (responseJson) {
    $.each(responseJson, function (index, elemento) {
      // AUDITORIA-2026-07-16: posicao reservada de forma sincrona pros 2 tipos (era so' imagem, e so' assincrono).
      posOcupadas.push({ x: elemento.x, y: elemento.y });
      if (elemento.tipo === "IMG") {
        var imageObj = new Image();
        imageObj.onload = function () {
          drawImage(this, stage, {
            layer: layer,
            transformer: transformer,
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
  }).fail(function () {
    alert("Erro ao carregar os elementos salvos do quadro. Recarregue a página.");
  });

  // UX-STORYTELLING-AUDIO-RECARREGA: recarrega a lista do servidor apos salvar ("remover" vira "excluir").
  function carregarAudiosSalvos() {
    $('#recordingslist').empty();
    $.get("RetornaAudio", function (responseJson) {
      $.each(responseJson, function (index, elemento) {
        var b64 = b64toBlob(elemento.caminho, 'audio/wav');
        var url = URL.createObjectURL(b64);
        var li  = document.createElement('li');
        var au  = document.createElement('audio');
        var hf  = document.createElement('a');
        var rm  = document.createElement('a');

        au.controls = true;
        au.src = url;
        hf.href = url;
        hf.download = new Date().toISOString() + '.wav';
        hf.innerHTML = hf.download;
        // AUDITORIA-2026-07-16: exclui audio ja salvo via DeletarObjServlet (mesmo padrao de imagem/texto).
        rm.href = '#';
        rm.style.marginLeft = '8px';
        rm.style.fontWeight = 'bold';
        rm.innerHTML = 'excluir';
        rm.addEventListener('click', function (e) {
          e.preventDefault();
          $.ajax({
            type: 'POST',
            url: 'DeletarObjServlet',
            contentType: 'application/json',
            data: JSON.stringify(elemento.codigo),
            success: function () {
              li.parentNode.removeChild(li);
              alert("Áudio excluído com sucesso!");
            },
            error: function (xhr) {
              tratarErroEtapaFinalizada(xhr, "Erro ao excluir o áudio no servidor. Recarregue a página e tente novamente.");
            }
          });
        });
        li.appendChild(au);
        li.appendChild(hf);
        li.appendChild(rm);
        // JS-AJAX-ERR: getElementById explícito em vez de acesso implícito global.
        document.getElementById('recordingslist').appendChild(li);
      });
    }).fail(function () {
      alert("Erro ao carregar os áudios salvos.");
    });
  }
  carregarAudiosSalvos();

  function salvarProgresso() {
    $("#overlay").attr('style', 'display: block !important');
    var lista = [];

    for (var i = 0; i < stage.children.length; i++) {
      for (var j = 0; j < stage.children[i].children.length; j++) {
        var node = stage.children[i].children[j];
        // KONVA-UPGRADE-FASE2: guard pra nao salvar o proprio no do Transformer como se fosse figura.
        if (node === transformer) continue;
        var attrs = node.attrs;
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
        // UX-STORYTELLING-SALVAR-AUDIO-JUNTO: Salvar do cabecalho tambem manda audio pendente.
        if (blobAudio) {
          salvaAudio(blobAudio, true, carregarAudiosSalvos);
          blobAudio = undefined;
        }
      },
      error: function (xhr) {
        $("#overlay").attr('style', 'display: none !important');
        tratarErroEtapaFinalizada(xhr, "Erro ao salvar!");
      }
    });
  }

  function adicionaForma(forma, corForma) {
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
            transformer: transformer,
            codigo:  response.codigo,
            x:       response.x,
            y:       response.y,
            altura:  response.altura,
            largura: response.largura
          });
        };
        imageObj.src = 'imagens/' + dadosCompletos.tipo + '.png';
      },
      error: function (xhr) {
        tratarErroEtapaFinalizada(xhr, "Erro ao adicionar a forma!");
      }
    });
  }

  $('#deletarAlgo').click(function () {
    var x = document.getElementById("deletarAlgo");
    if (x.style.display === "") {
      x.style.display = "block";
      x.innerHTML = "Desativar borracha";
      stage.container().style.cursor = 'crosshair';
      // KONVA-UPGRADE-FASE2: desanexa o Transformer ao entrar em modo borracha.
      transformer.nodes([]);
      stage.draw();
    } else {
      x.style.display = "";
      x.innerHTML = "Ativar borracha";
      stage.container().style.cursor = 'default';
    }
  });

  $('#exporta').on('click', function () {
    geraPDF(stage);
  });

  $("#salvarBotao").on("click", function () {
    salvarProgresso();
  });

  // STR-15: salvarProgresso() removido do fluxo de upload, rodava a cada clique.
  // STR-14: removido handler morto #b2_00, id não existe no JSP.
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

  $('#b3').on('click', function () {
    var font  = $('#fontFamily :selected').val();
    var size  = $('#fontSize :selected').val();
    var color = document.getElementById("corTexto").value;

    // STM-21: próxima célula livre, texto novo não nasce por cima.
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
      },
      error: function (xhr) {
        tratarErroEtapaFinalizada(xhr, "Erro ao adicionar o texto!");
      }
    });
  });

  var blobAudio;

  // AUDITORIA-2026-07-16: botao unico (toggleRecording) chama start ou stop conforme o estado.
  document.getElementById("gravar-btn").addEventListener("click", function () {
    toggleRecording(function (AudioBLOB) {
      var url = URL.createObjectURL(AudioBLOB);
      var li  = document.createElement('li');
      var au  = document.createElement('audio');
      var hf  = document.createElement('a');
      var rm  = document.createElement('a');

      blobAudio = AudioBLOB;
      li.className = 'gravacao-local';
      au.controls = true;
      au.src = url;
      hf.href = url;
      hf.download = new Date().toISOString() + '.wav';
      hf.innerHTML = hf.download;
      // AUDITORIA-2026-07-16: "remover" so' tira da tela (gravacao nunca enviada ao servidor).
      rm.href = '#';
      rm.style.marginLeft = '8px';
      rm.style.fontWeight = 'bold';
      rm.innerHTML = 'remover';
      rm.addEventListener('click', function (e) {
        e.preventDefault();
        li.parentNode.removeChild(li);
        // UX-STORYTELLING-AUDIO-REMOVER-LOCAL: limpa blobAudio se a removida era a mais recente.
        if (blobAudio === AudioBLOB) {
          blobAudio = undefined;
        }
      });
      li.appendChild(au);
      li.appendChild(hf);
      li.appendChild(rm);
      // UX-STORYTELLING-AUDIO-1-SLOT: colapsa a lista pra 1 gravacao por vez (backend so' guarda 1).
      $('#recordingslist li').not(li).remove();
      // JS-AJAX-ERR: getElementById explícito em vez de acesso implícito global.
      document.getElementById('recordingslist').appendChild(li);
    });
  }, false);

  $('#save-btn').on('click', function () {
    // UX-STORYTELLING-SALVAR-AUDIO-JUNTO: limpa blobAudio apos enviar, evita reenvio duplicado.
    salvaAudio(blobAudio, false, carregarAudiosSalvos);
    blobAudio = undefined;
  });

  // TK-32b: removidas 3 variáveis que sombreavam as globais de controleAudio.js.
  Initialize();
};
