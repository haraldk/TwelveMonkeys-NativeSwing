import com.twelvemonkeys.spice.osx.appkit.NSWorkspace;
import org.rococoa.cocoa.foundation.NSAutoreleasePool;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * TestLargeIcons
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: TestLargeIcons.java,v 1.0 Jan 16, 2010 2:46:53 PM haraldk Exp$
 */
public class TestLargeIcons {
    // TODO: Use code to create an Icon manager
    public static void main(final String[] pArgs) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame("Test icons");

                NSAutoreleasePool pool = NSAutoreleasePool.new_();

                NSWorkspace workspace = NSWorkspace.sharedWorkspace();

                System.out.println("workspace.isFilePackageAtPath(\"/Applications/Mail.app\"): " + workspace.isFilePackageAtPath("/Applications/Mail.app"));
                System.out.println("workspace.isFilePackageAtPath(\"/Applications/\"): " + workspace.isFilePackageAtPath("/Applications/"));
                BufferedImage image = workspace.iconForFile(new File("/Applications/iTunes.app"), new Dimension(96, 96));
//                NSImage image = workspace.iconForFile("/Applications/iTunes.app");
//                NSImage image = workspace.iconForFile("/Users/haraldk/Desktop/me.jpg");
//                NSImage image = workspace.iconForFileType("jpeg");

                // TODO: TIFFRepresentation fails here (unless using no compression)..
                // Anyway, it's not the icon we're looking for...
                // Apple says: Use NSImage.NSImageNameMultipleDocuments instead if more than one
//                NSImage image = workspace.iconForFiles("/Applications/Mail.app", "/Applications/Mail.app");
//                NSImage image = workspace.iconForFileType("pdf");

                /*
                System.out.println("image: " + image);
                System.out.println("image.representations(): " + image.representations());
                System.out.println("image.size(): " + image.size());


                // Set size and do the magic lock/unlock combo to make sure the representation will be that size
                image.setSize(new NSSize(96f, 96f)); // -- This breaks with later JNA (between 3.2.2 and 3.2.4) releases...
                image.lockFocus();
                image.unlockFocus();
                */

//                BufferedImage buffered = OSXImageUtil.toBufferedImage(image);
//                frame.add(new JLabel(new ImageIcon(buffered)));
                frame.add(new JLabel(new ImageIcon(image)));


                frame.setUndecorated(true);
                frame.setBackground(new Color(0, true));
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);

                pool.drain();
            }
        });
    }
}
