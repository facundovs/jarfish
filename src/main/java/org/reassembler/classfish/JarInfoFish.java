package org.reassembler.classfish;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

public class JarInfoFish extends BaseFish {
    public JarInfoFish(Properties config) {
        super(config);
    }
    
    public void foundArchive(FindFile file) {
        super.foundArchive(file);
        
        emitArchiveName();
        
        try {
            JarInputStream jin = new JarInputStream(file.getStream());
            
            trace(file.getMeta());
            
            Manifest manifest = jin.getManifest();
            if (manifest != null) {
                Attributes attributes = manifest.getMainAttributes();
            
                if (attributes != null) {
                    trace("  Manifest Attributes (Main)");
                    Iterator it = attributes.entrySet().iterator();
                    while (it.hasNext()) {
                        Entry e = (Entry) it.next();
                        trace("    " + e.getKey() + ": " + e.getValue());
                    }
                
                    trace("--------");
            
                    JarEntry je = null;
                    while ((je = jin.getNextJarEntry()) != null) {
                        String name = je.getName();
                        if (name.endsWith("pom.properties")) {
                            String content = slurp(jin);
                    
                            trace("*#*#*#*#*#*#*#*#*##*#*");
                            trace("  pom.properties");
                            trace(content);
                            trace("*#*#*#*#*#*#*#*#*##*#*");
                        }
                    }
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void trace(Object msg) {
        System.out.println(msg);
    }
}
