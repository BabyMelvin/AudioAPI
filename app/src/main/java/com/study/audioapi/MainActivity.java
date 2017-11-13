package com.study.audioapi;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        needPermission();
    }

    private void needPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
            Log.i(TAG, "onRequestPermissionsResult: ");
            Toast.makeText(this, "授权", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "未授权", Toast.LENGTH_SHORT).show();
        }
    }

    public void startInnerMusicPlayer(View view) {
        startActivity(new Intent(this,MyAudioPlayer.class));
    }

    public void startCustom(View view) {
        startActivity(new Intent(this,CustomAudioPlayer.class));
    }

    public void startBrowser(View view) {
        startActivity(new Intent(this,AudioBrowser.class));
    }

    public void startBackground(View view) {
        startActivity(new Intent(this,BackgroundAudio.class));
    }

    public void playNetMusic(View view) {
        startActivity(new Intent(this,AudioHttpPlayer.class));
    }

    public void playHttpStream(View view) {
        startActivity(new Intent(this,HTTPAudioPlaylistPlayer.class));
    }
}
