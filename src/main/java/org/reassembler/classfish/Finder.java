package org.reassembler.classfish;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Finder {
    private FindListener listener;
    private boolean recursive = true;

    public FindListener getListener() {
        return listener;
    }

    public void setListener(FindListener listener) {
        this.listener = listener;
    }
    
    // XXX TODO probably could be refactored to work like the classpath finder
    public void findFromList(File path) {
        String ln = null;
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(path));
            while ((ln = in.readLine()) != null) {
                String line = ln.trim();
                File file = new File(line);
                
                if (file.exists()) {
                    this.listener.found(file);
        
                    if (this.recursive && file.isDirectory()) {
                        find(file);
                    }
                }
                else {
                    System.err.println("file in list does not exist: " + file);
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (IOException e) {}
            }
        }
    }

    public void find(File path) {
        if (!path.exists()) {
            throw new IllegalArgumentException("file does not exist: " + path);
        }
        
        File []files = null;
        if (path.isDirectory()) {
            files = path.listFiles();
        }
        else {
            files = new File[1];
            files[0] = path;
        }
            
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
        
                this.listener.found(file);
        
                if (this.recursive && file.isDirectory()) {
                    find(file);
                }
            }
        }
        else {
            System.err.println("could not scan: " + path);
        }
    }

    public boolean isRecursive() {
        return recursive;
    }

    public void setRecursive(boolean recursive) {
        this.recursive = recursive;
    }
}
