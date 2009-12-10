package org.reassembler.jarfish;

public class Method {
    private String name;
    private String descriptor;
    private Attribute[] attributes;
    private int accessFlags;

    private CodeTable codeTable;
    
    public Attribute[] getAttributes() {
        return attributes;
    }

    public void setAttributes(Attribute[] attributes) {
        this.attributes = attributes;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CodeTable getCodeTable() {
        if (this.codeTable == null) {
            buildCodeTable();
        }
        
        return this.codeTable;
    }

    private void buildCodeTable() {
        CodeAttribute ca = null;
        LineNumberTableAttribute lnt = null;
        
        for (int i = 0; i < this.attributes.length; i++) {
            Attribute at = this.attributes[i];
            
            if (at.getName().equals(Attribute.CODE)) {
                ca = (CodeAttribute) at;
                Attribute[] ats = ca.getAttributes();
                for (int j = 0; j < ats.length; j++) {
                    Attribute cat = ats[j];
                    if (cat.getName().equals(Attribute.LINE_NUMBER_TABLE)) {
                        lnt = (LineNumberTableAttribute) cat;
                    }
                }
            }
        }
        
        CodeTable ct = new CodeTable();
        ct.setCodeAttribute(ca);
        ct.setLineNumberTableAttribute(lnt);
        
        this.codeTable = ct;
    }

    public int getAccessFlags() {
        return accessFlags;
    }

    public void setAccessFlags(int accessFlags) {
        this.accessFlags = accessFlags;
    }
}
