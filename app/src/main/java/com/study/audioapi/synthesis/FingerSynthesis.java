package com.study.audioapi.synthesis;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.study.audioapi.R;

public class FingerSynthesis extends AppCompatActivity implements View.OnTouchListener {
    private static final String TAG = "FingerSynthesis";
    private static final float BASE_FREQUENCY=440;
    private float synthFrequency=BASE_FREQUENCY;
    boolean play=false;
    private View mMainView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finger_synthesis);
        initView();

    }

    private void initView() {
        mMainView = findViewById(R.id.activity_finger_synthesis);
        mMainView.setOnTouchListener(this);
        new AudioSynthesisTask().execute();
    }

    @Override
    protected void onPause() {
        super.onPause();
        play=false;
        finish();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                play=true;
                synthFrequency=event.getX()+BASE_FREQUENCY;
                Log.i(TAG, "onTouch down: synthFrequency="+synthFrequency);
                break;
            case MotionEvent.ACTION_MOVE:
                play=true;
                synthFrequency=event.getX()+BASE_FREQUENCY;
                Log.i(TAG, "onTouch move: synthFrequency="+synthFrequency);
                break;
            case MotionEvent.ACTION_UP:
                play=false;
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            default:
                break;
        }
        return false;
    }
    private class AudioSynthesisTask extends AsyncTask<Void,Void,Void>{

        private AudioTrack mAudioTrack;

        @Override
        protected Void doInBackground(Void... params) {
            final int SAMPLE_RATE=11025;
            int minSize= AudioTrack.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_CONFIGURATION_MONO,AudioFormat.ENCODING_PCM_16BIT);
            mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, minSize, AudioTrack.MODE_STREAM);
            mAudioTrack.play();
            short[] buffer=new short[minSize];
            float angle=0;
            while (true){
                if(play){
                    for(int i=0;i<buffer.length;i++){
                        float angularFrequency= (float) (2*Math.PI*synthFrequency/SAMPLE_RATE);
                        buffer[i]= (short) (Short.MAX_VALUE*Math.sin(angle));
                        angle+=angularFrequency;
                    }
                    mAudioTrack.write(buffer,0,buffer.length);
                }else {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
