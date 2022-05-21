package com.example.hide.lasd5.lasd5.dict;


import android.net.Uri;
import android.util.Log;

import androidx.documentfile.provider.DocumentFile;

import com.example.hide.lasd5.MainActivity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

public class History{
    private final String TAG = "History";
    private static final String HISTORY = "history.dat";

    private int[] history;   // history[0]:lenght , history[1]... データ
    private DocumentFile saveDir;

    private int SIZE = 16;

    public History(DocumentFile saveDir){
        this.saveDir = saveDir;
        history = new int[SIZE];
        for(int i = 0; i < SIZE; ++i){
            history[i] = -1;
        }

        read(saveDir);
    }

    public void write(DocumentFile saveDir){
        Log.d(TAG, "writeHistory:" + saveDir);

        DocumentFile targetFile = saveDir.findFile(HISTORY);
        if(targetFile != null && targetFile.exists()){
            targetFile.delete();
        }
        targetFile = saveDir.createFile("application/octet-stream", HISTORY);
        Uri file = targetFile.getUri();

        try (OutputStream outputStream = MainActivity.getInstance().getContentResolver().openOutputStream(file);
             DataOutputStream out = new DataOutputStream(new BufferedOutputStream(Objects.requireNonNull(outputStream)))) {

            out.writeInt(history.length);   // [0]:lenght
            for (int aHistory : history) {
                out.writeInt(aHistory);     // [1]...:各データ
            }

            out.close();
        } catch (IOException e) {
            Log.d(TAG, "writeHistory:IOException:" + e);
        }
    }

    private void read(DocumentFile saveDir){
        Log.d(TAG, "readHistory" + saveDir);

        DocumentFile targetFile = saveDir.findFile(HISTORY);
        if(targetFile != null && targetFile.exists()) {
            Uri file = targetFile.getUri();

            try (InputStream inputStream = MainActivity.getInstance().getContentResolver().openInputStream(file);
                 DataInputStream in = new DataInputStream(new BufferedInputStream(Objects.requireNonNull(inputStream)))) {

                int length = in.readInt();
                if (length > SIZE) {
                    length = SIZE;
                }

                for (int i = 0; i < length; ++i) {
                    history[i] = in.readInt();
                }

                in.close();
            } catch (IOException e) {
                Log.d(TAG, "readHistory:IOException:" + e);
            }
        }

    }

    public int get(int pos){
        return history[pos];
    }

    public int getSize(){
        return history.length;
    }

    public void push(int no){
        // ダブリに -1 上書き
        for(int i =0; i< history.length; ++i){
            if(history[i] == no){
                history[i] = -1;
            }
        }

        // -1 で初期化
        int[] temp = new int[history.length];
        for(int i =0; i< history.length; ++i){
              temp[i] = -1;
        }

        for(int i = 0, j = 1 ; i < history.length - 1; ++i){
            if(history[i] != -1) {
                temp[j] = history[i];
                ++j;
            }
        }
        temp[0] = no;

        history = temp;
    }

    public void clear(){
        for(int i = 0; i < history.length; ++i){
            history[i] = -1;
        }
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        for (int aHistory : history) {
            sb.append(aHistory).append("\n");
        }
        return sb.toString();
    }
}
