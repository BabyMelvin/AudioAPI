package com.study.audioapi;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

public class AudioHttpPlayer extends AppCompatActivity implements View.OnClickListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnPreparedListener {
    private static final String TAG = "AudioHttpPlayer";
    private MediaPlayer mMediaPlayer;
    private TextView mStatusText;
    private TextView mUnknownText;
    private TextView mBufferText;
    private Button mStartHttp;
    private Button mStopHttp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_http_player);
        initView();
        initData();
    }
    /**@"http://wvideo.spriteapp.cn/video/2016/0328/56f8ec01d9bfe_wpd.mp4",
     *@"http://baobab.wdjcdn.com/1456117847747a_x264.mp4",
    * @"http://baobab.wdjcdn.com/14525705791193.mp4",
     @"http://baobab.wdjcdn.com/1456459181808howtoloseweight_x264.mp4",
     @"http://baobab.wdjcdn.com/1455968234865481297704.mp4",
     @"http://baobab.wdjcdn.com/1455782903700jy.mp4",
     @"http://baobab.wdjcdn.com/14564977406580.mp4",
     @"http://baobab.wdjcdn.com/1456316686552The.mp4",
     @"http://baobab.wdjcdn.com/1456480115661mtl.mp4",
     @"http://baobab.wdjcdn.com/1456665467509qingshu.mp4",
     @"http://baobab.wdjcdn.com/1455614108256t(2).mp4",
     @"http://baobab.wdjcdn.com/1456317490140jiyiyuetai_x264.mp4",
     @"http://baobab.wdjcdn.com/1455888619273255747085_x264.mp4",
     @"http://baobab.wdjcdn.com/1456734464766B(13).mp4",
     @"http://baobab.wdjcdn.com/1456653443902B.mp4",
     @"http://baobab.wdjcdn.com/1456231710844S(24).mp4
     * http://www.szzx1000.com/homepage/yuwen/wyzl/PSCzuopin/11.mp3
     * */

    private void initData() {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        try {
            mMediaPlayer.setDataSource("http://www.szzx1000.com/homepage/yuwen/wyzl/PSCzuopin/11.mp3");
            mStatusText.setText("setDataResource Done");
            mStatusText.setText("calling AsyncPrepare");
            mMediaPlayer.prepareAsync();
            mMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        mStatusText = (TextView) findViewById(R.id.text_status);
        mUnknownText = (TextView) findViewById(R.id.text_unknown);
        mBufferText = (TextView) findViewById(R.id.text_buffer);
        mStartHttp = (Button) findViewById(R.id.start_button_http);
        mStopHttp = (Button) findViewById(R.id.stop_button_http);
        mStartHttp.setOnClickListener(this);
        mStopHttp.setOnClickListener(this);
        mStopHttp.setEnabled(false);
        mStopHttp.setEnabled(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.start_button_http:
                mMediaPlayer.start();
                mStatusText.setText("start called");
                mStartHttp.setEnabled(false);
                mStopHttp.setEnabled(true);
                break;
            case R.id.stop_button_http:
                mMediaPlayer.stop();
                mStatusText.setText("stop called");
                mStartHttp.setEnabled(true);
                break;
            default:
                break;
        }

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mStatusText.setText("onCompletion called");
        mStopHttp.setEnabled(false);
        mStartHttp.setEnabled(true);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.i(TAG, "onError: "+extra);
        switch (what){
            //一般视频播放比较慢或视频本身有问题会引发
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                mStatusText.setText("MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK"+extra);
                Log.i(TAG, "onError: "+extra);
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                mStatusText.setText("MEDIA_ERROR_SERVER_DIED"+extra);
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                mStatusText.setText("MEDIA_ERROR_UNKNOWN"+extra);
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        mBufferText.setText(""+percent+"%");
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mStatusText.setText("onPrepared called");
        mStartHttp.setEnabled(true);
    }
}
