package org.reassembler.jarfish;

/**
  4.7.8 The LineNumberTable Attribute
The LineNumberTable attribute is an optional variable-length attribute in the attributes table of a Code (§4.7.3) attribute. It may be used by debuggers to determine which part of the Java virtual machine code array corresponds to a given line number in the original source file. If LineNumberTable attributes are present in the attributes table of a given Code attribute, then they may appear in any order. Furthermore, multiple LineNumberTable attributes may together represent a given line of a source file; that is, LineNumberTable attributes need not be one-to-one with source lines.

The LineNumberTable attribute has the following format:


    LineNumberTable_attribute {
        u2 attribute_name_index;
        u4 attribute_length;
        u2 line_number_table_length;
        {  u2 start_pc;      
           u2 line_number;       
        } line_number_table[line_number_table_length];
    }

The items of the LineNumberTable_attribute structure are as follows:

attribute_name_index
    The value of the attribute_name_index item must be a valid index into the constant_pool table. The constant_pool entry at that index must be a CONSTANT_Utf8_info (§4.4.7) structure representing the string "LineNumberTable".

attribute_length
    The value of the attribute_length item indicates the length of the attribute, excluding the initial six bytes.

line_number_table_length
    The value of the line_number_table_length item indicates the number of entries in the line_number_table array.

line_number_table[]
    Each entry in the line_number_table array indicates that the line number in the original source file changes at a given point in the code array. Each line_number_table entry must contain the following two items:

start_pc
    The value of the start_pc item must indicate the index into the code array at which the code for a new line in the original source file begins. The value of start_pc must be less than the value of the code_length item of the Code attribute of which this LineNumberTable is an attribute.

line_number
    The value of the line_number item must give the corresponding line number in the original source file.

 */
public class LineNumberTableAttribute extends Attribute {
    private LineNumberEntry []lineNumberTable;

    public LineNumberEntry[] getLineNumberTable() {
        return lineNumberTable;
    }

    public void setLineNumberTable(LineNumberEntry[] lineNumberTable) {
        this.lineNumberTable = lineNumberTable;
    }
}
