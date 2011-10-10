import com.twelvemonkeys.spice.osx.appkit.NSMenu;
import com.twelvemonkeys.spice.osx.appkit.NSMenuItem;
import com.twelvemonkeys.spice.osx.appkit.NSStatusItem;
import org.rococoa.Selector;
import org.rococoa.cocoa.foundation.NSAutoreleasePool;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

import static com.twelvemonkeys.spice.osx.appkit.NSStatusBar.*;

/**
 * TestSystemStatusBar
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: TestSystemStatusBar.java,v 1.0 Dec 15, 2010 8:58:13 PM haraldk Exp$
 */
public class TestSystemStatusBar {
    public static void main(String[] args) throws InterruptedException {
        // TODO: Use with the menu proxy system to allow full Swing inter-op 

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setLocationRelativeTo(null);
//                frame.setSize(0, 0);
//                frame.setLocation(-100, -100);
                frame.setVisible(true);

                NSAutoreleasePool.new_();

//                NSApplication.sharedApplication();
                NSStatusItem statusItem = systemStatusBar().statusItemWithLength(NSSquareStatusItemLength);
                statusItem.setImage(createImage(false));
                statusItem.setAlternateImage(createImage(true));
                statusItem.setHighlightMode(true);
                statusItem.setToolTip("Foo");
//                System.err.println("statusItem.menu(): " + statusItem.menu());
                NSMenu menu = NSMenu.init("foo");
                statusItem.setMenu(menu);
                menu.setAutoenablesItems(false);

                NSMenuItem foo = menu.addItem("Foo", null, "");
                foo.setEnabled(true);
                menu.addItem("Bar",  new Selector(), "");
                menu.addItem("Baz (the real one)",  new Selector(), "");

                menu.update();
            }
        });


//        while (true) {
//            Thread.sleep(1000l);
//        }
    }

    private static BufferedImage createImage(boolean alternate) {
        BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = image.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setStroke(new BasicStroke(1.8f));

            if (!alternate) {
                g.setColor(Color.WHITE);
                g.drawOval(1, 2, image.getWidth() - 4, image.getHeight() - 4);
            }
            
            g.setColor(alternate ? Color.WHITE : Color.BLACK);
            g.drawOval(1, 1, image.getWidth() - 4, image.getHeight() - 4);

//            g.setColor(Color.CYAN);
//            g.drawLine(0, 0, image.getWidth(), image.getHeight());
//
//            g.setColor(Color.BLACK);
//            g.drawLine(image.getWidth(), 0, 0, image.getHeight());
        }
        finally {
            g.dispose();
        }

        return image;
    }
}
