import java.awt.*;

/**
 * TestBSDVMCrash
 */
public class TestBSDVMCrash {
    public static void main(String[] args) {
        System.err.println("TestBSDVMCrash.main");

        Toolkit toolkit = Toolkit.getDefaultToolkit(); // Crashes with SIGSEGV using -XXaltjvm=bsdserver

        System.err.println("toolkit: " + toolkit);
    }
}
