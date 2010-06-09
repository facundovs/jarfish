package org.reassembler.classfish;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.reassembler.classfish.FindFile;
import org.reassembler.classfish.JarFinder;
import org.reassembler.jarfish.JarFish;

import junit.framework.TestCase;

public class JarFinderTest extends TestCase {
    public void testFollowPath() {
        JarFinder f = new JarFinder();
        
        ArchiveCountListener cl = new ArchiveCountListener();
        cl.setCountClasses(false);
        f.setListener(cl);
        
        File path = new File("src/test/resources/jarfinder-tests/simple");
        
        f.find(path);
        
        assertEquals(5, cl.getCount());
    }
    
    public void testGetEntryShortName() {
        assertEquals("Gepo.class", JarFinder.getEntryShortName("Gepo.class"));
        assertEquals("Gepo.class", JarFinder.getEntryShortName("org/reass/joker/Gepo.class"));
        assertEquals("range.jar", JarFinder.getEntryShortName("/nested/lib/watch/range.jar"));
    }
    
    public void testFollowPathWithClasses() {
        JarFinder f = new JarFinder();
        
        ArchiveCountListener cl = new ArchiveCountListener();
        cl.setCountClasses(true);
        cl.setCountArchives(false);
        f.setListener(cl);
        
        File path = new File("src/test/resources/jarfinder-tests/simple-classes");
        
        f.find(path);
        
        assertEquals(23, cl.getCount());
    }    
    
    public void testFindClassInNestedJarExhaustStreams() {
        JarFinder f = new JarFinder();
        
        ArchiveCountListener cl = new ArchiveCountListener() {
            public void foundClass(FindFile file) {
                super.foundClass(file);
                
                byte []buffer = new byte[4096];
                int bytesRead = -1;
                InputStream in;
                try {
                    in = file.getStream();
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                
                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                
                    out.close();
                    
                    System.out.println(file);
                    if (file.getName().equals("Simple.class")) {
                        int length = out.toByteArray().length;
                        assertTrue(length == 541 | length == 451);
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        
        cl.setCountClasses(false);
        cl.setCountArchives(false);
        
        f.setListener(cl);
        
        File path = new File("src/test/resources/jarfinder-tests/nestedjar/");
        
        f.find(path);
        
        //assertEquals(3, cl.getCount());        
    }
    
    
    public void testFindClassInNestedJarCountJars() {
        JarFinder f = new JarFinder();
        
        ArchiveCountListener cl = new ArchiveCountListener();
        cl.setCountClasses(false);
        cl.setCountArchives(true);
        
        f.setListener(cl);
        
        File path = new File("src/test/resources/jarfinder-tests/nestedjar/");
        
        f.find(path);
        
        assertEquals(3, cl.getCount());        
    }    
    
    public void testFindClassInNestedJarCountClasses() {
        JarFinder f = new JarFinder();
        
        ArchiveCountListener cl = new ArchiveCountListener();
        cl.setCountClasses(true);
        cl.setCountArchives(false);
        
        f.setListener(cl);
        
        File path = new File("src/test/resources/jarfinder-tests/nestedjar/");
        
        f.find(path);
        
        assertEquals(9, cl.getCount());        
    }
    
    
    public void testIsArchiveType() {
        JarFinder f = new JarFinder();
        
        assertTrue(f.isArchiveType("welcome.jar"));
        assertTrue(f.isArchiveType("c:/programs/keller.sar"));
        assertTrue(f.isArchiveType("/programs/keller.ear"));
        assertFalse(f.isArchiveType("welcome.nar"));
    }
    
    public void testFindWithClassPath() {
        String []parts = {
                "src/test/resources/simple",
                "src/test/resources/jars/gepo-1.2.1.jar",
                "src/test/resources/dupe-jars",
                };
        
        String cp = "";
        for (int i = 0; i < parts.length; i++) {
            cp += parts[i];
            
            if (i + 1 < parts.length) {
                cp += File.pathSeparator;
            }
        }
        
        String []args = {"jarinfo", "-cp", cp};
        
        Properties props = JarFish.parseArgs(args);
        
        JarFinder jf = new JarFinder(props);
        
        ArchiveCountListener cl = new ArchiveCountListener();
        jf.setListener(cl);
        
        jf.start();
        
        
        assertEquals(18, cl.getClasses().size());
        assertEquals(3, cl.getArchives().size());
    }
}
