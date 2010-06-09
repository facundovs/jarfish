package org.reassembler.classfish;

import java.util.jar.JarFile;

/**
 * Just a wrapper for JarFile to keep track lineage
 * @author dpetersh
 *
 */
public class Jar {
    private Jar parent;
    private JarFile jarFile;
    private String name;

    public Jar(JarFile jar) {
        this.jarFile = jar;
    }

    public Jar(Jar parent, JarFile jarFile) {
        this.parent = parent;
        this.jarFile = jarFile;
    }

    public Jar getParent() {
        return parent;
    }

    public void setParent(Jar parent) {
        this.parent = parent;
    }

    public JarFile getJarFile() {
        return jarFile;
    }

    public void setJarFile(JarFile jarFile) {
        this.jarFile = jarFile;
    }

    public String getName() {
        if (this.name == null) {
            return this.jarFile.getName();
        }
        else {
            return this.name + " --> " + this.jarFile.getName();
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLineage() {
        String me = "";
        
        Jar parent = null;
        Jar jar = this;
        while ((parent = jar.getParent()) != null) {
           me += parent.getName() 
               + " <-- "; 
           
           jar = parent;
        }
        
        return me + getJarFile().getName();
    }
}
