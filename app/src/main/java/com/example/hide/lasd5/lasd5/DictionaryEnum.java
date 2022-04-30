package com.example.hide.lasd5.lasd5;

/**
 * Created by hide on 6/18/16.
 */

public enum DictionaryEnum {
    LASD5("LASDE5", "lasde5.data"),
    LDOCE5("LDOCE5", "ldoce5.data");

    private final String topDir;
    private final String dataDir;

    DictionaryEnum(String str1, String str2){
        topDir = str1;
        dataDir = str2;
    }

    public String getTopDir(){
        return topDir;
    }
    public String getDataDir() { return dataDir; }
}
