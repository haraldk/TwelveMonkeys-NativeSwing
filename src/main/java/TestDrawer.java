import com.twelvemonkeys.spice.osx.appkit.NSDrawer;
import com.twelvemonkeys.spice.osx.appkit.NSNotification;
import com.twelvemonkeys.spice.osx.appkit.NSRectEdge;
import com.twelvemonkeys.spice.osx.appkit.NSWindow;
import org.rococoa.NSObject;
import org.rococoa.Rococoa;
import org.rococoa.cocoa.foundation.NSAutoreleasePool;
import org.rococoa.cocoa.foundation.NSSize;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * TestDrawer
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: TestDrawer.java,v 1.0 Mar 21, 2008 8:23:14 PM haraldk Exp$
 */
public class TestDrawer {

    public static void main(String[] pArgs) {
        System.setProperty("apple.laf.useScreenMenuBar", "true");

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                NSAutoreleasePool pool = NSAutoreleasePool.new_();
                
                JFrame frame = new JFrame("JFrame with NSDrawer");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                JLabel label = new JLabel("Hello drawer", JLabel.CENTER);
                label.setVerticalAlignment(JLabel.CENTER);
                label.setPreferredSize(new Dimension(300, 200));
                frame.add(label);
                frame.pack();
                frame.setLocationRelativeTo(null);

                NSWindow window = NSWindow.CLASS.windowFromAWT(frame);


                final JWindow drawerContent = new JWindow(frame);
//                drawerContent.setPreferredSize(new Dimension(100, 100));
                drawerContent.setLocationRelativeTo(null);
                JPanel panel = new JPanel(new BorderLayout(4, 4));
                panel.setBorder(BorderFactory.createEmptyBorder(4, 6, 8, 8));
                panel.add(new Label("Foo bar"), BorderLayout.NORTH);
                List list = new List(0, true);
                list.add("one");
                list.add("two");
                list.add("three");
                list.add("four");
                panel.add(list);

                drawerContent.getContentPane().add(panel);
                drawerContent.pack();
//                drawerContent.doLayout();
                drawerContent.addNotify();
//                drawerContent.setVisible(true);


                NSSize contentSize = new NSSize(100, 100);
                final NSDrawer drawer = NSDrawer.create(contentSize, NSRectEdge.NSMaxXEdge);
                drawer.setMinContentSize(contentSize);
//                drawer.setMaxContentSize(contentSize);
                DrawerDelegate drawerDelegate = new DrawerDelegate() {
                    @Override
                    public NSSize drawerWillResizeContents_toSize(final NSDrawer sender, final NSSize contentSize) {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                drawerContent.setSize(new Dimension(contentSize.width.intValue(), contentSize.height.intValue()));
                                drawerContent.doLayout();
                            }
                        });
                        return super.drawerWillResizeContents_toSize(sender, contentSize);
                    }

                    @Override
                    public void drawerWillOpen(NSNotification notification) {
                        final NSDrawer drawer = Rococoa.wrap(notification.object(), NSDrawer.class);
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                NSSize contentSize = drawer.contentSize();
                                drawerContent.setSize(new Dimension(contentSize.width.intValue(), contentSize.height.intValue()));
                                drawerContent.doLayout();
                            }
                        });
                        super.drawerDidOpen(notification);
                    }
                };
                frame.addComponentListener(new ComponentAdapter() {
                    @Override
                    public void componentResized(ComponentEvent e) {
                        NSSize contentSize = drawer.contentSize();
                        drawerContent.setSize(new Dimension(contentSize.width.intValue(), contentSize.height.intValue()));
                        drawerContent.doLayout();
                    }
                });


                NSObject dDproxy = Rococoa.proxy(drawerDelegate);
                frame.getRootPane().putClientProperty(getClass().getName() + ".drawerDelegate", dDproxy); // Keep the delegate from being CG'ed
                drawer.setDelegate(dDproxy.id());
                drawer.setParentWindow(window);


                NSWindow dw = NSWindow.CLASS.windowFromAWT(drawerContent);
                drawer.setContentView(dw.contentView());

                label.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        int state = drawer.state();
                        if (NSDrawer.NSDrawerOpeningState == state || NSDrawer.NSDrawerOpenState == state) {
                            drawer.close();
                        }
                        else {
                            drawer.open();
                        }
                    }
                });

                drawer.openOnEdge(NSRectEdge.NSMaxXEdge);

                frame.setVisible(true);

            }
        });
    }

    public static class DrawerDelegate {
        public boolean drawerShouldOpen(NSDrawer sender) {
            System.err.println("TestToolbar3$DrawerDelegate.drawerShouldOpen: " + sender);
            return true;
        }

        public void drawerWillOpen(NSNotification notification) {
            System.err.println("TestToolbar3$DrawerDelegate.drawerWillOpen: " + notification);
        }

        public void drawerDidOpen(NSNotification notification) {
            System.err.println("TestToolbar3$DrawerDelegate.drawerDidOpen: " + notification);
        }

        public boolean drawerShouldClose(NSDrawer sender) {
            System.err.println("TestToolbar3$DrawerDelegate.drawerShouldClose: " + sender);
            return true;
        }

        public void drawerWillClose(NSNotification notification) {
            System.err.println("TestToolbar3$DrawerDelegate.drawerWillClose: " + notification);
        }

        public void drawerDidClose(NSNotification notification) {
            System.err.println("TestToolbar3$DrawerDelegate.drawerDidClose: " + notification);
        }

        public NSSize drawerWillResizeContents_toSize(NSDrawer sender, NSSize contentSize) {
            System.err.println("TestToolbar3$DrawerDelegate.drawerWillResizeContents_toSize: " + sender + " " + contentSize);
            return contentSize;
        }
    }
}