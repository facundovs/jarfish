package org.reassembler.classfish;

import java.util.Properties;

public class FindFish extends BaseFish {
    protected String query;
    protected String normalizedQuery;
    protected boolean caseInsensitive;
    protected boolean searchClassPackage;
    private String packageName;
    private String className;
    protected boolean searchForPackageOnly;
    
    public FindFish(Properties config) {
        super(config);
        
        setProperties(config);
    }
    
    public void setProperties(Properties props) {
        super.setProperties(props);
        
        this.query = config.getProperty("query");
        
        if (this.query == null) {
            throw new IllegalArgumentException("query can not be empty");
        }
        this.query = this.query.replaceAll("\\/", ".");
        
        ScanFish.traceLoud("QUERY: " + this.query);
        
        this.normalizedQuery = this.query.toLowerCase();
        
        String []qparts = this.query.split("\\.");
        
        this.searchClassPackage = qparts.length > 1;
        this.caseInsensitive = config.getProperty("caseInsensitive", "false").equals("true");
        
        if (this.searchClassPackage) {
            this.packageName = null;
            for (int i = 0; i < qparts.length; i++) {
                String name = qparts[i];
                char first = name.charAt(0);
                
                // if first character is upper case, 
                // then by convention, we have reached
                // the class name portion of the name
                if (Character.isUpperCase(first)) {
                    this.className = name;
                    break;
                }
                else {
                    if (this.packageName == null) {
                        this.packageName = name;
                    }
                    else {
                        this.packageName += "." + name;
                    }
                }
            }
            
            if (this.className == null) {
                this.searchClassPackage = false;
                this.searchForPackageOnly = true;
            }
        }
        else {
            this.className = this.query;
        }
    }
        
    
    public void foundFile(FindFile file) {
        boolean hit = false;
        
        hit = file.getName().indexOf(this.query) != -1;
        
        if (hit) {
            emitArchiveName();
            emitFile(file);
        }
    }
    
    public void foundClass(FindFile file) {
        boolean hit = false;
        
        String []parts = file.getName().split("/"); 
        String name = parts[parts.length - 1];
        
        
        if (this.searchClassPackage) {
            String className = getLongNameFromFileName(file.getName());
            
            if (this.query.equals(className)) {
                hit = true;
            }
        }
        else if (this.searchForPackageOnly) {
            String packageName = getPackageNameFromFileName(file.getName());
            
            if (this.query.equals(packageName)) {
                hit = true;
            }
        }
        else {
            if (this.caseInsensitive) {
                hit = name.toLowerCase().indexOf(this.normalizedQuery) != -1;
            }
            else {
                hit = name.indexOf(this.query) != -1;
            }
        }
        
        if (hit) {
            emitArchiveName();
            emitClass(file);
        }
    }

    public boolean isSearchClassPackage() {
        return searchClassPackage;
    }

    public void setSearchClassPackage(boolean searchClassPackage) {
        this.searchClassPackage = searchClassPackage;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public boolean isSearchForPackageOnly() {
        return searchForPackageOnly;
    }

    public void setSearchForPackageOnly(boolean searchForPackageOnly) {
        this.searchForPackageOnly = searchForPackageOnly;
    }

    public static String getShortNameFromFileName(String name) {
        String []parts = name.split("\\/");
        
        return parts[parts.length -1].split("\\.")[0];
    }

    public static String getLongNameFromFileName(String name) {
        String packageName = getPackageNameFromFileName(name);
        String shortName = getShortNameFromFileName(name);
        if (packageName == null) {
            return shortName;
        }
        else {
            return packageName + "." + shortName;
        }
    }

    public static String getPackageNameFromFileName(String name) {
        String []parts = name.split("\\/");
        
        if (parts.length == 1) {
            return null;
        }
        
        String packageName = null;
        
        for (int i = 0; i < parts.length - 1; i++) {
            String part = parts[i];
            if (packageName == null) {
                packageName = part;
            }
            else {
                packageName += "." + part;
            }
        }

        return packageName;
    }


}
