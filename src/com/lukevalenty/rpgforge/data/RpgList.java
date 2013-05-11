package com.lukevalenty.rpgforge.data;

import java.util.ArrayList;

public class RpgList {
    private ArrayList<String> rpgs;
    
    public RpgList() {
        rpgs = new ArrayList<String>();
    }
    
    public int getSize() {
        return rpgs.size();
    }
    
    public String get(final int i) {
        return rpgs.get(i);
    }
    
    public void add(final String name) {
        rpgs.add(name);
    }
    
    public void remove(final String name) {
        rpgs.remove(name);
    }
    
    public void remove(final int i) {
        rpgs.remove(i);
    }
}
