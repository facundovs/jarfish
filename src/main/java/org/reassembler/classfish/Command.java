package org.reassembler.classfish;

import java.util.Properties;

public interface Command {
    void setProperties(Properties props);
    void start();
}
