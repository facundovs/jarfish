package org.reassembler.jarfish;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

public class ClassParserTest extends TestCase {
    private int simplePoolSize = 33;
    
    public void testParse() throws Exception {
        File classFile = new File("src/test/resources/simple/Simple.class");
        DataInputStream din = new DataInputStream(new FileInputStream(classFile));
        
        ClassParser cp = new ClassParser();
        ClassMeta cm = new ClassMeta();
        
        cp.parse(cm, din);
        
        
        assertEquals(0, cm.getMinorVersion());
        assertEquals(49, cm.getMajorVersion());
        
        
        assertTrue(cm.isPublic());
        assertFalse(cm.isFinal());
        assertFalse(cm.isInterface());
        assertFalse(cm.isAbstract());
        
        Field []fis = cm.getFields();
        
        assertEquals(1, fis.length);
        assertEquals("name", fis[0].getName());

        assertEquals("Simple.java", cm.getSourceFileName());
        
        assertEquals(this.simplePoolSize, cm.getConstantPoolSize());
        
        
        assertEquals("Simple", cm.getTypeName());
        assertEquals("java/lang/Object", cm.getSuperTypeName());
        
        
        String []interfaces = cm.getInterfaces();
        
        assertEquals(1, interfaces.length);
        assertEquals("java/lang/Runnable", interfaces[0]);
        
        
        
        Method []ms = cm.getMethods();
        
        assertEquals(3, ms.length);
        
        
        int i = 0;
        Method m = ms[i++];
        assertEquals("<init>", m.getName());
        assertEquals("()V", m.getDescriptor());
        CodeTable ct = m.getCodeTable();
        assertNotNull(ct);
        assertEquals(5, ct.getCodeBytes().length);
        
        LineNumberEntry []lnt = ct.getLineNumberTable();
        assertEquals(1, lnt.length);
        assertEquals(2, lnt[0].getLineNumber());
        assertEquals(0, lnt[0].getStartPc());
        
        m = ms[i++];
        assertEquals("run", m.getName());
        assertEquals("()V", m.getDescriptor());
        ct = m.getCodeTable();
        assertNotNull(ct);
        
        lnt = ct.getLineNumberTable();
        assertEquals(2, lnt.length);
        assertEquals(6, lnt[0].getLineNumber());
        assertEquals(0, lnt[0].getStartPc());
 
        assertEquals(7, lnt[1].getLineNumber());
        assertEquals(8, lnt[1].getStartPc());
 
        m = ms[i++];
        assertEquals("stop", m.getName());
        assertEquals("(Ljava/lang/Thread;[Ljava/lang/String;)I", m.getDescriptor());
        
        din.close();     
    }
    
    
    public void testParseGepo() throws Exception {
        File classFile = new File("src/test/resources/classparsertests/Gepo.class");
        DataInputStream din = new DataInputStream(new FileInputStream(classFile));
        
        ClassParser cp = new ClassParser();
        ClassMeta cm = new ClassMeta();
        
        cp.setDebug(true);
        
        cp.parse(cm, din);
        
        assertEquals(3, cm.getMinorVersion());
        assertEquals(45, cm.getMajorVersion());
        
        assertTrue(cm.isPublic());
        assertFalse(cm.isFinal());
        assertFalse(cm.isInterface());
        assertFalse(cm.isAbstract());
        
        Field []fis = cm.getFields();
        
        assertEquals(9, fis.length);
        assertEquals("log", fis[0].getName());

        assertEquals("Gepo.java", cm.getSourceFileName());
        
        assertEquals(351, cm.getConstantPoolSize());
        
        assertEquals("GEPO_SKIP was found, skipping file: ", cm.getPoolString(278));
        
        assertEquals("org/reassembler/gepo/Gepo", cm.getTypeName());
        
        assertEquals("java/lang/Object", cm.getSuperTypeName());
        
        
        String []interfaces = cm.getInterfaces();
        
        assertEquals(0, interfaces.length);
        
        Method []ms = cm.getMethods();
        
        assertEquals(19, ms.length);
        
        for (int i = 0; i < ms.length; i++) {
            Method method = ms[i];
            if (method.getName().equals("loadProperties")) {
                assertEquals(2, method.getCodeTable().getCodeAttribute().getExceptionTable().length);
            }
        }
        
        din.close();     
    }
    
    
    public void testParseHeader() throws Exception {
        File classFile = new File("src/test/resources/simple/Simple.class");
        DataInputStream din = new DataInputStream(new FileInputStream(classFile));
        
        ClassParser cp = new ClassParser();
        ClassMeta cm = new ClassMeta();
        
        cp.testSig(din);
        cp.parseHeader(cm, din);
        
        assertEquals(0, cm.getMinorVersion());
        assertEquals(49, cm.getMajorVersion());
    }
    
