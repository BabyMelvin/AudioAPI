package com.study.audioapi.record;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.study.audioapi.R;

public class IntentAudioRecorder extends AppCompatActivity implements View.OnClickListener, MediaPlayer.OnCompletionListener {

    private Button mPlayRecording;
    private Button mRecordButton;
    private final int RECORD_REQUEST=1;
    private Uri mAudioFileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intent_audio_recorder);
        initView();
        initData();
    }

    private void initData() {

    }

    private void initView() {
        mPlayRecording = (Button) findViewById(R.id.intent_play_recording);
        mRecordButton = (Button) findViewById(R.id.intent_record);
        mPlayRecording.setOnClickListener(this);
        mRecordButton.setOnClickListener(this);
        mPlayRecording.setEnabled(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.intent_play_recording:
                MediaPlayer mediaPlayer=MediaPlayer.create(this,mAudioFileUri);
                mediaPlayer.setOnCompletionListener(this);
                mediaPlayer.start();
                mPlayRecording.setEnabled(false);
                break;
            case R.id.intent_record:
                Intent intent=new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                startActivityForResult(intent,RECORD_REQUEST);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case RECORD_REQUEST:
                if(requestCode== RESULT_OK){
                    mAudioFileUri = data.getData();
                    mPlayRecording.setEnabled(true);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mPlayRecording.setEnabled(true);
    }
}
