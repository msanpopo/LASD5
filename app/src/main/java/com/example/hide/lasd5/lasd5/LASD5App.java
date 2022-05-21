package com.example.hide.lasd5.lasd5;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import com.example.hide.lasd5.MainActivity;
import com.example.hide.lasd5.R;

public class LASD5App {
    private static final String TAG = LASD5App.class.getSimpleName();

    public LASD5App(){
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
}
