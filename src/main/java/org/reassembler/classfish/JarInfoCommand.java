package org.reassembler.classfish;


public class JarInfoCommand extends BaseCommand {
    public void start() {
        JarFinder jf = new JarFinder(getProperties());
        jf.setListener(new JarInfoFish(getProperties()));
        jf.start();
    }
}
