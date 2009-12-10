package org.reassembler.jarfish;

public class ConstantEntry {
    public static final String []NAMES = {
            "phantom",
            "CONSTANT_Utf8",                    // 1
            "",                                 // 2
            "CONSTANT_Integer",                 // 3
            "CONSTANT_Float",                   // 4
            "CONSTANT_Long",                    // 5
            "CONSTANT_Double",                  // 6
            "CONSTANT_Class",                   // 7
            "CONSTANT_String",                  // 8
            "CONSTANT_Fieldref",                // 9
            "CONSTANT_Methodref",               // 10
            "CONSTANT_InterfaceMethodref",      // 11
            "CONSTANT_NameAndType",             // 12
    };

    public static final int STRING = 8;
    
    private int type;
    private Object value;

    public ConstantEntry() {}
    
    public ConstantEntry(int type, Object value) {
        setType(type);
        setValue(value);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
