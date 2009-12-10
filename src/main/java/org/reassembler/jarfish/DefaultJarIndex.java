package org.reassembler.jarfish;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.reassembler.jarfish.JarMeta.EntryIterator;

public class DefaultJarIndex implements JarIndex {
    private List jars = new ArrayList();

    public int getJarCount() {
        return this.jars.size();
    }

    public void addJar(JarMeta jm) {
        this.jars.add(jm);
    }
    
    public EntryMeta[] findString(String query, Config conf) {
        List entries = new ArrayList();

        Iterator it = this.jars.iterator();
        while (it.hasNext()) {
            JarMeta jm = (JarMeta) it.next();

            EntryIterator iter = jm.entryIterator();
            while (iter.hasNext()) {
                EntryMeta em = (EntryMeta) iter.next();
                
                try {
                    jm.loadExtendedMeta(em);
                } 
                catch (IOException e) {
                    e.printStackTrace();
                }
                
                if (JarFish.typeisText(em.getType())) {
                    String s = JarFish.getEntryContentAsString(em);
                    
                    if (s == null) {
                        continue;
                    }
                    
                    boolean found = false;
                    if (conf.isQueryCaseInsensitive()) {
                        if (s.toLowerCase().indexOf(query.toLowerCase()) != -1) {
                            entries.add(em);
                            found = true;
                        }
                    }
                    else {
                        if (s.indexOf(query) != -1) {
                            entries.add(em);
                            found = true;
                        }
                    }
                    
                    if (found) {
                        System.out.println(em.getJarName());
                        System.out.println("  " + em.getEntryName());
                    }
                }
                else if ("class".equals(em.getType())) {
                    ClassMeta cm = (ClassMeta) em.getExtendedMeta().get("classMeta");
                    
                    if (cm == null) {
                        System.err.println("no class meta data available for search search");
                        continue;
                    }
                    
                    List strings = cm.getStringEntries();
                    
                    Iterator itera = strings.iterator();
                    while (itera.hasNext()) {
                        Object o = itera.next();
                        if (!(o instanceof String) || o == null) {
                            System.err.println("bad constant string received: " + o);
                            System.err.println(em.getEntryName() + " - (" + em.getJarName() + ")");
                            continue; 
                        }
                        String s = (String) o;
                        
                        boolean found = false;
                        if (conf.isQueryCaseInsensitive()) {
                            if (s.toLowerCase().indexOf(query.toLowerCase()) != -1) {
                                entries.add(em);
                                found = true;
                            }
                        }
                        else {
                            if (s.indexOf(query) != -1) {
                                entries.add(em);
                                found = true;
                            }
                        }
                        
                        if (found) {
                            System.out.println(em.getJarName());
                            System.out.println("  " + em.getEntryName());
                            System.out.println("    " + s);
                        }
                    }
                }
            }
        }

        return (EntryMeta[]) entries.toArray(new EntryMeta[entries.size()]);
    }
    
    public EntryMeta[] find(String name, String type, Config conf) {
        List entries = new ArrayList();

        Iterator it = this.jars.iterator();
        while (it.hasNext()) {
            JarMeta jm = (JarMeta) it.next();

            Collection found = jm.getEntry(name, conf);
            Iterator iter = found.iterator();
            while (iter.hasNext()) {
                EntryMeta em = (EntryMeta) iter.next();
                
                if (type == null || type.equals("any")) { 
                    entries.add(em);
                }
                else if (type.equals("class") && "class".equals(em.getType())) {
                    String []p = em.getEntryName().split("/");
                    String cname = p[p.length - 1].split("\\.")[0]; 
                        
                    if (name.equals(cname)) {
                        entries.add(em);
                    }
                }
                else if (type.equals(em.getType())) {
                    entries.add(em);
                    
                }
            }
        }

        return (EntryMeta[]) entries.toArray(new EntryMeta[entries.size()]);
    }

    public void dump(PrintStream out) {
        Iterator it = this.jars.iterator();
        while (it.hasNext()) {
            JarMeta jm = (JarMeta) it.next();
            
            jm.dump(out);
        }
    }

    public List getJars() {
        return new ArrayList(this.jars);
    }
}
