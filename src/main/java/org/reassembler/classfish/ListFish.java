package org.reassembler.classfish;

import java.util.Properties;

public class ListFish extends BaseFish {
    public ListFish(Properties config) {
        super(config);
    }
    
    public void foundFile(FindFile file) {
        super.foundFile(file);
        emitFile(file);
    }
    
    public void foundClass(FindFile file) {
        super.foundClass(file);
        emitClass(file);
    }
    
    public void foundArchive(FindFile file) {
        super.foundArchive(file);
        emitArchiveName();
    }
}
