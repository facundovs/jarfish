package org.reassembler.jarfish;

import java.io.PrintStream;
import java.util.List;

public interface JarIndex {
    public int getJarCount();
    public void addJar(JarMeta jm);
    public EntryMeta[] find(String name, String type, Config conf);
    public void dump(PrintStream out);
    public EntryMeta[] findString(String query, Config conf);
    public List getJars();
}
