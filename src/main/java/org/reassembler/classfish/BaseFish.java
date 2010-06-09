package org.reassembler.classfish;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public abstract class BaseFish implements ArchiveScanListener {
    protected Properties config;
    protected FindFile lastArchive;
    private boolean emitted;
    protected ArchiveScanListener resultsListener;
    
    public BaseFish(Properties config) {
        this.config = config;
    }

    public void foundArchive(FindFile file) {
        this.lastArchive = file;
        this.emitted = false;
    }

    public void foundClass(FindFile file) {
    }

    public void foundFile(FindFile file) {
    }
    
    /**
     * Print the current archive name if it hasn't been printed yet.
     */
    protected void emitArchiveName() {
        if (!this.emitted) {
            if (this.resultsListener != null) {
                this.resultsListener.foundArchive(this.lastArchive);                
            }
            else {
                System.out.println(this.lastArchive);
            }
            
            this.emitted = true;
        }
    }
    
    protected void emitClass(FindFile file) {
        if (this.resultsListener != null) {
            this.resultsListener.foundClass(file);
        }
        else {
            System.out.println("    " + file.getName());
        }
    }
    
    protected void emitFile(FindFile file) {
        if (this.resultsListener != null) {
            this.resultsListener.foundFile(file);
        }
        else {
            System.out.println("    " + file.getName());
        }
    }
    
    
    protected static String slurp(InputStream jin) throws IOException {
        byte []buffer = new byte[4096];
        int bytesRead = -1;
        InputStream in = jin;
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        while ((bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }
        
        out.close();
        
        return out.toString();
    }

    /**
     * 
     * @param string - e.g. Gepo
     * @param className - e.g. org/reassembler/gepo/Gepo.class
     * @return
     */
    static boolean classNameMatches(String string, String className) {
        String []parts = className.split("\\.");
        
        parts = parts[parts.length - 2].split("\\/");
        String cleanedName = parts[parts.length - 1];
        
        return string.equals(cleanedName);
    }

    public ArchiveScanListener getResultsListener() {
        return resultsListener;
    }

    public void setResultsListener(ArchiveScanListener resultsListener) {
        this.resultsListener = resultsListener;
    }

    public void setProperties(Properties props) {
        this.config = props;
    }
}
