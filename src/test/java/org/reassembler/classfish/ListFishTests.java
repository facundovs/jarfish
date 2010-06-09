package org.reassembler.classfish;

import java.util.Properties;

import org.reassembler.jarfish.JarFish;

import junit.framework.TestCase;

public class ListFishTests extends TestCase {
    public void testList() {
        String []args = {"list", "src/test/resources/simple"};

        Properties props = JarFish.parseArgs(args);
                
        ListFish lf = new ListFish(props);
        
        ArchiveCountListener cl = new ArchiveCountListener();
        cl.setCountClasses(true);
        cl.setCountArchives(true);
        
        lf.setResultsListener(cl);
        
        JarFinder jf = new JarFinder(props);
        jf.setListener(lf);
        jf.start();
        
        assertEquals(12, cl.getClasses().size());
        assertEquals(1, cl.getArchives().size());
    }
    
    public void testListWithIgnoreRawClasses() {
        String []args = {"list", "-w", "src/test/resources/simple"};

        Properties props = JarFish.parseArgs(args);
                
        ListFish lf = new ListFish(props);
        
        ArchiveCountListener cl = new ArchiveCountListener();
        cl.setCountClasses(true);
        cl.setCountArchives(true);
        
        lf.setResultsListener(cl);
        
        JarFinder jf = new JarFinder(props);
        jf.setListener(lf);
        jf.start();
        
        assertEquals(5, cl.getClasses().size());
        assertEquals(1, cl.getArchives().size());
    }    
    
    
    public void testNestedList() {
        String []args = {"list", "src/test/resources/jarfinder-tests/nestedjar"};

        Properties props = JarFish.parseArgs(args);
                
        ListFish lf = new ListFish(props);
        
        ArchiveCountListener cl = new ArchiveCountListener();
        cl.setCountClasses(true);
        cl.setCountArchives(true);
        
        lf.setResultsListener(cl);
        
        JarFinder jf = new JarFinder(props);
        jf.setListener(lf);
        jf.start();
        
        assertEquals(9, cl.getClasses().size());
        assertEquals(3, cl.getArchives().size());
    }        
}
