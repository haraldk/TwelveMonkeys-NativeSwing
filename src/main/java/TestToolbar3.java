import com.twelvemonkeys.spice.osx.appkit.*;
import com.twelvemonkeys.spice.osx.appkit.NSImage;
import com.twelvemonkeys.spice.osx.appkit.NSNotification;
import com.twelvemonkeys.spice.osx.foundation.NSRect;
import org.rococoa.Foundation;
import org.rococoa.NSObject;
import org.rococoa.Rococoa;
import org.rococoa.cocoa.foundation.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TestToolbar3
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: TestToolbar3.java,v 1.0 Mar 21, 2008 8:23:14 PM haraldk Exp$
 */
public class TestToolbar3 {
    // A third thread is introduced, to avoid deadlocks between AppKit and AWT... Probably not the "right" way..
    private static final ExecutorService DISPATCHER = Executors.newSingleThreadExecutor();

    // TODO: Completely new approach, instead of JComponent, stick to NSView subclasses on the NSToolbarItems.
    //       - API for adding actions to the toolbar
    //       - API for adding groups of actions
    //          - Handle toggling... Radio-style (mutually exclusive) or checkbox style (single toggle)
    //       - API for creating search box
    //       - API for adding flexible or rigid space and separators
    //       - API for adding "tracking space" (as in Mail)

    public static void main(String[] pArgs) {
        System.setProperty("apple.laf.useScreenMenuBar", "true");

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                NSAutoreleasePool pool = NSAutoreleasePool.new_();
                
                final JFrame frame = new JFrame("JFrame with NSToolbar");
//                frame.getRootPane().putClientProperty("apple.awt.brushMetalLook", Boolean.TRUE);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                JLabel label = new JLabel("Hello Toolbar", JLabel.CENTER) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
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

//                frame.setVisible(true);

                final NSWindow window = NSWindow.CLASS.windowFromAWT(frame);
//                window.setAutorecalculatesContentBorderThickness_forEdge(false, NSWindow.NSMaxYEdge);
//                window.setAutorecalculatesContentBorderThickness_forEdge(false, NSWindow.NSMinYEdge);
//                System.err.println("window.contentBorderThicknessForEdge(NSWindow.NSMinYEdge): " + window.contentBorderThicknessForEdge(NSWindow.NSMinYEdge));
//                window.setContentBorderThickness_forEdge(new CGFloat(20), NSWindow.NSMinYEdge);
//                System.err.println("window.contentBorderThicknessForEdge(NSWindow.NSMinYEdge): " + window.contentBorderThicknessForEdge(NSWindow.NSMinYEdge));
//                System.err.println("window.contentBorderThicknessForEdge(NSWindow.NSMinYEdge): " + window.contentBorderThicknessForEdge(NSWindow.NSMaxYEdge));
//                window.setContentBorderThickness_forEdge(new CGFloat(0), NSWindow.NSMaxYEdge);
//                System.err.println("window.contentBorderThicknessForEdge(NSWindow.NSMinYEdge): " + window.contentBorderThicknessForEdge(NSWindow.NSMaxYEdge));

//                DISPATCHER.execute(new Runnable() {
//                    public void run() {
                        // Create the toolbar, so far, so good...
                        NSToolbar toolbar = NSToolbar.CLASS.initWithIdentifier(getClass().getName() + ".toolbar");
                        final NSObject delegate = Rococoa.proxy(toolbarDelegate);
                        toolbar.setDelegate(delegate.id());
                        toolbar.setDisplayMode(NSToolbar.NSToolbarDisplayModeIconOnly);
//                        toolbar.setDisplayMode(NSToolbar.NSToolbarDisplayModeDefault);

                        toolbar.setAllowsUserCustomization(true); // Needed to get right-click menu

                        // Attach the toolbar to the frame
                        window.setToolbar(toolbar);

//                        SwingUtilities.invokeLater(new Runnable() {
//                            @Override
//                            public void run() {
                                frame.getRootPane().putClientProperty(getClass().getName() + ".toolbarDelegate", delegate); // Keep the delegate from being CG'ed
                                frame.setVisible(true);
//                            }
//                        });



//                window.setShowsToolbarButton(false); // Default is show
//                    }
//                });

            }
        });
    }

