import com.twelvemonkeys.spice.ApplicationMenu;
import com.twelvemonkeys.spice.osx.appkit.*;
import org.rococoa.ID;
import org.rococoa.NSObject;
import org.rococoa.Rococoa;
import org.rococoa.cocoa.foundation.NSAutoreleasePool;
import org.rococoa.cocoa.foundation.NSData;
import org.rococoa.cocoa.foundation.NSInteger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * TestOpenPanel
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: TestOpenPanel.java,v 1.0 Mar 21, 2008 8:23:14 PM haraldk Exp$
 */
public class TestApplicationMenu {

    // TODO: Add API:
    // OSXApplication
    //   JMenu getApplicationMenu() // Fully mutable, prepopulated with all the default items
    //   DockTile getDocTile()
    //   BufferedImage getApplicationImage()
    //   void setApplicationImage(BufferedImage image)
    // OSXDockTile
    //   boolean isShowsBadge();
    //   void setShowsBadge(boolean show)
    //   String getBadgeLabel()
    //   void setBadgeLabel(String label)
    //   ? BufferedImage getImage()
    //   ? void setImage(BufferedImage image)

    public static void main(String[] pArgs) {
        System.setProperty("apple.laf.useScreenMenuBar", "true");

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                NSAutoreleasePool pool = NSAutoreleasePool.new_();
                
                JMenu windowMenu = new JMenu("Window");
                windowMenu.add(new JCheckBoxMenuItem(new AbstractAction("Toggle menu bar") {
                    {
                        // Needed to turn on again...
                        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_M, 0)); // Seems shortcut is rendered wrong...
                    }
                    public void actionPerformed(ActionEvent e) {
                        NSMenu.setMenuBarVisible(!NSMenu.menuBarVisible());
                    }
                }));
                windowMenu.add(new AbstractAction("Jump!") {
                    public void actionPerformed(ActionEvent e) {
                        Timer t = new Timer(1000, new ActionListener() {
                            int count = 3;

                            public void actionPerformed(ActionEvent e) {
                                System.out.println(count);
                                if (count-- <= 0) {
                                    // NOTE: Will only jump if application does not already have focus
                                    NSInteger value = NSApplication.sharedApplication().requestUserAttention(NSApplication.NSCriticalRequest);// Enum, 0 or 10
                                    System.out.println("value: " + value);
                                    ((Timer) e.getSource()).stop();
                                }
                            }
                        });
                        t.start();
                    }
                });

                JMenuBar menubar = new JMenuBar();
                menubar.add(windowMenu);

                final JFrame frame = new JFrame("The frame...");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setJMenuBar(menubar);
                frame.setPreferredSize(new Dimension(300, 200));
                frame.pack();
                frame.setLocationRelativeTo(null);

                JMenu appMenu = ApplicationMenu.getMenu();
                System.out.println("app.getText(): " + appMenu.getText());
                appMenu.setText("Foo"); // Works (according to toString), but no visual effect
                //appMenu.removeAll();
                
                appMenu.insert(new JMenuItem(new AbstractAction("Check for Updates...") {
                    {
                        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_U, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK));
                    }
                    public void actionPerformed(ActionEvent e) {
                        System.out.println("Updates.actionPerformed: " + e);
                    }
                }), 1);

                appMenu.insert(new JMenuItem(new AbstractAction("Preferences...") {
                    {
                        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_COMMA, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
                    }
                    public void actionPerformed(ActionEvent e) {
                        System.out.println("Preferences.actionPerformed: " + e);
                        System.out.println("Thread.currentThread(): " + Thread.currentThread());
                    }
                }), 3);

                appMenu.insert(new JMenuItem(new AbstractAction("Other stuff") {
                    {
                        setEnabled(false);
                    }
                    public void actionPerformed(ActionEvent e) {
                    }
                }), 4);

                appMenu.insertSeparator(5); // No effect if already separator at this index?

                System.out.println("NSApplication.sharedApplication().mainMenu().itemAtIndex(0).submenu(): " + NSApplication.sharedApplication().mainMenu().itemAtIndex(0).submenu());

                JMenuItem quitItem = appMenu.getItem(appMenu.getItemCount() - 1);
                System.out.println("quitItem: " + quitItem);
                quitItem.setText("Quit it!");
                System.out.println("quitItem.getAccelerator(): " + quitItem.getAccelerator());

                final JMenuItem aboutItem = appMenu.getItem(0);
                final Action oldAboutAction = aboutItem.getAction();
                System.out.println("aboutItem: " + aboutItem);
                aboutItem.setAction(new AbstractAction("Replace this item with About") {
                    public void actionPerformed(ActionEvent e) {
                        System.out.println("About.actionPerformed: " + e);
                        aboutItem.setAction(oldAboutAction);
                    }
                });

                for (int i = 0; i < appMenu.getItemCount(); i++) {
                    JMenuItem item = appMenu.getItem(i);
                    System.out.println("item[" + i + "]: " + item);
                    System.out.println("item.getAccelerator(): " + (item != null ? item.getAccelerator() : null));
                }

                // PoC: Clear menu...
