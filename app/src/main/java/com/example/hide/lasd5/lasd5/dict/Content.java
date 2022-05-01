package com.example.hide.lasd5.lasd5.dict;

import android.net.Uri;

import androidx.documentfile.provider.DocumentFile;

import com.example.hide.lasd5.MainActivity;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

/*
    辞書データ または 発音データ
    個々のデータの末尾に null をつけたものをいくつかつなげて圧縮している
    (圧縮したものをチャンクと呼んでいる)
    そうしてできたチャンクを全部つなげたものが content.tda

    n番目のデータを取り出す時、
    (1)files.dat から n番目のデータのオフセット取得
    (2)そのオフセットがどのチャンクにあるかを content.tda.tdz から取得
    (3)そのチャンクが content.tda のどこにありサイズはいくらかを content.tda.tdz から取得
    (4)チャンクを取り出して解凍
    (5)解凍した中のどこにあるかを tda と tdz から計算して取り出し

    tda と tdz のデータは IndexArray の中にまとめて保存してある
 */
public class Content {
    private final static String FILENAME = "CONTENT.tda";
    private DocumentFile targetFile;

    public Content(DocumentFile path){
        targetFile = path.findFile(FILENAME);
    }

    public Content(DocumentFile path, int length, FilesConfigCft e, IndexArray ia) {
        targetFile = path.findFile(FILENAME);

        // TODO ここで indexarray をつくる必要ない。外でできる
        Tdz tdz = new Tdz(path);
        FilesDat filesDat = new FilesDat(path, length, e);

        for(int i = 0; i < length; ++i){
            int offsetAll = filesDat.getContentOffset(i); // n 番目の mp3 の全 content 上でのオフセット
            int chunkIndex = tdz.indexOf(offsetAll); // そのオフセットが入っているチャンクのインデックス

            ia.compressedChunkSize[i] = tdz.getCompressedSize(chunkIndex);
            ia.chunkSize[i] = tdz.getRawSize(chunkIndex);
            ia.chunkOffset[i] = tdz.getCompressedOffset(chunkIndex);

            int first = tdz.getRawOffset(chunkIndex); // chunkIndex 番目の圧縮前の先頭オフセット

            ia.rawOffset[i] = offsetAll - first;

            if (i < filesDat.getSize() - 1) {
                ia.rawSize[i] = filesDat.getContentOffset(i + 1) - filesDat.getContentOffset(i);
            } else if (i == filesDat.getSize() - 1) {
                ia.rawSize[i] = tdz.getTotalRawSize() - filesDat.getContentOffset(i);
            } else {
                System.out.println("Content.get ?? len ?  n:" + i);
            }
        }
    }

    public byte[] get(int n, IndexArray ia){
        byte[] chunk = getChunk(ia.chunkOffset[n], ia.compressedChunkSize[n], ia.chunkSize[n]);

        return getData(chunk, ia.rawOffset[n], ia.rawSize[n]);
    }

    private byte[] getChunk(int offset, int size, int rawSize){
        byte[] result = null;

        Uri uri = targetFile.getUri();

        try (InputStream inputStream = MainActivity.getInstance().getContentResolver().openInputStream(uri);
             DataInputStream is = new DataInputStream(new BufferedInputStream(Objects.requireNonNull(inputStream)))) {

         //   System.out.println("getChunk offset:" + offset + " size:" + size);

            byte[] bin = new byte[size];

            is.skipBytes(offset);
            is.readFully(bin);

            result = decompress(bin, rawSize);

         //   System.out.println("getChunk chunk size:" + result.length);

        } catch (IOException ex) {
            Logger.getLogger(Content.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;
    }

    private static byte[] decompress(byte[] bin, int resultSize) {
        Inflater inflater = new Inflater();
        byte[] result = new byte[resultSize];

        inflater.setInput(bin);
        try {
            int l = inflater.inflate(result);
            inflater.end();

    //        if (l != resultSize) {
    //            System.out.println("oops resultSize not match:" + resultSize + " " + l);
    //        }

        } catch (DataFormatException ex) {
            Logger.getLogger(Content.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;
    }

    private static byte[] getData(byte[] chunk, int offset, int len) {
        DataInputStream is = null;
        byte[] result = null;

        try {
            is = new DataInputStream(new BufferedInputStream(new ByteArrayInputStream(chunk)));
            result = new byte[len];

      //      System.out.println("getData  offset:" + offset + " len:" + len);
            is.skipBytes(offset);
            is.readFully(result);
       //   System.out.println("getData data size:" + result.length);

        } catch (IOException ex) {
            Logger.getLogger(Content.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(Content.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return result;
    }
}