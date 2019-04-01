package io.gvox.phonecalltrap;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.Objects;

import static android.content.Context.TELEPHONY_SERVICE;

public class PhoneCallTrap extends CordovaPlugin {

    private CallStateListener listener;
    private AudioManager audioManager;
    private NotificationManager notificationManager = null;

    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {

        if (ActivityCompat.checkSelfPermission(cordova.getContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(cordova.getActivity(), new String[]{Manifest.permission.READ_PHONE_STATE}, 3);
        }

        if (ActivityCompat.checkSelfPermission(cordova.getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(cordova.getActivity(), new String[]{Manifest.permission.CALL_PHONE}, 3);
        }

        switch (action) {

            case "onCall":

                prepareListener();

                listener.setCallbackContext(callbackContext);
                break;
            case "rejectCall":
                try {
                    TelephonyManager tm = (TelephonyManager) cordova.getActivity().getSystemService(TELEPHONY_SERVICE);

                    @SuppressLint("PrivateApi")
                    Method m1 = Objects.requireNonNull(tm).getClass().getDeclaredMethod("getITelephony");
                    m1.setAccessible(true);
                    Object iTelephony = m1.invoke(tm);

                    Method m3 = iTelephony.getClass().getDeclaredMethod("endCall");

                    m3.invoke(iTelephony);
                } catch (NoSuchMethodException e) {
                    callbackContext.error("No such method error " + e.getMessage());
                } catch (Exception e3) {
                    callbackContext.error("Exception: " + e3.getMessage());
                }
                break;
            case "silenceCall":
                Context context = cordova.getContext();
                if (this.audioManager == null) {
                    this.audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                    listener.setAudioManager(this.audioManager);
                }

                if (this.notificationManager == null) {
                    this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                }
                if (this.audioManager != null && this.notificationManager != null) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || this.notificationManager.isNotificationPolicyAccessGranted()) {
                        int currentMode = this.audioManager.getRingerMode();
                        this.audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                        listener.setLastCallMode(currentMode);
                    } else {
                        cordova.getActivity().startActivity(new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS));
                    }
                }
                break;
            default:
                return false;
        }

        return true;
    }

    private void prepareListener() {
        if (listener == null) {
            listener = new CallStateListener();
            TelephonyManager TelephonyMgr = (TelephonyManager) cordova.getActivity().getSystemService(TELEPHONY_SERVICE);
            if (TelephonyMgr != null) {
                TelephonyMgr.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
            }
        }
    }
}

class CallStateListener extends PhoneStateListener {

    private CallbackContext callbackContext;
    private int lastCallMode = -1;
    private AudioManager audioManager;

    void setLastCallMode(int mode) {
        this.lastCallMode = mode;
    }

    void setAudioManager(AudioManager am) {
        this.audioManager = am;
    }

    void setCallbackContext(CallbackContext callbackContext) {
        this.callbackContext = callbackContext;
    }

    private void resetCallMode() {
        if (this.lastCallMode != -1 && this.audioManager != null) {
            this.audioManager.setRingerMode(this.lastCallMode);
            this.lastCallMode = -1;
        }
    }

    public void onCallStateChanged(int state, String incomingNumber) {
        super.onCallStateChanged(state, incomingNumber);
        this.resetCallMode();
        if (callbackContext == null) return;

        String msg = "";

        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE:
                msg = "IDLE";
                break;

            case TelephonyManager.CALL_STATE_OFFHOOK:
                msg = "OFFHOOK";
                break;

            case TelephonyManager.CALL_STATE_RINGING:
                msg = "RINGING";
                break;
        }

        // add number info
        JSONObject callResult = new JSONObject();
        try {
            callResult.put("state", msg);
            callResult.put("number", incomingNumber);
        } catch (JSONException e) {
            //squelch
        }

        PluginResult result = new PluginResult(PluginResult.Status.OK, callResult);
        result.setKeepCallback(true);

        callbackContext.sendPluginResult(result);
    }
}
