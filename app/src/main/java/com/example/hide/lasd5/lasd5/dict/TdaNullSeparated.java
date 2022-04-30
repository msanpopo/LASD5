package com.example.hide.lasd5.lasd5.dict;

import android.net.Uri;
import android.util.Log;

import androidx.documentfile.provider.DocumentFile;

import com.example.hide.lasd5.MainActivity;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
    textTitle.tda(見出し語), NAME.tda(音声ファイルの名前)のデータ形式
 */
public class TdaNullSeparated {
    private final String TAG = "Tda";
    private final ArrayList<String> l = new ArrayList<>();

    public TdaNullSeparated(DocumentFile file) {
        Uri uri = file.getUri();
        try (InputStream inputStream = MainActivity.getInstance().getContentResolver().openInputStream(uri);
             DataInputStream is = new DataInputStream(new BufferedInputStream(Objects.requireNonNull(inputStream)))) {

            StringBuilder sb = new StringBuilder();

            while (is.available() > 0) {
                byte b = is.readByte();
                if (b != 0x0) {
                    sb.append((char) b);
                } else {
                    l.add(sb.toString());
                    sb = new StringBuilder();
                }
            }

        } catch (IOException ex) {
            Log.d(TAG, "read index:" + ex);
        }

    }

    public int getSize() {
        return l.size();
    }

    public String get(int i) {
        return l.get(i);
    }

    public int indexOf(String text){
        return l.indexOf(text); // if not found, return -1.
    }
}