import com.twelvemonkeys.spice.osx.appkit.AppKit;
import com.twelvemonkeys.spice.osx.appkit.NSPasteboard;
import com.twelvemonkeys.spice.osx.foundation.NSData;
import com.twelvemonkeys.spice.osx.quicklook.QuickLook;
import org.rococoa.cocoa.foundation.NSAutoreleasePool;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.List;

/**
 * TestQuickLook
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: TestQuickLook.java,v 1.0 Jan 17, 2010 8:17:53 PM haraldk Exp$
 */
public class TestQuickLook {
    private static final boolean DEBUG = true;

    public static void main(final String[] pArgs) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                final File path = new File("/Users/haraldk/Downloads/About Downloads.pdf");
//                File path = new File("/Users/haraldk/Downloads/oslo-vest.pdf");
//                File path = new File("/Users/haraldk/Desktop/me2.jpg");
                NSAutoreleasePool pool = NSAutoreleasePool.new_();

                final JFrame frame = new JFrame("QuickLook");

//                frame.add(new JLabel(new ImageIcon(QuickLook.createThumbnail(path, new Dimension(256, 256)))));
                JLabel label = new JLabel(new ImageIcon(QuickLook.createThumbnail(path, new Dimension(1024, 1024))));
                label.setDropTarget(new DropTarget(label, new DropTargetAdapter() {
                    public void drop(DropTargetDropEvent dtde) {
                        dtde.acceptDrop(DnDConstants.ACTION_COPY);

                        System.out.println("dtde.getCurrentDataFlavorsAsList(): " + dtde.getCurrentDataFlavorsAsList());
                        // text/uri-list;representationclass=java.lang.String
                        // image/x-pict;representationclass=java.io.InputStream
                        // image/x-java-image;representationclass=java.awt.Image
                        try {
                            DataFlavor uriList = new DataFlavor("text/uri-list;class=java.lang.String");
                            if (dtde.isDataFlavorSupported(uriList)) {
                                String data = (String) dtde.getTransferable().getTransferData(uriList);
                                System.out.println("data: " + data);

                                URL url = new URL(data);
                                InputStream in = url.openStream();
                                BufferedImage image = ImageIO.read(in);
                                System.out.println("image: " + image);
                            }

                            DataFlavor pict = new DataFlavor("image/x-pict");
                            if (dtde.isDataFlavorSupported(pict)) {
                                InputStream in = (InputStream) dtde.getTransferable().getTransferData(pict);

//                                File temp = File.createTempFile("temp-", ".pict");
//                                System.out.println("open " + temp.getAbsolutePath());
//                                FileOutputStream out = new FileOutputStream(temp);
//                                byte[] buf = new byte[1024];
//                                int len = 0;
//                                while ((len = in.read(buf)) >= 0) {
//                                    out.write(buf, 0, len);
//                                }
//                                out.close();
//                                in.close();
//                                in = (InputStream) dtde.getTransferable().getTransferData(pict);

                                BufferedImage image = ImageIO.read(in);
                                System.out.println("image: " + image);
                            }
                        }
                        catch (Exception e) {
                            throw new RuntimeException(e);
                        }

                        if (handleChromeImageDrop(dtde)) {
                            return;
                        }

                        if (handleImageDrop(dtde)) {
                            return;
                        }

                        handleFileDrop(dtde);
                    }

                    private void acceptOrRecject(DropTargetDragEvent dtde) {
                        if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor) || dtde.isDataFlavorSupported(DataFlavor.imageFlavor)) {
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

                label.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "quickLook");
                label.getActionMap().put("quickLook", new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        NSAutoreleasePool pool = NSAutoreleasePool.new_();

                        QuickLook.showQuickLookPanel(
                                frame,
                                new File("/Users/haraldk/Downloads/About Downloads.pdf"),
                                new File("/Users/haraldk/Downloads/oslo-vest.pdf"),
                                new File("/Users/haraldk/Desktop/me2.jpg")
                        );

                        pool.drain();
                    }
                });

                JScrollPane scroll = new JScrollPane(label);
                scroll.setBorder(BorderFactory.createEmptyBorder());
                frame.add(scroll);

                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);

                pool.drain();
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


    private static boolean handleFileDrop(final DropTargetDropEvent dtde) {
        NSAutoreleasePool pool = NSAutoreleasePool.new_();

        try {
            @SuppressWarnings({"unchecked"})
            List<File> files = (List<File>) dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
            JLabel l = (JLabel) dtde.getDropTargetContext().getComponent();
            BufferedImage image = QuickLook.createThumbnail(files.get(0), l.getSize());

            if (image != null) {
                l.setIcon(new ImageIcon(image));
            }

            dtde.dropComplete(true);
        }
        catch (Exception e) {
            e.printStackTrace();
            dtde.dropComplete(false);
        }
        finally {
            pool.drain();
        }

        return true;
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
