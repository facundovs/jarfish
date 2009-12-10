package org.reassembler.jarfish;

public class Config {
    private boolean queryIsCaseInsensitive;
    private boolean loadExtendedMetaData;
    
    public boolean isQueryCaseInsensitive() {
        return queryIsCaseInsensitive;
    }
    
    public void setQueryCaseInsensitive(boolean queryIsCaseInsensitive) {
        this.queryIsCaseInsensitive = queryIsCaseInsensitive;
    }
    
    public boolean getLoadExtendedMetaData() {
        return loadExtendedMetaData;
    }
    
    public void setLoadExtendedMetaData(boolean loadExtendedMetaData) {
        this.loadExtendedMetaData = loadExtendedMetaData;
    }
}
