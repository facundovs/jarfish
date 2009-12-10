package de.dixti.jarscan.core;

import de.dixti.jarscan.FileStream;
import de.dixti.jarscan.Jar;
import de.dixti.jarscan.Result;
import de.dixti.jarscan.Scanner;
import java.io.*;
import java.util.regex.Pattern;

/**
 * This class is used to scan simple files.
 * @author Lars Fiedler
 */
public class FileScanner implements Scanner {
    private String searchString;
    //private final Pattern pattern;


    public FileScanner(String searchString) {
        this.searchString = searchString;
        //pattern = Pattern.compile(".*" + searchString + ".*");
    }
    
    public void scanJar(Jar jar, Result jarResult) throws IOException {
        // do nothing
    }

    public void scanFile(FileStream fileStream, Result parentResult) throws IOException {
        String line = scan(searchString, fileStream.getInputStream());
        if(line != null) {
            Result result = new Result(parentResult, fileStream.getPath(), false);
            result.setMessage(line);
        }
    }

    /**
     * Scans the file and returns the line with the first occurence of searchString.
     * Note that the returned line is cut if it is longer than 100 + searchString.length().
     * @param searchString the String to search for
     * @return the first line that contains searchString
     * @throws java.io.IOException
     */
    public String scan(String searchString, InputStream in) throws IOException {
        LineNumberReader reader = new LineNumberReader(new InputStreamReader(in));
        String line;
        while((line = reader.readLine()) != null) {
            /*if(pattern.matcher(line).matches()) {
                return formatLine(line, searchString);
            }*/
            if(line.indexOf(searchString) != -1) {
                return formatLine(line, searchString);
            }
        }
        return null;
    }
    
    /**
     * Shortens the line if it is greater than 100 + searchString.length().
     * <p>
     * 0--------index----searchStringEnd----------->
     * @param line
     * @param searchString
     * @return shorter line
     */
    private String formatLine(String line, String searchString) {
        int length = searchString.length();
        if(line.length() > 100 + length) {
            int index = line.indexOf(searchString);
            int searchStringEnd = index + length;
            if(index < 100) {
                line = line.substring(0, searchStringEnd);
            }else {  // index >= 100
                line = line.substring(searchStringEnd - 100, searchStringEnd);
            }
        }
        return line;
    }
   
}
