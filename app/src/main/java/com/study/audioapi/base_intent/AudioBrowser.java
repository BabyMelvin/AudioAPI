package com.study.audioapi.base_intent;

import android.Manifest;
import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.study.audioapi.PermissionUtils;
import com.study.audioapi.R;

import java.io.File;

public class AudioBrowser extends ListActivity {
    private static final String TAG = "AudioBrowser";

    private ListView mListView;
    private static int STATE_SELECT_ALBUM=0;
    private static int STATE_SELECT_SONG=-1;
    int currentState=STATE_SELECT_ALBUM;
    private Cursor mCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_browser);
        Log.i(TAG, "onCreate: ");
        initView();
        initData();
    }

    private void initData() {
        Log.i(TAG, "initData: ");
        PermissionUtils.myRequest(this, Manifest.permission.READ_EXTERNAL_STORAGE,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        String[] columns={MediaStore.Audio.Albums._ID,
        MediaStore.Audio.Albums.ALBUM};
        mCursor = managedQuery(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, columns, null, null, null);
        String[] displayFields=new String[]{MediaStore.Audio.Albums.ALBUM};
        int[] displayViews=new int[]{android.R.id.text1};
        setListAdapter(new SimpleCursorAdapter(this,android.R.layout.simple_expandable_list_item_1,mCursor,displayFields,displayViews));
    }

    private void initView() {
        Log.i(TAG, "initView: ");
      //  mListView = (ListView) findViewById(R.id.list_browser);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
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

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Log.i(TAG, "onListItemClick: 0");
        super.onListItemClick(l, v, position, id);
        if(currentState==STATE_SELECT_ALBUM){
            Log.i(TAG, "onListItemClick:1 ");
            if(mCursor.moveToPosition(position)){
                String[] columns={
                    MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.MIME_TYPE,
                };
                String where= MediaStore.Audio.Media.ALBUM+"=?";
                String whereVal[]={mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM))};
                String orderBy= MediaStore.Audio.Media.TITLE;
                mCursor=managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,columns,where,whereVal,orderBy);
                String[] displayFields=new String[]{MediaStore.Audio.Media.DISPLAY_NAME};
                int[] displayViews=new int[]{android.R.id.text1};
                setListAdapter(new SimpleCursorAdapter(this,android.R.layout.simple_list_item_1,mCursor,displayFields,displayViews));
                currentState=STATE_SELECT_SONG;
            }else if(currentState==STATE_SELECT_SONG){
                Log.i(TAG, "onListItemClick: 1");
                if(mCursor.moveToPosition(position)) {
                    int fileColumn=mCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
                    int mimeTypeColumn=mCursor.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE);
                    String audioFilePath=mCursor.getString(fileColumn);
                    String mimeType=mCursor.getString(mimeTypeColumn);
                    Intent intent=new Intent(Intent.ACTION_VIEW);
                    File newFile=new File(audioFilePath);
                    intent.setDataAndType(Uri.fromFile(newFile),mimeType);
                    startActivity(intent);
                }
            }
        }
    }
}
