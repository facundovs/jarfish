package org.reassembler.classfish;

import org.reassembler.classfish.ArchiveScanListener;
import org.reassembler.classfish.FindFile;


public class ArchiveCountListener extends CountListener implements ArchiveScanListener {
    private boolean countArchives = true;
    private boolean countClasses = true;
    private boolean debug;

    public void foundClass(FindFile file) {
        if (isDebug()) {
            System.out.println("ArchiveCountListener.foundClass()");
        }
        
        if (countClasses) {
            increment();
        }
    }

    public boolean isCountClasses() {
        if (isDebug()) {
            System.out.println("ArchiveCountListener.isCountClasses()");
        }
        
        return countClasses;
    }


    public void setCountClasses(boolean countClasses) {
        if (isDebug()) {
            System.out.println("ArchiveCountListener.setCountClasses()");
        }
        
        this.countClasses = countClasses;
    }


    public boolean isCountArchives() {
        if (isDebug()) {
            System.out.println("ArchiveCountListener.isCountArchives()");
        }
        
        return countArchives;
    }


    public void setCountArchives(boolean countArchives) {
        if (isDebug()) {
            System.out.println("ArchiveCountListener.setCountArchives()");
        }
        
        this.countArchives = countArchives;
    }

    public void foundArchive(FindFile file) {
        if (isDebug()) {
            System.out.println("ArchiveCountListener.foundArchive()");
        }
        
        if (countArchives) {
            increment();
        }
    }



    public void foundFile(FindFile file) {
        if (isDebug()) {
            System.out.println("ArchiveCountListener.foundFile()");
        }
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}
