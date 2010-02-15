package org.reassembler.classfish;

import java.io.File;

import org.reassembler.classfish.FindListener;

class CountListener implements FindListener {
    private int count = 0;

    public void found(File file) {
        this.count++;
    }
    
    protected void increment() {
        this.count++;
    }

    public int getCount() {
        return this.count;
    }
    
    public void reset() {
        this.count = 0;
    }

    public void foundArchive(File file) {
        this.count++;
    }
}
