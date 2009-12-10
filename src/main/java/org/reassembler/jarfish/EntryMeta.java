package org.reassembler.jarfish;

import java.util.Properties;

public class EntryMeta {
    private String jarName;
    private String entryName;
    private String type;
    private Properties meta;

    public EntryMeta(String jarName, String entryName, String type) {
        this.jarName = jarName;
        this.entryName = entryName;
        this.type = type;
    }

    public String getJarName() {
        return jarName;
    }

    public void setJarName(String jarName) {
        this.jarName = jarName;
    }

    public String getEntryName() {
        return entryName;
    }

    public void setEntryName(String entryName) {
        this.entryName = entryName;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(this.meta != null ? this.meta.toString() : "{}");
        
        return "[jarName:" + this.jarName + ",entryName:" + this.entryName 
                + ",type:" + this.type + ",extended: " + sb + "]";
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public void clearExtendedMeta() {
        if (this.meta != null) {
            this.meta.clear();
        }
    }

    public Properties getExtendedMeta() {
        ensureExtended();
        
        return this.meta;
    }    

    private void ensureExtended() {
        if (this.meta == null) {
            this.meta = new Properties();
        }    
   }

    public void setExtended(String name, String value) {
        ensureExtended();
        
        if (name != null && value != null) {
            this.meta.setProperty(name, value);
        }
        else {
            System.err.println("invalid extended value: " + name);
        }
    }    
}