    public void testtestSig() throws Exception {
        File classFile = new File("src/test/resources/simple/Simple.class");
        DataInputStream din = new DataInputStream(new FileInputStream(classFile));
        
        ClassParser cp = new ClassParser();
        
        assertTrue(cp.testSig(din));
        assertFalse(cp.testSig(din));
    }

    public void testParseFlags() throws Exception {
        File classFile = new File("src/test/resources/simple/Simple.class");
        DataInputStream din = new DataInputStream(new FileInputStream(classFile));
        
        ClassParser cp = new ClassParser();
        ClassMeta cm = new ClassMeta();
        
        cp.testSig(din);
        cp.parseHeader(cm, din);
        cp.parsePool(cm, din);

        cp.parseAccessFlags(cm, din);

        assertTrue(cm.isPublic());
        assertFalse(cm.isFinal());
        assertFalse(cm.isInterface());
        assertFalse(cm.isAbstract());
        
        
        din.close();

        classFile = new File("src/test/resources/simple/Interface.class");
        din = new DataInputStream(new FileInputStream(classFile));
        
        cp = new ClassParser();
        cm = new ClassMeta();
        
        cp.testSig(din);
        cp.parseHeader(cm, din);
        cp.parsePool(cm, din);

        cp.parseAccessFlags(cm, din);

        assertTrue(cm.isPublic());
        assertFalse(cm.isFinal());
        assertTrue(cm.isInterface());
        assertTrue(cm.isAbstract());

        din.close();
    }
    
    public void testSkipJunk() throws Exception {
        File classFile = new File("src/test/resources/simple/Simple.class");
        DataInputStream din = new DataInputStream(new FileInputStream(classFile));
        
        ClassParser cp = new ClassParser();
        ClassMeta cm = new ClassMeta();
        
        cp.testSig(din);
        cp.parseHeader(cm, din);
        cp.parsePool(cm, din);

        cp.parseAccessFlags(cm, din);


        cp.skipJunk(cm, din);
        cp.parseMethods(cm, din);

        assertEquals(3, cm.getMethods().length);
        din.close();
    }
    
    public void testReadFields() throws Exception {
        File classFile = new File("src/test/resources/simple/Simple.class");
        DataInputStream din = new DataInputStream(new FileInputStream(classFile));
        
        ClassParser cp = new ClassParser();
        ClassMeta cm = new ClassMeta();
        
        cp.testSig(din);
        cp.parseHeader(cm, din);
        cp.parsePool(cm, din);

        cp.parseAccessFlags(cm, din);


        cp.readTaxonomy(cm, din);
        
        cp.readFields(cm, din);
        
        Field []fis = cm.getFields();
        
        assertEquals(1, fis.length);
        assertEquals("name", fis[0].getName());

        din.close();        
    }
    
    public void testParsePool() throws Exception {
        File classFile = new File("src/test/resources/simple/Simple.class");
        DataInputStream din = new DataInputStream(new FileInputStream(classFile));
        
        ClassParser cp = new ClassParser();
        ClassMeta cm = new ClassMeta();
        
        cp.testSig(din);
        cp.parseHeader(cm, din);
        cp.parsePool(cm, din);
        
        assertEquals(this.simplePoolSize, cm.getConstantPoolSize());
        assertEquals(ConstantEntry.STRING, cm.getConstantEntry(2).getType());
        assertEquals(new Integer(22), cm.getConstantEntry(2).getValue());
        assertEquals("Hello World", cm.getConstantEntry(21).getValue());
        
        List ses = cm.getStringEntries();
        assertEquals(1, ses.size());
        
        din.close();
    }
    
    
    public void testParsePool2() throws Exception {
        File classFile = new File("src/test/resources/simple/ViLayer.class");
        DataInputStream din = new DataInputStream(new FileInputStream(classFile));
        
        ClassParser cp = new ClassParser();
        ClassMeta cm = new ClassMeta();
        
        cp.testSig(din);
        cp.parseHeader(cm, din);
        cp.parsePool(cm, din);
        
        assertEquals(365, cm.getConstantPoolSize());
        /*
        assertEquals(ConstantEntry.STRING, cm.getConstantEntry(2).getType());
        assertEquals(new Integer(20), cm.getConstantEntry(2).getValue());
        assertEquals("Hello World", cm.getConstantEntry(19).getValue());
        *
        */
        
        List ses = cm.getStringEntries();
        assertEquals(11, ses.size());
        
        Object []es = ses.toArray();
        
        int index = 0;
        assertEquals("wordseparators", es[index++]);
        assertEquals("", es[index++]);
        assertEquals("CommandMode", es[index++]);
        assertEquals("InsertMode", es[index++]);
        assertEquals("VisualMode", es[index++]);
        assertEquals("VisualLineMode", es[index++]);
        assertEquals("ReplaceMode", es[index++]);
        assertEquals("Something unexpected happened. Please report an error!", es[index++]);
        assertEquals("vimcursor", es[index++]);
        assertEquals("Trial Version", es[index++]);
        assertEquals("To remove this message buy a full version!\nhttp://www.satokar.com/viplugin/index.php", es[index++]);
        
        
        
        Iterator t = ses.iterator();
        while (t.hasNext()) {
            t.next();
            index++;
        }
        
        din.close();
    }    
    
    
    public void testParsePool3() throws Exception {
        File classFile = new File("src/test/resources/simple/Simple.class");
        DataInputStream din = new DataInputStream(new FileInputStream(classFile));
        
        ClassParser cp = new ClassParser();
        ClassMeta cm = new ClassMeta();
        
        cp.testSig(din);
        cp.parseHeader(cm, din);
        cp.parsePool(cm, din);
        
        assertEquals(this.simplePoolSize, cm.getConstantPoolSize());
        
        din.close();
    }
    
