import com.apple.eawt.Application;
import com.apple.eawt.ApplicationAdapter;
import com.apple.eawt.ApplicationEvent;

import javax.swing.*;
import java.awt.*;

public class TestHandleQuit {
    public static void main(String[] args) {
        System.setProperty("apple.laf.useScreenMenuBar", "true");

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame(TestHandleQuit.class.getSimpleName());

                Application application = Application.getApplication();

//                application.addApplicationListener(new ApplicationAdapter());

                application.addApplicationListener(new ApplicationAdapter() {
                    @Override
                    public void handleQuit(ApplicationEvent event) {
                        System.err.println("First TestHandleQuit.handleQuit");
                    }
                });

                application.addApplicationListener(new ApplicationAdapter() {
                    @Override
                    public void handleQuit(ApplicationEvent event) {
                        System.err.println("Second TestHandleQuit.handleQuit");
//                        event.setHandled(true); // Change to true, and see that the next handlers are now invoked
                    }
                });

                application.addApplicationListener(new ApplicationAdapter() {
                    @Override
                    public void handleQuit(ApplicationEvent event) {
                        System.err.println("Second TestHandleQuit.handleQuit");
//                        event.setHandled(false);
                    }
                });

                application.addApplicationListener(new ApplicationAdapter() {
                    @Override
                    public void handleQuit(ApplicationEvent event) {
                        System.err.println("Third TestHandleQuit.handleQuit");
                        event.setHandled(true);
                    }
                });

                frame.add(new JLabel("Testing, testing, 1-2-3", JLabel.CENTER) {
                    @Override
                    public Dimension getPreferredSize() {
                        return new Dimension(400, 300);
                    }
                });

                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
            }
        });
    }
}
