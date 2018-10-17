package com.study.audioapi.juster;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

/**********************************
 * Copyright 2018 SH-HG Inc.
 * Author:HangCao(Melvin)         
 * Email:hang.yasuo@gmail.com     
 * ProNm:AudioAPI  （Application）  
 * Date: 2018/10/17.
 * Desc:播放一帧音频
 **********************************/
/*
 * MediaPlayer,SoundPool,AudioTrack
 *   MediaPlayer更适合在后台长时间播放本地音乐文件或者在线流式资源
 *   SoundPool时候播放比较短的音频片段，比如游戏声音，按键，铃声片段等，可同时播放多个音频
 *   AudioTrack更接近底层，非常强大的控制功能，适合流媒体和VoIPd语音电话等场景
 *
 *   AudioTrack工作流程
 *       1.配置参数，初始化内部音频缓冲区
 *       2.开始播放
 *       3.需要一个线程，不断向AudioTrack缓冲区写入音频数据
 *           一定要及时，否则出现underrun错误
 *       4.停止播放，释放资源
 *
 *   参数：
 *       streamType:代表哪一个音频管理策略，当系统有多个需要播放音频时，管理策略会最终展现效果。
 *                   在AudioManager类中，包括：
 *                   STREAM_VOICE_CALL:电话声音
 *                   STREAM_SYSTEM:系统声音
 *                   STREAM_RING:铃声
 *                   STREAM_MUSIC:音乐声
 *                   STREAM_ALARM:警告声
 *                   STREAM_NOTIFICATION:通知声
 *       sampleInRateHz:采样率范围必须在4000Hz~192000Hz之间
 *       channelConfig:CHANNEL_IN_MONO（单通道）,CHANNEL_IN_STEREO(双通道)
 *       audioFormat:位宽
 *       bufferSizeInBytes:
 *       mode:两种模式，static和streaming方式。
 *           static需要一次性将所有数据写入缓冲区中，简单高效，通常用于播放铃声，系统提醒的声音片段
 *           streaming:按照一定时间间隔不断写入音频数据，理论上可用于任何音频播放的场景
 * */
public class AudioPlayer {
    private static final String TAG = "AudioPlayer";

    private static final int DEFAULT_STREAM_TYPE = AudioManager.STREAM_MUSIC;
    private static final int DEFAULT_SAMPLE_RATE = 44100;
    private static final int DEFAULT_CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_STEREO;
    private static final int DEFAULT_AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private static final int DEFAULT_PLAY_MODE = AudioTrack.MODE_STREAM;

    private boolean mIsPlayStarted = false;
    private int mMinBufferSize = 0;
    private AudioTrack mAudioTrack;
    private int mMinBufferSize1;

    public boolean startPlayer() {
        return startPlayer(DEFAULT_STREAM_TYPE, DEFAULT_SAMPLE_RATE, DEFAULT_CHANNEL_CONFIG, DEFAULT_AUDIO_FORMAT);
    }

    public boolean startPlayer(int streamType, int sampleRate, int channelConfig, int audioFormat) {
        if (mIsPlayStarted) {
            Log.e(TAG, "Player already started ! ");
            return false;
        }
        mMinBufferSize1 = AudioTrack.getMinBufferSize(sampleRate, channelConfig, audioFormat);
        if (mMinBufferSize == AudioTrack.ERROR_BAD_VALUE) {
            Log.e(TAG, "Invalid parameter !");
            return false;
        }
        Log.d(TAG, "getMinBufferSize= " + mMinBufferSize + " bytes!");
        mAudioTrack = new AudioTrack(streamType, sampleRate, channelConfig, audioFormat, mMinBufferSize, DEFAULT_PLAY_MODE);
        if (mAudioTrack.getState() == AudioTrack.ERROR_BAD_VALUE) {
            Log.e(TAG, "AudioTrack initialize fail !");
            return false;
        }
        mIsPlayStarted = true;
        Log.d(TAG, "Start audio player success !");
        return true;
    }

    public int getMinBufferSize() {
        return mMinBufferSize;
    }

    public void stopPlayer() {
        if (!mIsPlayStarted) {
            return;
        }
        if (mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
            mAudioTrack.stop();
        }
        mAudioTrack.release();
        mIsPlayStarted = false;
        Log.d(TAG, "Stop audio player success !");
    }

    public boolean play(byte[] audioData, int offsetInBytes, int sizeInBytes) {
        if (!mIsPlayStarted) {
            Log.e(TAG, "Player not started");
            return false;
        }
        if (sizeInBytes < mMinBufferSize) {
            Log.e(TAG, "audio data is not enough!");
            return false;
        }
        if (mAudioTrack.write(audioData, offsetInBytes, sizeInBytes) != sizeInBytes) {
            Log.e(TAG, "could not write all the samples to the audio device !");
        }
        mAudioTrack.play();
        Log.d(TAG, "Ok played" + sizeInBytes + "bytes !");
        return true;
    }
}
