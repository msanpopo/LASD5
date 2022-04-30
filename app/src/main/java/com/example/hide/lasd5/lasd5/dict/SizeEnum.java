package com.example.hide.lasd5.lasd5.dict;

public enum SizeEnum {
    UBYTE(1),
    USHORT(2),
    U24(3),
    ULONG(4);    
    
    private final int length;
    
    SizeEnum(int l){
        length = l;
    }
    
    public int getLength(){
        return length;
    }
}
