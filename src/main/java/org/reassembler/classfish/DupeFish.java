package org.reassembler.classfish;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class DupeFish extends BaseFish implements ArchiveScanListener {
    private Map classIndex = new HashMap();
    private Set dupes = new HashSet();
    private int classesSeen;
    private String classFilter;

    public DupeFish(Properties config) {
        super(config);
        
        this.classFilter = config.getProperty("classFilter");
    }
    
    public void foundClass(FindFile file) {
        this.classesSeen++;
        
        super.foundClass(file);
        
        String name = file.getName();
        
        if (this.classFilter != null) {
            boolean matches = BaseFish.classNameMatches(this.classFilter, name);
            
            if (!matches) {
                ScanFish.traceLoud("rejected: " + name);
                return;
            }
        }
                        
        List l = (List) this.classIndex.get(name);
                        
        if (l == null) {
            l = new ArrayList(4);
            this.classIndex.put(name, l);
        }
                        
        l.add(file);
                        
        if (l.size() == 2) {
            ScanFish.traceLoud(">");
            dupes.add(l);
        }
    }

    public Set getDupes() {
        return dupes;
    }

    
    public List getMixed() {
        List mixed = new ArrayList();
        Iterator it = this.dupes.iterator();
        while (it.hasNext()) {
            List list = (List) it.next();
            Map versions = getQuickClassVersion(list);
            
            if (versions.size() > 1) {
                mixed.add(list);
            }
        }
        
        Collections.sort(mixed, new Comparator() {
            public int compare(Object o1, Object o2) {
                FindFile f1 = (FindFile) ((List) o1).get(0);
                FindFile f2 = (FindFile) ((List) o2).get(0);
                
                return f1.getName().compareTo(f2.getName());
            }});
        
        return mixed;
    }
    
    private static Map getQuickClassVersion(List entries) {
        Map m = new HashMap();
        Iterator ite = entries.iterator();
        while (ite.hasNext()) {
            FindFile em = (FindFile) ite.next();
            Long size = new Long(em.getSize());
            
            Integer ver = (Integer) m.get(size);
            if (ver == null) {
                ver = new Integer(m.size() + 1);
            }
            
            m.put(size, ver);
            em.getMeta().put("quickVersion", ver);
        }
        
        return m;
    }

    public int getClassesSeen() {
        return classesSeen;
    }
    
}
