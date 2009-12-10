package org.reassembler.jarfish;

public class CodeTable {
    private CodeAttribute codeAttribute;
    private LineNumberTableAttribute lineNumberTableAttribute;

    public CodeAttribute getCodeAttribute() {
        return codeAttribute;
    }

    public void setCodeAttribute(CodeAttribute codeAttribute) {
        this.codeAttribute = codeAttribute;
    }

    public LineNumberTableAttribute getLineNumberTableAttribute() {
        return lineNumberTableAttribute;
    }

    public void setLineNumberTableAttribute(
            LineNumberTableAttribute lineNumberTableAttribute) {
        this.lineNumberTableAttribute = lineNumberTableAttribute;
    }
    
    public byte[] getCodeBytes() {
        return this.codeAttribute.getCode();
    }

    public LineNumberEntry[] getLineNumberTable() {
        return this.lineNumberTableAttribute.getLineNumberTable();
    }
}
