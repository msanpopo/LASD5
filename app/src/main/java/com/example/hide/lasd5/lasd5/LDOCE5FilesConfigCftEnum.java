package com.example.hide.lasd5.lasd5;

import com.example.hide.lasd5.lasd5.dict.FilesConfigCft;
import com.example.hide.lasd5.lasd5.dict.SizeEnum;

public enum LDOCE5FilesConfigCftEnum implements FilesConfigCft {
    FS("fs.skn", "files.skn", SizeEnum.UBYTE, SizeEnum.ULONG, SizeEnum.U24, SizeEnum.U24, SizeEnum.USHORT, SizeEnum.U24, SizeEnum.USHORT),
    EXA_PRON("exa_pron.skn", "files.skn", SizeEnum.UBYTE, SizeEnum.ULONG, SizeEnum.U24, SizeEnum.U24, SizeEnum.U24, SizeEnum.U24, SizeEnum.UBYTE),
    GB_HWD_PRON("gb_hwd_pron.skn", "files.skn", SizeEnum.UBYTE, SizeEnum.ULONG, SizeEnum.U24, SizeEnum.USHORT, SizeEnum.USHORT, SizeEnum.USHORT, SizeEnum.USHORT),
    SFX("sfx.skn", "files.skn", SizeEnum.UBYTE, SizeEnum.U24, SizeEnum.USHORT, SizeEnum.UBYTE, SizeEnum.UBYTE, SizeEnum.UBYTE, SizeEnum.UBYTE),
    US_HWD_PRON("us_hwd_pron.skn", "files.skn", SizeEnum.UBYTE, SizeEnum.ULONG, SizeEnum.U24, SizeEnum.USHORT, SizeEnum.USHORT, SizeEnum.USHORT, SizeEnum.USHORT);

    private final String dir;
    private final String subDir;
    private final int[] length = new int[7];
    private final int totalLength;

    LDOCE5FilesConfigCftEnum(String d, String f, SizeEnum a0, SizeEnum a1, SizeEnum a2, SizeEnum a3, SizeEnum a4, SizeEnum a5, SizeEnum a6) {
        dir = d;
        subDir = f;
        length[0] = a0.getLength();
        length[1] = a1.getLength();
        length[2] = a2.getLength();
        length[3] = a3.getLength();
        length[4] = a4.getLength();
        length[5] = a5.getLength();
        length[6] = a6.getLength();

        int sum = 0;
        for (int l : length) {
            sum += l;
        }
        totalLength = sum;
    }

    public String getName(){ return dir;}

    public int getTotalLength() {
        return totalLength;
    }

    public int getLength(int i) {
        return length[i];
    }

    public String getDirName() {
        return dir;
    }
    public String getSubDirName() {
        return subDir;
    }
        
    public String getFileName() {
        return "config.cft";
    }
    
    public int getSizeEnumCount(){
        return length.length;
    }

}
