package org.reassembler.jarfish;

import org.reassembler.jarfish.EntryMeta;
import org.reassembler.jarfish.JarMeta;
import org.reassembler.jarfish.JarMeta.EntryIterator;

import java.util.Properties;

import junit.framework.TestCase;

public class JarMetaTest extends TestCase {
    public void testGetFileName() {
        JarMeta jar = new JarMeta("gepo.jar");
        
        assertEquals("gepo.jar", jar.getName());
    }
    
    public void testGetExtension() {
        assertEquals("class", JarMeta.getExtension("/qwe/qwe/qwe/qwe/qwe.class"));
        assertEquals("class", JarMeta.getExtension("Gepo$1.class"));
        assertEquals(null, JarMeta.getExtension("/qwe/qwe/qwe/qwe/qwe"));
    }
    
    public void testEntryIterator() {
       JarMeta jar = new JarMeta("gepo.jar");
        
        String []entries = {"com/mybking/Visc",
                "police/cars/are/cool",
                "NoSuchMethodExceptionNull",
                "WHATAWAYTO GO.txt"};
        
        jar.setEntries(entries);
        
        int c = 0;
        EntryIterator it = jar.entryIterator();
        while (it.hasNext()) {
            EntryMeta em = it.next();
            assertEquals(jar.getName(), em.getJarName());
            c++;
        }
        
        assertEquals(4, c);
        
        try {
            it.next();
            assertTrue(false);
        } 
        catch (Exception e) { }
        
    }
    
    public void testGetEntries() {
        JarMeta jar = new JarMeta("gepo.jar");
        
        String []entries = {"com/mybking/Visc",
                "police/cars/are/cool",
                "NoSuchMethodExceptionNull",
                "WHATAWAYTO GO.txt"};
        
        jar.setEntries(entries);
        
        entries = jar.getEntries();
        
        assertEquals(4, entries.length);
    }

    public void testLoadExtendedMeta() throws Exception {
        String jarName = "./src/test/resources/jars/gepo-1.2.1.jar";
        EntryMeta em = new EntryMeta(jarName, "org/reassembler/gepo/Gepo.class", "class");
        
        JarMeta jm = new JarMeta(jarName);
        
        jm.loadExtendedMeta(em);

        Properties props = em.getExtendedMeta();
        
        ClassMeta cm = (ClassMeta) props.get("classMeta");

        assertNotNull(props);
        assertEquals("45.3", cm.getClassVersion());
        assertEquals("1.0/1.1", cm.getJavaVersion());
        assertEquals("7456", props.getProperty("size"));
        assertNotNull(props.getProperty("time"));
    }
}
