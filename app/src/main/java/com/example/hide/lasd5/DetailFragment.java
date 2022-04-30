package com.example.hide.lasd5;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;

public class DetailFragment extends Fragment implements MainActivity.WordChangedListener{
    private static final String TAG = "DetailFragment";
    private static final String ARG_POSITION = "position";
    private int position;

    private WebView webView;

    static DetailFragment newInstance(int position) {
        DetailFragment f = new DetailFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        f.setArguments(args);
        return f;
    }


    public DetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            position = getArguments().getInt(ARG_POSITION);
        }

        Log.d(TAG, "onCreate:" + position);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView:" + position);

        MainActivity activity = (MainActivity)getActivity();

        webView = new WebView(activity);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d("aaa", "shouldOver:" + url);
                String[] s = url.split("/");
                String res = s[s.length - 2];
                String mp3 = s[s.length - 1];
                Log.d("aaa", "shouldOver:res:" + res + " mp3:" + mp3);

                MainActivity activity = (MainActivity)getActivity();
                activity.play(res, mp3);

                return true;
            }
        });

        FrameLayout frameLayout = new FrameLayout(activity);
        frameLayout.addView(webView);

        String html;
        if(position == 2){
            html = activity.getHtml();
        }else{
            html = activity.getSource();
        }
        webView.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null);

        activity.addWordChangeListener(this);

        return frameLayout;
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.d(TAG, "onStart:" + position);
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d(TAG, "onResume:" + position);
    }


    @Override
    public void onPause(){
        super.onPause();
        Log.d(TAG, "onPause:" + position);
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.d(TAG, "onStop:" + position);
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");

        MainActivity activity = (MainActivity)getActivity();
        activity.removeWordChangeListener(this);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d(TAG, "onDestroy:" + position);
    }

    public void wordChanged(){
        Log.d(TAG, "wordChanged: pos:" + position);

        MainActivity a = (MainActivity)getActivity();
        String html;
        if(position == 2){
            html = a.getHtml();
        }else{
            html = a.getSource();
        }
        webView.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null);
    }

    @Override
    public int getPosition(){
        return position;
    }
}
