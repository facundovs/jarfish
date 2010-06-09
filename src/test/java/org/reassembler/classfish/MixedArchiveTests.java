package org.reassembler.classfish;

import junit.framework.TestCase;

public class MixedArchiveTests extends TestCase {
    public void testGetpackageFromClasName() {
        assertEquals("org.reassembler.gepo", MixedArchive.getPackageFromClassName("org.reassembler.gepo.Gepo"));
        assertEquals("", MixedArchive.getPackageFromClassName("Gepo"));
    }
    
    public void testConstruction() {
        MixedArchive ma = new MixedArchive("hollis.jar");
        assertEquals("hollis.jar", ma.getArchiveName());
    }
    
    public void testAddPackage() {
        MixedArchive ma = new MixedArchive("hollis.jar");
        
        ma.addPackage("org.reassembler.gepo");
        ma.addPackage("org.reassembler.gepo");
        ma.addPackage("javax.mail");
        
        
        assertEquals(2, ma.getPackages().size());
        
    }
}
