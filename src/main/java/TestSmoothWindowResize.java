import com.twelvemonkeys.spice.osx.appkit.NSWindow;
import com.twelvemonkeys.spice.osx.foundation.NSRect;
import org.rococoa.cocoa.foundation.NSSize;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * TestSmoothWindowResize
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: TestSmoothWindowResize.java,v 1.0 Mar 5, 2010 3:34:00 PM haraldk Exp$
 */
public class TestSmoothWindowResize {
    private final static Executor dispatcher = Executors.newSingleThreadExecutor();

    public static void main(final String[] pArgs) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame("Smooth resize");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                JTabbedPane tabs = new JTabbedPane() {
                    @Override
                    public Dimension getPreferredSize() {
                        Dimension size = super.getPreferredSize();
                        Component comp = getComponent(getSelectedIndex());
                        size.height = 100 + comp.getPreferredSize().height; // Lazy, should know the height of tabs
                        return size;
                    }
                };
                tabs.setFocusable(false);
                tabs.add("One", new JLabel("One") {
                    @Override
                    public Dimension getPreferredSize() {
                        Dimension size = super.getPreferredSize();
                        size.width = Math.max(size.width, 200);
                        size.height = Math.max(size.height, 100);
                        return size;
                    }
                });
                tabs.add("Two", new JLabel("Two") {
                    @Override
                    public Dimension getPreferredSize() {
                        Dimension size = super.getPreferredSize();
                        size.width = Math.max(size.width, 200);
                        size.height = Math.max(size.height, 300);
                        return size;
                    }
                });
                tabs.addChangeListener(new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        Window window = SwingUtilities.getWindowAncestor((Component) e.getSource());
                        Dimension pref = window.getPreferredSize();
                        Dimension size = window.getSize();
                        size.height = pref.height;

                        // TODO: Figure out how to avoid weird bottom-left origin...
                        final NSWindow nswin = NSWindow.CLASS.windowFromAWT(window);
                        NSRect rect = nswin.frame();
                        final NSRect newRect = new NSRect(rect.origin.x.intValue(), rect.origin.y.intValue() - (size.height - rect.size.height.intValue()), rect.size.width.intValue(), size.height);

                        dispatcher.execute(new Runnable() {
                            public void run() {
                                nswin.setFrame_display_animate(newRect, true, true);
                            }
                        });
                    }
                });

                frame.add(tabs);

                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
}
