package org.reassembler.classfish;

import java.io.File;

import org.reassembler.classfish.Finder;

import junit.framework.TestCase;

public class FinderTest extends TestCase {
    public void testScanList() {
        Finder f = new Finder();
        
        CountListener cl = new CountListener();
        f.setListener(cl);
        
        File path = new File("src/test/resources/finder-tests/list-test/list1.txt");
        
        f.findFromList(path);
        
        assertEquals(2, cl.getCount());
        
        
    }
    
    public void testFollowPath() {
        Finder f = new Finder();
        
        CountListener cl = new CountListener();
        f.setListener(cl);
        
        File path = new File("src/test/resources/finder-tests/simple");
        
        f.find(path);
        
        assertEquals(3, cl.getCount());
    }
    
    public void testFollowPathWithBadPath() {
        Finder f = new Finder();
        
        CountListener cl = new CountListener();
        f.setListener(cl);
        
        File path = new File("src/test/resources/finder-324324324234234234234234234/simple");
        
        try {
            f.find(path);
            assertFalse(true);
        }
        catch (IllegalArgumentException e) {}
        
    }
    
    public void testDrainStreams() {
        Finder f = new Finder();
        
        DrainListener cl = new DrainListener();
        f.setListener(cl);
        
        File path = new File("src/test/resources/finder-324324324234234234234234234/simple");
        
        try {
            f.find(path);
            assertFalse(true);
        }
        catch (IllegalArgumentException e) {}
        
    }
    
    public void testFollowRecursion() {
        Finder f = new Finder();
        
        CountListener cl = new CountListener();
        f.setListener(cl);
        
        File path = new File("src/test/resources/finder-tests/nested");
        
        // recurse is on by default
        f.find(path);
        
        assertEquals(8, cl.getCount());
        
        // recursion off
        cl = new CountListener();
        f.setListener(cl);
        f.setRecursive(false);
        
        f.find(path);
        
        assertEquals(1, cl.getCount());
        
        
        path = new File("src/test/resources/finder-tests/nested/simple");
        cl = new CountListener();
        f.setListener(cl);
        f.setRecursive(false);
        
        f.find(path);
        
        assertEquals(4, cl.getCount());
    }
}
