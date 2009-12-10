package org.reassembler.jarfish;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class JarMeta {
    private String name;
    private String []entries;

    public JarMeta(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String[] getEntries() {
        return entries;
    }

    public void setEntries(String[] entries) {
        this.entries = entries;
    }

    public EntryIterator entryIterator() {
        return new EntryIterator();
    }
    
    public class EntryIterator {
        private int entryCount = JarMeta.this.entries.length;
        private int index = 0;
        
        public boolean hasNext() {
            return index < entryCount;
        }

        public EntryMeta next() {
            if (JarMeta.this.entries.length != entryCount) {
                throw new ConcurrentModificationException("entries changed while iterating");
            }
            
            return metaForEntry(JarMeta.this.entries[index++]);
        }

        public void remove() {
            throw new IllegalStateException("method not implemented");
        }
    }
    
    private EntryMeta metaForEntry(String entry) {
        String ename = entry;
        String fileName = lastBit(ename);
        
        String type = null;
        if (ename.endsWith("/") && !(ename.startsWith("META-INF"))) {
            type = "package";    
        }
        else {
            type = JarFish.getTypeByExtension(getExtension(fileName));
        }

        EntryMeta meta = new EntryMeta(this.name, ename, type);
        
        return meta;
    }

    public Collection getAllEntries() {
        List entries = new ArrayList();
        for (int i = 0; i < this.entries.length; i++) {
            System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
            String ename = this.entries[i];
            String fileName = lastBit(ename);
            
            String type = null;
            if (ename.endsWith("/") && !(ename.startsWith("META-INF"))) {
                type = "package";    
            }
            else {
                type = JarFish.getTypeByExtension(getExtension(fileName));
            }

            EntryMeta meta = new EntryMeta(this.name, ename, type);
            
            boolean load = type.equals("class");
                            
            if (load) {
                try {
                    loadExtendedMeta(meta);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }    
            }    
                
            entries.add(meta);
        }
        
        return entries;        
    }
    
    public Collection getEntry(String name, Config conf) {
        List entries = new ArrayList();
        for (int i = 0; i < this.entries.length; i++) {
            String ename = this.entries[i];
            String fileName = lastBit(ename);
            String compareName = name;
            
            if (conf.isQueryCaseInsensitive()) {
                fileName = fileName.toLowerCase();
                compareName = compareName.toLowerCase();
            }
            
            if (fileName.indexOf(compareName) != -1) {
                String type = null;
                if (ename.endsWith("/") && !(ename.startsWith("META-INF"))) {
                    type = "package";    
                }
                else {
                    type = JarFish.getTypeByExtension(getExtension(fileName));
                }

                EntryMeta meta = new EntryMeta(this.name, ename, type);

                if (conf.getLoadExtendedMetaData()) {
                    try {
                        loadExtendedMeta(meta);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }    
                }    

                entries.add(meta);
            }
        }
        
        return entries;
    }

    static String getExtension(String fileName) {
        String []parts = fileName.split("\\.");
        if (parts.length == 1) {
            return null;
        }
        else {
            return parts[parts.length - 1];
        }
    }

    void loadExtendedMeta(EntryMeta meta) throws IOException {
        JarFile jar = new JarFile(meta.getJarName());
        ZipEntry je = jar.getEntry(meta.getEntryName());
        
        meta.setExtended("size", Long.toString(je.getSize()));
        meta.setExtended("time", new Date(je.getTime()).toString());
        
        byte []extra = je.getExtra();
        if (extra != null) {
            meta.setExtended("extra", new String(extra));
        }
        
        String comment = je.getComment();
        if (comment != null) {
            meta.setExtended("comment", comment);
        }

        if ("class".equals(meta.getType())) {
            DataInputStream din = new DataInputStream(jar.getInputStream(je));
            
            // check first four bytes
            ClassParser cp = new ClassParser();
            if (cp.testSig(din)) {
                ClassMeta cm = new ClassMeta();
                
                cp.parseHeader(cm, din);
                cp.parsePool(cm, din);
                
                meta.getExtendedMeta().put("classMeta", cm);
            }
        
            din.close();
        }
    }

    
    private String lastBit(String name) {
		String []parts = name.split("/");
		
		return parts[parts.length - 1];
	}

	public String toString() {
        return this.name;
    }

    public void dump(PrintStream out) {
        out.println();
        out.println(this.name);
        
        for (int i = 0; i < this.entries.length; i++) {
            String name = this.entries[i];
            out.println("    " + name);
        }
    }
}
