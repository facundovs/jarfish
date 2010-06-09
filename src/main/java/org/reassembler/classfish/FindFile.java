package org.reassembler.classfish;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class FindFile implements FoundFile {
    public static String CLASS = "class";
    public static String UNKNOWN = "unknown";
    public static String ARCHIVE = "archive";
    
    private File file;
    private String type = "";
    private Map meta = new HashMap();
    private long size;

    public FindFile() {}
    
    public FindFile(File file) {
        this.file = file;
        setSize(file.length());
    }

    public String getName() {
        return this.file.getName();
    }
    
    public InputStream getStream() throws IOException {
        return new FileInputStream(this.file);
    }

    public boolean isArchive() {
        return false;
    }
    
    public String toString() {
        return String.valueOf(this.file);
    }

    public Map getMeta() {
        return meta;
    }

    public void setMeta(Map meta) {
        this.meta = meta;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
        
        this.meta.put("size", new Long(size));
    }
}
