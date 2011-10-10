package com.twelvemonkeys.spice;

import apple.awt.CEmbeddedFrame;
import com.sun.jna.Native;
import com.twelvemonkeys.spice.osx.appkit.*;
import com.twelvemonkeys.spice.osx.foundation.NSRect;
import org.rococoa.*;
import org.rococoa.cocoa.foundation.NSArray;
import org.rococoa.cocoa.foundation.NSNotification;
import org.rococoa.cocoa.foundation.NSURL;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.lang.reflect.*;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.*;

/**
 * OS X FileChooserDelegate implementation.
 *
 * Known issues:
 * <ul>
 * <li>Does not support fully support Apple client property "JFileChooser.appBundleIsTraversable" (treated as
 *  "JFileChooser.packageIsTraversable").</li>
 * <li>Currently very limited accessory view support.</li>
 * </ul>
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: OSXFileChooserDelegate.java,v 1.0 Mar 23, 2008 9:45:21 PM haraldk Exp$
 */
final class OSXFileChooserDelegate implements FileChooserDelegate {
    // Many thanks: 
    // - Andrew Thompson for all the great input
    // - The people on the users@rococoa.dev.java.net list 
    // - Duncan McGregor & Timothy Wall for making it all possible

    // Static compiler hack to allow us to run on Java 5 without modality support 
    private static final boolean IS_JAVA_6 = isJava6();

    private static boolean isJava6() {
        try {
            Class<?> dialogClass = Class.forName("java.awt.Dialog");
            Class<?>[] declaredClasses = dialogClass.getDeclaredClasses();
            for (Class<?> declaredClass : declaredClasses) {
                if (declaredClass.getSimpleName().equals("ModalityType")) {
                    return true;
                }
            }
        }
        catch (Throwable ignore) {
        }

        return false;
    }

    enum Modality {
        MODELESS,
        DOCUMENT_MODAL,
        APPLICATION_MODAL,
        TOOLKIT_MODAL
    }

    // TODO: This should ideally depend upon modality, however there's no standard way to set modality of a JFileChooser
    public final static String PROPERTY_MODALITY_TYPE = "JFileChooser.modalityType";

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    // TODO: Allow modal/non-modal? See Dialog.isModal/setModal, should be possible now with the delegate stuff

    // TODO: AccessoryView (might just be possible)

    // TODO: Support fine-tuning through client properties
    // - Create dir in file open/not in file save
    // - Message
    // - Labels
    // ...
    OSXFileChooserDelegate() {
    }

    public int showDialog(final JFileChooser chooser, final Component parent, final String approveButtonText) {
        int result;

        final Modality modality = getModality(chooser);

        // NOTE: JFileChooser treats approveButtonText != null to mean DialogType == CUSTOM
        if (approveButtonText == null && chooser.getDialogType() == JFileChooser.SAVE_DIALOG) {
            final NSSavePanel save = NSSavePanel.savePanel();
            configureBeforeRun(chooser, save);

            result = runDialog(chooser, parent, modality, save);

            if (result == NSSavePanel.NSOKButton) {
                File file = new File(save.filename());
                chooser.setSelectedFiles(new File[]{file});
            }

        }
        else {
            final NSOpenPanel open = NSOpenPanel.openPanel();
            configureBeforeRun(chooser, open);
            open.setAllowsMultipleSelection(chooser.isMultiSelectionEnabled());
            open.setCanChooseDirectories(chooser.isDirectorySelectionEnabled());
            open.setCanChooseFiles(chooser.isFileSelectionEnabled());

            if (approveButtonText != null) {
                open.setPrompt(approveButtonText);
                // Set more options?
            }

            result = runDialog(chooser, parent, modality, open);

            if (result == NSSavePanel.NSOKButton) {
                NSArray names = open.filenames();

//                System.err.println("names: " + names);

                File[] files = new File[names.count()];
                for (int i = 0; i < files.length; i++) {
                    files[i] = new File(names.objectAtIndex(i).toString());
                }

                // WORKAROUND: New AquaUI selection listener does weird things, causing the selection to change on
                // setSelectedFiles, again causing the selected files to be reset to first file in array...
                // First setting the selected file to first in array, avoids selection change, working around this
                // issue.
                chooser.setSelectedFile(files.length > 0 ? files[0] : null);
                chooser.setSelectedFiles(files);
            }
        }

        return result == NSSavePanel.NSOKButton ? JFileChooser.APPROVE_OPTION : JFileChooser.CANCEL_OPTION;
    }

