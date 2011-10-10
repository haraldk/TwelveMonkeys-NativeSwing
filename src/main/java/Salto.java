import com.apple.eawt.CocoaComponent;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.twelvemonkeys.spice.osx.OSXImageUtil;
import com.twelvemonkeys.spice.osx.appkit.NSImage;
import com.twelvemonkeys.spice.osx.appkit.NSView;
import org.rococoa.*;
import org.rococoa.cocoa.foundation.NSAutoreleasePool;
import org.rococoa.cocoa.foundation.NSDate;
import org.rococoa.cocoa.foundation.NSURL;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * TestWebKit
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: TestWebKit.java,v 1.0 Mar 21, 2008 8:23:14 PM haraldk Exp$
 */
public class Salto {
    private static final String TITLE = "Cappelen Damm";
    // TODO: Proper mouse tracking (mouse cursor updates seem to occur only every now and then)
    // TODO: Fix resize repaint bug (happens only - read: a lot more frequently - when JWebView is the only component in the frame)
    // TODO: Fix focus issues: Both Swing and native component displays focus ring

    public static void main(String[] pArgs) {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.eawt.CocoaComponent.CompatibilityMode", "false");

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                NSAutoreleasePool.new_();
                final JFrame frame = new JFrame(TITLE);
//                frame.getRootPane().putClientProperty("apple.awt.brushMetalLook", Boolean.TRUE);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//                frame.setUndecorated(true);
//                frame.getRootPane().setBorder(new TestPaletteWindow.PaletteWindowBorder(new TestPaletteWindow.PaletteTitleBar() {
//                    {
//                        if ("!Aqua".equals(UIManager.getLookAndFeel().getID())) {
//                            titleLabel.putClientProperty("JComponent.sizeVariant", null);
//                        }
//
//                        Font font = titleLabel.getFont();
//                        titleLabel.setFont(font.deriveFont(font.getSize2D() * 1.2f));
//                    }
//
//                    @Override
//                    protected void paintComponent(Graphics gr) {
//                        gr.setColor(new Color(0xD3E0F0));
//                        gr.fillRect(0, 0, getWidth(), getHeight());
//
//                        gr.setColor(new Color(0xF0F5FA));
//                        gr.fillRect(0, 0, getWidth(), 1);
//                        gr.fillRect(0, 0, 1, getHeight());
//
//                        gr.setColor(new Color(0x8DB2E3));
//                        gr.fillRect(1, getHeight() - 1, getWidth(), 1);
//                        gr.fillRect(getWidth() - 1, 1, 1, getHeight());
//                    }
//                }));

                final JWebView webView = new JWebView();

                final URLTextField addressField = new URLTextField(20);

                webView.addProgressListener(new ProgressListener() {
                    public void titleReceived(WebView pView, String pTitle, WebFrame pFrame) {
                        if (pFrame.equals(pView.mainFrame())) {
                            frame.setTitle(pTitle + " | " + TITLE);
                        }
                    }

                    public void loadingStarted(WebView pView, WebFrame pFrame) {
                        if (pFrame.equals(pView.mainFrame())) {
                            addressField.setInProgress(true);
                            addressField.setIcon(null);
                            frame.setTitle(TITLE);
                        }
                    }

                    public void loadingCommitted(WebView pView, WebFrame pFrame) {
                        // Them Google guys are probably right in this being a Good Idea (TM)
                        if (pFrame.equals(pView.mainFrame())) {
                            String url = pView.mainFrameURL();
                            int index = url.indexOf("://");
                            int start = index < 0 ? 0 : index + 3;
                            addressField.setText(url.substring(start, url.endsWith("/") ? url.length() - 1 : url.length()));
                        }
                    }

                    public void loadingFinished(WebView pView, WebFrame pFrame) {
                        if (pFrame.equals(pView.mainFrame())) {
                            addressField.setInProgress(false);
                        }
                    }

                    @Override
                    public void iconReceived(WebView view, Image icon, WebFrame frame) {
                        addressField.setIcon(icon != null ? new ImageIcon(icon.getScaledInstance(-1, 16, Image.SCALE_SMOOTH)) : null);
                    }
                });

                JComponent top = new JPanel(new BorderLayout());
                top.setOpaque(false);
                top.setBorder(
                        BorderFactory.createCompoundBorder(
                                new AbstractBorder() {
                                    static final int BORDER_HEIGHT = 1;
                                    @Override
                                    public Insets getBorderInsets(Component c) {
                                        return getBorderInsets(c, new Insets(0, 0, 0, 0));
                                    }

                                    @Override
                                    public Insets getBorderInsets(Component c, Insets insets) {
                                        insets.bottom = BORDER_HEIGHT;
                                        return insets;
                                    }

                                    @Override
                                    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                                        Window window = SwingUtilities.getWindowAncestor(c);
                                        g.setColor(window != null && window.isFocused() ? Color.DARK_GRAY : Color.GRAY);
                                        g.fillRect(0, height - BORDER_HEIGHT, width, BORDER_HEIGHT);
                                    }
                                },
                                BorderFactory.createEmptyBorder(0, 6, 2, 6)
                        )
                );

