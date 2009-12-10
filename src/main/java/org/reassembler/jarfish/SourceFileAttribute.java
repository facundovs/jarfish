package org.reassembler.jarfish;

public class SourceFileAttribute extends Attribute {
    private String sourceFileName;
    private int sourceNameIndex;
    
    public String getSourceFileName() {
        return sourceFileName;
    }

    public void setSourceFileName(String sourceFileName) {
        this.sourceFileName = sourceFileName;
    }

    public int getSourceNameIndex() {
        return sourceNameIndex;
    }

    public void setSourceNameIndex(int sourceNameIndex) {
        this.sourceNameIndex = sourceNameIndex;
    }
}
