package org.reassembler.classfish;


public interface ArchiveScanListener {
    /**
     * Called when an archive file is found.
     */
    public void foundArchive(FindFile file);
    
    /**
     * Called when a class file is found.
     */
    public void foundClass(FindFile file);
    
    /**
     * Called when any type of file besides an archive or a class is found.
     */
    public void foundFile(FindFile file);
}
