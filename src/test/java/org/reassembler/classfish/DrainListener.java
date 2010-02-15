package org.reassembler.classfish;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.reassembler.classfish.FindFile;

public class DrainListener extends ArchiveCountListener {
    public void foundClass(FindFile file) {
        super.foundClass(file);
        
        byte []buffer = new byte[4096];
        int bytesRead = -1;
        
        try {
            InputStream in = file.getStream();
        
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            
            System.out.println(file + ", length: " + out.toByteArray().length);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
