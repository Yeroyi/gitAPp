package com.jbufa.firefighting.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;

import com.jbufa.firefighting.R;

import java.io.IOException;

public class SoundPlayUtils {

    // SoundPool对象
    public static SoundPool mSoundPlayer = new SoundPool(10,
            AudioManager.STREAM_SYSTEM, 5);
    public static SoundPlayUtils soundPlayUtils;
    // 上下文
    static Context mContext;
    private static MediaPlayer mediaPlayer;

    /**
     * 初始化
     *
     * @param context
     */
    public static SoundPlayUtils init(Context context) {
        if (soundPlayUtils == null) {
            soundPlayUtils = new SoundPlayUtils();
        }

        // 初始化声音
        mContext = context;

        mSoundPlayer.load(mContext, R.raw.alarm, 1);// 1

        return soundPlayUtils;
    }

    /**
     * 播放声音
     *
     * @param soundID
     */
    public static void play(int soundID) {
        mSoundPlayer.play(soundID, 1, 1, 0, -1, 1);

    }

    /**
     * 播放声音
     *
     * @param soundID
     */
    public static void Stop(int soundID) {
        mSoundPlayer.stop(soundID);

    }

    /**
     * 播放声音 不能同时播放多种音频
     * 消耗资源较大
     * @param rawId
     */
    public static void playSoundByMedia(Context mContext,int rawId) {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);
            try {
                AssetFileDescriptor file = mContext.getResources().openRawResourceFd(
                        rawId);
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(0.50f, 0.50f);
                mediaPlayer.prepare();
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            Log.e("ceshi","音效开始播放");
            mediaPlayer.seekTo(0);

        }
    };
    public static void stopSound() {
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=null;
        }
    }
}
