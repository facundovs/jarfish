package org.reassembler.jarfish;

/*
 Code_attribute {
 u2 attribute_name_index;
 u4 attribute_length;
 u2 max_stack;
 u2 max_locals;
 u4 code_length;
 u1 code[code_length];
 u2 exception_table_length;
 {       u2 start_pc;
 u2 end_pc;
 u2  handler_pc;
 u2  catch_type;
 }   exception_table[exception_table_length];
 u2 attributes_count;
 attribute_info attributes[attributes_count];
 }

 */
public class CodeAttribute extends Attribute {
    private int maxStack;
    private int maxLocals;
    private int codeLength;
    private byte []code;

    public int getMaxStack() {
        return maxStack;
    }

    public void setMaxStack(int maxStack) {
        this.maxStack = maxStack;
    }

    public int getMaxLocals() {
        return maxLocals;
    }

    public void setMaxLocals(int maxLocals) {
        this.maxLocals = maxLocals;
    }

    public int getCodeLength() {
        return codeLength;
    }

    public void setCodeLength(int codeLength) {
        this.codeLength = codeLength;
    }

    public byte[] getCode() {
        return code;
    }

    public void setCode(byte[] code) {
        this.code = code;
    }

}
