package org.reassembler.jarfish;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;

import org.reassembler.jarfish.JarMeta.EntryIterator;

public class JarIndexer {
    private List indeces = new ArrayList();

    private void indexPath(File path, final boolean recurse) {
        File []files = null;
        if (path.isDirectory()) {
            if (!path.canRead()) {
                throw new IllegalArgumentException("can not read path: " + path);
            }

            files = path.listFiles(new FileFilter() {
                public boolean accept(File pathname) {
                    return (recurse && pathname.isDirectory() && pathname.canRead())
                            || pathname.getName().matches(".+(jar|zip)$");
                }
            });
        }
        else {
            // assume the file is jar file
            files = new File[1];
            files[0] = path;
        }
        
        JarIndex ji = new DefaultJarIndex();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];

            if (file.isDirectory()) {
                indexPath(file, recurse);
            }
            else {
                JarMeta jm = new JarMeta(file.getAbsolutePath());

                if (file.canRead()) {
                    switch (JarFish.verbosityThreshold) {
                    case JarFish.NORMAL:
                        System.out.print(".");
                        break;
                        
                    case JarFish.QUIET:
                        break;
                        
                    case JarFish.LOUD:
                        System.out.println("+" + file);
                        break;
                    }
                    
                    String[] entries;

                    try {
                        entries = scan(file);
                        jm.setEntries(entries);

                        ji.addJar(jm);
                    } 
                    catch (IOException e) {
                        System.err.println("error scanning: " + file);
                        e.printStackTrace();
                    }
                } 
                else {
                    System.err.println("\ncan't read: " + file.getAbsolutePath());
                }
            }
        }

        this.indeces.add(ji);
    }

    private String[] scan(File file) throws IOException {
        JarFile jf = new JarFile(file);
        List entries = new ArrayList();

        Enumeration e = jf.entries();
        while (e.hasMoreElements()) {
            Object entry = (Object) e.nextElement();

            if (JarFish.verbosityThreshold > JarFish.NORMAL) {
                System.out.println(entry);
            }

            entries.add(entry.toString());
        }

        return (String[]) entries.toArray(new String[entries.size()]);
    }

    public int getJarCount() {
        int count = 0;
        Iterator it = this.indeces.iterator();
        while (it.hasNext()) {
            JarIndex ji = (JarIndex) it.next();
            count += ji.getJarCount();
        }

        return count;
    }

    public EntryMeta[] find(String name, String type, Config conf) {
        Collection entries = new ArrayList();
        Iterator it = this.indeces.iterator();
        while (it.hasNext()) {
            JarIndex ji = (JarIndex) it.next();

            entries.addAll(Arrays.asList(ji.find(name, type, conf)));
        }

        return (EntryMeta[]) entries.toArray(new EntryMeta[entries.size()]);
    }

    public void dump(PrintStream out) {
        Iterator it = this.indeces.iterator();
        while (it.hasNext()) {
            JarIndex ji = (JarIndex) it.next();

            ji.dump(out);
        }
    }

    public void index(File file, boolean recurse) {
        if (file.getName().endsWith(".lst")) {
            String ln = null;
            BufferedReader in;
            try {
                in = new BufferedReader(new FileReader(file));

                while ((ln = in.readLine()) != null) {
                    String line = ln.trim();
                    if (line.startsWith("#") || line.length() == 0) {
                        continue;
                    }

                    File lfile = new File(line);

                    indexPath(lfile, recurse);
                }

                in.close();
            } 
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            indexPath(file, recurse);
        } 
    }

    public EntryMeta[] findStringInJavaClass(String query, Config conf) {
        Collection entries = new ArrayList();
        Iterator it = this.indeces.iterator();
        while (it.hasNext()) {
            JarIndex ji = (JarIndex) it.next();

            entries.addAll(Arrays.asList(ji.findString(query, conf)));
        }

        return (EntryMeta[]) entries.toArray(new EntryMeta[entries.size()]);
    }

    public List findDuplicateClasses(Config conf) {
        List dupes = new ArrayList();
        Map m = new HashMap();
        
        Piper indexPipe = new Piper(10, '=');
        Iterator it = this.indeces.iterator();
        while (it.hasNext()) {
            JarIndex ji = (JarIndex) it.next();
            
            indexPipe.pipe();
            
            Piper metaPipe = new Piper(10, 'o');
            
            Iterator tor = ji.getJars().iterator();
            while (tor.hasNext()) {
                JarMeta jm = (JarMeta) tor.next();
                metaPipe.pipe();
                
                Piper entryPipe = new Piper(500, '$');
                
                EntryIterator eit = jm.entryIterator();
                while (eit.hasNext()) {
                    EntryMeta em = (EntryMeta) eit.next();
                    
                    entryPipe.pipe();
                    
                    if ("class".equals(em.getType())) {
                        String name = em.getEntryName();
                        
                        List l = (List) m.get(name);
                        
                        if (l == null) {
                            l = new ArrayList(4);
                            m.put(name, l);
                        }
                        
                        l.add(em);
                        
                        int size = l.size();
                        
                        if (l.size() == 2) {
                            // we have found a duplicate, so let's load
                            // extended meta info.
                            
                            try {
                                // load this one
                                jm.loadExtendedMeta(em);
                                
                                // load meta data for the first occurrence of the 
                                // class, we lazy load meta info to avoid 
                                // loading it for classes that don't end up
                                // having duplicates
                                jm.loadExtendedMeta(((EntryMeta) l.get(0)));
                            } 
                            catch (IOException e) {
                                e.printStackTrace();
                            }
                            
                            dupes.add(l);
                        }
                        else if (size > 2) {
                            try {
                                jm.loadExtendedMeta(em);
                            } 
                            catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        
        return dupes;
    }
    
    public Collection getIndeces() {
        return new ArrayList(this.indeces);
    }
}
