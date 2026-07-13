var audio_context;
var recorder;
var audio_stream;

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
  // UX-STORYTELLING-AUDIO-API: navigator.mediaDevices.getUserMedia (Promise) -- a antiga foi removida dos navegadores modernos.
  if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
    alert('Este navegador não suporta gravação de áudio, ou a página não está em um contexto seguro (HTTPS).');
    return;
  }
  // TK-31: resume() do audio_context aqui dentro do clique (autoplay policy pode deixa-lo "suspended").
  if (audio_context && audio_context.state === 'suspended') {
    audio_context.resume();
  }
  // TK-30b: desabilita o start-btn JA, sincrono, antes da Promise (evita 2 getUserMedia concorrentes).
  document.getElementById("start-btn").disabled = true;
  navigator.mediaDevices.getUserMedia({audio: true}).then(function (stream) {
    audio_stream = stream;
    var input = audio_context.createMediaStreamSource(stream);
    recorder = new Recorder(input);
    recorder && recorder.record();
    document.getElementById("start-btn").disabled = true;
    document.getElementById("stop-btn").disabled = false;
  }).catch(function (e) {
    // TK-30b: reabilita o start-btn se a permissao for negada (senao ficaria travado pra sempre).
    document.getElementById("start-btn").disabled = false;
    console.error('No live audio input: ' + e);
    alert('Não foi possível acessar o microfone. Verifique as permissões do navegador.');
  });
}

function stopRecording(callback, AudioFormat) {
  recorder && recorder.stop();

  // TK-30: audio_stream sem guard -- clicar "Parar" antes de "Gravar" dava TypeError nao tratado.
  if (audio_stream) {
    audio_stream.getAudioTracks()[0].stop();
  }

  document.getElementById("start-btn").disabled = false;
  document.getElementById("stop-btn").disabled = true;

  if (typeof (callback) == "function") {
    recorder && recorder.exportWAV(function (blob) {
      callback(blob);
      recorder.clear();
    }, (AudioFormat || "audio/wav"));
  }
}
