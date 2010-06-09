package org.reassembler.classfish;

import java.util.Properties;

public abstract class BaseCommand implements Command {
    protected Properties properties;

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

}
