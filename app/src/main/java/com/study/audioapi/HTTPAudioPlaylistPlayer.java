package com.study.audioapi;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

/**
 * Android上的MediaPlayer不能自动解析M3U文件。创建一基于HTTP流式音频播放器，
 * 必须自己处理分析工作，同时将MediaPlayer用于实际的播放器。
 *
 * 该Activity完成，解析并播放来自己连机广播电台的M3U文件或者URL字段中输入的任何M3U文件
 * */
public class HTTPAudioPlaylistPlayer extends AppCompatActivity implements MediaPlayer.OnPreparedListener
,View.OnClickListener,MediaPlayer.OnCompletionListener{
    private static final String TAG = "HTTPAudioPlaylistPlayer";

    private EditText mUrlEditText;
    private TextView mPlayListText;
    private Button mParseButton;
    private Button mPlayButton;
    private Button mStopButton;
    private MediaPlayer mMediaPlayer;
    private Vector mPlaylistItem;
    private int currentPlaylistItemNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_httpaudio_playlist_player);
        initView();
        initData();
    }

    private void initData() {
        mUrlEditText.setText("http://live.kboo.fm:8000/high.m3u");
        mPlayButton.setEnabled(false);
        mStopButton.setEnabled(false);
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(this);
    }

    private void initView() {
        mUrlEditText = (EditText) findViewById(R.id.http_edit_text_url);
        mPlayListText = (TextView) findViewById(R.id.http_play_list_text);
        mParseButton = (Button) findViewById(R.id.http_button_parse);
        mPlayButton = (Button) findViewById(R.id.http_play_button);
        mStopButton = (Button) findViewById(R.id.http_stop_button);

        mParseButton.setOnClickListener(this);
        mPlayButton.setOnClickListener(this);
        mStopButton.setOnClickListener(this);



    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mStopButton.setEnabled(true);
        Log.i(TAG, "onPrepared: ");
        mMediaPlayer.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.http_button_parse:
                parsePlaylistFile();
                break;
            case R.id.http_play_button:
                playPlaylistItems();
                break;
            case R.id.stop_button_http:
                stop();
                break;
            default:
                break;
        }
    }

    private void stop() {
        mMediaPlayer.pause();
        mPlayButton.setEnabled(true);
        mStopButton.setEnabled(false);
    }

    private void playPlaylistItems() {
        mPlayButton.setEnabled(false);
        currentPlaylistItemNumber=0;
        if(mPlaylistItem.size()>0){
            String path=((PlaylistFile)mPlaylistItem.get(currentPlaylistItemNumber)).getFilePath();
            try {
                mMediaPlayer.setDataSource(path);
                mMediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void parsePlaylistFile() {
        mPlaylistItem = new Vector();
        //从Web获取M3U文件
        try {
            URL url = new URL(mUrlEditText.getText().toString());
            Log.i(TAG, "parsePlaylistFile: Url="+url);
            HttpURLConnection httpURLConnection= (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(30000);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            int responseCode = httpURLConnection.getResponseCode();
            if(responseCode==httpURLConnection.HTTP_OK){
                Log.i(TAG, "parsePlaylistFile: responseCode="+responseCode);
                InputStream inputstream=httpURLConnection.getInputStream();
                BufferedReader bufferReader=new BufferedReader(new InputStreamReader(inputstream));
                String line;
                while((line=bufferReader.readLine())!=null){
                    Log.i(TAG, "parsePlaylistFile: orig="+line);
                    if(line.startsWith("#")){
                        //元数据
                    }else if(line.length()>0){
                        //不是空行长度大于0,一个播放列表条目
                        String filePath="";
                        if(line.startsWith("http://")){
                            //假定一个完完整URL
                            filePath=line;
                        }else {
                            //假定它是相对的
                            filePath=httpURLConnection.getURL().toString();
                        }
                        //将它添加到播放列表条目的向量中
                        PlaylistFile playlistFile=new PlaylistFile(filePath);
                        mPlaylistItem.add(playlistFile);
                    }
                }
                inputstream.close();
                mPlayButton.setEnabled(true);
            }else {
                Log.i(TAG, "parsePlaylistFile: responseCode="+responseCode);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.i(TAG, "onCompletion: ");
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        if(mPlaylistItem.size()>currentPlaylistItemNumber+1){
            currentPlaylistItemNumber++;
            String path=((PlaylistFile)mPlaylistItem.get(currentPlaylistItemNumber)).getFilePath();
            try {
                mMediaPlayer.setDataSource(path);
                mMediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    class PlaylistFile{
        String filePath;
        PlaylistFile(String _filePath){
            filePath=_filePath;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }
    }
}
