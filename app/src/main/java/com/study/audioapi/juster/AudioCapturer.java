package com.study.audioapi.juster;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

/**********************************
 * Copyright 2018 SH-HG Inc.
 * Author:HangCao(Melvin)         
 * Email:hang.yasuo@gmail.com
 * ProNm:AudioAPI  （Application）  
 * Date: 2018/10/17.
 * Desc:  录制音频
 **********************************/
/*
    MediaRecorder和AudioRecord，提供音频采集API，前者更上层API，后者接近底层，灵活控制，可以得到PCM数据
    AudioRecord工作流程：
        1.配置参数，初始化内部音频数据缓冲区
        2.开始采集
        3.需要一个线程，不断地从AudioRecord缓冲区将音频数据"读"出来
            这个过程要及时否则出现overrun错误。
        4.停止采集
     参数：
        audioSource:DEFAULT默认，VOICE_RECOGNITION语音识别，等同DEFAULT,MIC麦克风
                    VOICE_COMMUNICATION用于VoIP应用
        sampleRateInHz:采样率，44100Hz唯一保证兼容Android手机采样率
        channelConfig:在AudioFormat中定义，常用于CHANNEL_IN_MONO单通道，CHANNEL_IN_STEREO双通道
        audioFormat:数据位宽，ENCODING_PCM_16BIT,ENCODING_PCM_8BIT,前者能够保证数据兼容性
        bufferSizeInBytes:AudioRecord内部音频缓冲区大小 int size=采样率x位宽x采样时间x通道数
 */
public class AudioCapturer {
    private static final String TAG = "AudioCapturer";

    private static final int DEFAULT_SOURCE = MediaRecorder.AudioSource.MIC;
    private static final int DEFAULT_SAMPLE_RATE = 44100;
    private static final int DEFAULT_CHANNL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int DEFAULT_AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private boolean mIsCaptureStarted = false;
    private int mMinBufferSize;
    private AudioRecord mAudioRecord;
    private boolean mIsLoopExit;
    private Thread mCaptureThread;
    private OnAudioFrameCatpuredListener mOnAudioFrameCatpuredListener;

    public interface OnAudioFrameCatpuredListener {
        void onAudioFrameCaptured(byte[] audioData);
    }

    public void setOnAudioFrameCatpuredListener(OnAudioFrameCatpuredListener listener) {
        mOnAudioFrameCatpuredListener = listener;
    }

    public boolean isCaptureStarted() {
        return mIsCaptureStarted;
    }

    public boolean startCapture() {
        return startCapture(DEFAULT_SOURCE, DEFAULT_SAMPLE_RATE, DEFAULT_CHANNL_CONFIG, DEFAULT_AUDIO_FORMAT);
    }

    public boolean startCapture(int audioSource, int sampleRateInHz, int channlConfig, int audioFormat) {
        if (mIsCaptureStarted) {
            Log.e(TAG, "startCapture: Capture already started !");
            return false;
        }
        mMinBufferSize = AudioRecord.getMinBufferSize(sampleRateInHz, channlConfig, audioFormat);
        if (mMinBufferSize == AudioRecord.ERROR_BAD_VALUE) {
            Log.e(TAG, "startCapture:invalid parameter! ");
            return false;
        }
        Log.d(TAG, "getMinBufferSize" + mMinBufferSize + " bytes!");
        mAudioRecord = new AudioRecord(audioSource, sampleRateInHz, channlConfig, audioFormat, mMinBufferSize);
        if (mAudioRecord.getState() == AudioRecord.STATE_UNINITIALIZED) {
            Log.e(TAG, "AudioRecord initialize fail!" );
            return false;
        }
        //todo 开始录制
        mAudioRecord.startRecording();
        mIsLoopExit = false;
        mCaptureThread = new Thread(new AudioCaptureRunnable());
        mCaptureThread.start();
        mIsCaptureStarted = true;
        Log.d(TAG, "start audio capture success! ");
        return true;
    }

    public void stopCapture(){
        if(!mIsCaptureStarted){
            return;
        }
        mIsLoopExit=true;
        try {
            mCaptureThread.interrupt();
            mCaptureThread.join(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(mAudioRecord.getRecordingState()==AudioRecord.RECORDSTATE_RECORDING){
            mAudioRecord.stop();
        }
        mAudioRecord.release();
        mIsCaptureStarted=false;
        mOnAudioFrameCatpuredListener=null;
        Log.d(TAG, "stop audio capture success!");
    }
    private class AudioCaptureRunnable implements Runnable {
        @Override
        public void run() {
            while (!mIsLoopExit) {
                byte[] buffer = new byte[mMinBufferSize];
                int ret = mAudioRecord.read(buffer, 0, mMinBufferSize);
                if (ret == AudioRecord.ERROR_INVALID_OPERATION) {
                    Log.e(TAG, "error ERROR_INVALID_OPERATION ");
                } else if (ret == AudioRecord.ERROR_BAD_VALUE) {
                    Log.e(TAG, "error ERROR_BAD_VALUE");
                } else {
                    if (mOnAudioFrameCatpuredListener != null) {
                        mOnAudioFrameCatpuredListener.onAudioFrameCaptured(buffer);
                    }
                    Log.d(TAG, "OK Captured " + ret + " bytes!");
                }
            }
        }
    }
}
