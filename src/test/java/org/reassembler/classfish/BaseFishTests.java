package org.reassembler.classfish;

import junit.framework.TestCase;

public class BaseFishTests extends TestCase {
    public void testClassNameMatchesWithCase() {
        String className = "FileGepo.class";
        
        assertTrue(BaseFish.classNameMatches("FileGepo", className));
        assertFalse(BaseFish.classNameMatches("Gepo", className));
        
        className = "org/reassembler/gepo/FileGepo.class";
        assertTrue(BaseFish.classNameMatches("FileGepo", className));
        assertFalse(BaseFish.classNameMatches("Gepo", className));
    }

}
