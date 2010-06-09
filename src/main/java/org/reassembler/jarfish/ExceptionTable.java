package org.reassembler.jarfish;

/**
 * Part of the Code_attribute
 * <pre>
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
</pre>

 * @author reassembler
 *
 */
public class ExceptionTable {
    private int startPc;
    private int endPc;
    private int handlerPc;
    private int catchType;
    
    public int getStartPc() {
        return startPc;
    }
    
    public void setStartPc(int startPc) {
        this.startPc = startPc;
    }
    
    public int getEndPc() {
        return endPc;
    }
    
    public void setEndPc(int endPc) {
        this.endPc = endPc;
    }
    
    public int getHandlerPc() {
        return handlerPc;
    }
    
    public void setHandlerPc(int handlerPc) {
        this.handlerPc = handlerPc;
    }
    
    public int getCatchType() {
        return catchType;
    }
    
    public void setCatchType(int catchType) {
        this.catchType = catchType;
    }
}
