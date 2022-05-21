package com.example.hide.lasd5.lasd5;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import androidx.core.content.ContextCompat;

import com.example.hide.lasd5.MainActivity;
import com.example.hide.lasd5.R;

import java.io.File;

public class LASD5App {
    private static final String TAG = LASD5App.class.getSimpleName();

    private DictionaryEnum dictType;

    public static final int REQUEST_EXTERNAL_STORAGE_CODE = 0x01;
    private static String[] mPermissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    public LASD5App(DictionaryEnum dictType){
        this.dictType = dictType;
    }

    public void saveTopDir(Uri topDirUri) {
        Activity activity = MainActivity.getInstance();
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);

        String topDirString = topDirUri.toString();

        Log.d(TAG, "saveTopDir:uri string:" + topDirString);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(activity.getString(R.string.top_dir), topDirString);
        editor.commit();
    }

    public Uri getTopDirUri(){
        Activity activity = MainActivity.getInstance();
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);

        String topDirString = sharedPref.getString(activity.getString(R.string.top_dir), null);

        Log.d(TAG,"getTopDirUri:string:" + topDirString);
        Uri topDirUri = Uri.parse(topDirString);

        return topDirUri;
    }

    public File getTopDir(){
        String top = dictType.getTopDir();

        //File sdPath = Environment.getExternalStorageDirectory();
        //topDir = new File(sdPath, top);
        File topDir = new File("/storage/self/primary", top);

        return topDir;
    }

    public boolean hasDataDir(){
        File topDir = getTopDir();
        if(topDir.exists()){
            Log.d(TAG, "hasDataDir: exists !! topDir:" + topDir);
            return true;
        }else{
            Log.d(TAG, "hasDataDir: oops! topDir not found!" + topDir);
            return false;
        }
    }

    public boolean verifyStoragePermissions(Activity activity) {
        int readPermission = ContextCompat.checkSelfPermission(activity, mPermissions[0]);
        int writePermission = ContextCompat.checkSelfPermission(activity, mPermissions[1]);

        Log.d(TAG, "verifyStoragePermissions  (0 is granted) before:" + writePermission + ":" + readPermission);


        if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED) {
/*
            if(ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE) ){
                System.out.println("permission read: should not show request");
            }else{
                System.out.println("permission read: should show request");
            }
            if(ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) ){
                System.out.println("permission write: should not show request");
            }else{
                System.out.println("permission write: should show request");
            }
            ActivityCompat.requestPermissions(activity, mPermissions, REQUEST_EXTERNAL_STORAGE_CODE);
*/
            return false;
        }else{
            return true;
        }
    }

}
