package org.reassembler.jarfish;

public class Attribute {
    public static final String LINE_NUMBER_TABLE = "LineNumberTable";
    public static final String CODE = "Code";
    public static final Object SOURCE_FILE = "SourceFile";
    
    private String name;
    private int length;
    private Attribute[] attributes;

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public void setAttributes(Attribute[] attributes) {
        this.attributes = attributes;
    }
    
    public Attribute[] getAttributes() {
        return this.attributes;
    }
    
}
