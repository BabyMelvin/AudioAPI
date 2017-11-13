package com.study.audioapi;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class CustomAudioPlayer extends AppCompatActivity implements MediaPlayer.OnCompletionListener,View.OnClickListener,View.OnTouchListener{
    private static final String TAG = "CustomAudioPlayer";
    private MediaPlayer mMediaPlayer;
    private View mView;
    private int postion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_audio_player);
        initData();
    }

    private void initData() {
        mMediaPlayer = MediaPlayer.create(this, R.raw.fade);
        mMediaPlayer.setOnCompletionListener(this);
        mView = findViewById(R.id.the_view);
        mView.setOnTouchListener(this);
    }

    public void start(View view) {
        if(mMediaPlayer!=null){
            mMediaPlayer.start();
        }
    }

    public void stop(View view) {
        if(mMediaPlayer!=null){
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }
    }

    /**
     * 可以简单设置mMediaPlayer.setLooping(true),进行循环播放。
     * */
    @Override
    public void onCompletion(MediaPlayer mp) {
        mMediaPlayer.start();
        mMediaPlayer.seekTo(postion);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_MOVE:
                if(mMediaPlayer.isPlaying()){
                    postion= (int) event.getX()/mView.getWidth()*mMediaPlayer.getDuration();
                    Log.i(TAG, "onTouch: postion="+postion);
                    mMediaPlayer.seekTo(postion);
                }
                break;
            default:
                break;
        }
        return true;
    }
}
