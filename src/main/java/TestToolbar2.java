import com.sun.jna.Native;
import com.twelvemonkeys.spice.osx.appkit.NSImage;
import com.twelvemonkeys.spice.osx.appkit.NSNotification;
import com.twelvemonkeys.spice.osx.appkit.*;
import com.twelvemonkeys.spice.osx.foundation.NSRect;
import org.rococoa.Foundation;
import org.rococoa.ID;
import org.rococoa.NSObject;
import org.rococoa.Rococoa;
import org.rococoa.cocoa.foundation.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TestToolbar2
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: TestToolbar2.java,v 1.0 Mar 21, 2008 8:23:14 PM haraldk Exp$
 */
public class TestToolbar2 {
    // TODO: Check if CEmbeddedFrame might help us

    // A third thread is introduced, to avoid deadlocks between AppKit and AWT... Probably not the "right" way..
    private static final ExecutorService DISPATCHER = Executors.newSingleThreadExecutor();

    // TODO: Maybe a better approach would be to create the NSToolbar in XCode and only attach actions?
    //       - Possibly by adding the option of adding swing components for non-button type components...

    // TODO: Resize content pane when toolbar shows/hides..
    // - I'll need a callback for that
    //   - Possible to listen for window size change (window.addComponentListener), and check if toolbar.isVisible is same as previous size change...

    // TODO: Add API:

    public static void main(String[] pArgs) {
        System.setProperty("apple.laf.useScreenMenuBar", "true");

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                NSAutoreleasePool pool = NSAutoreleasePool.new_();

                final JFrame frame = new JFrame("JFrame with hybrid JToolbar/NSToolbar");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                JLabel label = new JLabel("Hello Toolbar", JLabel.CENTER) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        g.setColor(Color.RED);
                        g.drawLine(0, 0, getWidth(), getHeight());
                        g.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2);
                        g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                    }
                };
                label.setVerticalAlignment(JLabel.CENTER);
//                label.setOpaque(true);
//                label.setBackground(Color.LIGHT_GRAY);
                label.setPreferredSize(new Dimension(300, 200));
                frame.add(label);
                frame.pack();
                frame.setLocationRelativeTo(null);

                final ToolbarDelegate toolbarDelegate = new ToolbarDelegate(frame);

                frame.setVisible(true);

                final NSWindow window = NSWindow.CLASS.windowFromAWT(frame);

                // TODO: Either (depending on what works)
                // - Add an NSView (java.awt.Panel) for each JComoponent in the toolbar
                // - Add a component to a toolbar item that occupies the entire toolbar area, and use that for Swing components
                // - Make an empty toolbar and not show the "baseline separator" and use a listener to show/hide

                DISPATCHER.execute(new Runnable() {
                    public void run() {
                        // Create the toolbar, so far, so good...
                        NSToolbar toolbar = NSToolbar.CLASS.initWithIdentifier("toolbar");
                        NSObject delegate = Rococoa.proxy(toolbarDelegate);
                        frame.getRootPane().putClientProperty("toolbarDelegate", delegate); // Keep the delegate from being CG'ed
                        toolbar.setDelegate(delegate.id());
                        toolbar.setDisplayMode(NSToolbar.NSToolbarDisplayModeIconOnly);

                        toolbar.setAllowsUserCustomization(false); // Needed to get right-click menu
//                        toolbar.setAllowsUserCustomization(true); // Needed to get right-click menu
//                toolbar.setShowsBaselineSeparator(false);

                        // Attach the toolbar to the frame
                        window.setToolbar(toolbar);

//                window.setShowsToolbarButton(false); // Default is show
                    }
                });

            }
        });
    }

