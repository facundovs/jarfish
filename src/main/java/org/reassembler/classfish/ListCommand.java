package org.reassembler.classfish;

public class ListCommand extends BaseCommand {
    public void start() {
        JarFinder jf = new JarFinder(this.properties);
        jf.setListener(new ListFish(this.properties));
        
        jf.start();
    }
}
