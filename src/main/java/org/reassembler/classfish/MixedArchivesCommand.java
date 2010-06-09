package org.reassembler.classfish;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

public class MixedArchivesCommand extends BaseCommand {
    public void start() {
        Properties config = getProperties();
        DupeFish df = new DupeFish(config);
        JarFinder jf = new JarFinder(config);
        
        jf.setListener(df);
        jf.start();
        
        Iterator it = df.getMixed().iterator();
        while (it.hasNext()) {
            boolean header = true;
            List vl = (List) it.next();
            
            Collections.sort(vl, new Comparator() {
                public int compare(Object o1, Object o2) {
                    FindFile f1 = (FindFile) o1;
                    FindFile f2 = (FindFile) o2;
                    
                    Integer s1 = (Integer) f1.getMeta().get("quickVersion"); 
                    Integer s2 = (Integer) f2.getMeta().get("quickVersion"); 
                    return s1.compareTo(s2);
                }});
            
            Iterator li = vl.iterator();
            while (li.hasNext()) {
                FindFile ff = (FindFile) li.next();
                
                if (header) {
                    System.out.println(ff.getName());
                    header = false;
                }
                
                Object v = ff.getMeta().get("quickVersion");
                ff.getMeta().get("source");
                System.out.println("    [" + v + "] " + ff.getMeta().get("source"));
            }
            
            System.out.println("-----------------");
        }
        
        ScanFish.traceLoud("classes seen: " + df.getClassesSeen());
    }
}
