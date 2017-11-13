package com.study.audioapi.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.study.audioapi.R;

public class BackgroundService extends Service implements MediaPlayer.OnCompletionListener{
    private static final String TAG = "BackgroundService";
    private MediaPlayer mMediaPlayer;
    private final IBinder baseBinder=new BackgroundAudioServiceBinder();
    public class BackgroundAudioServiceBinder extends Binder{
        public BackgroundService getService(){
            return BackgroundService.this;
        }
    }

    public BackgroundService() {
    }
    /**
     * first create
     * */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: ");
        mMediaPlayer = MediaPlayer.create(this, R.raw.fade);
        mMediaPlayer.setOnCompletionListener(this);
    }
    /**
     * startService called
     * START_STICKY：sticky的意思是“粘性的”。使用这个返回值时，我们启动的服务跟应用程序"粘"在一起，如果在执行完onStartCommand后，
     *              服务被异常kill掉，系统会自动重启该服务。当再次启动服务时，传入的第一个参数将为null;
     * START_NOT_STICKY：“非粘性的”。使用这个返回值时，如果在执行完onStartCommand后，服务被异常kill掉，系统不会自动重启该服务。
     * START_REDELIVER_INTENT：重传Intent。使用这个返回值时，如果在执行完onStartCommand后，服务被异常kill掉，
     *                         系统会自动重启该服务，并将Intent的值传入。
     * */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand: ");
        if(!mMediaPlayer.isPlaying()){
            mMediaPlayer.start();
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind: ");
        return baseBinder;
    }

    /**
     * stopService
     * */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
        if(mMediaPlayer.isPlaying()){
            mMediaPlayer.stop();
        }
        mMediaPlayer.release();
        stopSelf();
    }
    public void haveFun(){
        if(mMediaPlayer.isPlaying()){
            //倒回到几秒钟，2.5秒
            mMediaPlayer.seekTo(mMediaPlayer.getCurrentPosition()-2500);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.i(TAG, "onCompletion: ");
        stopSelf();
    }
}
