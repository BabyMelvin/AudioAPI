package com.study.audioapi.base_intent;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.study.audioapi.PermissionUtils;
import com.study.audioapi.R;

import java.io.File;

public class MyAudioPlayer extends AppCompatActivity {
    private static final String TAG = "MyAudioPlayer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: ");
        setContentView(R.layout.activity_my_audio_player);
        PermissionUtils.myRequest(this, Manifest.permission.READ_EXTERNAL_STORAGE,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 0:
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "授权", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this, "未授权", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    public void playMusic(View view) {
        Log.i(TAG, "playMusic: ");
        Intent intent=new Intent(Intent.ACTION_VIEW);
        File sdCard= Environment.getExternalStorageDirectory();
        File audioFile=new File(sdCard.getPath()+"/Netease/cloudmusic/Music/fade.mp3");
        Log.i(TAG, "playMusic: audioFile="+audioFile);
        intent.setDataAndType(Uri.fromFile(audioFile),"audio/mp3");
        startActivity(intent);
    }
}
