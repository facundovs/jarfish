package org.reassembler.classfish;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class FindArchiveFile extends FindFile {
    private JarEntry entry;
    private byte []content;
    private JarFile parent;
    private Jar jar;

    public FindArchiveFile(JarFile parent, Jar jar, JarEntry entry) {
        this.jar = jar;
        this.entry = entry;
        this.parent = parent;
        
        setSize(entry.getSize());
    }
    
    public FindArchiveFile(Jar jar, JarEntry entry) {
        this(null, jar, entry);
    }
    
    public InputStream getStream() throws IOException {
        if (this.content != null) {
            return new ByteArrayInputStream(this.content);
        }
        else {
            return this.jar.getJarFile().getInputStream(this.entry);
        }
    }
    
    public String getName() {
        return this.entry.getName();
    }

    public Jar getJar() {
        return jar;
    }

    public void setJar(Jar jar) {
        this.jar = jar;
    }

    public ZipEntry getEntry() {
        return entry;
    }

    public void setEntry(JarEntry entry) {
        this.entry = entry;
    }
    
    public boolean isArchive() {
        return true;
    }

    public JarFile getJarFile() {
        return null;
    }
    
    public String toString() {
        String parent = null;
        if (this.parent != null) {
            parent = this.parent.getName();
            
        }
        return (parent == null ? "" : "parent: " + parent + ", ") + this.jar.getName() + " <-- " + this.entry.getName();
    }

    public JarFile getParent() {
        return parent;
    }

    public void setParent(JarFile parent) {
        this.parent = parent;
    }
}
