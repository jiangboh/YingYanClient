package com.jiangboh.bti.yingyanclient.PublicUnit;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static android.content.ContentValues.TAG;


public class MusicPlayer {

    private static MediaPlayer mediaPlayer;

    private static AssetFileDescriptor assetFileDescriptor;

    private static AudioManager audioManager;

    private static AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {

        @Override
        public void onAudioFocusChange(int focusChange)
        {
            Log.w(TAG,"audio focus change to " + focusChange);
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS
                    || focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT
                    || focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK
                    || focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    Log.w(TAG,"audio focus loss...");
                    mediaPlayer.pause();
                } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                    Log.w(TAG,"audio focus gain...");
                    mediaPlayer.start();
                }
            }
        }
    };

    public static void init(Context context) {
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setLooping(true);
        try {
            assetFileDescriptor = context.getAssets().openFd("Kalimba.mp3");
        } catch (IOException e) {
            Log.w(TAG,e);
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                audioManager.abandonAudioFocus(onAudioFocusChangeListener);
                Log.w(TAG,"play completely and abandon audio focus");
//                    mediaPlayer.stop();
                    /*mediaPlayer.start();
                    mediaPlayer.setLooping(true);*/
            }
        });
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                mp.setLooping(true);
                Log.w(TAG,"start play...");
            }
        });
    }

    public static void play(Context context) {
        init(context);
        if (mediaPlayer.isPlaying()) {
            return;
        }
        if (isAudioFocused()) {
            Log.w(TAG,"audio is on focus");
            return;
        }
        int result = audioManager.requestAudioFocus(
                onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_FAILED) { // 获取焦点失败
            Log.w(TAG,"request audio focus failed");
            return;
        }
        Log.w(TAG,"request audio focus successfully");
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(), assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            Log.w(TAG,e);
        }
    }

    private static boolean isAudioFocused() {
        boolean isFocused = false;
        try {
            Method method = Class.forName("android.media.AudioSystem").getMethod("isStreamActive", int.class, int.class);
            if (method != null) {
                boolean music = (boolean) method.invoke(null, AudioManager.STREAM_MUSIC, 0);
                boolean alarm = (boolean) method.invoke(null, AudioManager.STREAM_ALARM, 0);
                boolean ring = (boolean) method.invoke(null, AudioManager.STREAM_RING, 0);
                boolean voiceCall = (boolean) method.invoke(null, AudioManager.STREAM_VOICE_CALL, 0);
                boolean notification = (boolean) method.invoke(null, AudioManager.STREAM_NOTIFICATION, 0);
                boolean system = (boolean) method.invoke(null, AudioManager.STREAM_SYSTEM, 0);
                boolean bluetoothSco = (boolean) method.invoke(null, 6, 0);
                boolean systemEnforced = (boolean) method.invoke(null, 7, 0);
                boolean dtmf = (boolean) method.invoke(null, AudioManager.STREAM_DTMF, 0);
                boolean tts = (boolean) method.invoke(null, 9, 0);
                isFocused = music || alarm || ring || voiceCall || notification || system || bluetoothSco || systemEnforced || dtmf || tts;
                /*SyncLogUtil.i("music:" + music);
                SyncLogUtil.i("alarm:" + alarm);
                SyncLogUtil.i("ring:" + ring);
                SyncLogUtil.i("voiceCall:" + voiceCall);
                SyncLogUtil.i("notification:" + notification);
                SyncLogUtil.i("system:" + system);
                SyncLogUtil.i("bluetoothSco:" + bluetoothSco);
                SyncLogUtil.i("systemEnforced:" + systemEnforced);
                SyncLogUtil.i("dtmf:" + dtmf);
                SyncLogUtil.i("tts:" + tts);*/
            } else {
                Log.w(TAG,"android.media.AudioSystem.isStreamActive method is null");
            }
        } catch (Exception e) {
            Log.w(TAG,e);
        }
        return isFocused;
    }

    public static void recycle() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        assetFileDescriptor = null;

        try {
            Method method = audioManager.getClass().getMethod("unregisterAudioFocusListener", AudioManager.OnAudioFocusChangeListener.class);
            if (method != null) {
                method.invoke(audioManager, onAudioFocusChangeListener);
                Log.w(TAG,"unregisterAudioFocusListener successfully");
            }
        } catch (NoSuchMethodException e) {
            Log.w(TAG,e);
        } catch (InvocationTargetException e) {
            Log.w(TAG,e);
        } catch (IllegalAccessException e) {
            Log.w(TAG,e);
        } finally {
            audioManager = null;
        }
    }
}
