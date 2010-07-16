package org.reassembler.classfish;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.reassembler.jarfish.ClassMeta;
import org.reassembler.jarfish.ClassParser;
import org.reassembler.jarfish.JarFish;

public class JarFinder implements FindListener {
    private ArchiveScanListener listener;
    private boolean recursive = true;
    
    private Pattern jarFilter;
    
    private List archiveExtensions = 
            Arrays.asList(new String[]{".jar", ".car", ".ear", ".sar", ".war", ".zip"});
    private Properties config;
    private boolean scanRawClassFiles = true;
    
    private boolean loadExtended = false;
    
    // for unit tests
    JarFinder() {
    }
    
    // for unit tests
    void find(File path) {
        this.config = new Properties();
        this.config.setProperty("path", path.getAbsolutePath());
        
        start();
    }
    
    public JarFinder(Properties config) {
        this.config = config;
        String filterPattern = config.getProperty("jarFilter");
        
        if (filterPattern != null) {
            this.jarFilter = Pattern.compile(filterPattern);
        }
        
        setRecursive(new Boolean(config.getProperty("recurse", "true")).booleanValue());

        this.scanRawClassFiles = new Boolean(config.getProperty("scanRawClassFiles", "true")).booleanValue();
        
        this.loadExtended = new Boolean(config.getProperty("loadExtended", "false")).booleanValue();
    }
    
    public void start() {
        Finder f = new Finder();
        f.setListener(this);
        
        if (config.getProperty("listFile") != null) {
            f.findFromList(new File(config.getProperty("listFile")));
        }
        else if (config.getProperty("path") != null) {
            f.find(new File(config.getProperty("path")));
        }
        else if (config.getProperty("classPath") != null) {
            String []paths = config.getProperty("classPath").split(File.pathSeparator);
            
            for (int i = 0; i < paths.length; i++) {
                File path = new File(paths[i]);
                f.find(path);
            }
        }
    }
    
    public boolean isRecursive() {
        return recursive;
    }
    
    public void setRecursive(boolean recursive) {
        this.recursive = recursive;
    }

    public boolean isArchiveType(String name) {
        int pos = name.lastIndexOf(".");
        
        return pos == -1 ? false : this.archiveExtensions.contains(name.substring(pos));
    }
  
    public void found(File file) {
        if (file.isDirectory()) {
            ScanFish.traceLoud("/");
            return;
        }
        
        String fileName = file.getName();
        
        boolean isArchive = isArchiveType(fileName);
        
        if (isArchive) {
            ScanFish.traceLoud(".");
            
            if (matchesFilter(fileName)) {
                this.listener.foundArchive(new FindFile(file));
            }
            
            try {
                findInJar(new Jar(new JarFile(file)));
            }
            catch (IOException e) {
                ScanFish.trace("error processing file: " + file + ", " + e, JarFish.LOUD);
            }
        }
        else if (file.getName().endsWith(".class") && this.scanRawClassFiles) {
            ScanFish.traceLoud("+");
            FindFile ff = new FindFile(file);
            ff.setSize(file.length());
            ff.getMeta().put("source", file.getAbsolutePath());
            
            if (this.loadExtended) {
                ClassMeta cm = new ClassMeta();
                ClassParser cp = new ClassParser();
                try {
                    cp.parse(cm, new DataInputStream(new FileInputStream(file)));
                    ff.getMeta().put("classMeta", cm);
                }
                catch (Exception e) {
                    ScanFish.traceLoud("error parsing class: " + e);
                }
            }
            
            this.listener.foundClass(ff);
        }
        else {
            ScanFish.traceLoud("-");
        }
    }
    
   

    private void findInJar(Jar jar) {
        try {
            ScanFish.traceLoud(jar.getJarFile().getName());
            JarFile jarFile = jar.getJarFile();
            Enumeration en = jarFile.entries();
            while (en.hasMoreElements()) {
                JarEntry entry = (JarEntry) en.nextElement();
                
                Jar parent = jar.getParent();

                FindArchiveFile faf;
                if (parent != null) {
                    faf = new FindArchiveFile(jar.getParent().getJarFile(), jar, entry);
                }
                else {
                    faf = new FindArchiveFile(jar, entry);
                }
                
                faf.getMeta().put("source", jar.getLineage());
                
                if (this.loadExtended && faf.getName().endsWith(".class")) {
                    ClassMeta cm = new ClassMeta();
                    ClassParser cp = new ClassParser();
                    
                    try {
                        cp.parse(cm, new DataInputStream(faf.getStream()));
                        faf.getMeta().put("classMeta", cm);
                    }
                    catch (Exception e) {
                        System.err.println("error parsing class: " + faf.getName());
                    }
                }
                
                
                String shortName = getEntryShortName(entry.getName());
                
                if (shortName.endsWith(".class")) {
                    this.listener.foundClass(faf);
                }
                else if (isArchiveType(shortName)) {
                    if (matchesFilter(shortName)) {
                        this.listener.foundArchive(faf);
                    }
                    
                    JarFile jf = streamToFile(faf, entry.getName());
                    
                    Jar jarjar = new Jar(jar, jf);
                    jarjar.setName(entry.getName());
                    
                    findInJar(jarjar);
                    
                    jf.close();
                }
                else {
                    this.listener.foundFile(faf);
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    static String getEntryShortName(String name) {
        String []parts = name.split("\\/");
        return parts[parts.length - 1];
    }

    private boolean matchesFilter(String name) {
        if (this.jarFilter == null) {
            return true;
        }
        else {
            Matcher m = this.jarFilter.matcher(name);
            
            return m.matches();
        }
    }

    private JarFile streamToFile(FindArchiveFile faf, String entryName) throws IOException {
        byte []buffer = new byte[4096];
        int bytesRead = -1;
        InputStream in = faf.getStream();
        
        char []safe = entryName.toCharArray();
        for (int i = 0; i < safe.length; i++) {
            char ch = safe[i];
            if (!Character.isLetterOrDigit((int) ch)) {
                safe[i] = '_';
            }
        }
        
        File tempJar = File.createTempFile("jarfish-" + new String(safe), "-tempfile-delete-me");
        
        FileOutputStream out = new FileOutputStream(tempJar);
        
        while ((bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }
        
        out.close();
        
        return new JarFile(tempJar, false, JarFile.OPEN_DELETE | JarFile.OPEN_READ);
    }

    public ArchiveScanListener getListener() {
        return listener;
    }

    public void setListener(ArchiveScanListener listener) {
        this.listener = listener;
    }

}
