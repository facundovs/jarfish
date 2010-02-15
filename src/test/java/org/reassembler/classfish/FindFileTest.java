package org.reassembler.classfish;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.reassembler.classfish.FindFile;

import junit.framework.TestCase;

public class FindFileTest extends TestCase {
    public void testGetSimpleStream() throws IOException {
        File classFile = new File("src/test/resources/simple/A.class");
        FindFile cf = new FindFile(classFile);
        
        byte []buffer = new byte[4096];
        int bytesRead = -1;
        InputStream in = cf.getStream();
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        while ((bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }
        
        out.close();
        
        assertEquals(427, out.size());
    }
    

    
    public void testGetname() {
        File classFile = new File("src/test/resources/simple/A.class");
        FindFile cf = new FindFile(classFile);
        
        assertEquals(classFile.getName(), cf.getName());
    }
}
