package com.study.audioapi.synthesis;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.study.audioapi.R;
/**
 * 数字音频合成(Digital Audio Synthesis).数字领域中，模拟电信号驱动扬声器来实现该操作
 * 数字音频系统会包含一个芯片或电路板来执行数字--模拟转换（Digital-to-Analog Conversion,DAC）
 * DAC将接受表示音频样本一系列数字作为数据，转换成电压，然后扬声器将把电压转换成声音。
 * */

public class AudioSynthesis extends AppCompatActivity implements View.OnClickListener {

    private Button mStartButton;
    private Button mStopButton;
    private boolean keepGoing;
    private AudioSynthesisTask mAudioSynthesisTask;
    private final int mSynthFrequncy=440;//440Hz,Middle A

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_synthesis);
        initView();
        initData();
    }

    private void initData() {
        mStopButton.setEnabled(false);
    }

    private void initView() {
        mStartButton = (Button) findViewById(R.id.sync_start_button);
        mStopButton = (Button) findViewById(R.id.sync_stop_button);
        mStartButton.setOnClickListener(this);
        mStopButton.setOnClickListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        keepGoing=false;
        mStopButton.setEnabled(false);
        mStartButton.setEnabled(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sync_start_button:
                keepGoing=true;
                mAudioSynthesisTask = new AudioSynthesisTask();
                mAudioSynthesisTask.execute();
                mStopButton.setEnabled(true);
                mStartButton.setEnabled(false);
                break;
            case R.id.sync_stop_button:
                keepGoing=false;
                mStopButton.setEnabled(false);
                mStartButton.setEnabled(true);
                break;
            default:
                break;
        }
    }
    private class AudioSynthesisTask extends AsyncTask<Void,Void,Void>{

        private AudioTrack mAudioTrack;

        @Override
        protected Void doInBackground(Void... params) {
            final int SAMPLE_RATE=11025;
            int minSize= AudioTrack.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_CONFIGURATION_MONO,AudioFormat.ENCODING_PCM_16BIT);
            mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, minSize, AudioTrack.MODE_STREAM);
            mAudioTrack.play();
            /*10个样本，样本集合的短波形*/
            short[] buffer={
                8130,15752,32065,12253,4329,
                    -3865,-19032,-32722,-16160,-466
            };
            /*可以使用正弦波*/
            short[] bufferSin=new short[minSize];
            float angluarFrequency= (float) ((2*Math.PI)*mSynthFrequncy/SAMPLE_RATE);
            float angle=0;
            while (keepGoing){
               // mAudioTrack.write(buffer,0,buffer.length);
                for(int i=0;i<bufferSin.length;i++){
                    /*固定Short.MAX_VALUE或Short.MIN_VALUE,实现平稳快速方波示例*/
                    bufferSin[i]= (short) (Short.MAX_VALUE*Math.sin(angle));
                    angle+=angluarFrequency;
                }
                mAudioTrack.write(bufferSin,0,bufferSin.length);
            }
            return null;
        }
    }
}
