package org.reassembler.jarfish;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ClassMeta {
    private int minorVersion;
    private int majorVersion;
    private int constantPoolSize;
    private boolean _public;
    private boolean _final;
    private boolean _interface;
    private boolean _super;
    private boolean _abstract;
    private Method[] methods;
    private List entries;
    private String sourceFileName;
    private String superTypeName;
    private String typeName;
    private String[] interfaces;
    private Field[] fields;
    private Attribute[] attributes;
    private String classVersion;
    private String javaVersion;
    
    public void reset() {
        minorVersion = 0;
        majorVersion = 0;
        constantPoolSize = 0;
        _public = false;
        _final = false;
        _interface = false;
        _super = false;
        _abstract = false;
        methods = null;
        entries = null;
        sourceFileName = null;
        superTypeName = null;
        typeName = null;
        interfaces = null;
        fields = null;
        attributes = null;
        classVersion = null;
        javaVersion = null; 
    }
   
    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append("java version:")
            .append(this.javaVersion)
            .append(", class version:")
            .append(this.classVersion);

        return sb.toString();
    }

    public Method[] getMethods() {
        return this.methods;
    }

    public void setMethods(Method[] methods) {
        this.methods = methods;
    }

    public void setPublic(boolean _public) {
        this._public = _public;
    }

    public boolean isPublic() {
        return this._public;
    }

    public void setFinal(boolean _final) {
        this._final = _final;
    }

    public boolean isFinal() {
        return this._final;
    }

    public void setInterface(boolean _interface) {
        this._interface = _interface;
    }

    public boolean isInterface() {
        return this._interface;
    }

    public void setSuper(boolean _super) {
        this._super = _super;
    }

    public boolean isSuper() {
        return this._super;
    }

    public void setAbstract(boolean _abstract) {
        this._abstract = _abstract;
    }

    public boolean isAbstract() {
        return this._abstract;
    }

    public int getMinorVersion() {
        return minorVersion;
    }

    public void setMinorVersion(int minorVersion) {
        this.minorVersion = minorVersion;
    }

    public int getMajorVersion() {
        return majorVersion;
    }

    public void setMajorVersion(int majorVersion) {
        this.majorVersion = majorVersion;
    }

    public int getConstantPoolSize() {
        return constantPoolSize;
    }

    public void setConstantPoolSize(int constantPoolSize) {
        this.constantPoolSize = constantPoolSize;
    }

    public ConstantEntry getConstantEntry(int i) {
        return (ConstantEntry) this.entries.get(i);
    }

    public List getEntries() {
        return entries;
    }

    public void setEntries(List entries) {
        this.entries = entries;
    }

    public List getStringEntries() {
        List es = new ArrayList();
        Iterator it = this.entries.iterator();
        while (it.hasNext()) {
            ConstantEntry ce = (ConstantEntry) it.next();
            if (ce.getType() == ConstantEntry.STRING) {
                int index = ((Integer) ce.getValue()).intValue();
                es.add(getConstantEntry(index - 1).getValue());
            }
        }

        return es;
    }

    public String getPoolString(int i) {
        ConstantEntry e = getConstantEntry(i); 
        return e.getValue().toString();
    }

    public String getSourceFileName() {
        return sourceFileName;
    }

    public void setSourceFileName(String sourceFileName) {
        this.sourceFileName = sourceFileName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getSuperTypeName() {
        return superTypeName;
    }

    public void setSuperTypeName(String superTypeName) {
        this.superTypeName = superTypeName;
    }

    public String[] getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(String[] interfaces) {
        this.interfaces = interfaces;
    }

    public Field[] getFields() {
        return fields;
    }

    public void setFields(Field[] fields) {
        this.fields = fields;
    }

    public Attribute[] getAttributes() {
        return attributes;
    }

    public void setAttributes(Attribute[] attributes) {
        this.attributes = attributes;
    }

    public String getClassVersion() {
        return classVersion;
    }

    public void setClassVersion(String classVersion) {
        this.classVersion = classVersion;
    }

    public String getJavaVersion() {
        return javaVersion;
    }

    public void setJavaVersion(String javaVersion) {
        this.javaVersion = javaVersion;
    }

}
