package org.reassembler.classfish;

import java.util.Properties;

import junit.framework.TestCase;

import org.reassembler.jarfish.JarFish;

public class FindFishTests extends TestCase {
    public void testFindOnlyPackage() {
        String []args = {"find", "org.reassembler.gepo",  "./src/test/resources/jars/gepo-1.2.1.jar"};
        Properties props = JarFish.parseArgs(args);
        FindFish ff = new FindFish(props);
        
        assertEquals(false, ff.isSearchClassPackage());
        assertEquals(true, ff.isSearchForPackageOnly());
        assertEquals("org.reassembler.gepo", ff.getPackageName());
    }         
    
    public void testFindWithPackage() {
        String []args = {"find", "org.reassembler.gepo.Gepo",  "./src/test/resources/jars/gepo-1.2.1.jar"};
        Properties props = JarFish.parseArgs(args);
        FindFish ff = new FindFish(props);
        
        assertEquals(true, ff.isSearchClassPackage());
        assertEquals("org.reassembler.gepo", ff.getPackageName());
        
        JarFinder jf = new JarFinder(props);
        jf.setListener(ff);
        
        ArchiveCountListener cl = new ArchiveCountListener();
        cl.setCountArchives(false);
        cl.setCountClasses(true);
        ff.setResultsListener(cl);
        jf.start();
        
        assertEquals(1, cl.getCount());
        
        args = new String[]{"find", "org.reassembler.gepo.Gepo$1",  "./src/test/resources/jars/gepo-1.2.1.jar"};
        props = JarFish.parseArgs(args);
        jf = new JarFinder(props);
        ff = new FindFish(props);
        jf.setListener(ff);
        cl = new ArchiveCountListener();
        cl.setCountArchives(false);
        cl.setCountClasses(true);
        ff.setResultsListener(cl);
        jf.start();
        
        assertEquals(1, cl.getCount());
    }
    
    
    public void testFindWithSlashesInPackage() {
        String []args = {"find", "org/reassembler/gepo/Gepo",  "./src/test/resources/jars/gepo-1.2.1.jar"};
        Properties props = JarFish.parseArgs(args);
        FindFish ff = new FindFish(props);
        
        assertEquals(true, ff.isSearchClassPackage());
        assertEquals("org.reassembler.gepo", ff.getPackageName());
        
        JarFinder jf = new JarFinder(props);
        jf.setListener(ff);
        
        ArchiveCountListener cl = new ArchiveCountListener();
        cl.setCountArchives(false);
        cl.setCountClasses(true);
        ff.setResultsListener(cl);
        jf.start();
        
        assertEquals(1, cl.getCount());
        
        args = new String[]{"find", "org.reassembler.gepo.Gepo$1",  "./src/test/resources/jars/gepo-1.2.1.jar"};
        props = JarFish.parseArgs(args);
        jf = new JarFinder(props);
        ff = new FindFish(props);
        jf.setListener(ff);
        cl = new ArchiveCountListener();
        cl.setCountArchives(false);
        cl.setCountClasses(true);
        ff.setResultsListener(cl);
        jf.start();
        
        assertEquals(1, cl.getCount());
    }    
    
    public void testFindPackage() {
        String []args = {"find", "org.reassembler.gepo",  "./src/test/resources/jars/gepo-1.2.1.jar"};
        Properties props = JarFish.parseArgs(args);
        FindFish ff = new FindFish(props);
        
        assertEquals(false, ff.isSearchClassPackage());
        assertEquals(true, ff.isSearchForPackageOnly());
        assertEquals("org.reassembler.gepo", ff.getPackageName());
        
        JarFinder jf = new JarFinder(props);
        jf.setListener(ff);
        
        ArchiveCountListener cl = new ArchiveCountListener();
        cl.setCountArchives(false);
        cl.setCountClasses(true);
        ff.setResultsListener(cl);
        jf.start();
        
        assertEquals(3, cl.getCount());
    }    
    
    public void testGetShortNameFromFileName() {
        String []names = {
                "org/reassembler/gepo/Gepo$1.class",
                "org/reassembler/gepo/Gepo$Default.class",
                "org/reassembler/gepo/Gepo.class",
        };
        
        assertEquals("Gepo$1", FindFish.getShortNameFromFileName(names[0]));
        assertEquals("Gepo$Default", FindFish.getShortNameFromFileName(names[1]));
        assertEquals("Gepo", FindFish.getShortNameFromFileName(names[2]));
    }
    
    public void testGetLongNameFromFileName() {
        String []names = {
                "org/reassembler/gepo/Gepo$1.class",
                "org/reassembler/gepo/Gepo$Default.class",
                "org/reassembler/gepo/Gepo.class",
        };
        
        assertEquals("org.reassembler.gepo.Gepo$1", FindFish.getLongNameFromFileName(names[0]));
        assertEquals("org.reassembler.gepo.Gepo$Default", FindFish.getLongNameFromFileName(names[1]));
        assertEquals("org.reassembler.gepo.Gepo", FindFish.getLongNameFromFileName(names[2]));
    }
    
    
    public void testGetPackageNameFromFileName() {
        String []names = {
                "org/reassembler/gepo/Gepo$1.class",
                "org/reassembler/gepo/Gepo$Default.class",
                "org/reassembler/gepo/Gepo.class",
                "org/reassembler/gepo/mia/Gepo.class",
                "Gepo.class",
        };
        
        assertEquals("org.reassembler.gepo", FindFish.getPackageNameFromFileName(names[0]));
        assertEquals("org.reassembler.gepo", FindFish.getPackageNameFromFileName(names[1]));
        assertEquals("org.reassembler.gepo", FindFish.getPackageNameFromFileName(names[2]));
        assertEquals("org.reassembler.gepo.mia", FindFish.getPackageNameFromFileName(names[3]));
        assertNull(FindFish.getPackageNameFromFileName(names[4]));
    }
    
    public void testFindWithoutPackage() {
        String []args = {"find", "Gepo",  "./src/test/resources/jars/gepo-1.2.1.jar"};
        Properties props = JarFish.parseArgs(args);
        FindFish ff = new FindFish(props);
        
        assertEquals(false, ff.isSearchClassPackage());
        assertNull(ff.getPackageName());
        assertEquals("Gepo", ff.getClassName());
    } 
    
 
}
