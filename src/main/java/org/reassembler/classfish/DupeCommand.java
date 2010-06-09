package org.reassembler.classfish;

import java.util.Iterator;
import java.util.List;

public class DupeCommand extends BaseCommand {
    public void start() {
        JarFinder jf = new JarFinder(properties);
        DupeFish df = new DupeFish(properties);
        jf.setListener(df);
        jf.start();
        
        Iterator it = df.getDupes().iterator();
        while (it.hasNext()) {
            Iterator li = ((List) it.next()).iterator();
            while (li.hasNext()) {
                FindFile ff = (FindFile) li.next();
                System.out.println(ff.getName() + ":" + ff.getMeta());
            }
            
            System.out.println("-----------------");
        }
        
        ScanFish.traceLoud("classes seen: " + df.getClassesSeen());
    }

}
