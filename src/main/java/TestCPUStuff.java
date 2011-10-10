/**
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 */
public class TestCPUStuff {
    public static void main(String[] args) {
        Runtime runtime = Runtime.getRuntime();
        System.err.println("runtime.availableProcessors(): " + runtime.availableProcessors());
        System.err.println("runtime.maxMemory(): " + runtime.maxMemory());
        System.err.println("runtime.freeMemory(): " + runtime.freeMemory());
        System.err.println("runtime.toString(): " + runtime.toString());
    }
}
