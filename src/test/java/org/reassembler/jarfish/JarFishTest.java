package org.reassembler.jarfish;

import java.io.File;
import java.util.Map;
import java.util.Properties;

import junit.framework.TestCase;

public class JarFishTest extends TestCase {
    public void testListJar() {
        String []args = {"list", "-v1", "./src/test/resources/jars/gepo-1.2.1.jar"};
        Properties config = JarFish.parseArgs(args);
        JarIndexer ji = JarFish.list(config);
        
        assertEquals(1, ji.getJarCount());
    }
    
    public void testGetEntryContentAsString() {
        EntryMeta em = new EntryMeta("./src/test/resources/find-string.jar", "hello.txt", "text");
        String s = JarFish.getEntryContentAsString(em);
        assertEquals("my business", s.trim());
        
        em = new EntryMeta("./src/test/resources/find-.jar", "hello.txt", "text");
        s = JarFish.getEntryContentAsString(em);
        assertEquals(null, s);
    }
    
    
    public void testFindString() {
        String []args = {"findString", "-i", "business", "./src/test/resources/find-string.jar"};
        Properties config = JarFish.parseArgs(args);
        EntryMeta[] entries = JarFish.findString(config);
        assertEquals(2, entries.length);
        
        entries = JarFish.findString2(config);
        assertEquals(2, entries.length);
        
        args = new String[] {"findString", "business", "./src/test/resources/find-string.jar"};
        config = JarFish.parseArgs(args);
        
        entries = JarFish.findString(config);
        assertEquals(1, entries.length);
        
        entries = JarFish.findString2(config);
        assertEquals(1, entries.length);
    }
   
    
    public void testGetJarFilesFromPath() {
        String []paths = {"file.jar", "./src/test/resources/file.lst", "./src/test/resources/jars"};
        
        File []jars = JarFish.getJarsFromPath(paths[0], true);
        assertEquals(1, jars.length);
        
        jars = JarFish.getJarsFromPath(paths[1], true);
        assertEquals(2, jars.length);
        
        jars = JarFish.getJarsFromPath(paths[2], true);
        assertEquals(4, jars.length);
    }
    
    public void testIsArchiveType() {
        File []files = new File[] {
                new File("."),
                new File("a.jar"),
                new File("b.zip"),
                new File("a.car"),
                new File("a.war"),
                new File("a.ear"),
                new File("a.sar"),
        };
        
        assertEquals(false, JarFish.isArchiveType(files[0]));
        assertEquals(true, JarFish.isArchiveType(files[1]));
        assertEquals(true, JarFish.isArchiveType(files[2]));
        assertEquals(true, JarFish.isArchiveType(files[3]));
        assertEquals(true, JarFish.isArchiveType(files[4]));
        assertEquals(true, JarFish.isArchiveType(files[5]));
        assertEquals(true, JarFish.isArchiveType(files[6]));
    }
    
    public void testParseArgsWithFile() {
        String []args = {"find", "-f", "files.txt", "Gepo"};
        Map config = JarFish.parseArgs(args);
        
        assertNotNull(config);
        
        assertEquals("files.txt", config.get("path"));
        assertEquals("find", config.get("action"));
        assertEquals("Gepo", config.get("query"));
    }
    
    public void testParseArgsDefault() {
        String []args = {"find", "Gepo", "home", "newHome"};
        Properties config = JarFish.parseArgs(args);
        
        assertNotNull(config);
        
        assertEquals("find", config.get("action"));
        assertEquals("Gepo", config.get("query"));
        assertEquals("home,newHome", config.get("path"));
        
        args = new String[]{"find", "-f", "files.txt", "Gepo"};
        config = JarFish.parseArgs(args);
        
        assertNotNull(config);
        
        assertEquals("find", config.get("action"));
        assertEquals("Gepo", config.get("query"));
        assertEquals("files.txt", config.get("path"));
    }
    
    
    public void testAddTypesByExtension() {
        String []args = {"find", "-a", "map", "text", "Gepo", "home", "newHome"};
        assertEquals("unknown", JarFish.getTypeByExtension("map"));
        Properties config = JarFish.parseArgs(args);
        
        assertEquals("text", JarFish.getTypeByExtension("map"));
        
        assertEquals("find", config.get("action"));
        assertEquals("Gepo", config.get("query"));
        assertEquals("home,newHome", config.get("path"));
        
        args = new String[]{"find", "-f", "files.txt", "Gepo"};
        config = JarFish.parseArgs(args);
        
        assertNotNull(config);
        
        assertEquals("find", config.get("action"));
        assertEquals("Gepo", config.get("query"));
        assertEquals("files.txt", config.get("path"));
    }
    
    public void testParseArgsNotDefault() {
        String []args = {"list", "-t", "package", "-i", "-v3", "Gepo", "home", "newHome"};
        Map config = JarFish.parseArgs(args);
        
        assertNotNull(config);
        
        assertEquals("list", config.get("action"));
        assertEquals("true", config.get("caseInsensitive"));
        assertEquals("loud", config.get("verbosity"));
        assertEquals("package", config.get("type"));
    }
    
    public void testParseArgsNoRecurse() {
        String []args = {"find", "-nor", "Gepo", "./src/test/resources"};
        Map config = JarFish.parseArgs(args);
        
        assertNotNull(config);
        
        assertEquals("false", config.get("recurse"));
    }
}