//    public static String getAppKitGlobalString(String globalVarName) {
//        return Foundation.toString(ID.fromLong(NativeLibrary.getInstance("AppKit").getGlobalVariableAddress(globalVarName).getNativeLong(0).longValue()));
//    }

    public static class ToolbarDelegate {
        private final JFrame mFrame;

        private final Map<String, Component> mComponents = new HashMap<String, Component>();
        // TODO: Consider making the two next maps weak and use component as key?
        private final Map<Component, Dimension> mMaxSizes = new WeakHashMap<Component, Dimension>();
        private final Map<Component, Dimension> mMinSizes = new WeakHashMap<Component, Dimension>();
        private final Map<String, NSToolbarItem> mItems = new HashMap<String, NSToolbarItem>();
//        private final Map<String, Component> mImages = new HashMap<String, Component>();
        private final Map<String, NSImage> mImages = new HashMap<String, NSImage>();

        // TODO: Add JToolbar or Component... as parameter?

        public ToolbarDelegate(final JFrame pFrame) {
            mFrame = pFrame;

            mFrame.addComponentListener(new ComponentAdapter() {
                int toolbarHeight;
                boolean toolbarVisible;

                @Override
                public void componentResized(ComponentEvent e) {
                    DISPATCHER.execute(new Runnable() {
                        public void run() {
                            NSWindow window = NSWindow.CLASS.windowFromAWT(mFrame);
                            final int oldHeight = toolbarHeight;
                            toolbarHeight = getToolbarHeight(window);

                            // Only invoke if change
                            if (oldHeight != toolbarHeight) {
                                SwingUtilities.invokeLater(new Runnable() {
                                    public void run() {
                                        // TODO: Figure out how to make it work with correct (top) border..
                                        mFrame.getRootPane().setBorder(BorderFactory.createEmptyBorder(0, 0, toolbarHeight, 0));

                                        // TODO: Consider using client properties
                                        // Fire event if toolbar.isVisible != toolbarVisible
//                                        mFrame.firePropertyChange("toolbarVisible", toolbarVisible, toolbarVisible = toolbarHeight > 0);
                                        mFrame.firePropertyChange("toolbarHeight", oldHeight, toolbarHeight);
                                    }
                                });
                            }
                        }
                    });
                }
            });

            Component foo = createAWTView("foo");
            mComponents.put("foo", foo);
            mImages.put("foo", createNSImage((JComponent) ((Container) foo).getComponent(0)));
        }

        // TODO: Create createAWTViewForComponent(Component);
        // TODO: Keep visibility in sync with toolbar items
        // TODO: Keep sizes in sync (pref/min/max)
        //       - Listen to window resize, to see if there's more space?

        //        NSView createAWTView(final String identifier) {

        Component createAWTView(final String identifier) {
            JToolBar bar = new JToolBar("Toolbar");
//            Border b = bar.getBorder();
//            Insets i = b.getBorderInsets(bar);
//            bar.setBorder(BorderFactory.createMatteBorder(i.top, i.left, i.bottom, i.right, Color.RED));
            bar.setBorder(BorderFactory.createEmptyBorder());
            bar.setFloatable(false);
            bar.setName(identifier);
            bar.setOpaque(false);

            JButton button = new JButton("Foo");
            button.putClientProperty("JButton.buttonType", "textured");
//                    button.putClientProperty("JButton.buttonType", "segmentedCapsule");
//                    button.putClientProperty("JButton.buttonType", "segmentedTextured");
//                    button.putClientProperty("JButton.segmentPosition", "only");
            button.setRequestFocusEnabled(false);
            button.setOpaque(false);
            bar.add(button);

            button = new JButton("Bar");
            button.putClientProperty("JButton.buttonType", "textured");
//                    button.putClientProperty("JButton.buttonType", "segmentedCapsule");
//                    button.putClientProperty("JButton.buttonType", "segmentedTextured");
//                    button.putClientProperty("JButton.segmentPosition", "only");
            button.setRequestFocusEnabled(false);
            button.setOpaque(false);
            bar.add(button);

            bar.add(Box.createHorizontalGlue());

            JTextField textField = new JTextField();
            textField.putClientProperty("JTextField.variant", "search");
//            textField.setRequestFocusEnabled(false);
            textField.setOpaque(false);
            textField.setColumns(15);
            textField.setMaximumSize(textField.getPreferredSize());
            textField.setColumns(5);
            bar.add(textField);

            // TODO: Make sure this panel does not propagate mouse events to it's parent
            Panel panel = new MyPanel(new BorderLayout(0, 0));
//            panel.setBackground(Color.ORANGE);
            panel.add(bar);

            panel.setVisible(false);
//            panel.addNotify(); // Hack to give us a peer... (gives NPE if panel.parent == null)
            mFrame.getLayeredPane().add(panel, JLayeredPane.PALETTE_LAYER);

//            long windowId = Native.getWindowID(mFrame);
//            System.out.println("windowId: " + windowId);
//            CEmbeddedFrame cframe = new CEmbeddedFrame(windowId);
//            System.out.println("cframe.getBounds(): " + cframe.getBounds());
//            System.out.println("cframe.isVisible(): " + cframe.isVisible());
//            System.out.println("cframe: " + cframe);
//            cframe.setBackground(Color.ORANGE);
//            cframe.setBounds(0, -50, 1000, 55);
//            cframe.setVisible(false);
//            cframe.add(button);
//
            Dimension pref = panel.getPreferredSize();
            panel.setBounds(new Rectangle(new Point(0, -pref.height), pref));
            panel.doLayout();

            mMaxSizes.put(bar, bar.getMaximumSize());
            mMinSizes.put(bar, bar.getMinimumSize());

            return panel;
//            return cframe;
        }

        public NSToolbarItem toolbar_itemForItemIdentifier_willBeInsertedIntoToolbar(final NSToolbar toolbar, String identifier, boolean insert) {
            // NOTE: We are now on the AppKit/Main thread, be careful...
            System.out.println("itemForIdentifier: " + identifier + " insert: " + insert);
//            System.out.println("toolbar.customizationPaletteIsRunning: " + toolbar.customizationPaletteIsRunning());

            if (!insert) {
                NSToolbarItem item = Rococoa.create("NSToolbarItem", NSToolbarItem.class).initWithItemIdentifier(identifier);

                String label = Character.toUpperCase(identifier.charAt(0)) + identifier.substring(1);
                item.setLabel(label);
                item.setPaletteLabel(label);
                item.setImage(mImages.get(identifier));
//                Panel component = (Panel) mImages.get(identifier);
                Container component = (Container) mComponents.get(identifier);
//
//                syncComponentVisibility(component, true);
//
//                NSView view = Rococoa.wrap(ID.fromLong(Native.getComponentID(component)), NSView.class);
//                item.setView(view);

                Dimension max = mMaxSizes.get(component.getComponent(0));
                if (max != null) {
                    item.setMaxSize(new NSSize(max));
                }
                Dimension min = mMinSizes.get(component.getComponent(0));
                if (min != null) {
                    item.setMinSize(new NSSize(min));
                }


                return item;
            }

            NSToolbarItem item = mItems.get(identifier);
            if (item == null) {
                item = Rococoa.create("NSToolbarItem", NSToolbarItem.class).initWithItemIdentifier(identifier);
                mItems.put(identifier, item);

                String label = Character.toUpperCase(identifier.charAt(0)) + identifier.substring(1);
                item.setLabel(label);
                item.setPaletteLabel(label);

                item.setAutovalidates(false);
                item.setEnabled(true);

//                Panel component = (Panel) mComponents.get(identifier);
                Container component = (Container) mComponents.get(identifier);

                syncComponentVisibility(component, true);

                NSView view = Rococoa.wrap(ID.fromLong(Native.getComponentID(component)), NSView.class);
                item.setView(view);

                // TODO: Set action to forward to the swing action
//                Component child = ((Container) component).getComponent(0);
//                System.out.println("child: " + child);
//                if (child instanceof AbstractButton) {
//                    System.out.println("Yeas");
//                    AbstractButton button = (AbstractButton) child;
//                    Action action = button.getAction();
//                    if (action != null) {
//                        System.out.println("OH yeah");
                // NOTE: Docs says something about setAction being forwarded to the view "if it responds"...
//                        NSObject proxy = Rococoa.proxy(action);
//                        item.setTarget(proxy.id());
//                        item.setAction(Foundation.selector("actionPerformed")); // TODO: Probably better to invoke a separate method, to get event/source correct
//                    }
//                }

                // TODO: Update these if changes?
                // Set max/min size according to component (easy, except for deadlocks.. )
//            Dimension max = component.getMaximumSize();
                Dimension max = mMaxSizes.get(component.getComponent(0));
                if (max != null) {
                    item.setMaxSize(new NSSize(max));
                }
//            Dimension min = component.getMinimumSize();
                Dimension min = mMinSizes.get(component.getComponent(0));
                if (min != null) {
                    item.setMinSize(new NSSize(min));
                }

                // TODO: Make sure we re-layout the component if more/less space is available?
                // - We could do that based on window size change
            }

            return item;
        }

        private NSImage createNSImage(final JComponent pComponent) {
//        private Component createNSImage(final JComponent pComponent) {
            final ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            System.out.println("component: " + pComponent);
//            Dimension size = pComponent.getPreferredSize();
//            System.out.println("pref: " + size);
//            BufferedImage image = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
            BufferedImage image = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
            pComponent.setBounds(0, 0, image.getWidth(), image.getHeight());
            Graphics2D g = image.createGraphics();
            try {
//                System.out.println("Painting...");
                pComponent.paint(g);
//                System.out.println("painted");
//                g.setColor(Color.RED);
//                g.drawRect(0, 0, pref.width - 1, pref.height - 1);
//                g.drawLine(0, 0, pref.width, pref.height);
            }
            finally {
                g.dispose();
            }

//    Panel panel = new MyPanel(new BorderLayout(0, 0));
//    panel.add(new JLabel(new ImageIcon(image)));
//    panel.setVisible(false);
//    mFrame.getLayeredPane().add(panel, JLayeredPane.FRAME_CONTENT_LAYER);
//    Dimension pref = panel.getPreferredSize();
//    panel.setBounds(new Rectangle(new Point(0, -pref.height), pref));
//    panel.doLayout();
//    return panel;
            try {
                ImageIO.write(image, "PNG", stream);
            }
            catch (IOException ignore) {
            }
            NSData data = NSData.CLASS.dataWithBytes_length(stream.toByteArray(), stream.size());
            return Rococoa.create("NSImage", NSImage.class).initWithData(data);
        }

        private Component componentForIdentifier(final NSNotification notification) {
            NSDictionary dict = notification.userInfo();
            NSToolbarItem item = Rococoa.cast(dict.objectForKey("item"), NSToolbarItem.class);
            return mComponents.get(item.itemIdentifier());
        }

        private void syncComponentVisibility(final Component component, final boolean visible) {
            // Component will be null for any standard item, like space, separator etc
            if (component == null) {
                return;
            }

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    component.setVisible(visible);
                }
            });
        }

        public void toolbarWillAddItem(final NSNotification notification) {
//            System.out.println("TestApplicationMenu$ToolbarDelegate.toolbarWillAddItem: " + notification.description());
            // Show component
            syncComponentVisibility(componentForIdentifier(notification), true);
        }

        public void toolbarDidRemoveItem(final NSNotification notification) {
//            System.out.println("TestToolbar$ToolbarDelegate.toolbarDidRemoveItem: " + notification.description());
            // Hide component
            syncComponentVisibility(componentForIdentifier(notification), false);
        }

        public NSArray toolbarAllowedItemIdentifiers(NSToolbar toolbar) {
            return NSArray.CLASS.arrayWithObjects(
                    NSString.stringWithString("foo"),
                    NSString.stringWithString("baz"),
                    NSString.stringWithString("bar"),
                    // Standard items: separator, space and flexible space
                    NSToolbarItem.NSToolbarSeparatorItemIdentifier,
                    NSToolbarItem.NSToolbarSpaceItemIdentifier,
                    NSToolbarItem.NSToolbarFlexibleSpaceItemIdentifier,
                    null
            );
//            return NSArray.CLASS.arrayWithObjects(null);
        }

        public NSArray toolbarDefaultItemIdentifiers(NSToolbar toolbar) {
            return NSArray.CLASS.arrayWithObjects(
                    NSString.stringWithString("foo"),
                    // Flexible space here by default
//                    NSToolbarItem.NSToolbarSpaceItemIdentifier,
//                    NSToolbarItem.NSToolbarFlexibleSpaceItemIdentifier,
//                    NSString.stringWithString("bar"),
                    null
            );
//            return NSArray.CLASS.arrayWithObjects(null);
        }

        public NSArray toolbarSelectableItemIdentifiers(NSToolbar toolbar) {
            return NSArray.CLASS.arrayWithObjects(
                    (NSObject[]) null
            );
//            return NSArray.CLASS.arrayWithObjects(null);
        }

        private static class MyPanel extends Panel {
            protected NSView mView;

            public MyPanel(final LayoutManager layout) {
                super(layout);
            }

//            @Override
//            public void paint(Graphics g) {
////                    // TODO: Avoid this...
////                    ((Graphics2D) g).setPaint(new GradientPaint(0, 0, new Color(0xb2b2b2), 0, 35, new Color(0x949494)));
////                    g.fillRect(0, 0, getWidth(), 35);
//                super.paint(g);
//            }

            @Override
            public void addNotify() {
                super.addNotify();
                mView = Rococoa.wrap(ID.fromLong(Native.getComponentID(this)), NSView.class);

                RootPaneContainer window = (RootPaneContainer) SwingUtilities.getWindowAncestor(this);
                window.getRootPane().addComponentListener(new ComponentAdapter() {
                    @Override
                    public void componentResized(ComponentEvent e) {
                        // The underlying view now has it's new size, but we have to reflect this to this component...
                        final NSViewOffMain view = Rococoa.cast(mView, NSViewOffMain.class);
                        Rectangle2D rect = view.bounds().getBounds();
                        System.out.println("rect: " + rect);
                        final int newWidth = (int) rect.getWidth();

                        // NOTE: javaToolbarHeight includes frame title height in addition to toolbar height
                        Window window = SwingUtilities.getWindowAncestor(MyPanel.this);
                        Container contentPane = ((RootPaneContainer) window).getContentPane();
                        System.out.println("javaToolbarHeight: " + (window.getHeight() - contentPane.getHeight()));

                        final Rectangle bounds = getBounds();
                        if (bounds.width != newWidth) {
//                            SwingUtilities.invokeLater(new Runnable() {
                            DISPATCHER.execute(new Runnable() {
                                 public void run() {
                                    System.err.println("Here!");

                                    int toolbarHeight = getToolbarHeight(view.window());
                                    System.out.println("toolbarHeight: " + toolbarHeight);

                                    System.out.println("newWidth: " + newWidth);
                                    System.out.println("bounds: " + bounds);

                                     // -- It could be that we run into an issue with OS X/Cocoa/NextStep bottom-left origin..

                                    // TODO: Figure out how to set the y-value, so that the component doesn't jump...
                                    // (We might however, exploit this to get rid of the excess borders...)
                                    // TODO: This is foobar, when in text + icon mode,
//                                    bounds.y = - (toolbarHeight - 17); // Where on earth does this value come from/what sense does it make?!
//                                    bounds.y = -19; // Where on earth does this value come from/what sense does it make?!
//                                    bounds.y = -5; // Where on earth does this value come from/what sense does it make?!
                                    bounds.y = toolbarHeight - (bounds.height * 2);
                                    bounds.y += bounds.y < -10 ? 1 : -1; // :-P

                                    // Text + icon: 52 => -5 (text under...)
                                    // Icon only:   36 => -19
                                    bounds.width = newWidth;
                                    setBounds(bounds); // TODO: How could this possibly move the component? :-P
                                    // This should make a repaint...
                                }
                            });
                        }
                    }
                });
            }

            @Override
            public void removeNotify() {
                mView = null;
                super.removeNotify();
            }

            @Override
            public void update(Graphics g) {
                paint(g);
            }

            @Override
            public void repaint(final long tm, final int x, final int y, final int width, final int height) {
                // TODO: This is needed, only for components with non-opaque children...
//                    super.repaint(tm, x, y, width, height);
                // NOTE: We don't need to care about tm, as it's the maximum time for a repaint, and we do it now

                // NOTE: Can't test for needsDisplay here, as there could be a different rect
                // However, it could maybe be possible to queue up repaints...
                NSViewOffMain view = Rococoa.cast(mView, NSViewOffMain.class);
                view.setNeedsDisplayInRect(new NSRect(x, y, width, height));
            }

            /// HACK: Very, very experimental...
//                @Override
//                public Container getParent() {
//                    return mFrame;
//                }
        }
    }

    private static int getToolbarHeight(final NSWindow window) {
        if (window == null || window.id().isNull()) { // Rococoa weirdness
            return 0;
        }

        final AtomicInteger height = new AtomicInteger(0);

        Foundation.runOnMainThread(new Runnable() {
            public void run() {
                NSToolbar toolbar = window.toolbar();
                if (!(toolbar == null || toolbar.id().isNull()) && toolbar.isVisible()) {
                    NSRect windowFrame = NSWindow.CLASS.contentRectForFrameRect_styleMask(window.frame(), window.styleMask());

                    height.set(windowFrame.size.height.intValue() - window.contentView().frame().size.height.intValue());
                }
            }
        });

        return height.intValue();
    }

    // TODO: Figure out how to solve this better in Rococoa, maybe have @RunOnMainThread on methods, rather than class (or not at all?)
    private static interface NSViewOffMain extends NSObject {
        void setNeedsDisplayInRect(NSRect pNSRect);
        NSRect bounds();

        NSWindow window();
    }
}