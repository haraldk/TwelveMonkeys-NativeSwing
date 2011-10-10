import com.apple.eawt.*;

import javax.swing.*;
import java.awt.*;

/**
 * TestNewAppDelegates
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: TestNewAppDelegates.java,v 1.0 Aug 17, 2010 10:38:30 AM haraldk Exp$
 */
public class TestNewAppDelegates {
    public static void main(String[] args) {
        System.setProperty("apple.laf.useScreenMenuBar", "true");

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                final JFrame frame = new JFrame("The frame...");

                ////////////////////////////////////////////////////////////////////////////////////////////////////////
                // BRAND NEW STUFF: Requires latest developer preview
                // Where are the docs?
                ////////////////////////////////////////////////////////////////////////////////////////////////////////

                Application app = Application.getApplication();

//                // TODO: FATAL! Does not work any more: Existing QUIT and Preferences etc handlers does not work..
                app.addApplicationListener(new ApplicationAdapter() {
                    // Never invoked, breaks existing apps...
                    public void handleQuit(ApplicationEvent applicationEvent) {
                        System.err.println("TestNewAppDelegates.handleQuit");
                    }

//                    public void handlePreferences(ApplicationEvent applicationEvent) {
//                        System.err.println("TestNewAppDelegates.handlePreferences");
//                    }
//
//                    public void handleAbout(ApplicationEvent applicationEvent) {
//                        System.err.println("TestNewAppDelegates.handleAbout");
//                    }
//
//                    public void handleOpenApplication(ApplicationEvent applicationEvent) {
//                        System.err.println("TestNewAppDelegates.handleOpenApplication");
//                    }
//
//                    public void handleOpenFile(ApplicationEvent applicationEvent) {
//                        System.err.println("TestNewAppDelegates.handleOpenFile");
//                    }

                    public void handlePrintFile(ApplicationEvent applicationEvent) {
                        System.err.println("TestNewAppDelegates.handlePrintFile");
                    }

//                    public void handleReOpenApplication(ApplicationEvent applicationEvent) {
//                        System.err.println("TestNewAppDelegates.handleReOpenApplication");
//                    }
                });
//                app.addPreferencesMenuItem();

                // TODO: Why not just use the more familiar Actions? This is what developers typically use for JMenuItems
//                app.setPreferencesHandler(new AbstractAction() {
//                    public void actionPerformed(ActionEvent e) {
//                        // ...
//                    }
//                });
//                app.setPreferencesHandler(new PreferencesHandler() {
//                    public void handlePreferences(AppEvent.PreferencesEvent preferencesEvent) {
//                        System.err.println("TestNewAppDelegates.handlePreferences");
//                        System.err.println("preferencesEvent: " + preferencesEvent);
//                    }
//                });
//
//                app.setPreferencesHandler(new PreferencesHandler() {
//                    public void handlePreferences(AppEvent.PreferencesEvent preferencesEvent) {
//                        throw new UnsupportedOperationException("Method handlePreferences not implemented"); // TODO: Implement
//                    }
//                });
//
//                app.setPreferencesHandler(null);
//
//                // TODO: Where's the replacement? Still need these to sync with Actions enabled state
//                app.setEnabledPreferencesMenu(true);

                // Actually, I would prefer getting a proxy of the entire App Menu available in Java, to allow adding/removing menu items etc
                // Have a PoC of this

//                app.setQuitHandler(new QuitHandler() {
//                    public void handleQuitRequestWith(AppEvent.QuitEvent quitEvent, QuitResponse quitResponse) {
//                        throw new UnsupportedOperationException("Method handleQuitRequestWith not implemented"); // TODO: Implement
//                    }
//                });

                /**
                // Should throw IllegalState if an ApplicationListener exists?
                app.setQuitHandler(new QuitHandler() {
                    public void handleQuitRequestWith(AppEvent.QuitEvent quitEvent, QuitResponse quitResponse) {
                        System.err.println("quitEvent: " + quitEvent);
                        System.err.println("quitResponse: " + quitResponse);
                        if (JOptionPane.showConfirmDialog(frame, "Quit", "Are you sure you want to quit?", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                            quitResponse.cancelQuit();
                        }
                        // TODO: Consider letting Apple know this is stupid (read: unnecessary boilerplate)...
                        // The quitResponse could just quit the app, if I don't invoke cancelQuit.
                        // If I really want to handle the quit myself (custom strategy), I could call cancelQuit and still go on...
                        else {
                            quitResponse.performQuit();
                        }
                        // Right now there's a third option, and that is no-op, which in effect disables the quit action...

                        // How about just boolean allowQuitRequest(QuitEvent qe)?
                    }
                });
                //*/
//                app.setQuitStrategy(QuitStrategy.SYSTEM_EXIT_0);
//                app.setQuitStrategy(QuitStrategy.CLOSE_ALL_WINDOWS);

                // Throws IllegalStateException
//                app.addApplicationListener(new ApplicationAdapter() {
//                });


//                app.addAppEventListener();
//                app.removeAppEventListener();
                // TODO: app.getAppEventListeners() for completeness?

//                app.requestUserAttention(false);

                ////////////////////////////////////////////////////////////////////////////////////////////////////////

                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setPreferredSize(new Dimension(300, 200));
                frame.add(new JLabel("Testing, testing, 1-2-3", JLabel.CENTER));
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);

            }
        });
    }
}
