package org.reassembler.classfish;


public class FindCommand extends BaseCommand {

    public void start() {
        JarFinder jf = new JarFinder(getProperties());
        jf.setListener(new FindFish(getProperties()));
        jf.start();
    }
}