    private Modality getModality(final JFileChooser chooser) {
        if (IS_JAVA_6) {
            // TODO: Support Dialog.ModalityType.MODELESS and TOOLKIT_MODAL?!
            Dialog.ModalityType modality = (Dialog.ModalityType) chooser.getClientProperty(PROPERTY_MODALITY_TYPE);
            if (modality == Dialog.ModalityType.DOCUMENT_MODAL) {
                return Modality.DOCUMENT_MODAL;
            }
        }

        return Modality.APPLICATION_MODAL;
    }

    private int runDialog(final JFileChooser chooser, Component parent, final Modality pModality, final NSSavePanel pSave) {
        final Window window = getWindowAncestor(parent);

        final JMenuBar menuBar = window instanceof JFrame ? ((JFrame) window).getJMenuBar() : new JMenuBar();
        boolean wasEnabled = menuBar.isEnabled();
        try {
            menuBar.setEnabled(false);

            final Future<Integer> future = executor.submit(new Callable<Integer>() {
                public Integer call() throws Exception {
                    if (pModality == Modality.DOCUMENT_MODAL) {
                        ModalDelegate delegate = new ModalDelegate();
                        NSObject proxy = Rococoa.proxy(delegate);
                        pSave.beginSheet(
                                chooser.getCurrentDirectory(), chooser.getSelectedFile(), window,
                                proxy.id(), Foundation.selector("panelDidEnd:returnCode:"), null
                        );

                        return delegate.getResult();
                    }
                    else /*Dialog.ModalityType.APPLICATION_MODAL || Dialog.ModalityType.TOOLKIT_MODAL */ {
                        return pSave.runModal(chooser.getCurrentDirectory(), chooser.getSelectedFile());
                    }
                }
            });

            // NOTE: The event pump seems to stall the EDT until new events occurs, so we'll keep it alive...
            Timer timer = new Timer(100, null);
            timer.setRepeats(true);

            // Block until dialog is done
            try {
                timer.start();

                new EventPump(new Conditional() {
                    public boolean evaluate() {
                        return !future.isDone();
                    }
                }, chooser).start();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
            finally {
                timer.stop();
            }

            // The future should now be done, so just get the result
            return future.get();
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        finally {
            menuBar.setEnabled(wasEnabled);
        }
    }

    static Window getWindowAncestor(final Component pComponent) {
        Component component = pComponent;
        Container parent;

        while (component != null && (parent = component.getParent()) != null) {
            if (parent instanceof JPopupMenu) {
                JPopupMenu menu = (JPopupMenu) parent;
                component = menu.getInvoker();
            }
            else {
                component = parent;
            }
        }

        return component instanceof Window ? (Window) component : SwingUtilities.getWindowAncestor(component);
    }


    private void configureBeforeRun(final JFileChooser chooser, final NSSavePanel panel) {
        panel.setTitle(chooser.getDialogTitle());
        panel.setFrameAutosaveName(chooser.getName());
        panel.setPrompt(chooser.getApproveButtonText());

        // Apple LAF legacy support
        panel.setTreatsFilePackagesAsDirectories(treatsFilePackageAsDir(chooser));


        panel.setCanCreateDirectories(chooser.getDialogType() == JFileChooser.SAVE_DIALOG);

        // TODO: Better way of keeping delegates from being GC'ed...
        // Prevent the delegate from being GC'ed by adding as a client property
        NSObject delegate = (NSObject) chooser.getClientProperty("SavePanelDelegate");
        if (delegate == null) {
            delegate = Rococoa.proxy(new SavePanelDelegate(chooser));
            chooser.putClientProperty("SavePanelDelegate", delegate);
        }
        panel.setDelegate(delegate.id());

        // TODO: Is there a way to get Swing components to paint on the native stuff? More deadlock possibilities... :-P


        // TODO: Keep native and Swing in sync with listeners...

        final FileFilter[] filters = chooser.getChoosableFileFilters();
        if (filters.length > 1) {
            NSArray topLevel = loadTopLevelObjectsFromNIB("/com/twelvemonkeys/spice/osx/OpenSavePanelAccessories.nib");
//            System.out.println("topLevel: " + topLevel);

            // TODO: Figure out ordering/querying of top level objects, for now, use NSView
            for (int i = 0; i < topLevel.count(); i++) {
                NSObject object = topLevel.objectAtIndex(i);
                if (object.isKindOfClass(Rococoa.createClass("NSView", NSClass.class))) {
                    NSView view = Rococoa.cast(object, NSView.class);
//                    System.out.println("accessory: " + view);
                    panel.setAccessoryView(view);
                    break;
                }
            }

            NSView accessory = panel.accessoryView();

            addAuxillaryComponent(accessory);
            //Rococoa.cast(accessory.subviews().objectAtIndex(1), NSView.class).setAutoresizingMask(new NSUInteger(1 |  2));
            //accessory.setAutoresizingMask(new NSUInteger(2));
//            accessory.superview().setAutoresizesSubviews(true);
//            accessory.setAutoresizesSubviews(true);
//            accessory.setAutoresizingMask(new NSUInteger(2 | 16));
//            System.out.println("panel.accessoryView().subviews(): " + accessory.subviews());

            NSTextField label = Rococoa.cast(accessory.subviews().objectAtIndex(1), NSTextField.class);
            label.setTitleWithMnemonic("Enable:");
            //label.sizeToFit();
            //accessory.sizeToFit();

            NSView view = Rococoa.cast(accessory.subviews().objectAtIndex(2), NSView.class);

//            view.setHidden(false);
//            System.out.println(view.isHidden());
//            view.setHidden(0);
//            System.out.println(view.isHidden());
//            view.setHidden(true);
//            System.out.println(view.isHidden());
//            view.setHidden(1);
//            System.out.println(view.isHidden());

            view.setHidden(true);

            final NSPopUpButton popup = Rococoa.cast(accessory.subviews().objectAtIndex(0), NSPopUpButton.class);
            popup.removeAllItems();

            int idx = 0;
            int separatorIndex = -1;
            for (FileFilter filter : filters) {
                // TODO: Always add the all filter first, if present? Seems to always be the case though...

                popup.addItemWithTitle(filter.getDescription());
                if (filter.equals(chooser.getFileFilter())) {
                    popup.selectItemAtIndex(idx);
                }

                if (filter == chooser.getAcceptAllFileFilter() && idx == 0) {
                    popup.menu().addItem(NSMenuItem.separatorItem());
                    idx++;
                    separatorIndex = idx;
                }

                idx++;
            }

            final int separator = separatorIndex;
            NSObject actionTarget = Rococoa.proxy(new Object() {
                public void actionPerformed(ID id) {
                    int index = popup.indexOfSelectedItem();
                    if (separator != -1 && index >= separator) {
                        index--;
                    }

                    final int currentIndex = index;
                    try {
                        SwingUtilities.invokeAndWait(new Runnable() {
                            public void run() {
                                chooser.setFileFilter(filters[currentIndex]);
                            }
                        });
                    }
                    catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    catch (InvocationTargetException e) {
                        // Should never happen
                        Throwable cause = e.getCause();
                        if (cause instanceof Error) {
                            throw (Error) cause;
                        }
                        if (cause instanceof RuntimeException) {
                            throw (RuntimeException) cause;
                        }

                        throw new UndeclaredThrowableException(cause);
                    }

                    panel.validateVisibleColumns();
                }
            });
            chooser.putClientProperty("PopUpDelegate", actionTarget);

            popup.setTarget(actionTarget.id());
            popup.setAction(Foundation.selector("actionPerformed:"));

//            System.out.println("accessory.frame(): " + accessory.frame());
//            System.out.println("accessory.bounds(): " + accessory.bounds());
//
//            System.out.println("NativeLong.SIZE: " + NativeLong.SIZE);

            popup.sizeToFit();
  
//            final NSPopUpButton popup = Rococoa.create("NSPopUpButton", NSPopUpButton.class);
//
//            int i = 0;
//            for (FileFilter filter : filters) {
//                // TODO: Always add the all filter first, if present?
//                // TODO: Separator between all filter and others?
//
//                popup.addItemWithTitle(filter.getDescription());
//                if (filter.equals(chooser.getFileFilter())) {
//                    popup.selectItemAtIndex(i);
//                }
//                i++;
//            }
//
//            NSObject actionTarget = Rococoa.proxy(new Object() {
//                public void actionPerformed(ID id) {
//                    chooser.setFileFilter(filters[popup.indexOfSelectedItem()]);
//                    panel.validateVisibleColumns();
//                }
//            });
//            chooser.putClientProperty("PopUpDelegate", actionTarget);
//
//            popup.setTarget(actionTarget.id());
//            popup.setAction(Foundation.selector("actionPerformed:"));
//            popup.sizeToFit();
//
//            NSTextField label = Rococoa.create("NSTextField", NSTextField.class);
//            label.setEditable(false);
//            label.setSelectable(false);
//            label.setBordered(false);
//            label.setDrawsBackground(false);
//
//            label.setTitleWithMnemonic("Enable: ");
//            label.sizeToFit();
//
//            popup.setFrameOrigin(new NSPoint((float) label.bounds().size.width, 0));
//            float w = (float) (label.bounds().size.width + popup.bounds().size.width);
//            float h = (float) Math.max(label.bounds().size.height, popup.bounds().size.height);
////            popup.setFrameOrigin(new NSPoint(label.bounds().size.width.floatValue(), 0));
////            float w = label.bounds().size.width.floatValue() + popup.bounds().size.width.floatValue();
////            float h = Math.max(label.bounds().size.height.floatValue(), popup.bounds().size.height.floatValue());
//            label.setFrameSize(new NSSize((float) label.bounds().size.width, 22f)); // TODO: Avoid hardcoding...
//
//            NSView accessory = Rococoa.create("NSView", NSView.class, "alloc").initWithFrame(new NSRect(w, h));
//
//            accessory.addSubview(label);
//            accessory.addSubview(popup);
//
////            System.out.println("label.bounds(): " + label.bounds());
////            System.out.println("label.frame(): " + label.frame());
////            System.out.println("popup.bounds(): " + popup.bounds());
////            System.out.println("popup.frame(): " + popup.frame());
//            panel.setAccessoryView(accessory);
        }
    }

    private static boolean treatsFilePackageAsDir(JFileChooser chooser) {
        String pkgAsDir = (String) chooser.getClientProperty("JFileChooser.packageIsTraversable");
        String appAsDir = (String) chooser.getClientProperty("JFileChooser.appBundleIsTraversable");
        // TODO: How can we differentiate the two?
        return "always".equalsIgnoreCase(pkgAsDir) || "always".equalsIgnoreCase(appAsDir);
    }

    private void addAuxillaryComponent(final NSView pAccessory) {
        // TODO: Invert test to show
        if (false || pAccessory == null) {
            return;
        }

        // TODO: This currently very, very experimental code is a vague PoC:
        // - Only heavy-weight components are displayed
        AbstractAction helloAction = new AbstractAction("Hello Swing") {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Hello Swing");
            }
        };


//        JFrame frame = new JFrame("Jahoo");
//        Window frame = new Window(null);
//        NSView subview = Rococoa.create("NSControl", NSControl.class);
//        pAccessory.addSubview(subview);
//        Window frame = new CEmbeddedFrame(subview.id().longValue());
        NSControl accessoryView = Rococoa.cast(pAccessory.subviews().objectAtIndex(2), NSControl.class);
        Window frame = new CEmbeddedFrame(accessoryView.id().longValue());
//        accessoryView.setAutoresizingMask(new NSUInteger(2 | 16));

        Panel p = new MyPanel(new BorderLayout(0, 0));

//        Panel p = new Panel(new BorderLayout(0, 0)) {
//            @Override
//            public void update(Graphics g) {
//                paint(g);
//            }
//        };
//        JPanel p = new JPanel(new BorderLayout(0, 0));
//        p.setOpaque(false);

        frame.add(p);

        JButton button = new JButton(helloAction);
        button.setFocusable(false); // TODO: Make sure ALL components added are non-focusable at the moment, as focus crashes...
//        JComboBox button = new JComboBox(new Object[] {"one", "two", "three"});
        button.setOpaque(false);
        p.add(button);
//        Button b = new Button("Hello Java");
//        b.addActionListener(helloAction);
//        p.add(b);

//        frame.setUndecorated(true);
        //frame.setBounds(0, 0, 0, 0);
        //frame.setVisible(true);
        //NSView subview = Rococoa.wrap(ID.fromLong(Native.getComponentID(frame)), NSView.class);
        //NSView subview = Rococoa.wrap(ID.fromLong(Native.getWindowID(frame)), NSView.class);
        //frame.setVisible(false);
        frame.pack();

//        System.out.println("frame.getInsets(): " + frame.getInsets());
//        System.out.println("frame.getPreferredSize(): " + frame.getPreferredSize());

//        NSRect bounds = new NSRect(frame.getContentPane().getBounds());
        Rectangle rectangle = new Rectangle(frame.getPreferredSize());
//        frame.setBounds(rectangle);
        NSRect bounds = new NSRect(rectangle);
//        subview.setBounds(bounds);

        //accessoryView.sizeToFit();
        //pAccessory.addSubview(subview);
    }

    // TODO: Instead try to instantiate the NIB with owner, like mentioned by AndyT
    private NSArray loadTopLevelObjectsFromNIB(final String pNIBName) {
        // Load the UI from a NIB-file
        URL uri = getClass().getResource(pNIBName);
        if (uri == null) {
            throw new IllegalComponentStateException("Unable to load NIB from class path: " + pNIBName);
        }

        NSNib nib = Rococoa.create("NSNib", NSNib.class, "alloc").initWithContentsOfURL(NSURL.CLASS.URLWithString(uri.toString()));

        NSObjectByReference byRef = new NSObjectByReference();
        if (!nib.instantiateNibWithOwner_topLevelObjects(null, byRef)) {
            throw new IllegalComponentStateException("Could not instantiate NIB: " + uri);
        }

        return byRef.getValueAs(NSArray.class);
    }

    private static File toFile(final NSURL pURL) {
        // NOTE: localhost authority weirdness seems strange, but that might just be NSURL
        String url = pURL.toString();
        if (url.startsWith("file://localhost")) {
            url = url.replaceFirst("//localhost", ""); // TODO: Should use look-ahead to check next char is ':' or '/' 
        }
        return new File(URI.create(url));
    }

    static class SavePanelDelegate {
        private JFileChooser mChooser;
        private final FileSystemView mFileSystemView;
        private final NSWorkspace workspace;

        public SavePanelDelegate(final JFileChooser pChooser) {
            mChooser = pChooser;
            mFileSystemView = pChooser.getFileSystemView();
            workspace = NSWorkspace.sharedWorkspace();
        }

        // TODO: Allow changing filter, and rescan
        // validateVisibleColumns

        public boolean panel_isValidFilename(ID panel, String filename) {
//            System.err.println("OSXFileChooserDelegate$SavePanelDelegate.panel_isValidFilename: " + filename);
            return mChooser == null || mChooser.accept(new File(filename));
        }

        public boolean panel_shouldShowFilename(ID panel, String filename) {
//            System.out.println("panel: " + panel);
//            System.out.println("filename: " + filename);
//            return filename.endsWith(".txt") || new File(filename).isDirectory();

            return mChooser == null || acceptOrIsDirectory(filename);
        }

        private boolean acceptOrIsDirectory(final String filename) {
            File file = new File(filename);
            return (mFileSystemView.isTraversable(file) && (!workspace.isFilePackage(file) || treatsFilePackageAsDir(mChooser))) || mChooser.accept(file);
        }

        public void windowDidResize(NSNotification notification) {
            System.out.println("OSXFileChooserDelegate$SavePanelDelegate.windowDidResize");
        }
    }

    private static class ModalDelegate {
        private transient int result = -1;

        // TODO: Rococoa don't want to pass the pointer to the
        public void panelDidEnd_returnCode(NSSavePanel panel, int returnCode) {
            result = returnCode;

            synchronized (this) {
                notify();
            }
        }

        public int getResult() {
            if (result == -1) {
                synchronized (this) {
                    while (result == -1) {
                        try {
                            wait();
                        }
                        catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                }
            }

            return result;
        }
    }

    private static class MyPanel extends Panel {
        protected NSView mView;

        public MyPanel(final LayoutManager layout) {
            super(layout);
        }

        @Override
        public void update(Graphics g) {
//            paint(g);
            paintAll(g);
        }

        @Override
        public void removeNotify() {
            mView = null;
            super.removeNotify();
        }

        @Override
        public void addNotify() {
            super.addNotify();
            mView = Rococoa.wrap(ID.fromLong(Native.getComponentID(this)), NSView.class);
        }

        @Override
        public void repaint(final long tm, final int x, final int y, final int width, final int height) {
            // TODO: This is needed, only for components with non-opaque children...
                    super.repaint(tm, x, y, width, height);
            // NOTE: We don't need to care about tm, as it's the maximum time for a repaint, and we do it now

            // NOTE: Can't test for needsDisplay here, as there could be a different rect
            // However, it could maybe be possible to queue up repaints...
            mView.setNeedsDisplayInRect(new NSRect(x, y, width, height));
        }

    }

    static interface Conditional {
        boolean evaluate();
    }

    // Adapted from http://www.jroller.com/santhosh/entry/are_you_missing_maximize_button
    // @author Santhosh Kumar T - santhosh@in.fiorano.com
    static class EventPump {
        private final Conditional mConditional;

        final InvocationHandler mConditionalHandler = new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return mConditional.evaluate();
            }
        };

//        final InvocationHandler mFilterHandler = new InvocationHandler() {
//            final Method equalsMethod = getMethod("equals", Object.class);
//
//            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//                if (method.equals(equalsMethod)) {
//                    return Proxy.getInvocationHandler(proxy) == this;
//                }
//
//                // Else assume this is the acceptEvent mehtod
//                AWTEvent event = (AWTEvent) args[0];
//
//                // Hmmm.. Never invoked?!
//                Object source = event.getSource();
//                System.err.println("source: " + source);
//
//                Class<?> filterClass = Class.forName("java.awt.EventFilter");
//                Class<?> enumClass = filterClass.getDeclaredClasses()[0];
//                Object[] consts = enumClass.getEnumConstants();
//
//                System.out.println("OSXFileChooserDelegate$EventPump.invoke");
//                Object result = consts[1];
//                System.err.println("result: " + result);
//
//                return result;
//            }
//        };

        private Method getMethod(final String pName, Class<?>... pClasses) {
            try {
                return Object.class.getMethod(pName, pClasses);
            }
            catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        public EventPump(final Conditional pConditional, final Component pComponent) {
            mConditional = pConditional;
        }


        // The reflection calls in this method should be
        // replaced once Sun provides a public API to pump events.

        public void start() throws Exception {
            Class conditionalClass = Class.forName("java.awt.Conditional");
            Object conditional = Proxy.newProxyInstance(
                    conditionalClass.getClassLoader(),
                    new Class[]{conditionalClass},
                    mConditionalHandler
            );

//            Class filterClass = Class.forName("java.awt.EventFilter");
//            Object filter = Proxy.newProxyInstance(
//                    filterClass.getClassLoader(),
//                    new Class[]{filterClass},
//                    mFilterHandler
//            );

            //Method pumpMethod = Class.forName("java.awt.EventDispatchThread").getDeclaredMethod("pumpEventsForFilter", conditionalClass, filterClass);
            Method pumpMethod = Class.forName("java.awt.EventDispatchThread").getDeclaredMethod("pumpEvents", conditionalClass);
            pumpMethod.setAccessible(true);

            // NOTE: Current thread must be the EDT
//            pumpMethod.invoke(Thread.currentThread(), conditional, filter);
            pumpMethod.invoke(Thread.currentThread(), conditional);
        }
    }

}
