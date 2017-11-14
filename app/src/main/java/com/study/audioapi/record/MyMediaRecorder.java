package com.study.audioapi.record;

import android.content.ContentValues;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.study.audioapi.R;

import java.io.File;
import java.io.IOException;

/**
 * 主要使用MediaRecorder中setAudioEncoder和setAudioSource方法进行音频捕获.
 * 准备录音之前调用,setOutputFormat：录制文件格式，setOutputFile:目标文件中.顺序影响很大.
 *      getMaxAmplitude:允许请求由MediaPlayer录制的音频的最大振幅。每次调用次方法都会重置改值
 *                      因此每次调用都将返回自从上一次调用以来的最大振幅。
 *                      可通过定期调用该方法实现音量表.
 *      setMaxDuration:允许以毫秒为单位指定最大录制持续时间。
 *                      必须在setOutputFormat之后和prepare方法之前调用.
 *      setMaxSizeFileSize:允许字节为单位录制最大文件大小。和setMaxDuration调用时间一样。
 *      setAudioChannels:允许录制的音频通道数。要在prepare之前调用
 *      setAudioEncodingBitRate:允许指定压缩音频时编码器所使用的码率
 *      setAudioSampleRate:允许指定捕获和编码的音频的采样率。硬件和使用编码器将会决定合适采样率。需在prepare方法之前调用。
 *
 * AudioSource内部类中定义常量：
 *         MediaRecorder.AudioSource.MIC
 *     还有MediaReorder.AudioSource.VOICE_CALL
 *         MediaReorder.AudioSource.VOICE_DOWNLINK
 *         MediaRecorder.AudioSource.VOICE_UPLINK
 *         CAMCOEDER和VOICE_RECONGNITION
 * MediaRecorder输出格式
 *      MediaRecorder.OutputFormat.MPEG_4：输出MPEG-4文件，可能同时包含音频和视频轨
 *      MediaRecorder.OutputFormat.RAW_AMR:输出没有容器原始文件。只有没有捕获没有视频的音频且音频编码是AMR_NB时才会使用这个常量。
 *      MediaRecorder.OutputFormat.THREE_GPP:常量指定输出文件3GPP文件，可能是.3gp，可同时包含音频和视频轨
 * */
public class MyMediaRecorder extends AppCompatActivity implements View.OnClickListener, MediaPlayer.OnCompletionListener {

    private Button mFinishButton;
    private Button mPlayButton;
    private Button mStartButton;
    private Button mStopButton;
    private TextView mStatusText;
    private MediaRecorder mMediaRecorder;
    private File mAudioFile;
    private MediaPlayer mMediaPlayer;
    private TextView mAmplitudeText;
    private boolean isStartRecording;
    private RecordAmplitude mRecordAmplitude;
    private Button mButtonStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_media_recorder);
        initView();
        initData();
    }

    private void initData() {

    }

    private void initView() {
        mFinishButton = (Button) findViewById(R.id.my_finish_button);
        mPlayButton = (Button) findViewById(R.id.my_play_record);
        mStartButton = (Button) findViewById(R.id.my_start_record);
        mStopButton = (Button) findViewById(R.id.my_stop_record);
        mButtonStore = (Button) findViewById(R.id.my_stop_record);
        mStatusText = (TextView) findViewById(R.id.my_status_text);
        mAmplitudeText = (TextView) findViewById(R.id.my_amplitude_text);
        mFinishButton.setOnClickListener(this);
        mButtonStore.setOnClickListener(this);
        mPlayButton.setOnClickListener(this);
        mStartButton.setOnClickListener(this);
        mStopButton.setOnClickListener(this);
        mStopButton.setEnabled(false);
        mPlayButton.setEnabled(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.my_start_record:
                mMediaRecorder = new MediaRecorder();
                mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

                File path=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/com.appress.proandroid.media.ch07.customrecorder/files/");
                path.mkdir();
                try {
                    mAudioFile = File.createTempFile("recording", ".3pg", path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mMediaRecorder.setOutputFile(mAudioFile.getAbsolutePath());
                //准备工作已完成

                try {
                    mMediaRecorder.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mMediaRecorder.start();
                isStartRecording=true;
                mRecordAmplitude = new RecordAmplitude();
                mRecordAmplitude.execute();
                mStatusText.setText("Recording");
                mPlayButton.setEnabled(false);
                mStopButton.setEnabled(true);
                mStartButton.setEnabled(false);
                break;
            case R.id.my_stop_record:
                mMediaRecorder.stop();
                mMediaRecorder.release();

                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setOnCompletionListener(this);
                try {
                    mMediaPlayer.setDataSource(mAudioFile.getAbsolutePath());
                    mMediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mStatusText.setText("Ready to play");
                mPlayButton.setEnabled(true);
                mStopButton.setEnabled(false);
                mStartButton.setEnabled(true);
                break;
            case R.id.my_play_record:
                mMediaPlayer.start();
                mStatusText.setText("Playing");
                mPlayButton.setEnabled(false);
                mStopButton.setEnabled(false);
                mStartButton.setEnabled(false);
                break;
            case R.id.my_store_record:
                ContentValues contentValues=new ContentValues();
                contentValues.put(MediaStore.MediaColumns.TITLE,"this isn't music");
                contentValues.put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis());
                contentValues.put(MediaStore.MediaColumns.DATA,mAudioFile.getAbsolutePath());
                Uri newUri=getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,contentValues);
                break;
            case R.id.my_finish_button:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mPlayButton.setEnabled(true);
        mStartButton.setEnabled(true);
        mStopButton.setEnabled(false);
        mStatusText.setText("Ready");
    }
    //执行长期运行的任务
    /**
     *
     * */
    private class RecordAmplitude extends AsyncTask<Void,Integer,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            while (isStartRecording){
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                publishProgress(mMediaRecorder.getMaxAmplitude());
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            mAmplitudeText.setText(values[0].toString());
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}
