package com.example.hide.lasd5.lasd5.dict;

import android.net.Uri;
import android.util.Log;
import androidx.documentfile.provider.DocumentFile;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Objects;

import com.example.hide.lasd5.MainActivity;

/*
 content.tda.tdz と files.dat にある mp3 データなどを
 取り出すために必要な、オフセット値、チャンクサイズなどを次回使用時用に
 保存する
 */
public class IndexArray implements Serializable {
    private final String TAG = "IndexArray";

    public int length;

    public String[] contentName;


    public int[] chunkSize;
    public int[] compressedChunkSize;
    public int[] chunkOffset;

    public int[] rawSize;
    public int[] rawOffset;

    public IndexArray(TdaNullSeparated nameList, Tdz tdz, FilesDat filesDat){
        this.length = nameList.getSize();

        contentName = new String[length];
        chunkSize = new int[length];
        compressedChunkSize = new int[length];
        chunkOffset = new int[length];
        rawSize = new int[length];
        rawOffset = new int[length];

        for (int i = 0; i < length; ++i) {
            int offsetAll = filesDat.getContentOffset(i); // n 番目の mp3 の全 content 上でのオフセット
            int chunkIndex = tdz.indexOf(offsetAll); // そのオフセットが入っているチャンクのインデックス
            int first = tdz.getRawOffset(chunkIndex); // chunkIndex 番目の圧縮前の先頭オフセット

            contentName[i] = nameList.get(i);
            chunkSize[i] = tdz.getRawSize(chunkIndex);
            compressedChunkSize[i] = tdz.getCompressedSize(chunkIndex);
            chunkOffset[i] = tdz.getCompressedOffset(chunkIndex);
            rawOffset[i] = offsetAll - first;

            if (i < filesDat.getSize() - 1) {
                rawSize[i] = filesDat.getContentOffset(i + 1) - filesDat.getContentOffset(i);
            } else if (i == filesDat.getSize() - 1) {
                rawSize[i] = tdz.getTotalRawSize() - filesDat.getContentOffset(i);
            } else {
                System.out.println("Content.get ?? len ?  n:" + i);
            }
        }
    }

    public IndexArray(DocumentFile file){
        Log.d(TAG, "IndexArray:file:" + file);
        if(file != null) {
            read(file);
        }else{
            Log.d(TAG, "IndexArray:file is:!!!" + file);
        }
    }

    public int indexOf(String name){
        for(int i = 0; i < length; ++i){
            if(name.equals(contentName[i])){
                return i;
            }
        }
        return -1;
    }

    public void write(DocumentFile file){
        Uri uri = file.getUri();
        Log.d(TAG, "writeIndexFile:" + file.getName());

        try (OutputStream outputStream = MainActivity.getInstance().getContentResolver().openOutputStream(uri);
             DataOutputStream out = new DataOutputStream(new BufferedOutputStream(Objects.requireNonNull(outputStream)))) {

            out.writeInt(length);
            for(int i = 0; i < length; ++i){
                out.writeUTF(contentName[i]);
                out.writeInt(chunkSize[i]);
                out.writeInt(compressedChunkSize[i]);
                out.writeInt(chunkOffset[i]);
                out.writeInt(rawSize[i]);
                out.writeInt(rawOffset[i]);
            }
        } catch (IOException e) {
            Log.d(TAG, "writeIndexFile:" + file + ":" + e);
        }
    }

    private void read(DocumentFile file){
        Log.d(TAG, "readIndexFile:" + file);

        Uri fileUri = file.getUri();

        try (InputStream inputStream = MainActivity.getInstance().getContentResolver().openInputStream(fileUri);
             DataInputStream in = new DataInputStream(new BufferedInputStream(Objects.requireNonNull(inputStream)))) {

            length = in.readInt();

            contentName = new String[length];
            chunkSize = new int[length];
            compressedChunkSize = new int[length];
            chunkOffset = new int[length];
            rawSize = new int[length];
            rawOffset = new int[length];

            for (int i = 0; i < length; ++i) {
                contentName[i] = in.readUTF();
                chunkSize[i] = in.readInt();
                compressedChunkSize[i] = in.readInt();
                chunkOffset[i] = in.readInt();
                rawSize[i] = in.readInt();
                rawOffset[i] = in.readInt();
            }

        } catch (IOException e) {
            Log.d(TAG, "readIndexFile:" + file + ":" + e);
        }
    }

    private void writeObject(File file){
        Log.d(TAG, "writeIndexFile:" + file);

        FileOutputStream of;
        ObjectOutputStream out;

        try {
            of = new FileOutputStream(file);
            out = new ObjectOutputStream(new BufferedOutputStream(of));

            out.writeObject(this);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private IndexArray readObject(File file){
        Log.d(TAG, "readIndexFile:" + file);

        FileInputStream fis;
        ObjectInputStream ois;
        IndexArray obj = null;

        try {
            fis = new FileInputStream(file);
            ois = new ObjectInputStream(new BufferedInputStream(fis));

            try {
                obj = (IndexArray)ois.readObject();
            } catch (ClassNotFoundException e) {
                Log.d(TAG, "input:" + file + ":" +e);
            }
            ois.close();
        } catch (IOException e) {
            Log.d(TAG, "input:" + file + ":" +e);
        }

        return obj;
    }
}