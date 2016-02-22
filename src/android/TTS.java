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

    by VILIC VANE, modified by nash403 (https://github.com/nash403)
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
      tts = new TextToSpeech(cordova.getActivity().getApplicationContext(), this);
      tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
        @Override
        public void onStart(String callbackId) {
          if (!callbackId.equals("")) {
              CallbackContext context = new CallbackContext(callbackId, webView);
              PluginResult pr = new PluginResult(PluginResult.Status.OK,"START");
              pr.setKeepCallback(true);
              context.sendPluginResult(pr);
              //context.success();
          }
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
        return true;
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

    private void speak(JSONArray args, CallbackContext callbackContext)
            throws JSONException, NullPointerException {
        JSONObject params = args.getJSONObject(0);

        if (params == null) {
            callbackContext.error(ERR_INVALID_OPTIONS);
            return;
        }

        String text;
        String locale;
        String textId = null;
        double rate,pitch;

        if (tts == null) {
            callbackContext.error(ERR_ERROR_INITIALIZING);
            return;
        }

        if (!isReady()) {
            callbackContext.error(ERR_NOT_INITIALIZED);
            return;
        }
        if (params.isNull("text")) {
            callbackContext.error(ERR_INVALID_OPTIONS);
            return;
        } else {
            text = params.getString("text");
        }
        
        if (!params.isNull("id")) {
            textId = params.getString("id");
        }

        if (!params.isNull("locale")) {
          locale = params.getString("locale");
          tts.setLanguage(new Locale(locale));
        }

        if (!params.isNull("rate")) {
          rate = params.getDouble("rate");
          tts.setSpeechRate((float) rate);
        }

        if (!params.isNull("pitch")) {
          pitch = params.getDouble("pitch");
          tts.setPitch((float) pitch);
        }

        HashMap<String, String> ttsParams = new HashMap<String, String>();
        ttsParams.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, callbackContext.getCallbackId());


        tts.speak(text, TextToSpeech.QUEUE_ADD, ttsParams);
        if (textId != null){
            PluginResult pr = new PluginResult(PluginResult.Status.OK,"TTS-speaking-id:"+textId);
            pr.setKeepCallback(true);
            callbackContext.sendPluginResult(pr);
        }
    }
    private void interrupt(JSONArray args, CallbackContext callbackContext)
            throws JSONException, NullPointerException {
        JSONObject params = args.getJSONObject(0);

        if (params == null) {
            callbackContext.error(ERR_INVALID_OPTIONS);
            return;
        }

        String text;
        String locale;
        String textId = null;
        double rate,pitch;

        if (tts == null) {
            callbackContext.error(ERR_ERROR_INITIALIZING);
            return;
        }

        if (!isReady()) {
            callbackContext.error(ERR_NOT_INITIALIZED);
            return;
        }
        if (params.isNull("text")) {
            callbackContext.error(ERR_INVALID_OPTIONS);
            return;
        } else {
            text = params.getString("text");
        }
        
        if (!params.isNull("id")) {
            textId = params.getString("id");
        }

        if (!params.isNull("locale")) {
          locale = params.getString("locale");
          tts.setLanguage(new Locale(locale));
        }

        if (!params.isNull("speed")) {
          rate = params.getDouble("speed");
          tts.setSpeechRate((float) rate);
        }

        if (!params.isNull("pitch")) {
          pitch = params.getDouble("pitch");
          tts.setPitch((float) pitch);
        }

        HashMap<String, String> ttsParams = new HashMap<String, String>();
        ttsParams.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, callbackContext.getCallbackId());

        tts.speak(text, TextToSpeech.QUEUE_FLUSH, ttsParams);
        if (textId != null){
            PluginResult pr = new PluginResult(PluginResult.Status.OK,"TTS-speaking-id:"+textId);
            pr.setKeepCallback(true);
            callbackContext.sendPluginResult(pr);
        }
    }
    private void stop(JSONArray args, CallbackContext callbackContext)
      throws JSONException, NullPointerException {
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
    private void silence(JSONArray args, CallbackContext callbackContext)
      throws JSONException, NullPointerException {
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
    private void speed(JSONArray args, CallbackContext callbackContext)
      throws JSONException, NullPointerException {
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
      callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK));
    }
    private void pitch(JSONArray args, CallbackContext callbackContext)
      throws JSONException, NullPointerException {
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
      callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK));
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
    private void isLanguageAvailable(JSONArray args, CallbackContext callbackContext)
      throws JSONException, NullPointerException {
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
    private void setLanguage(JSONArray args, CallbackContext callbackContext)
      throws JSONException, NullPointerException {
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