    public void testReadClassAttributes() throws Exception {
        File classFile = new File("src/test/resources/simple/Simple.class");
        DataInputStream din = new DataInputStream(new FileInputStream(classFile));
        
        ClassParser cp = new ClassParser();
        ClassMeta cm = new ClassMeta();
        
        cp.testSig(din);
        cp.parseHeader(cm, din);
        cp.parsePool(cm, din);
    
        cp.parseAccessFlags(cm, din);
    
    
        cp.skipJunk(cm, din);
        cp.parseMethods(cm, din);
        
        cp.parseClassAttributes(cm, din);
        
        assertEquals("Simple.java", cm.getSourceFileName());
    }
    
    public void testReadTaxonomy() throws Exception {
        File classFile = new File("src/test/resources/simple/Simple.class");
        DataInputStream din = new DataInputStream(new FileInputStream(classFile));
        
        ClassParser cp = new ClassParser();
        ClassMeta cm = new ClassMeta();
        
        cp.testSig(din);
        cp.parseHeader(cm, din);
        cp.parsePool(cm, din);
    
        cp.parseAccessFlags(cm, din);
    
    
        cp.readTaxonomy(cm, din);
        
        assertEquals("Simple", cm.getTypeName());
        assertEquals("java/lang/Object", cm.getSuperTypeName());
        
        
        String []interfaces = cm.getInterfaces();
        
        assertEquals(1, interfaces.length);
        assertEquals("java/lang/Runnable", interfaces[0]);
        

        
    }

    public void testparseMethods() throws Exception {
        File classFile = new File("src/test/resources/simple/Simple.class");
        DataInputStream din = new DataInputStream(new FileInputStream(classFile));
        
        ClassParser cp = new ClassParser();
        ClassMeta cm = new ClassMeta();
        
        cp.testSig(din);
        cp.parseHeader(cm, din);
        cp.parsePool(cm, din);
    
        cp.parseAccessFlags(cm, din);
    
    
        cp.skipJunk(cm, din);
        cp.parseMethods(cm, din);
        
        Method []ms = cm.getMethods();
    
        assertEquals(3, ms.length);
        
        
        int i = 0;
        Method m = ms[i++];
        assertEquals("<init>", m.getName());
        assertEquals("()V", m.getDescriptor());
        CodeTable ct = m.getCodeTable();
        assertNotNull(ct);
        assertEquals(5, ct.getCodeBytes().length);
        
        LineNumberEntry []lnt = ct.getLineNumberTable();
        assertEquals(1, lnt.length);
        assertEquals(2, lnt[0].getLineNumber());
        assertEquals(0, lnt[0].getStartPc());
        
        m = ms[i++];
        assertEquals("run", m.getName());
        assertEquals("()V", m.getDescriptor());
        ct = m.getCodeTable();
        assertNotNull(ct);
        
        lnt = ct.getLineNumberTable();
        assertEquals(2, lnt.length);
        assertEquals(6, lnt[0].getLineNumber());
        assertEquals(0, lnt[0].getStartPc());
 
        assertEquals(7, lnt[1].getLineNumber());
        assertEquals(8, lnt[1].getStartPc());
 
        m = ms[i++];
        assertEquals("stop", m.getName());
        assertEquals("(Ljava/lang/Thread;[Ljava/lang/String;)I", m.getDescriptor());
        
        din.close();
    }    
    
 
}
