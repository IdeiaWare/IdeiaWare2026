// Expose globally your audio_context, the recorder instance and audio_stream
var audio_context;
var recorder;
var audio_stream;


function Initialize() {

  try {
    // Monkeypatch for AudioContext, getUserMedia and URL
    window.AudioContext = window.AudioContext || window.webkitAudioContext;
    navigator.getUserMedia = (navigator.getUserMedia ||
            navigator.webkitGetUserMedia ||
            navigator.mozGetUserMedia ||
            navigator.msGetUserMedia);
    window.URL = window.URL || window.webkitURL;

    // Store the instance of AudioContext globally
    audio_context = new AudioContext;
    console.log('Audio context is ready !');
    console.log('navigator.getUserMedia ' + (navigator.getUserMedia ? 'available.' : 'not present!'));
  } catch (e) {
    alert('No web audio support in this browser!');
  }
}

/**
 * Starts the recording process by requesting the access to the microphone.
 * Then, if granted proceed to initialize the library and store the stream.
 *
 * It only stops when the method stopRecording is triggered.
 */
function startRecording() {
  // Access the Microphone using the navigator.getUserMedia method to obtain a stream
  navigator.getUserMedia({audio: true}, function (stream) {
    // Expose the stream to be accessible globally
    audio_stream = stream;
    // Create the MediaStreamSource for the Recorder library
    var input = audio_context.createMediaStreamSource(stream);
//         console.log('Media stream succesfully created');

    // Initialize the Recorder Library
    recorder = new Recorder(input);
//         console.log('Recorder initialised');

    // Start recording !
    recorder && recorder.record();
//         console.log('Recording...');

    // Disable Record button and enable stop button !
    document.getElementById("start-btn").disabled = true;
    document.getElementById("stop-btn").disabled = false;
  }, function (e) {
    console.error('No live audio input: ' + e);
  });
}

/**
 * Stops the recording process. The method expects a callback as first
 * argument (function) executed once the AudioBlob is generated and it
 * receives the same Blob as first argument. The second argument is
 * optional and specifies the format to export the blob either wav or mp3
 */
function stopRecording(callback, AudioFormat) {
  // Stop the recorder instance
  recorder && recorder.stop();
//     console.log('Stopped recording.');

  // Stop the getUserMedia Audio Stream !
  audio_stream.getAudioTracks()[0].stop();

  // Disable Stop button and enable Record button !
  document.getElementById("start-btn").disabled = false;
  document.getElementById("stop-btn").disabled = true;

  // Use the Recorder Library to export the recorder Audio as a .wav file
  // The callback providen in the stop recording method receives the blob
  if (typeof (callback) == "function") {

    /**
     * Export the AudioBLOB using the exportWAV method.
     * Note that this method exports too with mp3 if
     * you provide the second argument of the function
     */
    recorder && recorder.exportWAV(function (blob) {
      callback(blob);

      // create WAV download link using audio data blob
      // createDownloadLink();

      // Clear the Recorder to start again !
      recorder.clear();
    }, (AudioFormat || "audio/wav"));
  }
}
