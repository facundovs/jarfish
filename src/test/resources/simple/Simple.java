/** must compile down to java 1.5 to get unit tests to pass */
public class Simple implements Runnable {
    private String name;
    
    public void run() {
        System.out.print("Hello World");
    }    

    public int stop(Thread t, String []s) {
        return 0;
    }
}    
