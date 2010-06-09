package org.reassembler.classfish;

import java.util.Properties;

import org.reassembler.jarfish.JarFish;


public class ScanFish {
    private static int verbosity = JarFish.NORMAL;
    
    public static void trace(Object msg, int level) {
        if (level >= verbosity) {
            System.out.println(msg);
        }
    }

    public static void traceLoud(Object msg) {
        trace(msg, JarFish.QUIET);
    }

    public static void main(String[] args) {
        Properties config;
        try {
            config = JarFish.parseArgs(args);
            
            if (config == null) {
                System.exit(1);
            }
        }
        catch (RuntimeException e) {
            e.printStackTrace();
            System.err.println(JarFish.USAGE);
            return;
        }
        
        String verbosityString = config.getProperty("verbosity", "normal");
        
        if ("normal".equals(verbosityString)) {
            verbosity = JarFish.NORMAL;
        }
        else if ("quiet".equals(verbosityString)) {
            verbosity = JarFish.QUIET;
        }
        else if ("loud".equals(verbosityString)) {
            verbosity = JarFish.LOUD;
        }
     
        
        String action = config.getProperty("action");
        traceLoud("action: " + action);
        
        Command cmd = new CommandFactory().getCommandByAction(action);
        
        
        if (cmd == null) {
            System.err.println(JarFish.USAGE);
            System.err.println("unknown action: '" + action + "'");
        }
        else {
            cmd.setProperties(config);
            cmd.start();
        }
    }


}
