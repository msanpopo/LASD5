package com.example.hide.lasd5.lasd5.dict;

import android.net.Uri;
import android.util.Log;

import androidx.documentfile.provider.DocumentFile;

import com.example.hide.lasd5.MainActivity;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
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
import java.util.Collection;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/*
データが入っている files.skn という名前のフォルダ
 */
public class FilesSkn {
    private final String TAG = "FilesSkn";
    private Content content;
    private IndexArray indexArray;
    private SideList sideList;
    private Headword[] sideListArray;

    public FilesSkn(DocumentFile dataDir, FilesConfigCft e, String tdaFilename) {
        DocumentFile tmpDir = dataDir.findFile(e.getDirName());
        DocumentFile targetPath = tmpDir.findFile(e.getSubDirName());
        DocumentFile indexFile = dataDir.getParentFile().findFile(e.getDirName() + "_index2");

        Log.d(TAG, "FilesSkn:dataDir:" + dataDir.getName());
        Log.d(TAG, "FilesSkn:targetPath:" + targetPath.getName());
      //  Log.d(TAG, "FilesSkn:indexFile:" + indexFile.getName());

        if(indexFile != null && indexFile.exists()){
            indexArray = new IndexArray(indexFile);
            content = new Content(targetPath);
        }else{
            DocumentFile tdaFile = targetPath.findFile(tdaFilename);
            TdaNullSeparated nameList = new TdaNullSeparated(tdaFile);
            int len = nameList.getSize();

            System.out.println("TdaNullSeparated len:" + len);

            indexArray = new IndexArray(len);
            for(int i = 0; i < len; ++i){
                indexArray.contentName[i] = nameList.get(i);
            }
            indexArray = createIndexArray(targetPath, len, e, indexArray);
            content = new Content(targetPath, len, e, indexArray);

            Log.d(TAG, "text_title size " + nameList.getSize());

            indexFile = dataDir.getParentFile().createFile("application/octet-stream", e.getDirName() + "_index2");
            indexArray.write(indexFile);
        }
    }

    private IndexArray createIndexArray(DocumentFile path, int length, FilesConfigCft e, IndexArray ia) {
        Tdz tdz = new Tdz(path);
        FilesDat filesDat = new FilesDat(path, length, e);

        for (int i = 0; i < length; ++i) {
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
        return ia;
    }
    private void writeListFile(Uri file){
        Log.d(TAG, "writeListFile:" + file);

        try (OutputStream outputStream = MainActivity.getInstance().getContentResolver().openOutputStream(file);
             ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(Objects.requireNonNull(outputStream)))) {

            out.writeObject(sideList);
        } catch (IOException e) {
            Log.d(TAG, "write:" + e);
        }
    }

    private SideList readListFile(Uri file){
        Log.d(TAG, "readListFile:" + file);

        SideList obj = null;

        try (InputStream inputStream = MainActivity.getInstance().getContentResolver().openInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(Objects.requireNonNull(inputStream)))) {

            try {
                obj = (SideList)ois.readObject();
                obj.setReady();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            ois.close();
        } catch (IOException e) {
            Log.d(TAG, "read:" + file + ":" + e);
        }

        return obj;
    }

    public SideList getSideList(DocumentFile baseDir, FilesConfigCft e) {
        Log.d(TAG, "getSideList");

        String saveName = e.getName() + "_list3";
        DocumentFile targetFile = baseDir.getParentFile().findFile(saveName);
        if(targetFile != null && targetFile.exists()) {
            Uri listFile = targetFile.getUri();
            Log.d(TAG, "getSideList:" + listFile);

            sideList = readListFile(listFile);
        }else {
            targetFile = baseDir.getParentFile().createFile("application/octet-stream", e.getName() + "_list3");
            sideList = createSideList();
            sideList.createArray();
            sideList.setReady();
            Uri targetUri = targetFile.getUri();
            writeListFile(targetUri);
        }

        return sideList;
    }

    private SideList createSideList() {
        Log.d(TAG, "createSideList");

        int length = indexArray.length;

        SideList sl = new SideList();

        for (int i = 0; i < length; ++i) {
            if (i % 1000 == 0) {
                Log.d(TAG, "createSideList " + i + "/" + length);
            }

            SAXHandlerHeadword handler = new SAXHandlerHeadword();
            handler.setIndex(i);

            parse(i, handler);

            Collection<Headword> l = handler.getHeadwordList();
            sl.addAll(l);
        }
        return sl;
    }

    public int getIndex(String name) {
        return indexArray.indexOf(name);
    }

    public byte[] getContent(String name){
        int n = indexArray.indexOf(name); // filename の mp3 が何番目のデータか

        System.out.println("getMp3:" + name + " n:" + n);

        if (n >= 0) {
            return content.get(n, indexArray);
        } else {
            return null;
        }
    }

    public String getSource(int n) {
        return parse(n, new SAXHandlerSimple());
    }

    public String getHtml(int n) {
        return  parse(n, new SAXHandlerHtml());
    }

    private String parse(int n, DefaultHandler handler) {
        byte[] c = content.get(n, indexArray);
        byte[] bin = new byte[c.length - 1];

        System.arraycopy(c, 0, bin, 0, bin.length); // 0x0 削除

        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();

            try {
                InputStream is = new ByteArrayInputStream(bin);
                parser.parse(is, handler);
            } catch (IOException ex) {
                Logger.getLogger(FilesSkn.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (ParserConfigurationException | SAXException ex) {
            Logger.getLogger(FilesSkn.class.getName()).log(Level.SEVERE, null, ex);
        }

        return handler.toString();
    }
}