package com.example.hide.lasd5.lasd5.dict;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class Headword implements Comparable, Serializable{

    String base;
    ArrayList<String> inflxList = new ArrayList<>();
    String homnum;
    String pos;     // 品詞
    boolean active;

    int index;

    public Headword(int i) {
        index = i;
        base = "";
        homnum = "";
        pos = "";
    }

    public void setBase(String str) {
        if (base.isEmpty()) {
            base = str;
        } else {
            base += str;
        }
    }

    public void addInflx(String a){
        inflxList.add(a);
    }
    
    public int getIndex(){
        return index;
    }

    public String getBase() {
        return base;
    }

    public String getPos(){
        return pos;
    }
    
    public void setPos(String str) {
        if (pos.isEmpty()) {
            pos = str;
        } else {
            pos += ", ";
            pos += str;
        }
    }

    public void setHomnum(String str) {
        homnum = str;
    }

    public boolean hasHomnum(){
        if(homnum.isEmpty()){
            return false;
        }else{
            return true;
        }
    }
    
    public void setActive(boolean bool) {
        active = bool;
    }
    
    public boolean isActive(){
        return active;
    }
    
    public String getBaseHomnum(){
        String str = base + homnum;
        return str;
    }

    @Override
    public String toString() {
        String str = base;

        if (homnum.isEmpty() == false) {
            str += " ";
            str += homnum;
        }
        if (homnum.isEmpty() == false && pos.isEmpty() == false) {
            str += " ";
            str += pos;
        }
        return str;
    }

    public boolean hasInflx(String text){
        String t = text.toLowerCase();

        for(String s : inflxList){
            if(t.equals(s.toLowerCase())){
                return true;
            }
        }

        return false;
    }

    @Override
    public int compareTo(Object o) {
        Headword w = (Headword) o;

        String base0 = this.getBaseHomnum();
        String base1 = w.getBaseHomnum();
        
        String b0 = base0.toLowerCase();
        b0 = b0.replaceAll("-", "");
        b0 = b0.replaceAll("'", "");
        b0 = b0.replaceAll("\\.", "");
        b0 = b0.replaceAll(" ", "");
        b0 = b0.replaceAll("/", "");
        b0 = b0.replaceAll("ˌ", "");
        b0 = b0.replaceAll("ˈ", "");
        b0 = b0.replaceAll("‘", "");
        b0 = b0.replaceAll("’", "");
        b0 = b0.replaceAll("à", "a");
        String b1 = w.base.toLowerCase();
        b1 = b1.replaceAll("-", "");
        b1 = b1.replaceAll("'", "");
        b1 = b1.replaceAll("\\.", "");
        b1 = b1.replaceAll(" ", "");
        b1 = b1.replaceAll("/", "");
        b1 = b1.replaceAll("ˌ", "");
        b1 = b1.replaceAll("ˈ", "");
        b1 = b1.replaceAll("‘", "");
        b1 = b1.replaceAll("’", "");
        b1 = b1.replaceAll("à", "a");

        if(b0.compareTo(b1) == 0){
            return base0.compareTo(base1);
        }else{
            return b0.compareTo(b1);
        }
    }

    public void write(DataOutputStream out) throws IOException {
        out.writeUTF(base);
        out.writeUTF(homnum);
        out.writeUTF(pos);     // 品詞
        out.writeBoolean(active);
        out.writeInt(index);
    }

    public void read(DataInputStream in) throws IOException {
        base = in.readUTF();
        homnum = in.readUTF();
        pos = in.readUTF();
        active = in.readBoolean();
        index = in.readInt();
    }
}
