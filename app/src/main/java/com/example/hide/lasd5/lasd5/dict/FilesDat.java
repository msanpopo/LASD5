package com.example.hide.lasd5.lasd5.dict;

import android.net.Uri;
import android.util.Log;

import androidx.documentfile.provider.DocumentFile;

import com.example.hide.lasd5.MainActivity;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
files.dat は n 番目のデータのオフセットの情報
(n番目の content データのオフセット位置だけ使用）

1つあたりのデータサイズはいくらかという情報は config.cft に書いてある
 */
public class FilesDat {
    private final String TAG = "FilesDat";

    private final String FILENAME = "files.dat";

    private final int length;
    private final int[] type;
    private final int[] contentOffset;   // これしか使ってない
    private final int[] nameOffset;
    private final int[] titleOffset;
    private final int[] aFile;
    private final int[] textTitleOffset;
    private final int[] aDirs;

    public FilesDat(DocumentFile path, int length, FilesConfigCft e) {
        DocumentFile targetFile = path.findFile(FILENAME);
        Uri targetUri = targetFile.getUri();

        this.length = length;
        type = new int[length];
        contentOffset = new int[length];
        nameOffset = new int[length];
        titleOffset = new int[length];
        aFile = new int[length];
        textTitleOffset = new int[length];
        aDirs = new int[length];

        try (InputStream inputStream = MainActivity.getInstance().getContentResolver().openInputStream(targetUri);
             DataInputStream is = new DataInputStream(new BufferedInputStream(Objects.requireNonNull(inputStream)))) {

            int l = e.getTotalLength();

            try {
                for (int i = 0; i < length; ++i) {
                    byte[] b = new byte[l];

                    is.readFully(b);
                    setSingleFilesDat(i, b, e);
                }
            } catch (EOFException ex) {
                Log.d(TAG, "input:" + ex);
                is.close();
            }
        } catch (IOException ex) {
            Log.d(TAG, "input:" + ex);
        }
    }

    private void setSingleFilesDat(int index, byte[] bData, FilesConfigCft e) {
        DataInputStream is = null;
        int c = 0;

        for (int i = 0; i < e.getSizeEnumCount(); ++i) {
            int l = e.getLength(i);
            byte[] b = new byte[4];

            for (int j = 0; j < l; ++j, ++c) {
                b[j] = bData[c];
            }

            int num;
            if (l == 1) {
                num = b[0];
            } else {
                ByteBuffer bb = ByteBuffer.wrap(b);
                bb.order(ByteOrder.LITTLE_ENDIAN);
                num = bb.getInt();
            }

            if (i == 0) {
                type[index] = num;
            } else if (i == 1) {
                contentOffset[index] = num;
            } else if (i == 2) {
                nameOffset[index] = num;
            } else if (i == 3) {
                titleOffset[index] = num;
            } else if (i == 4) {
                aFile[index] = num;
            } else if (i == 5) {
                textTitleOffset[index] = num;
            } else if (i == 6) {
                aDirs[index] = num;
            } else {
                System.out.println("FileDat:??? " + i);
            }
        }
    }

    public int getContentOffset(int i) {
        return contentOffset[i];
    }
    
    public int getSize(){
        return type.length;
    }
        
    public String toString(int i) {
        StringBuilder sb = new StringBuilder();
        sb.append("type:").append(type[i]);
        sb.append("  contentOffset:").append(contentOffset[i]);
        sb.append("  nameOffset:").append(nameOffset[i]);
        sb.append("  titleOffset:").append(titleOffset[i]);
        sb.append("  aFile:").append(aFile[i]);
        sb.append("  textTitleOffset:").append(textTitleOffset[i]);
        sb.append("  aDirs:").append(aDirs[i]);
        sb.append("\n");

        return sb.toString();
    }
}