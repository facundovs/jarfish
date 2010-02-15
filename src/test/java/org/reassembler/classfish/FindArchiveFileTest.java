package org.reassembler.classfish;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import junit.framework.TestCase;

public class FindArchiveFileTest extends TestCase {
    public void testGetArchiveStream() throws IOException {
        JarFile jar = new JarFile("src/test/resources/simple/simple.jar");
        JarEntry entry = jar.getJarEntry("Simple.class");
        FindArchiveFile fae = new FindArchiveFile(new Jar(jar), entry);
        
        byte []buffer = new byte[4096];
        int bytesRead = -1;
        InputStream in = fae.getStream();
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        while ((bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }
        
        out.close();
        
        assertEquals(451, out.size());
    }    
    
    public void testArchiveInArchiveParse() {
        Properties props = new Properties();
        props.setProperty("path", "src/test/resources/findarchivefiletests");
        JarFinder jf = new JarFinder(props);
        
        class Counter {
            int count = 0;
            void increment() {
                count++;
            }
        }
        
        final Counter counter = new Counter();
        
        ArchiveScanListener l = new ArchiveScanListener(){
            public void foundArchive(FindFile file) {
            }

            public void foundClass(FindFile file) {
                if (file.getName().endsWith("Gepo.class")) {
                    counter.increment();
                    System.out.println(file.getName());
                    try {
                        byte []bytes = loadStream(file.getStream());
                        
                        assertEquals(7456, bytes.length);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                        fail();
                    }
                }
            }

            public void foundFile(FindFile file) {
            }};
            
        jf.setListener(l);
        jf.start();
        assertEquals(1, counter.count);
    }    
    
    private byte[] loadStream(InputStream inputStream) throws IOException {
        byte []buffer = new byte[4096];
        int bytesRead = -1;
        InputStream in = inputStream;
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        while ((bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }
        
        in.close();
        
        return out.toByteArray(); 
    }


    
    public void testGetName() throws IOException {
        JarFile jar = new JarFile("src/test/resources/simple/simple.jar");
        JarEntry entry = jar.getJarEntry("Simple.class");
        FindArchiveFile fae = new FindArchiveFile(new Jar(jar), entry);
        
        assertEquals("Simple.class", fae.getName());
    }
}
