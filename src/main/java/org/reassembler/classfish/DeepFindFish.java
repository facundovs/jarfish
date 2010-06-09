package org.reassembler.classfish;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Properties;

import org.reassembler.jarfish.ClassMeta;
import org.reassembler.jarfish.ClassParser;

public class DeepFindFish extends FindFish {
    private ClassParser parser = new ClassParser();
    private ClassMeta meta = new ClassMeta();
    
    public DeepFindFish(Properties config) {
        super(config);
    }
    
    public void foundFile(FindFile file) {
        boolean hit = false;
        
        hit = file.getName().indexOf(this.query) != -1;
        
        if (hit) {
            emitArchiveName();
            emitFile(file);
        }
    }
    
    public void foundClass(FindFile file) {
        boolean hit = false;
        
        String []parts = file.getName().split("/"); 
        String name = parts[parts.length - 1];
        
        if (this.searchClassPackage) {
            try {
                DataInputStream din = new DataInputStream(file.getStream());
                this.meta.reset();
                this.parser.parse(this.meta, din);
                
                hit = this.meta.getTypeName().equals(this.query);
            }
            catch (IOException e) {
                e.printStackTrace();
            }            
        }
        else {
            if (this.caseInsensitive) {
                hit = name.toLowerCase().indexOf(this.normalizedQuery) != -1;
            }
            else {
                hit = name.indexOf(this.query) != -1;
            }
        }
        
        if (hit) {
            emitArchiveName();
            emitClass(file);
        }
    }

    public void _foundClass(FindFile file) {
        try {
            DataInputStream din = new DataInputStream(file.getStream());
            this.meta.reset();
            this.parser.parse(this.meta, din);
            
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
