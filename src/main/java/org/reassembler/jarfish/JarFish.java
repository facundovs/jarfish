package org.reassembler.jarfish;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarFish {
    public static final int QUIET = 0;
    public static final int NORMAL = 4;
    public static final int LOUD = 8;

    static int verbosityThreshold = NORMAL;
        
    private static int pipeCounter;
    private static int pipeInterval = 100;
    private static Properties typesByExtension = new Properties();
    private static List textTypes = new ArrayList();
    
    private static String []archiveExtensions = {
            ".zip",
            ".jar",
            ".car",
            ".war",
            ".ear",
            ".sar",
            };
    
    static {
        typesByExtension.setProperty("java", "source");
        typesByExtension.setProperty("class", "class");
        typesByExtension.setProperty("txt", "text");
        typesByExtension.setProperty("xml", "text");
        typesByExtension.setProperty("htm", "text");
        typesByExtension.setProperty("html", "text");
        typesByExtension.setProperty("properties", "text");
        
        textTypes.add("source");
        textTypes.add("text");
    }
    

    public static final String USAGE = "use JarFish <action> [options] [<query>] [<path>]+\n"
            + "where\n"
            + "    <action> is one of the following:\n"
            + "        find - finds classes in jars\n"
            + "        list - lists all contents of jars in path\n"
            + "        findString - finds a string in a classes Constant Pool or text files found in jars\n"
            + "        findString2 - experimental same as above, but uses less memory\n"
            + "        find - finds classes in jars\n"
            + "        dupes - finds duplicates occurences of classes in jars\n"
            + "        mixed - finds duplicates occurences of different versions of classes in jars\n"
            + "        jarinfo - prints manifest and maven info on each jar found\n"
            + "and \n"
            + "    <query> is the sequence to be found\n"
            + "    <path> is the name of a directory, jar file, or <listFile> <path> defaults to '.'\n"
            + "    <listFile> is a text file whose name ends with '.lst' containing a list of paths to be indexed.\n"
            + "    <scannerTypeName> is a fully qualified class name, e.g. org.foo.MyScanner.\n"
            + "      the type does not have to extend any particular type, but it must have a no-arg constructor\n" 
            + "      and have a method with the following signature: public void found(Object obj)\n"
            + "    <filterName> class or archive name, e.g. Driver or activation.jar\n"
            + "    <jarName> archive name, e.g. Driver or activation.jar\n"
            + "\n"
            + "and options are\n"
            + "-z extension         add an archive file extension that should be searched for class files\n"
            + "-a extension type    add extension type mapping\n"
            + "-f <listFile>        the name of the file to use as a <listFile>, does not have to end in .lst\n"
            + "-i                   make queries case insensitive\n"
            + "-nor                 ignore subdirectories\n"
            + "-T                   load text from text files found in jars\n"
            + "-noT                 do not load text from text files found in jars\n"
            + "-t type              file type to find defaults to 'any'\n"
            + "-v1                  quiet - don't print much trace to stdout\n"
            + "-v2                  normal - print normal amounts of trace to stdout\n"
            + "-v3                  loud - print all trace to stdout\n"
            + "-x                   load extended information for jar file entries\n"
            + "-w                   ignore raw class files found on the file system\n"
            + "-c <scannerTypeName> scan using an instance of the named type\n"
            + "-nox                 do not load extended information for jar file entries (the default)\n"
            + "-n <filterName>      filter names in mixed and dupes actions. Only classes matching the supplied name will be examined\n"
            + "-jn <jarName>        filter jar names in jarinfo, mixed and dupes actions using this regex. Only archives matching the supplied name will be examined\n"
            + "\n";

    private static void setVerbosity(Map config) {
        Object vs = config.get("verbosity");
        if ("quiet".equals(vs)) {
            verbosityThreshold = JarFish.QUIET;
        }
        else if ("loud".equals(vs)) {
            verbosityThreshold = JarFish.LOUD;
        }
        else {
            verbosityThreshold = JarFish.NORMAL;
        }
    }    

    public static void main(String[] args) throws Exception {
        Properties config;
        try {
            config = parseArgs(args);
        }
        catch (RuntimeException e) {
            e.printStackTrace();
            System.err.println(USAGE);
            return;
        }

        setVerbosity(config);
        
        Object action = config.get("action");
        if (verbosityThreshold > JarFish.NORMAL) {
            System.out.println("action: " + action);
            System.out.println("type extensions: " + typesByExtension);
            System.out.println("archive extensions: " + archiveExtensions);
        }
        
        if (action.equals("find")) {
            find(config);
        } 
        else if (action.equals("findString")) {
            findString(config);
        } 
        else if (action.equals("findString2")) {
            findString2(config);
        } 
        else if (action.equals("dupes")) {
            dumpDupes(false, config);
        } 
        else if (action.equals("mixed")) {
            dumpDupes(true, config);
        } 
        else if (action.equals("list")) {
            list(config);
        } 
        else {
            System.err.println("unknown action: " + action);
            System.err.println(USAGE);
            return;
        }
    }


    private static void dumpDupes(boolean onlyShowDifferentVersions, Properties config) {
        String[] paths = config.getProperty("path", ".").split(",");
        JarIndexer jix = new JarIndexer();
        for (int i = 0; i < paths.length; i++) {
            String path = paths[i];
            if (verbosityThreshold > NORMAL) {
                System.out.println(path);
            }

            boolean recurse = config.getProperty("recurse", "true").equals("true");
            jix.index(new File(path), recurse);
        }        
        
        System.out.println();
        
        Config conf = new Config();
        List dupes = jix.findDuplicateClasses(conf);
        Iterator it = dupes.iterator();
        while (it.hasNext()) {
            List l = (List) it.next();
            pipe('*');
            
            boolean header = true;
            Map versions = getQuickClassVersion(l);
            boolean multipleVersions = versions.size() > 1;
            
            Iterator ite = l.iterator();
            while (ite.hasNext()) {
                EntryMeta em = (EntryMeta) ite.next();
                
                if (onlyShowDifferentVersions && !multipleVersions) {
                    continue;
                }
                
                if (header) {
                    System.out.print(em.getEntryName());
                    if (multipleVersions) {
                        System.out.print(" [" + versions.size() + "]");
                    }
                    
                    System.out.println();
                    
                    
                    header = false;
                }
                
                System.out.print("    " + em.getJarName());
                if (multipleVersions) {
                    System.out.print(" [" + 
                            versions.get(em.getExtendedMeta().getProperty("size"))
                            + "]");
                }
                
                System.out.println();
                
                
                if (verbosityThreshold > NORMAL) {
                    System.out.println("    " + em.getExtendedMeta());
                }
            }
        }
    }
    
    static void pipe(char ch) {
        pipeCounter++;
        if (pipeCounter % pipeInterval == 0) {
            System.out.print(ch);
        }
    }

    private static Map getQuickClassVersion(List entries) {
        Map m = new HashMap();
        Iterator ite = entries.iterator();
        while (ite.hasNext()) {
            EntryMeta em = (EntryMeta) ite.next();
            String size = em.getExtendedMeta().getProperty("size");
            
            Integer ver = (Integer) m.get(size);
            if (ver == null) {
                m.put(size, new Integer(m.size() + 1));
            }
        }
        
        return m;
    }
    
    static JarIndexer list(Properties config) {
        JarIndexer jix = new JarIndexer();
        String []paths = config.getProperty("path", ".").split(",");
        
        for (int i = 0; i < paths.length; i++) {
            String path = paths[i];
            if (verbosityThreshold > NORMAL) {
                System.out.println(path);
            }

            
            boolean recurse = config.getProperty("recurse", "true").equals("true");
            jix.index(new File(path), recurse);
        }

        jix.dump(System.out);
        
        return jix;
    }

    static EntryMeta[] findString(Properties config) {
        String query = config.getProperty("query");
        
        System.out.println("query: " + query);

        String[] paths = config.getProperty("path", ".").split(",");
        JarIndexer jix = new JarIndexer();
        for (int i = 0; i < paths.length; i++) {
            String path = paths[i];
            if (verbosityThreshold > NORMAL) {
                System.out.println(path);
            }

            boolean recurse = config.getProperty("recurse", "true").equals("true");
            jix.index(new File(path), recurse);
        }

        boolean caseInsensitive = new Boolean(config.getProperty("caseInsensitive", "false")).booleanValue();
        
        Config conf = new Config();
        conf.setQueryCaseInsensitive(caseInsensitive);
        conf.setLoadExtendedMetaData(true);
        
        System.out.println();
        
        return jix.findStringInJavaClass(query, conf);
    }
    
    

    static EntryMeta[] findString2(Properties config) {
        String query = config.getProperty("query");
        List matchingEntries = new ArrayList(); 
        
        System.out.println("query: " + query);
        boolean caseInsensitive = new Boolean(config.getProperty("caseInsensitive", "false")).booleanValue();
        
        Config conf = new Config();
        conf.setQueryCaseInsensitive(caseInsensitive);
        conf.setLoadExtendedMetaData(true);
        
        boolean recurse = config.getProperty("recurse", "true").equals("true");
        String[] paths = config.getProperty("path", ".").split(",");
        for (int i = 0; i < paths.length; i++) {
            String path = paths[i];
            
            File []jars = JarFish.getJarsFromPath(path, recurse);
            
            for (int j = 0; j < jars.length; j++) {
                File file = jars[j];
                
                if (verbosityThreshold > NORMAL) {
                    System.out.println(path);
                }
                
                JarIndexer jix = new JarIndexer();
                jix.index(file, recurse);
                
                matchingEntries.addAll(Arrays.asList(jix.findStringInJavaClass(query, conf)));
            }
        }
        
        return (EntryMeta[]) matchingEntries.toArray(new EntryMeta[matchingEntries.size()]);
    }

    private static void find(Properties config) {
        String query = config.getProperty("query");

        String[] paths = config.getProperty("path", ".").split(",");
        JarIndexer jix = new JarIndexer();
        for (int i = 0; i < paths.length; i++) {
            String path = paths[i];
            if (verbosityThreshold > NORMAL) {
                System.out.println(path);
            }

            boolean recurse = config.getProperty("recurse", "true").equals("true");
            jix.index(new File(path), recurse);
        }

        boolean caseInsensitive = new Boolean(config.getProperty("caseInsensitive", "false")).booleanValue();
        boolean loadExtended = new Boolean(config.getProperty("loadExtended", "false")).booleanValue();
        
        Config conf = new Config();
        conf.setQueryCaseInsensitive(caseInsensitive);
        conf.setLoadExtendedMetaData(loadExtended);
        
        System.out.println();
        
        EntryMeta[] matches = jix.find(query, config.getProperty("type", "class"), conf);
        
        for (int i = 0; i < matches.length; i++) {
            EntryMeta meta = matches[i];

            System.out.print(meta.getEntryName());
            if (verbosityThreshold > JarFish.QUIET) {
                Object xm = meta.getExtendedMeta();
                String ms = xm != null ? xm.toString() : ""; 
                
                System.out.print(" " + ms);
            }
            
            System.out.println(" (" + meta.getJarName() + ")");
        }
    }

    public static Properties parseArgs(String[] args) {
        if (args.length == 0) {
            System.err.println(USAGE);
            return null;
        }
        

        Properties config = new Properties();
        String query = null;
        String path = null;

        List largs = new ArrayList(Arrays.asList(args));
        Object action = largs.remove(0);
        config.put("action", action);
        boolean doneWithSwitches = false;
        Iterator it = largs.iterator();
        while (it.hasNext()) {
            String arg = (String) it.next();
            it.remove();

            if (arg.startsWith("-")) {
                if (doneWithSwitches) {
                    throw new IllegalArgumentException("invalid argument: " + arg);                    
                }
                else if (arg.equals("-a")) {
                    String ext = (String) it.next();
                    it.remove();
                    
                    String type = (String) it.next();
                    it.remove();
                    
                    typesByExtension.setProperty(ext, type);
                }
                else if (arg.equals("-z")) {
                    String ext = (String) it.next();
                    it.remove();
                    
                    List l = new ArrayList(Arrays.asList(archiveExtensions));
                    l.add(ext);
                    
                    archiveExtensions = (String[]) l.toArray(new String[l.size()]);
                }
                else if (arg.equals("-f")) {
                    path = (String) it.next();
                    config.put("listFile", path);
                    it.remove();
                }
                else if (arg.equals("-i")) {
                    config.put("caseInsensitive", "true");
                } 
                else if (arg.equals("-t")) {
                    config.setProperty("type", it.next().toString());
                    it.remove();
                } 
                else if (arg.equals("-v1")) {
                    config.setProperty("verbosity", "quiet");
                } 
                else if (arg.equals("-v2")) {
                    config.setProperty("verbosity", "normal");
                } 
                else if (arg.equals("-v3")) {
                    config.setProperty("verbosity", "loud");
                }
                else if (arg.equals("-nor")) {
                    config.setProperty("recurse", "false");
                }
                else if (arg.equals("-nox")) {
                    config.setProperty("loadExtended", "false");
                }
                else if (arg.equals("-w")) {
                    config.setProperty("scanRawClassFiles", "false");
                }
                else if (arg.equals("-x")) {
                    config.setProperty("loadExtended", "true");
                }
                else if (arg.equals("-n")) {
                    String type = (String) it.next();
                    it.remove();
                    
                    config.setProperty("classFilter", type);
                }
                else if (arg.equals("-jn")) {
                    String type = (String) it.next();
                    it.remove();
                    
                    config.setProperty("jarFilter", type);
                }
                else {
                    throw new IllegalArgumentException("unknown option: " + arg);
                }
            } 
            else {
                if (!doneWithSwitches) {
                    doneWithSwitches = true;
                }
                
                // bare word. must be query or path
                // action has been set
                if (action.equals("find") && query == null) {
                    query = arg;
                } 
                else if (action.equals("findString") && query == null) {
                    query = arg;
                    config.put("loadExtended", "true");
                } 
                else if (action.equals("findString2") && query == null) {
                    query = arg;
                    config.put("loadExtended", "true");
                } 
                else {
                    if (path == null) {
                        path = arg;
                    } 
                    else {
                        path += "," + arg;
                    }
                }
            }
        }

        if (largs.size() > 0) {
            throw new IllegalArgumentException("invalid arguments: " + largs);
        }
        
        if (path != null) {
            config.put("path", path);
        }

        
        if (action.equals("find") 
                || action.equals("findString2")
                || action.equals("findString")) {
            if (query == null) {
                throw new IllegalArgumentException(
                        "<query> is required for find action");
            }

            config.put("query", query);
        }
        
        return config;
    }

    public static boolean typeisText(String type) {
        return textTypes.contains(type);
    }

    public static String getTypeByExtension(String extension) {
        if (extension == null) {
            return "unknown";
        }
        else {
            return typesByExtension.getProperty(extension, "unknown");
        }
    }

    public static File[] getJarsFromPath(String string, boolean recurse) {
        return getJarsFromPath(new File(string), recurse);
    }
    
    private static File[] jarsFromPath(File path, final boolean recurse) {
        final List fs = new ArrayList();
        
        if (path.isDirectory()) {
            if (!path.canRead()) {
                System.err.println("can not read path: " + path);
            }

            File []files = path.listFiles(new FileFilter() {
                public boolean accept(File pathname) {
                    if (recurse && pathname.isDirectory() && pathname.canRead()) {
                        return true;
                    }
                    else if (isArchiveType(pathname)) {
                        return true;
                    }
                    
                    return false;
                }
            });
            
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                
                if (file.isDirectory()) {
                    fs.addAll(Arrays.asList(jarsFromPath(file, recurse)));
                }
                else {
                    fs.add(file);
                }
            }
        }
        else {
            if (isArchiveType(path)) {
                fs.add(path);
            }
        }
        
        return (File[]) fs.toArray(new File[fs.size()]);
    }
    
    public static File[] getJarsFromPath(File file, boolean recurse) {
        List files = new ArrayList();
        
        if (file.getName().endsWith(".lst")) {
            String ln = null;
            BufferedReader in;
            try {
                in = new BufferedReader(new FileReader(file));

                while ((ln = in.readLine()) != null) {
                    String line = ln.trim();
                    if (line.startsWith("#") || line.length() == 0) {
                        continue;
                    }

                    File lfile = new File(line);

                    files.addAll(Arrays.asList(jarsFromPath(lfile, recurse)));
                }

                in.close();
            } 
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            files.addAll(Arrays.asList(jarsFromPath(file, recurse)));
        }         
        
        return (File[]) files.toArray(new File[files.size()]);
    }

    public static boolean isArchiveType(File file) {
        String fs = file.toString();
        for (int i = 0; i < archiveExtensions.length; i++) {
            String ext = archiveExtensions[i];
            
            if (fs.endsWith(ext)) {
                return true;
            }
        }
        
        return false;
    }

    public static String getEntryContentAsString(EntryMeta em) {
        try {
            JarFile jar = new JarFile(em.getJarName());
            JarEntry je = jar.getJarEntry(em.getEntryName());
        
            InputStream in = jar.getInputStream(je);
        
            ByteArrayOutputStream out = new ByteArrayOutputStream((int) je.getSize());
            byte []buffer = new byte[4096];
         
            int bytesRead = -1;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        
            in.close();
            
            return out.toString();
        } 
        catch (IOException e) {
            System.err.print(e);
        }
        
        return null;
    }
}
