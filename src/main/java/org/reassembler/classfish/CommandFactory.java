package org.reassembler.classfish;

import java.util.HashMap;
import java.util.Map;

public class CommandFactory {
    private Map commands = new HashMap();
    
    public CommandFactory() {
        this.commands.put("find", FindCommand.class);
        this.commands.put("jarinfo", JarInfoCommand.class);
        this.commands.put("dupes", DupeCommand.class);
        this.commands.put("mixed", MixedArchivesCommand.class);
        this.commands.put("mixedarchives", MixedPackageCommand.class);
        this.commands.put("list", ListCommand.class);
    }
    
    public Command getCommandByAction(String action) {
        try {
            return doGetCommand(action);
        }
        catch (Exception e) {
            throw new IllegalStateException("could not create command for action: " + action, e);
        }
    }
    
    private Command doGetCommand(String action) throws Exception {
        Class _class = (Class) this.commands.get(action);
        
        if (_class == null) {
            throw new IllegalArgumentException("no command class found for action: " + action);
        }
        
        Object o = _class.newInstance();
        
        Command cmd = (Command) o;
        
        return cmd;
    }
}
