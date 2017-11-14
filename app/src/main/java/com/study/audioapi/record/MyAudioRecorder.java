package com.study.audioapi.record;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.study.audioapi.R;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * AudioRecord方法比较灵活
 * AudioRecord和AudioTrack进行播放
 * */
public class MyAudioRecorder extends AppCompatActivity implements View.OnClickListener {

    //声明对象内部缓冲区大小的一半
    //private short[] mBuffer=new short[mBufferSize/4];
    private TextView mStatusText;
    private Button mStartRecordButton;
    private Button mStopRecordButton;
    private Button mStartPlayButton;
    private Button mStopPlayButton;

    private boolean isPlaying=false;
    private boolean isRecording=false;
    //这些应该是常量
    private final int FREQUENCY=11025;
    private final int CHANNEL_CONFIGURATION= AudioFormat.CHANNEL_CONFIGURATION_MONO;
    private final int AUDIO_ENCODING=AudioFormat.ENCODING_PCM_16BIT;
    private File mRecordFile;
    private RecordAudio mRecordAudio;
    private short[] mBuffer;
    private PlayAudio mPlayAudio;
    private AudioTrack mAudioTrack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_audio_recorder);
        initView();
        initData();
    }

    private void initData() {
        File path=new File(Environment.getExternalStorageDirectory().getAbsoluteFile()+"/Android/data/com.apress.proandroidmediao.ch07.altaudiorecorder/files/");
        path.mkdir();
        try {
            mRecordFile = File.createTempFile("recording", ".pcm", path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        mStatusText = (TextView) findViewById(R.id.audio_status_text);
        mStartRecordButton = (Button) findViewById(R.id.audio_start_record_button);
        mStopRecordButton = (Button) findViewById(R.id.audio_stop_record_button);
        mStartPlayButton = (Button) findViewById(R.id.audio_start_playback_button);
        mStopPlayButton = (Button) findViewById(R.id.audio_stop_playback_button);
        mStartPlayButton.setOnClickListener(this);
        mStopPlayButton.setOnClickListener(this);
        mStartRecordButton.setOnClickListener(this);
        mStopRecordButton.setOnClickListener(this);

        mStopRecordButton.setEnabled(false);
        mStartPlayButton.setEnabled(false);
        mStopPlayButton.setEnabled(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.audio_start_playback_button:
                startPlay();
                break;
            case R.id.audio_stop_playback_button:
                stopPlay();
                break;
            case R.id.audio_start_record_button:
                startRecord();
                break;
            case R.id.audio_stop_record_button:
                stopRecord();
                break;
            default:
                break;
        }
    }

    private void stopPlay() {
        isPlaying=false;
    }

    private void startPlay() {
        mStartPlayButton.setEnabled(false);
        mPlayAudio = new PlayAudio();
        mPlayAudio.execute();
        mStopPlayButton.setEnabled(true);
    }

    private void startRecord() {
        mStartRecordButton.setEnabled(false);
        mStopRecordButton.setEnabled(true);
        //for fun
        mStartPlayButton.setEnabled(true);
        mRecordAudio = new RecordAudio();
        mRecordAudio.execute();
    }

    private void stopRecord() {
        isRecording=false;
    }
    private class RecordAudio extends AsyncTask<Void,Integer,Void>{

        private AudioRecord mAudioRecord;
        private int mBufferSize;
        private DataOutputStream mDataOutputStream;

        @Override
        protected Void doInBackground(Void... params) {
            isRecording=true;
            try {
                    mDataOutputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(mRecordFile)));
                    mBufferSize = AudioRecord.getMinBufferSize(FREQUENCY, CHANNEL_CONFIGURATION,AUDIO_ENCODING);
                    mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, FREQUENCY, CHANNEL_CONFIGURATION, AUDIO_ENCODING, mBufferSize);
                    mBuffer = new short[mBufferSize];
                    mAudioRecord.startRecording();

                int r=0;
                while (isRecording){
                    int bufferReadResult=mAudioRecord.read(mBuffer,0,mBufferSize);
                    for(int i=0;i<bufferReadResult;i++){
                            mDataOutputStream.writeShort(mBuffer[i]);
                    }
                    publishProgress(new Integer(r));
                    r++;
                }
                mAudioRecord.stop();
                mDataOutputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            mStatusText.setText(values[0].toString());
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mStartRecordButton.setEnabled(true);
            mStopRecordButton.setEnabled(false);
            mStartPlayButton.setEnabled(true);
        }
    }
    private class PlayAudio extends AsyncTask<Void,Integer,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            isPlaying=true;
            try {
                int bufferSize= AudioTrack.getMinBufferSize(FREQUENCY, CHANNEL_CONFIGURATION,AUDIO_ENCODING);
                short[] audioData=new short[bufferSize/4];
                DataInputStream dis=new DataInputStream(new BufferedInputStream(new FileInputStream(mRecordFile)));
                mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, FREQUENCY, CHANNEL_CONFIGURATION, AUDIO_ENCODING, bufferSize, AudioTrack.MODE_STREAM);
                mAudioTrack.play();
                while (isPlaying&&dis.available()>0){
                    int i=0;
                    while (dis.available()>0&&i<audioData.length){
                        audioData[i]=dis.readShort();
                        i++;
                    }
                    mAudioTrack.write(audioData,0,audioData.length);
                }
                dis.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mStartPlayButton.setEnabled(false);
            mStopPlayButton.setEnabled(true);
        }
    }
}