                addressField.addActionListener(new AbstractAction() {
                    public void actionPerformed(ActionEvent event) {
                        JTextField textField = (JTextField) event.getSource();

                        try {
                            String url = textField.getText();
                            URI uri = URI.create(url);

                            if (uri.getScheme() == null) {
                                uri = new URI("http", url, null);
                            }

                            if (!("http".equals(uri.getScheme()) || "https".equals(uri.getScheme()))) {
                                throw new URISyntaxException(url, "Only HTTP or HTTPS allowed");
                            }

                            textField.setBackground(UIManager.getColor("TextField.background"));                            

                            webView.setMainFrameURL(uri);
                        }
                        catch (Exception e) {
                            textField.setBackground(new Color(0xFFcccc));
                            e.printStackTrace();
                        }
                    }
                });
                top.add(addressField);

//                frame.add(top, BorderLayout.NORTH);
                frame.add(webView, BorderLayout.CENTER);

                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);

//                webView.setMainFrameURL(URI.create("http://www.google.com/"));
//                webView.setMainFrameURL(URI.create("http://developer.apple.com/library/mac/documentation/Cocoa/Reference/WebKit/Classes/WebView_Class/Reference/Reference.html#//apple_ref/doc/c_ref/WebView"));
//                webView.setMainFrameURL(URI.create("http://agap07:8080/pes/"));
                webView.setMainFrameURL(URI.create("http://agap07:8080/pesto/"));
            }
        });
    }

    public static interface WebKit extends Library {
        public final static WebKit INSTANCE = (WebKit) Native.loadLibrary("WebKit", WebKit.class);
    }

    @RunOnMainThread
    public static abstract class WebView implements NSView, NSObject {
        public static WebView create() {
            return Foundation.callOnMainThread(new Callable<WebView>() {
                public WebView call() throws Exception {
                    return Rococoa.create("WebView", WebView.class);
                }
            });
        }

        public abstract void setMainFrameURL(String urlString);
        public abstract String mainFrameURL();

        public abstract String mainFrameTitle();

        public abstract ID frameLoadDelegate();
        public abstract void setFrameLoadDelegate(ID delegate);

        public abstract WebFrame mainFrame();
    }

    public static class JWebView extends CocoaComponent {
        // Need to load the WebKit library
        @SuppressWarnings({"UnusedDeclaration"})
        static final WebKit KIT = WebKit.INSTANCE;

        // Our WebView instance
        private final WebView view = WebView.create();

        // Need to keep a reference to the proxy, to prevent it from being GC'ed and thus reclaimed by the native runtime
        @SuppressWarnings({"FieldCanBeLocal"})
        private NSObject mWebFrameLoadDelegate;

        private final List<ProgressListener> mProgressListeners = new CopyOnWriteArrayList<ProgressListener>();

        public JWebView() {
            setFocusable(true);
        }

        public JWebView(URI pURL) {
            this();
            setMainFrameURL(pURL);
        }

        private void setMainFrameURL(URI pURL) {
            view.setMainFrameURL(pURL.toString());
            requestFocusInWindow();
        }

        @Deprecated
        public int createNSView() {
            return view.id().intValue();
        }

        @Override
        public long createNSViewLong() {
            return view.id().longValue();
        }

        public Dimension getMaximumSize() {
            return new Dimension(1600, 1200);
        }

        public Dimension getMinimumSize() {
            return new Dimension(400, 300);
        }

        public Dimension getPreferredSize() {
            return new Dimension(800, 600);
        }

        public void addProgressListener(final ProgressListener pProgressListener) {
            if (mWebFrameLoadDelegate == null && view.frameLoadDelegate().isNull()) {
                WebFrameLoadDelegate delegate = new WebFrameLoadDelegate();
                mWebFrameLoadDelegate = Rococoa.proxy(delegate);
                view.setFrameLoadDelegate(mWebFrameLoadDelegate.id());
            }

            mProgressListeners.add(pProgressListener);
        }

        public class WebFrameLoadDelegate {
            public void webView_didStartProvisionalLoadForFrame(final WebView view, final WebFrame frame) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        for (ProgressListener listener : mProgressListeners) {
                            listener.loadingStarted(view, frame);
                        }
                    }
                });
            }

            public void webView_didFinishLoadForFrame(final WebView view, final WebFrame frame) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        for (ProgressListener listener : mProgressListeners) {
                            listener.loadingFinished(view, frame);
                        }
                    }
                });
            }

            public void webView_didCommitLoadForFrame(final WebView view, final WebFrame frame) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        for (ProgressListener listener : mProgressListeners) {
                            listener.loadingCommitted(view, frame);
                        }
                    }
                });
            }

            public void webView_willCloseFrame(final WebView view, final WebFrame frame) {
                System.err.println("TestWebKit$JWebView$WebFrameLoadDelegate.webView_willCloseFrame");
            }

            public void webView_didChangeLocationWithinPageForFrame(final WebView view, final WebFrame frame) {
                System.err.println("TestWebKit$JWebView$WebFrameLoadDelegate.webView_didChangeLocationWithinPageForFrame");
            }

            // Data Received Messages

            public void webView_didReceiveTitle_forFrame(final WebView view, final String title, final WebFrame frame) {
                for (ProgressListener listener : mProgressListeners) {
                    listener.titleReceived(view, title, frame);
                }
            }

            public void webView_didReceiveIcon_forFrame(final WebView view, final NSImage icon, final WebFrame frame) {
                for (ProgressListener listener : mProgressListeners) {
                    listener.iconReceived(view, OSXImageUtil.toBufferedImage(icon), frame);
                }
            }

            // Error Messages
            public void webView_didFailProvisionalLoadWithError_forFrame(final WebView view, final WebFrame frame) {
                System.err.println("TestWebKit$JWebView$WebFrameLoadDelegate.webView_didFailProvisionalLoadWithError_forFrame");
            }

            public void webView_didFailLoadWithError_forFrame(final WebView view, final /*NSError*/ NSObject error, final WebFrame frame) {
                System.err.println("TestWebKit$JWebView$WebFrameLoadDelegate.webView_didFailLoadWithError_forFrame: " + error);
            }

            //
            // Client and Server Redirect Messages
            //
            public void webView_didCancelClientRedirectForFrame(final WebView view, final WebFrame frame) {
                System.err.println("TestWebKit$JWebView$WebFrameLoadDelegate.webView_didCancelClientRedirectForFrame");
            }

            public void webView_willPerformClientRedirectToURL_delay_fireDate_forFrame(final WebView view, final NSURL url, final /*NSTimeInterval*/ NSObject delay, final NSDate date, final WebFrame frame) {
                System.err.println("TestWebKit$JWebView$WebFrameLoadDelegate.webView_willPerformClientRedirectToURL_delay_fireDate_forFrame: " + url);
            }

            public void webView_didReceiveServerRedirectForProvisionalLoadForFrame(final WebView view, final WebFrame frame) {
                System.err.println("TestWebKit$JWebView$WebFrameLoadDelegate.webView_didReceiveServerRedirectForProvisionalLoadForFrame");
                System.err.println("view.mainFrameURL(): " + view.mainFrameURL());
            }
        }
    }

    static interface WebFrame extends NSObject {
    }

    static interface ProgressListener {
        void titleReceived(WebView pView, String pTitle, WebFrame pFrame);

        void loadingStarted(WebView pView, WebFrame pFrame);

        void loadingCommitted(WebView pView, WebFrame pFrame);

        void loadingFinished(WebView pView, WebFrame pFrame);

        void iconReceived(WebView view, Image icon, WebFrame frame);
    }

    public static class URLTextField extends JTextField {
        final static Dimension iconBounds = new Dimension(18, 18);

        final JLabel iconLabel = new JLabel();
        final JProgressBar progress = new JProgressBar();

        public URLTextField(final int columns) {
            super(columns);

            iconLabel.setVerticalAlignment(CENTER);
            iconLabel.setHorizontalAlignment(RIGHT);
            add(iconLabel);

            progress.setVisible(false);
            progress.setIndeterminate(true);
            progress.putClientProperty("JProgressBar.style", "circular");
            add(progress);
        }

        @Override
        public void doLayout() {
            super.doLayout();

            Insets insets = super.getInsets();
            iconLabel.setBounds(getWidth() - insets.left - iconBounds.width, insets.top - 1, iconBounds.width, iconBounds.height);
            progress.setBounds(getWidth() - insets.left - iconBounds.width, insets.top - 1, iconBounds.width, iconBounds.height);
        }

        @Override
        public void paint(Graphics g) {
            g.setColor(Color.WHITE);

            Rectangle bounds = iconLabel.getBounds();
            g.fillRect(bounds.x - 2, bounds.y, iconBounds.width + 4, iconBounds.height);

            super.paint(g);
        }

        @Override
        public Insets getInsets() {
            Insets insets = super.getInsets();

            insets.right += iconBounds.width - 2 + iconLabel.getIconTextGap();

            return insets;
        }

        final public void setIcon(Icon icon) {
            iconLabel.setIcon(icon);
            iconLabel.setVisible(icon != null);

            repaint();
        }

        final public Icon getIcon() {
            return iconLabel.getIcon();
        }

        final public void setInProgress(boolean inProgress) {
            progress.setVisible(inProgress);
            iconLabel.setVisible(!inProgress);

            repaint();
        }
    }

}