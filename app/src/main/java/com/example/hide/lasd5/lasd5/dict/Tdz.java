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
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
contents.tda の各チャンクの圧縮前後のデータサイズ一覧
8byte で一つのデータ
リトルエンディアン

前半 4byte 圧縮前サイズ
後半 4byte 圧縮後サイズ
 */

public class Tdz {
    private final String TAG = "Tdz";
    private final String FILENAME = "CONTENT.tda.tdz";

    private int length;
    private final ArrayList<Integer> rawSize = new ArrayList<>(); //圧縮前サイズ
    private final ArrayList<Integer> compressedSize = new ArrayList<>();  //圧縮後サイズ
    private final ArrayList<Integer> rawSizeSum = new ArrayList<>();  //圧縮前の累積サイズ
    private final ArrayList<Integer> compressedSizeSum = new ArrayList<>(); //圧縮後の累積サイズ

    public Tdz(DocumentFile path) {
        DocumentFile targetFile = path.findFile(FILENAME);
        if(targetFile.isFile()){
            Log.d(TAG, "Tdz:targetFile is File:" + targetFile.getName());
        }else{
            Log.d(TAG, "Tdz:targetFile is not file !!!! :" + targetFile);
        }

        Uri targetUri = targetFile.getUri();

        int sumBefore = 0;
        int sumAfter = 0;
        length = 0;

        try (InputStream inputStream = MainActivity.getInstance().getContentResolver().openInputStream(targetUri);
             DataInputStream is = new DataInputStream(new BufferedInputStream(Objects.requireNonNull(inputStream)))) {

            try {
                while (true) {
                    byte[] b0 = new byte[4];
                    byte[] b1 = new byte[4];

                    is.readFully(b0);
                    is.readFully(b1);

                    ByteBuffer bb0 = ByteBuffer.wrap(b0);
                    ByteBuffer bb1 = ByteBuffer.wrap(b1);
                    bb0.order(ByteOrder.LITTLE_ENDIAN);
                    bb1.order(ByteOrder.LITTLE_ENDIAN);
                    
                    int before = bb0.getInt();
                    int after = bb1.getInt();
                    sumBefore += before;
                    sumAfter += after;
                    rawSize.add(before);
                    compressedSize.add(after);
                    rawSizeSum.add(sumBefore);
                    compressedSizeSum.add(sumAfter);
                    
                    ++length;
                }
            } catch (EOFException ex) {
                Log.d(TAG, "read:EOFException:" + path.getName());
            }
        } catch (IOException ex) {
            Log.d(TAG, "read:IOException:" + ex);
        }
    }
    
    public int getLength() {
        return length;
    }
    
    public int getRawSize(int i) {   // 圧縮前
        return rawSize.get(i);
    }
    
    public int getCompressedSize(int i) {    // 圧縮後
        return compressedSize.get(i);
    }
    
    public int getRawSizeSum(int i) {
        return rawSizeSum.get(i);
    }

    /*
     i 番目の圧縮前データの先頭位置
     */
    public int getRawOffset(int i) {
        if (i == 0) {
            return 0;
        } else {
            return rawSizeSum.get(i - 1);
        }
    }
    
    public int getCompressedOffset(int i) {
        if (i == 0) {
            return 0;
        } else {
            return compressedSizeSum.get(i - 1);
        }
    }

    /*
     圧縮前データの先頭からのオフセット値がどのチャンクに含まれるか
     */
    public int indexOf(int offset) {
        for (int i = 0; i < length; ++i) {
            int n0 = getRawOffset(i); // チャンクの先頭
            int n1 = getRawSizeSum(i);    // 次のチャンクの先頭
            if (offset >= n0 && offset < n1) {
                //System.out.println("indexOf: offset:" + offset + " n0:" + n0 + " n1:" + n1);
                return i;
            }
        }
        System.out.println("tdz.indexOf:??  offset:" + offset);
        return -1;
    }
    
    public int getTotalRawSize(){
        return rawSizeSum.get(length -1);
    }
}