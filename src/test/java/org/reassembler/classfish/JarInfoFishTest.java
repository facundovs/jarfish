package org.reassembler.classfish;

import java.util.Properties;

import org.reassembler.jarfish.JarFish;

import junit.framework.TestCase;

public class JarInfoFishTest extends TestCase {
    public void testBasicScan() {
        String []args = {"jarinfo", "src/test/resources/jarfinder-tests/simple"};

        Properties props = JarFish.parseArgs(args);
        
        JarInfoFish f = new JarInfoFish(props);
        
        ArchiveCountListener cl = new ArchiveCountListener();
        cl.setCountArchives(true);
        cl.setCountClasses(false);
        f.setResultsListener(cl);
        JarFinder jf = new JarFinder(props);
        jf.setListener(f);
        jf.start();
        
        assertEquals(5, cl.getCount());
        
    }
    
    public void testNestedScan() {
        String []args = {"jarinfo", "src/test/resources/jarfinder-tests/nestedjar"};

        Properties props = JarFish.parseArgs(args);
        
        JarInfoFish f = new JarInfoFish(props);
        
        ArchiveCountListener cl = new ArchiveCountListener();
        cl.setCountArchives(true);
        cl.setCountClasses(false);
        f.setResultsListener(cl);
        JarFinder jf = new JarFinder(props);
        jf.setListener(f);
        jf.start();
        
        assertEquals(3, cl.getCount());
        
        System.out.println(cl.getArchives());
    }
    
    public void testNestedScanWithJarName() {
        String []args = {"jarinfo", "-jn", "simple.jar", "src/test/resources/jarfinder-tests/nestedjar"};

        Properties props = JarFish.parseArgs(args);
        
        JarInfoFish f = new JarInfoFish(props);
        
        ArchiveCountListener cl = new ArchiveCountListener();
        cl.setCountArchives(true);
        cl.setCountClasses(false);
        f.setResultsListener(cl);
        JarFinder jf = new JarFinder(props);
        jf.setListener(f);
        jf.start();
        
        assertEquals(1, cl.getCount());
        
        System.out.println(cl.getArchives());
    }
}
