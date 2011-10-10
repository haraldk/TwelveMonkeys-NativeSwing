import com.twelvemonkeys.spice.osx.appkit.AppKit;
import com.twelvemonkeys.spice.osx.appkit.NSPasteboard;
import com.twelvemonkeys.spice.osx.foundation.NSData;
import org.rococoa.cocoa.foundation.NSAutoreleasePool;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * TestChromeDrop
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @version $Id: TestChromeDrop.java,v 1.0 Jan 17, 2010 8:17:53 PM haraldk Exp$
 */
public class TestChromeDrop {
    private static final boolean DEBUG = true;

    public static void main(final String[] pArgs) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                final JFrame frame = new JFrame("TestChromeDrop");

                JLabel label = new JLabel() {
                    @Override
                    public Dimension getPreferredSize() {
                        Dimension size = super.getPreferredSize();
                        size.width = Math.max(size.width, 300);
                        size.height = Math.max(size.height, 200);
                        return size;
                    }
                };
                label.setDropTarget(new DropTarget(label, new DropTargetAdapter() {
                    public void drop(DropTargetDropEvent dtde) {
                        dtde.acceptDrop(DnDConstants.ACTION_COPY);

                        if (handleChromeImageDrop(dtde)) {
                            return;
                        }

                        handleImageDrop(dtde);
                    }

                    private void acceptOrRecject(DropTargetDragEvent dtde) {
                        if (dtde.isDataFlavorSupported(DataFlavor.imageFlavor)) {
                            dtde.acceptDrag(DnDConstants.ACTION_COPY);
                        }
                        else {
                            dtde.rejectDrag();
                        }
                    }

                    @Override
                    public void dragEnter(DropTargetDragEvent dtde) {
                        acceptOrRecject(dtde);
                    }

                    @Override
                    public void dropActionChanged(DropTargetDragEvent dtde) {
                        acceptOrRecject(dtde);
                    }

                    @Override
                    public void dragOver(DropTargetDragEvent dtde) {
                        acceptOrRecject(dtde);
                    }
                }));

                JScrollPane scroll = new JScrollPane(label);
                scroll.setBorder(BorderFactory.createEmptyBorder());
                frame.add(scroll);

                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
            }
        });
    }

    private static BufferedImage convertNSDataToImage(final NSData data, final long start) {
        // TODO: This code is modified from OSXImageUtil, consider merge...

        // TODO: Figure out RTF stuff header (first 4096 bytes)...
        byte[] bytes = new byte[data.length() - 4096];
        data.getBytes(bytes, 4096, bytes.length);

        try {
            BufferedImage buffered = ImageIO.read(new ByteArrayInputStream(bytes)); // Read data

            if (DEBUG) {
                System.out.println("image: " + buffered);
                System.out.println("NSImage to BufferedImage conversion time: " + (System.currentTimeMillis() - start) + " ms");
            }

            return buffered;
        }
        catch (IOException e) {
            // There's really no I/O here, unless we have a faulty ImageReader implementation
            throw new RuntimeException(e);
        }
    }

    private static boolean handleChromeImageDrop(final DropTargetDropEvent dtde) {
        NSAutoreleasePool pool = NSAutoreleasePool.new_();

        try {
            NSPasteboard pasteboard = NSPasteboard.pasteboardWithName(NSPasteboard.NSDragPboard);

            // TODO: Replace with UTI (now, how do we do that, when these are the types that are used?)
            String best = pasteboard.availableTypeFromArray(
                    AppKit.NSCreateFileContentsPboardType("png"),
                    AppKit.NSCreateFileContentsPboardType("jpeg"),
                    AppKit.NSCreateFileContentsPboardType("gif")
            );

            NSData data = pasteboard.dataForType(best);
            if (data != null) {
                BufferedImage image = convertNSDataToImage(data, DEBUG ? System.currentTimeMillis() : 0l);

                JLabel l = (JLabel) dtde.getDropTargetContext().getComponent();
                l.setIcon(new /*Buffered*/ImageIcon(image));

                dtde.dropComplete(true);

                return true;
            }
        }
        finally {
            pool.drain();
        }

        return false;
    }

    private static boolean handleImageDrop(final DropTargetDropEvent dtde) {
        if (dtde.isDataFlavorSupported(DataFlavor.imageFlavor)) {
            try {
                Image image = (Image) dtde.getTransferable().getTransferData(DataFlavor.imageFlavor);
                JLabel l = (JLabel) dtde.getDropTargetContext().getComponent();
                l.setIcon(new ImageIcon(image));

                dtde.dropComplete(true);
            }
            catch (Throwable e) {
                e.printStackTrace();
                dtde.dropComplete(false);
            }

            return true;
        }

        return false;
    }

}