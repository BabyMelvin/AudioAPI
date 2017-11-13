package com.study.audioapi;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by dell on 2017/11/13.
 */

public class PermissionUtils {
    public static void  myRequest(Context context,String string,String[] strings,int code){
        if(ContextCompat.checkSelfPermission(context,string)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions((Activity) context,strings,code);
        }
    }
}