//                for (int i = appMenu.getItemCount(); i > 0; i--) {
//                    appMenu.remove(i - 1);
//                }
//                while (appMenu.getItemCount() > 0) {
//                    appMenu.remove(0);
//                }
//                NSMenu menu = NSApplication.NSApp.mainMenu();
//                menu.removeItemAtIndex(0);

//                initApplicationImage();

                NSDockTile dockTile = NSApplication.sharedApplication().dockTile();
                //dockTile.setShowsApplicationBadge(true); // Not implemented!?
                dockTile.setBadgeLabel("Hello!");

                frame.setVisible(true);
            }
        });
    }

    private static void initApplicationImage() {
        // Yeah! Setting the application icon
        NSImage image = NSApplication.NSApp.applicationIconImage();
//        System.out.println("image: " + image);

        // TODO: Should be possible to get the data in PNG rep, but it's slow...
        // So, for now, it's better to just get the TIFF data and read from that

        NSData data;

//        long start = System.currentTimeMillis();

        data = image.TIFFRepresentation(); // Get TIFF data
        // Does't work, as there's no bitmap image rep...
//                NSArray reps = image.representations();
//                System.out.println("reps: " + reps);
//                NSData data = NSBitmapImageRep.CLASS.representationOfImageRepsInArray_usingType_properties(reps, NSBitmapImageRep.NSPNGFileType, null);

        // Slow but works...
        // TODO: Could add an additional conversion here, if there's no TIFF reader? Assuming there's always a PNG reader
//                NSBitmapImageRep rep = NSBitmapImageRep.CLASS.imageRepWithData(data);
//                data = rep.representationUsingType_properties(NSBitmapImageRep.NSPNGFileType, null);
//                System.out.println("rep: " + rep);

//                System.out.println("data: " + data);

        byte[] bytes = new byte[data.length()];
        data.getBytes(bytes);
        try {
            BufferedImage buffered = ImageIO.read(new ByteArrayInputStream(bytes)); // Read PNG/TIFF data
//            System.out.println("Time: " + (System.currentTimeMillis() - start));
//
//            System.out.println("buffered: " + buffered);

            int size = buffered.getWidth() / 4;

            Graphics2D g = buffered.createGraphics();
            try {
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(Color.BLACK);
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .3f));
                g.fillOval(buffered.getWidth() - size, 1, size, size);
                g.setComposite(AlphaComposite.SrcOver);
                g.setColor(Color.RED);
                g.fillOval(buffered.getWidth() - size - 1, 0, size, size);

                g.setFont(Font.decode("Helvetica").deriveFont(Font.BOLD, size / 2f));
                g.setColor(Color.WHITE);
                g.drawString("1", buffered.getWidth() - size / 2, size - size / 3);
            }
            finally {
                g.dispose();
            }

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ImageIO.write(buffered, "PNG", stream); // Write data back as PNG

//            System.out.println("Data written: " + stream.size());

            if (stream.size() > 0) {
                bytes = stream.toByteArray();

                data = NSData.CLASS.dataWithBytes_length(bytes, bytes.length);
//                System.out.println("data: " + data);

//                image = Rococoa.create("NSImage", NSImage.class);

                image = Rococoa.create("NSImage", NSImage.class).initWithData(data); // Returns the same instance...?

//                System.out.println("image: " + image);
                NSApplication.NSApp.setApplicationIconImage(image);

//                image = NSApplication.NSApp.applicationIconImage();
//                System.out.println("image: " + image);
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private NSImage createImage() {
        byte[] bytes;
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            BufferedImage image = (BufferedImage) Toolkit.getDefaultToolkit().getImage("NSImage://NSFlowViewTemplate");
            ImageIO.write(image, "PNG", stream);
            stream.close();
            bytes = stream.toByteArray();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        NSData data = NSData.CLASS.dataWithBytes_length(bytes, bytes.length);
        System.out.println("data: " + data);
        NSImage image = Rococoa.create("NSImage", NSImage.class).initWithData(data);
        System.out.println("image: " + image);
        return image;
    }

    public static interface NSMenuValidation extends NSObject {
        boolean validateMenuItem(NSMenuItem item);
    }

    public static class MenuItemActionProxy /*implements NSMenuValidation*/ {
        //NSMenuValidation

        // TODO Allow action(s) to be added to to this proxy 
        public MenuItemActionProxy() {
        }

        public void actionPerformed(ID id) {
            System.out.println("TestApplicationMenu$MenuItemActionProxy.actionPerformed: " + id);
        }

        public boolean validateMenuItem(final NSMenuItem item) {
            System.out.println("TestApplicationMenu$MenuItemActionProxy.validateMenuItem: " + item);
            return true;
        }
    }
}