package com.example.hide.lasd5.lasd5.dict;

import android.util.Log;

import androidx.documentfile.provider.DocumentFile;

import java.util.HashSet;

/**
 * Created by hide on 8/10/14.
 */
public class Dictionary {
    private static final String TAG = Dictionary.class.getSimpleName();

    private DocumentFile dataDir;
    private DocumentFile saveDir;

    private FilesSkn fs;
    private FilesSkn fsgb;
    private FilesSkn fsus;
    private FilesSkn fsexa;
    private FilesSkn fssfx;

    private SideList sideList;
    private Headword currentWord;
    private History history;

    private final HashSet<DictionaryStateListener> listenerSet;

    public interface DictionaryStateListener {
        void wordChanged();
    }

    public Dictionary(DocumentFile dataDir, FilesConfigCft f, FilesConfigCft gb, FilesConfigCft us, FilesConfigCft exa, FilesConfigCft sfx) {
        listenerSet = new HashSet<>();
        currentWord = null;
        this.dataDir = dataDir;
        saveDir = dataDir.getParentFile();

        fs = new FilesSkn(this.dataDir, f, "TEXTTITLE.tda");
        fsgb = new FilesSkn(this.dataDir, gb, "NAME.tda");   // 発音データ
        fsus = new FilesSkn(this.dataDir, us, "NAME.tda");   // 発音データ
        fsexa = new FilesSkn(this.dataDir, exa, "NAME.tda");  // 音声データ
        fssfx = new FilesSkn(this.dataDir, sfx, "NAME.tda");  // 音声データ

        sideList = fs.getSideList(this.dataDir, f);

        history = new History(saveDir);
    }

    public void addListener(DictionaryStateListener l){
        listenerSet.add(l);
    }

    public void removeListener(DictionaryStateListener l){
        listenerSet.remove(l);
    }

    public String getHtml() {
        if(currentWord == null){
            return "";
        }

        Log.d(TAG, "getHtml:" + currentWord.toString());

        int pos = currentWord.getIndex();

        String html = "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\">";
        html += "<link rel=\"stylesheet\" href=\"css/lasd5.css\" type=\"text/css\">";
        html += "</head><body>\n";
        html += fs.getHtml(pos);
        html += "<br><br><br><br><br><br>";
        html += "</body></html>\n";

        return html;
    }

    public String getSource() {
        if(currentWord == null){
            return "";
        }

        int pos = currentWord.getIndex();

        String source = "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\">";
        source += "</head><body><xmp>\n";
        source += fs.getSource(pos);
        source += "</xmp></body></html>\n";

        return source;
    }

    public Headword getHeadword(int pos) {
        return sideList.get(pos);
    }

    public Headword getHistoryHW(int pos){
        if(pos < 0){
            return null;
        }else {
            int n = history.get(pos);
            Headword hw = sideList.get(n);

            return hw;
        }
    }

    public int getSideListSize(){
        return sideList.getSize();
    }

    public boolean hasWord(String word){
        return sideList.search(word) != null;
    }

    public void setWord(String word){
        Log.d(TAG, "setWord:" + word);

        Headword hw = sideList.search(word);
        if(hasWord(word)) {
            setWord(hw);
        }
    }

    /*
    sideList 中の pos 番目の単語の Headword を保存
     */
    public void setWord(int listPos){
        Headword hw = sideList.get(listPos);

        Log.d(TAG, "selectSideList: listPos:" + listPos + " headword:" + hw.toString());

        setWord(hw);
    }

    public void setWord(Headword hw){
        currentWord = hw;
        history.push(sideList.getListIndex(currentWord));
        history.write(saveDir);
        wordChanged();
    }

    private void wordChanged(){
        Log.d(TAG, "wordChanged headword:" + currentWord.toString());

        for(DictionaryStateListener l : listenerSet){
            l.wordChanged();
        }
    }

    public int getListPosition() {
        if (currentWord != null) {
            return sideList.getListIndex(currentWord);
        }else{
            return -1;
        }
    }

    public byte[] getMp3(String res, String filename) {
        Log.d(TAG, "play: res:" + res + " filename:" + filename);

        FilesSkn fs = null;

        if (res.equals("gb_hwd_pron")) {
            fs = fsgb;
        } else if (res.equals("us_hwd_pron")) {
            fs = fsus;
        } else if (res.equals("exa_pron")) {
            fs = fsexa;
        } else if (res.equals("sfx")) {
            fs = fssfx;
        } else {
            System.out.println("play: unknown audio resource:" + res);
        }

        if (fs != null) {
            return fs.getContent(filename);
        }else{
            return null;
        }
    }

    public int getHistorySize(){
        return history.getSize();
    }

    public void save(){
        history.write(saveDir);
    }

    public void showHistory(){
        Log.d(TAG, "showHistory:" + history.toString());
    }
}
