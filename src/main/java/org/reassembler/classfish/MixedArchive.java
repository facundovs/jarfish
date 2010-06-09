package org.reassembler.classfish;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class MixedArchive {
    private String archiveName;
    private Set packages = new HashSet();

    MixedArchive(String archiveName) {
        this.archiveName = archiveName;
    }
    
    
    public void addPackage(String name) {
        this.packages.add(name);
    }
    
    static String getPackageFromClassName(String className) {
        String []parts = className.split("\\.");
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < parts.length - 1; i++) {
            String part = parts[i];
            sb.append(part);
            
            if ((i + 1) < parts.length - 1) {
                sb.append(".");
            }
        }
        
        return sb.toString();
    }


    public String getArchiveName() {
        return archiveName;
    }

    public Set getPackages() {
        return packages;
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(this.archiveName);
        
        Iterator it = this.packages.iterator();
        while (it.hasNext()) {
            String name = (String) it.next();
            sb.append("    ").append(name);
            if (it.hasNext()) {
                sb.append("\n");
            }
        }
        
        return sb.toString();
    }
}
