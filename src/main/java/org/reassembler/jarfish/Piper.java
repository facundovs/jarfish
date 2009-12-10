package org.reassembler.jarfish;

public class Piper {
    private int interval;
    private long counter;
    private char ch;
    
    Piper() {
        this(100, '_');
    }
    
    Piper(int interval, char ch) {
        this.interval = interval;
        this.ch = ch;
    }
    
    public void pipe() {
        pipe(this.ch);
    }
    
    public void pipe(char ch) {
        this.counter++;
        
        if (this.counter % this.interval == 0) {
            System.out.print(ch);
        }
    }
}
