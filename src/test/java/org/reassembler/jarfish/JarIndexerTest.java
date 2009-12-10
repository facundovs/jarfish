package org.reassembler.jarfish;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.reassembler.jarfish.Config;
import org.reassembler.jarfish.EntryMeta;
import org.reassembler.jarfish.JarIndexer;

import junit.framework.TestCase;

public class JarIndexerTest extends TestCase {

    public void testIndexPath() throws IOException {
        JarIndexer jix = new JarIndexer();
        jix.index(new File("src/test/resources/jars"), true);
        
        assertEquals(4, jix.getJarCount());
        
        
        jix = new JarIndexer();
        jix.index(new File("src/test/resources/jars"), false);
        
        assertEquals(3, jix.getJarCount());
    }
    
    public void testFind() throws IOException {
        JarIndexer jix = new JarIndexer();
        jix.index(new File("src/test/resources/jars"), true);
        
        assertEquals(4, jix.getJarCount());
        
        EntryMeta[] em = jix.find("Gepo", "class", new Config());
        assertEquals(2, em.length);
        
        
        em = jix.find("gepo", "package", new Config());
        assertEquals(2, em.length);
        
        em = jix.find("Vallo", "class", new Config());
        assertEquals(1, em.length);
    }
    
    public void testFindInClass() {
        JarIndexer jix = new JarIndexer();
        jix.index(new File("src/test/resources/jars"), true);
        
        assertEquals(4, jix.getJarCount());
        
        Config conf = new Config();
        conf.setLoadExtendedMetaData(true);
        
        EntryMeta[] em = jix.findStringInJavaClass("1.2", conf);
        assertEquals(3, em.length);
        assertTrue(em[0].getJarName().endsWith("gepo-1.2.1.jar"));
 
    }
    
    public void testFindDuplicateClasses() {
        JarIndexer jix = new JarIndexer();
        jix.index(new File("src/test/resources/jars"), true);
        
        List l = jix.findDuplicateClasses(new Config());
        assertEquals(3, l.size());
        
        System.out.println(l);
    }
    
    
    public void testFileList() throws IOException {
        JarIndexer jix = new JarIndexer();
        jix.index(new File("src/test/resources/file.lst"), true);
        
        assertEquals(2, jix.getJarCount());
        
        EntryMeta[] em = jix.find("Gepo", "class", new Config());
        assertEquals(1, em.length);
        
        
        em = jix.find("gepo", "package", new Config());
        assertEquals(1, em.length);
        
        em = jix.find("Vallo", "class", new Config());
        assertEquals(1, em.length);
    }
}
