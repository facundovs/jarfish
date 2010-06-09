package org.reassembler.classfish;

import java.io.IOException;
import java.io.InputStream;

public interface FoundFile {
    InputStream getStream() throws IOException;
    String getName();
    boolean isArchive();
}
