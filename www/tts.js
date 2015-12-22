/*

  Cordova Text-to-Speech Plugin
  https://github.com/vilic/cordova-plugin-tts

  by VILIC VANE
  https://github.com/vilic

  MIT License

*/

/**
 *
 * @param {DOMString} text
 * @param {Object} successCallback
 * @param {Object} errorCallback
 */
exports.speak = (text, successCallback, errorCallback) => {
  var options = {};

  if (typeof text == 'string') {
    options.text = text;
  } else {
    options = text;
  }

  cordova.exec(function () {successCallback();}, function (reason) {errorCallback(reason);}, 'TTS', 'speak', [options]);
};
/**
 * Stop any queued synthesized speech
 *
 * @param {DOMString} text
 * @param {Object} successCallback
 * @param {Object} errorCallback
 */
exports.interrupt = (text, successCallback, errorCallback) => {
  var options = {};

  if (typeof text == 'string') {
    options.text = text;
  } else {
    options = text;
  }

  cordova.exec(function () {successCallback();}, function (reason) {errorCallback(reason);}, 'TTS', 'interrupt', [options]);
};

/**
 * Stop any queued synthesized speech
 *
 * @param {Object} successCallback
 * @param {Object} errorCallback
 */
exports.stop = function(successCallback, errorCallback) {
  cordova.exec(function () {successCallback();}, function (reason) {errorCallback(reason);}, "TTS", "stop", []);
};
/**
 * Play silence for the number of ms passed in as duration
 *
 * @param {long} duration
 * @param {Object} successCallback
 * @param {Object} errorCallback
 */
exports.silence = function(duration, successCallback, errorCallback) {
  cordova.exec(function () {successCallback();}, function (reason) {errorCallback(reason);}, "TTS", "silence", [duration]);
};
/**
 * Set speed of speech.  Usable from 30 to 500.  Higher values make little difference.
 *
 * @param {long} speed
 * @param {Object} successCallback
 * @param {Object} errorCallback
 */
exports.speed = function(speed, successCallback, errorCallback) {
  cordova.exec(function () {successCallback();}, function (reason) {errorCallback(reason);}, "TTS", "speed", [speed]);
};
/**
 * Set pitch of speech.  Useful values are approximately 30 - 300
 *
 * @param {long} pitch
 * @param {Object} successCallback
 * @param {Object} errorCallback
 */
exports.pitch = function(pitch, successCallback, errorCallback) {
  cordova.exec(function () {successCallback();}, function (reason) {errorCallback(reason);}, "TTS", "pitch", [pitch]);
};
/**
 * Starts up the TTS Service
 *
 * @param {Object} successCallback
 * @param {Object} errorCallback
 */
exports.startup = function(successCallback, errorCallback) {
  console.log("TTS-Startup");
  cordova.exec(function () {successCallback();}, function (reason) {errorCallback(reason);}, "TTS", "startup", []);
};
/**
 * Shuts down the TTS Service if you no longer need it.
 *
 * @param {Object} successCallback
 * @param {Object} errorCallback
 */
exports.shutdown = function(successCallback, errorCallback) {
  cordova.exec(function () {successCallback();}, function (reason) {errorCallback(reason);}, "TTS", "shutdown", []);
};
/**
 * Finds out if the language is currently supported by the TTS service.
 *
 * @param {DOMString} lang
 * @param {Object} successCallback
 * @param {Object} errorCallback
 */
exports.isLanguageAvailable = function(lang, successCallback, errorCallback) {
  cordova.exec(function () {successCallback(res);}, function (reason) {errorCallback(reason);}, "TTS", "isLanguageAvailable", [lang]);
};
/**
 * Finds out the current language of the TTS service.
 *
 * @param {Object} successCallback
 * @param {Object} errorCallback
 */
exports.getLanguage = function(successCallback, errorCallback) {
  cordova.exec(function () {successCallback(res);}, function (reason) {errorCallback(reason);}, "TTS", "getLanguage", []);
};
/**
 * Sets the language of the TTS service.
 *
 * @param {DOMString} lang
 * @param {Object} successCallback
 * @param {Object} errorCallback
 */
exports.setLanguage = function(lang, successCallback, errorCallback) {
  console.log("changing language to: "+lang);
  cordova.exec(function () {successCallback();}, function (reason) {errorCallback(reason);}, "TTS", "setLanguage", [lang]);
};
