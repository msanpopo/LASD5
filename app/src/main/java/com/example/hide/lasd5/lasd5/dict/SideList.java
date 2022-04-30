package com.example.hide.lasd5.lasd5.dict;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

public class SideList implements Serializable {
    private static final String TAG = "SideList";

    private transient final TreeSet<Headword> sideList = new TreeSet<>();
    private ArrayList<Headword> sideListArray;

    private boolean isReady = false;

    public SideList(){
    }

    public boolean isReady(){
        return isReady;
    }

    public void setReady(){
        isReady = true;
    }

    public void addAll(Collection<Headword> c){
        //for(Headword hw : c){
        //    Log.d(TAG, "addAll:" + hw.toString());

        //}
        sideList.addAll(c);
    }

    public void createArray(){
        sideListArray = new ArrayList<Headword>();
        int i = 0;
        for (Headword w : sideList) {
            sideListArray.add(w);
            ++i;
        }
    }

    public List<Headword> getArray(){
        return sideListArray;
    }

    public int getSize(){
        return sideListArray.size();
    }


    public Headword get(int pos){
        if(pos < 0) {
            return null;
        }else{
            return sideListArray.get(pos);
        }
    }

    public Headword search(String word){
        Log.d(TAG, "search word:" + word);

        for(Headword hw: sideListArray){
            if(hw.hasInflx(word)){
                Log.d(TAG, "search: word:" + word + " hw:" + hw.toString());
                return hw;
            }
        }

        return null;
    }

    public int getListIndex(Headword hw){
        return sideListArray.indexOf(hw);
    }
}
