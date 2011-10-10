import com.apple.eawt.CocoaComponent;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.twelvemonkeys.spice.osx.appkit.NSView;
import com.twelvemonkeys.spice.osx.foundation.NSData;
import com.twelvemonkeys.spice.osx.foundation.NSSize;
import org.rococoa.*;
import org.rococoa.cocoa.foundation.NSAutoreleasePool;
import org.rococoa.cocoa.foundation.NSURL;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.net.URI;
import java.util.concurrent.Callable;

/**
 * TestPDFKit
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: TestPDFKit.java,v 1.0 05.04.11 11.41 haraldk Exp$
 */
public class TestPDFKit {
    public static void main(final String[] args) {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.eawt.CocoaComponent.CompatibilityMode", "false");

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                NSAutoreleasePool.new_();

                JFrame frame = new JFrame("PDFKit Test Application");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                final JPDFView view = new JPDFView(URI.create(args[0]));
                frame.add(view, BorderLayout.CENTER);

                frame.getRootPane().getActionMap().put("zoom-in", new AbstractAction("Zoom in") {
                    {
//                        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
                        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke((Character) '+', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
                    }
                    public void actionPerformed(ActionEvent e) {
                        System.err.println("TestPDFKit.actionPerformed: " + getValue(NAME) + " " + e);
                        view.zoomIn();
                    }
                });
                frame.getRootPane().getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ADD, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "zoom-in");

                frame.getRootPane().getActionMap().put("zoom-out", new AbstractAction("Zoom out") {
                    {
//                        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
                        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke((Character) '-', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
                    }
                    public void actionPerformed(ActionEvent e) {
                        System.err.println("TestPDFKit.actionPerformed: " + getValue(NAME) + " " + e);
                        view.zoomOut();
                    }
                });
                frame.getRootPane().getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_SUBTRACT, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "zoom-out");

                JMenuBar menubar = new JMenuBar();

                JMenu viewMenu = new JMenu("View");
                viewMenu.add(new JMenuItem(frame.getRootPane().getActionMap().get("zoom-in")));
                viewMenu.add(new JMenuItem(frame.getRootPane().getActionMap().get("zoom-out")));

                menubar.add(viewMenu);

                frame.setJMenuBar(menubar);


                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

    public static interface Quartz extends Library {
        public final static Quartz INSTANCE = (Quartz) Native.loadLibrary("Quartz", Quartz.class);
    }

    public static abstract class PDFDocument implements NSObject {
        private static PDFDocument alloc() {
            return Rococoa.create("PDFDocument", PDFDocument.class, "alloc");
         }

        public static PDFDocument initWithURI(URI uri) {
            NSURL url;
            if (uri.getScheme() == null) {
                url = NSURL.CLASS.fileURLWithPath(uri.toString());
            }
            else {
                url = NSURL.CLASS.URLWithString(uri.toString());
            }

            return alloc().initWithURL(url);
        }

        public static PDFDocument initWithData(byte[] data) {
            return alloc().initWithData(NSData.CLASS.dataWithBytes_length(data, data.length));
        }

        protected abstract PDFDocument initWithURL(NSURL url);

        protected abstract PDFDocument initWithData(NSData data);
    }

    

    public static abstract class PDFPage implements NSObject {

    }

    @RunOnMainThread
    public static abstract class PDFView implements NSView, NSObject {
        public static PDFView create() {
            return Foundation.callOnMainThread(new Callable<PDFView>() {
                public PDFView call() throws Exception {
                    return Rococoa.create("PDFView", PDFView.class);
                }
            });
        }

        public abstract void setDocument(PDFDocument document);
        public abstract PDFDocument document();

        public abstract PDFPage currentPage();
        public abstract NSSize rowSizeForPage(PDFPage page);

        public abstract void setAutoScales(boolean autoScales);
        public abstract boolean autoScales();

        public abstract void setScaleFactor(double scale);
        public abstract double scaleFactor();

        public abstract void zoomIn(ID sender);
        public abstract void zoomOut(ID sender);
    }


    public static class JPDFView extends CocoaComponent {
        // Need to load the Quartz library that contains PDFKit
        @SuppressWarnings({"UnusedDeclaration"})
        static final Quartz KIT = Quartz.INSTANCE;

        // Our PDFView instance
        private final PDFView view = PDFView.create();

        public JPDFView() {
            this(null);
        }

        public JPDFView(URI document) {
            setDocumentFromURI(document);
        }

        @Override
        public long createNSViewLong() {
            return view.id().longValue();
        }

        @Deprecated
        public int createNSView() {
            return view.id().intValue();
        }

        public Dimension getMaximumSize() {
            return new Dimension(Short.MAX_VALUE, Short.MAX_VALUE);
        }

        public Dimension getMinimumSize() {
            return new Dimension(0, 0);
        }

        public Dimension getPreferredSize() {
            return new Dimension(800, 500);
        }

        public final void setDocumentFromURI(URI uri) {
            PDFDocument document = PDFDocument.initWithURI(uri);
//            System.out.println("document: " + document);
            view.setDocument(document);
        }

        public void setAutoScaling(boolean autoScales) {
            view.setAutoScales(autoScales);
        }

        public boolean isAutoScaling() {
            return view.autoScales();
        }

        public void zoomIn() {
            view.zoomIn(view.id());
        }

        public void zoomOut() {
            view.zoomOut(view.id());
        }
    }
}
