package com.example.hide.lasd5;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.SearchManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import android.provider.DocumentsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.hide.lasd5.lasd5.CreateDataFragment;
import com.example.hide.lasd5.lasd5.DictionaryEnum;
import com.example.hide.lasd5.lasd5.LASD5FilesConfigCftEnum;
import com.example.hide.lasd5.lasd5.LASD5App;
import com.example.hide.lasd5.lasd5.LDOCE5FilesConfigCftEnum;
import com.example.hide.lasd5.lasd5.dict.Dictionary;
import com.example.hide.lasd5.lasd5.dict.FilesConfigCft;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements Dictionary.DictionaryStateListener,
        SearchView.OnQueryTextListener,
        ActivityCompat.OnRequestPermissionsResultCallback,
        CreateDataFragment.DialogListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private Dictionary dic;
    private HashSet<WordChangedListener> listenerSet;
    private String searchWord = null;
    private boolean clipOrSend = false;
    private LASD5App lasd5App;

    private static final DictionaryEnum dicType = DictionaryEnum.LASD5;

    private static MainActivity instance = null;


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    public static MainActivity getInstance(){
        return instance;
    }

    public interface WordChangedListener {
        void wordChanged();

        int getPosition();
    }

    /**
     * The PagerAdapter that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a FragmentStatePagerAdapter.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent");

        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                Log.d(TAG, "onNewInstance:text:" + sharedText);

                searchWord = sharedText;
                clipOrSend = true;
            }
        }/* else if (Intent.ACTION_SEARCH.equals(action)) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.d(TAG, "onNewInstance:query:" + query);

            showWord(query);
        }
        */
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        instance = this;

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        listenerSet = new HashSet<>();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("simple text", "Hello, World!");

                Log.d(TAG, "fab_pressed");

                if (clipboard.hasPrimaryClip()) {
                    ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);

                    CharSequence pasteData = item.getText();
                    Log.d(TAG, "fab_pressed:clip:" + pasteData);
                    showWord(pasteData.toString());
                }
            }
        });

        lasd5App = new LASD5App(dicType);
        if(lasd5App.hasDataDir()){
            Log.d(TAG,"onCreate: hasDataDir!");
            boolean permitted = lasd5App.verifyStoragePermissions(this);
            Uri topUri = lasd5App.getTopDirUri();

            if(topUri == null){
                permitted = false;
            }else{
                permitted = true;
            }
            permitted = false;
            if(permitted){
                Log.d(TAG, "onCreate: permitted!");

                //Uri topUri = lasd5App.getTopDirUri();
                createDic(topUri);
            }else{
                Log.d(TAG,"onCreate: not permitted!!!!");

                openPicker();
            }
        }else{
            Log.d(TAG, "onCreate: hasDataDir false!!!");
            dic = null;
        }

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private static final int READ_FILES = 1;

    public void openPicker() {
        Log.d(TAG,"openPicker");
        // system's file picker.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);

        //    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);

        // Optionally, specify a URI for the directory that should be opened in
        // the system file picker when it loads.
   //     Uri uriToLoad = Uri.fromFile(topDir);
   //     Log.d(TAG, "openDirectory:uriToLoad:" + uriToLoad);

   //     intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uriToLoad);

        startActivityForResult(intent, READ_FILES);
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        Log.d(TAG, "onActivityResult:");
        if (requestCode == READ_FILES && resultCode == Activity.RESULT_OK) {
            if (resultData != null) {
                Log.d(TAG, "onActivityResult: get resultData");
                Uri topUri = resultData.getData();

                final int takeFlags = resultData.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                // Check for the freshest data.
                MainActivity.getInstance().getContentResolver().takePersistableUriPermission(topUri, takeFlags);

                lasd5App.saveTopDir(topUri);
                createDic(topUri);
            }
        }
    }

    private void createDic(Uri topUri){
        DocumentFile topDir = DocumentFile.fromTreeUri(this, topUri);
        DocumentFile dataDir = topDir.findFile(dicType.getDataDir());

        if(dataDir == null){
            dic = null;
            return;
        }
        Log.d(TAG, "createDic:topDir:" + topDir.getName() + "  dataDir:" + dataDir.getName());

        FilesConfigCft fs;
        FilesConfigCft gb;
        FilesConfigCft us;
        FilesConfigCft exa;
        FilesConfigCft sfx;

        if(dicType.equals(DictionaryEnum.LASD5)){
            fs = LASD5FilesConfigCftEnum.FS;
            gb = LASD5FilesConfigCftEnum.GB_HWD_PRON;
            us = LASD5FilesConfigCftEnum.US_HWD_PRON;
            exa = LASD5FilesConfigCftEnum.EXA_PRON;
            sfx = LASD5FilesConfigCftEnum.SFX;
        } else {
            fs = LDOCE5FilesConfigCftEnum.FS;
            gb = LDOCE5FilesConfigCftEnum.GB_HWD_PRON;
            us = LDOCE5FilesConfigCftEnum.US_HWD_PRON;
            exa = LDOCE5FilesConfigCftEnum.EXA_PRON;
            sfx = LDOCE5FilesConfigCftEnum.SFX;
        }

        dic = new Dictionary(dataDir, fs, gb, us, exa, sfx);
        dic.addListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Log.d(TAG, "onStart");
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(Action.TYPE_VIEW,
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct. Otherwise, set the URL to null.
                Uri.parse("http://host/path"), // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.hide.lasd5/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        if(clipOrSend){
            showWord(searchWord);

            clipOrSend = false;
        }else if (clipboard.hasPrimaryClip()) {
            ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);

            CharSequence pasteData = item.getText();
            Log.d(TAG, "onResume:clip:" + pasteData);
            showWord(pasteData.toString());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(Action.TYPE_VIEW,
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct. Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.hide.lasd5/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        Log.d(TAG, "onStop");

        client.disconnect();

        if(dic != null) {
            dic.save();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.action_craetedata:
                showCreateDataDialog();
                return true;
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showCreateDataDialog(){
        DialogFragment dialog = new CreateDataFragment();
        dialog.show(getSupportFragmentManager(), "CreateDataFragment");
    }

    @Override
    public void onDialogStartClick(DialogFragment dialog) {
        Log.d(TAG, "onDialogStartClick");
    }

    private void showWord(String word) {
        word = word.trim();
        if(dic != null && dic.hasWord(word)){
            dic.setWord(word);
        }
    }

    public void wordChanged() {
        Log.d(TAG, "wordChanged");

        for (WordChangedListener l : listenerSet){
            l.wordChanged();
        }

        dic.showHistory();

        mViewPager.setCurrentItem(2);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return (Fragment)HistoryFragment.newInstance(position);
                case 1:
                    return WordListFragment.newInstance(position);
                case 2:
                    return DetailFragment.newInstance(position);
                case 3:
                    return DetailFragment.newInstance(position);
            }
            return null;

            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            // return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // 1:headword
            // 2:detail(html)
            // 3:detail(source)
            // 4:history
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.label_tab3).toUpperCase(l);
                case 1:
                    return getString(R.string.label_tab0).toUpperCase(l);
                case 2:
                    return getString(R.string.label_tab1).toUpperCase(l);
                case 3:
                    return getString(R.string.label_tab2).toUpperCase(l);
            }
            return null;
        }
    }

    public void addWordChangeListener(WordChangedListener l) {
        Log.d(TAG, "addWordChangeListener:" + l.getPosition());

        listenerSet.add(l);
    }

    public void removeWordChangeListener(WordChangedListener l) {
        Log.d(TAG, "removeWordChangeListener:" + l.getPosition());

        listenerSet.remove(l);
    }

    public String getHtml() {
        if(dic != null) {
            return dic.getHtml();
        }else{
            return "";
        }
    }

    public String getSource() {
        if (dic != null) {
            return dic.getSource();
        } else {
            return "";
        }
    }

    public int getListPosition() {
        if (dic != null) {
            return dic.getListPosition();
        } else {
            return 0;
        }
    }

    public void play(String res, String filename) {
        byte[] mp3 = dic.getMp3(res, filename);

        if(mp3 != null){
            playMp3(mp3);
        }
    }

    private void playMp3(byte[] mp3) {
        try {
            File temp = File.createTempFile("lasd5pron", "mp3", this.getCacheDir());
            temp.deleteOnExit();
            FileOutputStream fos = new FileOutputStream(temp);
            fos.write(mp3);
            fos.close();

            FileInputStream fis = new FileInputStream(temp);

            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(fis.getFD());
            mediaPlayer.prepare();
            mediaPlayer.start();

        } catch (IOException ex) {
            Log.d(TAG, "playMp3:" + ex.toString());
            ex.printStackTrace();
        }
    }

    public Dictionary getDictionary(){ return dic; }


    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.d(TAG, "onQueryTextSubmit:" + query);

        showWord(query);

        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
