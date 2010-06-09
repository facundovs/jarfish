package org.reassembler.classfish;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class MixedPackageCommand extends BaseCommand {
    public void start() {
        Properties config = getProperties();
        DupeFish df = new DupeFish(config);
        JarFinder jf = new JarFinder(config);
        
        jf.setListener(df);
        jf.start();
        
        Map mixedArchives = new HashMap();
        
        Iterator it = df.getMixed().iterator();
        while (it.hasNext()) {
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
                
                String source = ff.getMeta().get("source").toString();
                
                MixedArchive ma = (MixedArchive) mixedArchives.get(source);
                
                if (ma == null) {
                    ma = new MixedArchive(source);
                    mixedArchives.put(source, ma);
                }
                
                ma.addPackage(FindFish.getPackageNameFromFileName(ff.getName()));
            }
        }
        
        it = mixedArchives.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry me = (Map.Entry) it.next();;
            
            System.out.println(me.getValue());
        }
    }
    
  
}

