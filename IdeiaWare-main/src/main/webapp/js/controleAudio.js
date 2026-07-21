var audio_context;
var recorder;
var audio_stream;
var gravando = false;

function Initialize() {
  try {
    window.AudioContext = window.AudioContext || window.webkitAudioContext;
    navigator.getUserMedia = (navigator.getUserMedia ||
            navigator.webkitGetUserMedia ||
            navigator.mozGetUserMedia ||
            navigator.msGetUserMedia);
    window.URL = window.URL || window.webkitURL;

    audio_context = new AudioContext;
    console.log('Audio context is ready !');
    console.log('navigator.getUserMedia ' + (navigator.getUserMedia ? 'available.' : 'not present!'));
  } catch (e) {
    alert('No web audio support in this browser!');
  }
}

function startRecording() {
  // UX-STORYTELLING-AUDIO-API: usa mediaDevices.getUserMedia (API antiga removida).
  if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
    alert('Este navegador não suporta gravação de áudio, ou a página não está em um contexto seguro (HTTPS).');
    return;
  }
  // TK-31: resume() do audio_context, autoplay policy pode suspendê-lo.
  if (audio_context && audio_context.state === 'suspended') {
    audio_context.resume();
  }
  var btn = document.getElementById("gravar-btn");
  // TK-30b: desabilita o botão síncrono antes da Promise, evita concorrência.
  btn.disabled = true;
  navigator.mediaDevices.getUserMedia({audio: true}).then(function (stream) {
    audio_stream = stream;
    var input = audio_context.createMediaStreamSource(stream);
    recorder = new Recorder(input);
    recorder && recorder.record();
    gravando = true;
    btn.value = "Parar";
    btn.disabled = false;
  }).catch(function (e) {
    // TK-30b: reabilita o botão se a permissão for negada.
    btn.disabled = false;
    console.error('No live audio input: ' + e);
    // AUDITORIA-2026-07-16: distingue NotAllowedError (permissao) de NotFoundError (sem microfone).
    if (e && e.name === 'NotAllowedError') {
      alert('Não foi possível acessar o microfone. Verifique as permissões do navegador.');
    } else if (e && e.name === 'NotFoundError') {
      alert('Nenhum microfone foi encontrado neste computador. Conecte um microfone (ou verifique se ele está habilitado no sistema) e tente novamente.');
    } else {
      alert('Não foi possível iniciar a gravação de áudio. Tente novamente.');
    }
  });
}

function stopRecording(callback, AudioFormat) {
  recorder && recorder.stop();

  // TK-30: guard em audio_stream, evita TypeError se clicar "Parar" antes de "Gravar".
  // AUDITORIA-2026-07-16: para em TODAS as faixas, nao so' a primeira.
  if (audio_stream) {
    audio_stream.getAudioTracks().forEach(function (track) { track.stop(); });
  }

  gravando = false;
  document.getElementById("gravar-btn").value = "Gravar";

  if (typeof (callback) == "function") {
    recorder && recorder.exportWAV(function (blob) {
      callback(blob);
      recorder.clear();
    }, (AudioFormat || "audio/wav"));
  }
}

// AUDITORIA-2026-07-16: um so' botao alterna Gravar/Parar, estado "gravando" e' a unica fonte de verdade.
function toggleRecording(onStop) {
  if (gravando) {
    stopRecording(onStop, "audio/wav");
  } else {
    startRecording();
  }
}
