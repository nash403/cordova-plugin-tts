package com.wordsbaking.cordova.tts;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.UtteranceProgressListener;

import java.util.HashMap;
import java.util.Locale;

/*
    Cordova Text-to-Speech Plugin
    https://github.com/vilic/cordova-plugin-tts

    by VILIC VANE
    https://github.com/vilic

    MIT License
*/

public class TTS extends CordovaPlugin implements OnInitListener {

    public static final String ERR_INVALID_OPTIONS = "ERR_INVALID_OPTIONS";
    public static final String ERR_NOT_INITIALIZED = "ERR_NOT_INITIALIZED";
    public static final String ERR_ERROR_INITIALIZING = "ERR_ERROR_INITIALIZING";
    public static final String ERR_UNKNOWN = "ERR_UNKNOWN";

    boolean ttsInitialized = false;
    TextToSpeech tts = null;

    @Override
    public void initialize(CordovaInterface cordova, final CordovaWebView webView) {
      ttsInitialized = false;
      tts = new TextToSpeech(cordova.getActivity().getApplicationContext(), this);
      tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
        @Override
        public void onStart(String s) {
          // do nothing
        }

        @Override
        public void onDone(String callbackId) {
          if (!callbackId.equals("")) {
              CallbackContext context = new CallbackContext(callbackId, webView);
              context.success();
          }
        }

        @Override
        public void onError(String callbackId) {
          if (!callbackId.equals("")) {
              CallbackContext context = new CallbackContext(callbackId, webView);
              context.error(ERR_UNKNOWN);
          }
        }
      });
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {
      try {
        if (action.equals("speak")) {
          speak(args, callbackContext);
        }
        else if (action.equals("interrupt")) {
          interrupt(args, callbackContext);
        }
        else if (action.equals("stop")) {
          stop(args, callbackContext);
        }
        else if (action.equals("silence")) {
          silence(args, callbackContext);
        }
        else if (action.equals("speed")) {
          speed(args, callbackContext);
        }
        else if (action.equals("pitch")) {
          pitch(args, callbackContext);
        }
        else if (action.equals("startup")) {
          startup(args, callbackContext);
        }
        else if (action.equals("shutdown")) {
          shutdown(args, callbackContext);
        }
        else if (action.equals("getLanguage")) {
          getLanguage(args, callbackContext);
        }
        else if (action.equals("isLanguageAvailable")) {
          isLanguageAvailable(args, callbackContext);
        }
        else if (action.equals("setLanguage")) {
          setLanguage(args, callbackContext);
        }
      } catch (JSONException e) {
        e.printStackTrace();
        callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.JSON_EXCEPTION));
      }
      return false;
    }

    /**
     * Called when the TTS service is initialized.
     *
     * @param status
     */
    @Override
    public void onInit(int status) {
        if (status != TextToSpeech.SUCCESS) {
            tts = null;
        } else {
            // warm up the tts engine with an empty string
            HashMap<String, String> ttsParams = new HashMap<String, String>();
            ttsParams.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "");
            tts.setLanguage(new Locale("fr", "FR"));
            tts.speak("", TextToSpeech.QUEUE_FLUSH, ttsParams);

            ttsInitialized = true;
        }
    }
    /**
     * Clean up the TTS resources
     */
    public void onDestroy() {
      if (tts != null) {
        tts.shutdown();
      }
    }
    /**
     * Is the TTS service ready to play yet?
     *
     * @return
     */
    private boolean isReady() {
      return ttsInitialized;
    }

    private String beforeSpeak(JSONArray args, CallbackContext callbackContext) {
      JSONObject params = args.getJSONObject(0);

      if (params == null) {
          callbackContext.error(ERR_INVALID_OPTIONS);
          return null;
      }

      String text;
      String locale;
      double rate;

      if (params.isNull("text")) {
          callbackContext.error(ERR_INVALID_OPTIONS);
          return null;
      } else {
          text = params.getString("text");
      }

      if (params.isNull("locale")) {
          locale = "fr-FR";
      } else {
          locale = params.getString("locale");
      }

      if (params.isNull("rate")) {
          rate = 1.0;
      } else {
          rate = params.getDouble("rate");
      }

      if (tts == null) {
          callbackContext.error(ERR_ERROR_INITIALIZING);
          return null;
      }

      if (!isReady()) {
          callbackContext.error(ERR_NOT_INITIALIZED);
          return null;
      }

      String[] localeArgs = locale.split("-");
      tts.setLanguage(new Locale(localeArgs[0], localeArgs[1]));
      tts.setSpeechRate((float) rate);
      return text;
    }

    private void speak(JSONArray args, CallbackContext callbackContext)
            throws JSONException, NullPointerException {
        String text = beforeSpeak(args,callbackContext);
        if (text == null) return;
        HashMap<String, String> ttsParams = new HashMap<String, String>();
        ttsParams.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, callbackContext.getCallbackId());

        tts.speak(text, TextToSpeech.QUEUE_ADD, ttsParams);
    }
    private void interrupt(JSONArray args, CallbackContext callbackContext)
            throws JSONException, NullPointerException {
        String text = beforeSpeak(args,callbackContext);
        if (text == null) return;
        HashMap<String, String> ttsParams = new HashMap<String, String>();
        ttsParams.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, callbackContext.getCallbackId());

        tts.speak(text, TextToSpeech.QUEUE_FLUSH, ttsParams);
    }
    private void stop(JSONArray args, CallbackContext callbackContext) {
      if (tts == null) {
          callbackContext.error(ERR_ERROR_INITIALIZING);
          return;
      }

      if (!isReady()) {
          callbackContext.error(ERR_NOT_INITIALIZED);
          return;
      }
      tts.stop();
    }
    private void silence(JSONArray args, CallbackContext callbackContext) {
      if (tts == null) {
          callbackContext.error(ERR_ERROR_INITIALIZING);
          return;
      }

      if (!isReady()) {
          callbackContext.error(ERR_NOT_INITIALIZED);
          return;
      }
      HashMap<String, String> ttsParams = new HashMap<String, String>();
      ttsParams.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, callbackContext.getCallbackId());

      tts.playSilence(args.getLong(0), TextToSpeech.QUEUE_ADD, ttsParams);
    }
    private void speed(JSONArray args, CallbackContext callbackContext) {
      if (tts == null) {
          callbackContext.error(ERR_ERROR_INITIALIZING);
          return;
      }

      if (!isReady()) {
          callbackContext.error(ERR_NOT_INITIALIZED);
          return;
      }
      float speed = (float) (args.optLong(0, 100)) /(float) 100.0;
      tts.setSpeechRate(speed);
    }
    private void pitch(JSONArray args, CallbackContext callbackContext) {
      if (tts == null) {
          callbackContext.error(ERR_ERROR_INITIALIZING);
          return;
      }

      if (!isReady()) {
          callbackContext.error(ERR_NOT_INITIALIZED);
          return;
      }
      float pitch = (float) (args.optLong(0, 100)) /(float) 100.0;
      tts.setPitch(pitch);
    }
    private void startup(JSONArray args, CallbackContext callbackContext) {
      this.initialize(cordova, webView);
    }
    private void shutdown(JSONArray args, CallbackContext callbackContext) {
      if (tts != null) tts.shutdown();
    }
    private void getLanguage(JSONArray args, CallbackContext callbackContext) {
      if (tts == null) {
        callbackContext.error(ERR_ERROR_INITIALIZING);
        return;
      }

      if (!isReady()) {
        callbackContext.error(ERR_NOT_INITIALIZED);
        return;
      }
      String result = tts.getLanguage().toString();
      callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK,result));
    }
    private void isLanguageAvailable(JSONArray args, CallbackContext callbackContext) {
      if (tts == null) {
        callbackContext.error(ERR_ERROR_INITIALIZING);
        return;
      }

      if (!isReady()) {
        callbackContext.error(ERR_NOT_INITIALIZED);
        return;
      }
      Locale loc = new Locale(args.getString(0));
      int available = tts.isLanguageAvailable(loc);
      String result = available < 0 ? "false" : "true";
      callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK,result));
    }
    private void setLanguage(JSONArray args, CallbackContext callbackContext) {
      if (tts == null) {
        callbackContext.error(ERR_ERROR_INITIALIZING);
        return;
      }

      if (!isReady()) {
        callbackContext.error(ERR_NOT_INITIALIZED);
        return;
      }
      Locale loc = new Locale(args.getString(0));
      int available = tts.setLanguage(loc);
      String result = available < 0 ? "false" : "true";
      callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK,result));
    }
}
