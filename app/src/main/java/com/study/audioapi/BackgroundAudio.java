package com.study.audioapi;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.study.audioapi.service.BackgroundService;

public class BackgroundAudio extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "BackgroundAudio";

    private Button mStartButton;
    private Button mStopButton;
    private Intent mIntentService;
    private Button mHaveFun;
    private BackgroundService mBinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backgroud_audio);
        Log.i(TAG, "onCreate: ");
        initView();
        initData();
    }

    private void initData() {
        mIntentService = new Intent(this, BackgroundService.class);
    }

    private void initView() {
        mStartButton = (Button) findViewById(R.id.start_play_background);
        mStopButton = (Button) findViewById(R.id.stop_play_background);
        mHaveFun = (Button) findViewById(R.id.have_fun);
        mHaveFun.setOnClickListener(this);
        mStartButton.setOnClickListener(this);
        mStopButton.setOnClickListener(this);
    }
    /***
     * 连接的信息情况
     */

    private ServiceConnection mServiceConnection=new ServiceConnection() {



        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinder = ((BackgroundService.BackgroundAudioServiceBinder) service).getService();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
                mBinder=null;
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.start_play_background:
                Log.i(TAG, "onClick: 0");
                startService(mIntentService);
                bindService(mIntentService,mServiceConnection, Service.BIND_AUTO_CREATE);
                //finish();
                break;
            case R.id.stop_play_background:
                Log.i(TAG, "onClick: 1");
                stopService(mIntentService);
                unbindService(mServiceConnection);
               // finish();
                break;
            case R.id.have_fun:
                    if(mBinder!=null){
                        mBinder.haveFun();
                    }
                break;
            default:
                break;
        }
    }
}
