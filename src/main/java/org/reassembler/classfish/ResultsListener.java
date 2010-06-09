package org.reassembler.classfish;

public interface ResultsListener {
    void foundClass(FindFile file);
    void foundArchive(FindFile file);
}