//    public static String getAppKitGlobalString(String globalVarName) {
//        return Foundation.toString(ID.fromLong(NativeLibrary.getInstance("AppKit").getGlobalVariableAddress(globalVarName).getNativeLong(0).longValue()));
//    }

    public static class ToolbarDelegate {
        private final JFrame mFrame;

        private final Map<String, Component> mComponents = new HashMap<String, Component>();
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
        }

        public NSToolbarItem toolbar_itemForItemIdentifier_willBeInsertedIntoToolbar(final NSToolbar toolbar, String identifier, boolean insert) {
            // NOTE: We are now on the AppKit/Main thread, be careful...
            System.out.println("itemForIdentifier: " + identifier + " insert: " + insert);
//            System.out.println("toolbar.customizationPaletteIsRunning: " + toolbar.customizationPaletteIsRunning());

            NSToolbarItem item = mItems.get(identifier);
            if (item == null) {
                item = Rococoa.create("NSToolbarItem", NSToolbarItem.class).initWithItemIdentifier(identifier);
                mItems.put(identifier, item);

                String label = Character.toUpperCase(identifier.charAt(0)) + identifier.substring(1);
                item.setLabel(label);
                item.setPaletteLabel(label);

                item.setAutovalidates(false);
                item.setEnabled(true);

                if ("search".equals(identifier)) {
                    NSSearchField s = Rococoa.create("NSSearchField", NSSearchField.class);
                    item.setView(s);
                    item.setMinSize(new NSSize(64, 22));
                    item.setMaxSize(new NSSize(192, 22));
                }
                else if ("baz".equals(identifier)) {
                    NSSegmentedControl segmented = Rococoa.create("NSSegmentedControl", NSSegmentedControl.class);

                    segmented.setSegmentStyle(NSSegmentedControl.NSSegmentStyleTexturedRounded);
//                    segmented.setSegmentStyle(NSSegmentedControl.NSSegmentStyleSmallSquare);
                    segmented.setSegmentCount(3);

                    segmented.setImageForSegment(new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB), 0);
                    segmented.setImageForSegment(new BufferedImage(6, 6, BufferedImage.TYPE_INT_RGB), 1);
                    segmented.setImageForSegment(new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB), 2);

                    NSSegmentedCell cell = segmented.cell();
                    cell.setTrackingMode(NSSegmentedCell.NSSegmentSwitchTrackingSelectOne);
//                    cell.setTrackingMode(NSSegmentedCell.NSSegmentSwitchTrackingSelectAny);

//                    segmented.setSelectedForSegment(true, 0);
//                    segmented.setSelectedForSegment(true, 1);

                    AbstractAction action = new AbstractAction(identifier) {
                        // TODO: Move action to proxy/wrapper
                        public final void performAction() {
                            // TODO: Is it ok to invokeLater?? Should ideally be invokeAndWait, but could deadlock...
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    actionPerformed(new ActionEvent(ToolbarDelegate.this, -1, (String) getValue(Action.NAME)));
                                }
                            });
                        }

                        public void actionPerformed(ActionEvent e) {
                            System.err.println("TestToolbar3$ToolbarDelegate.actionPerformed: " + e);
                        }
                    };

                    NSObject proxy = Rococoa.proxy(action);
                    proxy.retain();

                    segmented.setTarget(proxy.id());
                    segmented.setAction(Foundation.selector("performAction"));


                    /*
                    // TODO: Selected icons
                    // TODO; Mixed mode selection? Multi & toggle + push/momentary: Seems not possible for NSSegmentStyleTexturedRounded

                    // TODO: Handle actions:
                    - (void)awakeFromNib
{
    [segControl setSegmentCount:3];
    [[segControl cell] setTag:0 forSegment:0];
    [[segControl cell] setTag:1 forSegment:1];
    [[segControl cell] setTag:2 forSegment:2];
    [segControl setTarget:self];
    [segControl setAction:@selector(segControlClicked:)];
}

- (IBAction)segControlClicked:(id)sender
{
    int clickedSegment = [sender selectedSegment];
    int clickedSegmentTag = [[sender cell] tagForSegment:clickedSegment];
    //...
}
                     */

                    segmented.sizeToFit();

                    item.setView(segmented);
                }
                else {
                    NSButton b = Rococoa.create("NSButton", NSButton.class);
                    b.setTitle(label);
//                    b.setImage(new BufferedImage(8, 8, BufferedImage.TYPE_INT_RGB));
                    b.setBezelStyle(NSButtonCell.NSTexturedRoundedBezelStyle);
                    b.sizeToFit();

                    AbstractAction action = new AbstractAction(identifier) {
                        // TODO: Move action to proxy/wrapper
                        public final void performAction() {
                            // TODO: Is it ok to invokeLater?? Should ideally be invokeAndWait, but could deadlock...
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    actionPerformed(new ActionEvent(ToolbarDelegate.this, -1, (String) getValue(Action.NAME)));
                                }
                            });
                        }

                        public void actionPerformed(ActionEvent e) {
                            System.err.println("TestToolbar3$ToolbarDelegate.actionPerformed: " + e);
                        }
                    };

                    NSObject proxy = Rococoa.proxy(action);
                    proxy.retain();

                    b.setTarget(proxy.id());
                    b.setAction(Foundation.selector("performAction"));
                    item.setView(b);
                }
            }

            return item;
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
                    NSString.stringWithString("bar"),
                    NSString.stringWithString("baz"),
                    NSString.stringWithString("search"),
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
                    NSString.stringWithString("baz"),
                    // Flexible space here by default
//                    NSToolbarItem.NSToolbarSpaceItemIdentifier,
                    NSToolbarItem.NSToolbarFlexibleSpaceItemIdentifier,
                    NSString.stringWithString("search"),
                    null
            );
//            return null;
        }

        public NSArray toolbarSelectableItemIdentifiers(NSToolbar toolbar) {
            return NSArray.CLASS.arrayWithObjects(
                    (NSObject[]) null
            );
//            return NSArray.CLASS.arrayWithObjects(
//                    NSString.stringWithString("search"),
//                    null
//            );
//            return NSArray.CLASS.arrayWithObjects(null);
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