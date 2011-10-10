package com.twelvemonkeys.spice;

import com.twelvemonkeys.spice.osx.appkit.NSApplication;
import com.twelvemonkeys.spice.osx.appkit.NSMenu;
import com.twelvemonkeys.spice.osx.appkit.NSMenuItem;
import org.rococoa.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
* OSXAppMenuProxy
*
* @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
* @author last modified by $Author: haraldk$
* @version $Id: OSXAppMenuProxy.java,v 1.0 Aug 17, 2010 11:42:07 AM haraldk Exp$
*/
final class OSXAppMenuProxy extends JMenu {
    final static ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();
    final Map<JMenuItem, NSMenuItem> mSwingToNative = new WeakHashMap<JMenuItem, NSMenuItem>();

    // NOTE: We assume that the menu can never change...
    private final NSMenu mNativeAppMenu;

    public OSXAppMenuProxy() {
        NSMenu menu = NSApplication.NSApp.mainMenu();
        mNativeAppMenu = menu.itemAtIndex(0).submenu();
    }

    @Override
    public String getText() {
        if (mNativeAppMenu == null) {
            return null;
        }

        // TODO: PCL "text"...
        return mNativeAppMenu.title();
    }

    @Override
    public void setText(final String text) {
        if (mNativeAppMenu != null) {
            // TODO: PCL "text"...
            mNativeAppMenu.setTitle(text);
        }
    }

    @Override
    public boolean isTopLevelMenu() {
        return true;
    }

    @Override
    public int getItemCount() {
        return mNativeAppMenu.numberOfItems();
    }

    @Override
    public void addSeparator() {
        insertSeparator(getItemCount());
    }

    @Override
    public void insertSeparator(int index) {
        mNativeAppMenu.insertItem(NSMenuItem.separatorItem(), index);
    }

    @Override
    public void insert(String s, int pos) {
        addImpl(new JMenuItem(s), null, pos);
    }

    @Override
    public JMenuItem insert(JMenuItem mi, int pos) {
        addImpl(mi, null, pos);
        return mi;
    }

    @Override
    public JMenuItem insert(Action a, int pos) {
        // NOTE: JMenu simply inserts a new JMenuItem in this case...?!
        return insert(createActionComponent(a), pos);
    }

    @Override
    protected void addImpl(Component comp, Object constraints, int index) {
        // NOTE: String and Action is covered by this as well
        if (!(comp instanceof JMenuItem)) {
            throw new IllegalArgumentException("Only JMenuItems allowed");
        }

        // TODO: Make sure an item is not added several times

        JMenuItem swingItem = (JMenuItem) comp;

        ApplicationMenu.MenuItemActionProxy actionProxy = new ApplicationMenu.MenuItemActionProxy(swingItem);
        NSObject proxy = Rococoa.proxy(actionProxy);
//            proxy.retain();
//
//            swingItem.putClientProperty("com.twelvemonkeys.spice.ApplicationMenu.actionProxy", actionProxy);
//            swingItem.putClientProperty("com.twelvemonkeys.spice.ApplicationMenu.nativeActionProxy", proxy);
//
//            System.out.println("actionProxy: " + actionProxy);
//            System.out.println("proxy.id(): " + proxy.id());

        KeyStroke keyStroke = swingItem.getAccelerator();
        String accelerator = getAccelerator(keyStroke);

        // TODO: Weakly cache swing and native item connection and proxy
        // TODO: Add PCL to swingItem as long as it's part of this menu

        NSMenuItem item = mNativeAppMenu.insertItem(swingItem.getText(), Foundation.selector("actionPerformed:"), accelerator, index);
        item.setKeyEquivalentModifierMask(getModifier(keyStroke));
        item.setTarget(proxy.id());
        item.setEnabled(swingItem.isEnabled());

        mSwingToNative.put(swingItem, item);
    }

    private int getModifier(final KeyStroke pKeyStroke) {
        if (pKeyStroke == null) {
            return 0;
        }
        /*
         enum {
            NSAlphaShiftKeyMask         = 1 << 16,
            NSShiftKeyMask              = 1 << 17,
            NSControlKeyMask            = 1 << 18,
            NSAlternateKeyMask          = 1 << 19,
            NSCommandKeyMask            = 1 << 20,
            NSNumericPadKeyMask         = 1 << 21,
            NSHelpKeyMask               = 1 << 22,
            NSFunctionKeyMask           = 1 << 23,
        #if MAC_OS_X_VERSION_MAX_ALLOWED >= MAC_OS_X_VERSION_10_4
            NSDeviceIndependentModifierFlagsMask    = 0xffff0000UL
        #endif
        };
        */

        int swingModifiers = pKeyStroke.getModifiers();
        int nativeModifiers = 0;

        // TODO: Complete mapping...
        if ((swingModifiers & KeyEvent.SHIFT_DOWN_MASK) != 0 ) {
            nativeModifiers |= 1 << 17;
        }
        if ((swingModifiers & KeyEvent.CTRL_DOWN_MASK) != 0 ) {
            nativeModifiers |= 1 << 18;
        }
        if ((swingModifiers & KeyEvent.META_DOWN_MASK) != 0 ) {
            nativeModifiers |= 1 << 20;
        }
        if ((swingModifiers & KeyEvent.ALT_DOWN_MASK) != 0 ) {
            nativeModifiers |= 1 << 19;
        }
        if ((swingModifiers & KeyEvent.ALT_GRAPH_DOWN_MASK) != 0 ) {
            nativeModifiers |= 1 << 19;
        }

        return nativeModifiers;
    }

    private String getAccelerator(final KeyStroke pKeyStroke) {
        // TODO: Is there a better way?!
        if (pKeyStroke == null) {
            return "";
        }

        if (pKeyStroke.getKeyEventType() == KeyEvent.KEY_TYPED) {
            return String.valueOf(pKeyStroke.getKeyChar()).toLowerCase();
        }

        int keyCode = pKeyStroke.getKeyCode();

        switch (keyCode) {
            case KeyEvent.VK_AMPERSAND:
                return "&";
            case KeyEvent.VK_ASTERISK:
                return "*";
            case KeyEvent.VK_AT:
                return "@";
            case KeyEvent.VK_BACK_QUOTE:
                return "`";
            case KeyEvent.VK_BACK_SLASH:
                return "\\";
            case KeyEvent.VK_BACK_SPACE:
                return "\u0008";
            case KeyEvent.VK_BRACELEFT:
                return "{";
            case KeyEvent.VK_BRACERIGHT:
                return "}";
            case KeyEvent.VK_CIRCUMFLEX:
                return "^";
            case KeyEvent.VK_CLOSE_BRACKET:
                return "]";
            case KeyEvent.VK_COLON:
                return ":";
            case KeyEvent.VK_COMMA:
                return ",";
            case KeyEvent.VK_DELETE:
                return "\\007f";
            case KeyEvent.VK_DIVIDE:
                return "\\00f7";
            case KeyEvent.VK_DOLLAR:
                return "$";
            case KeyEvent.VK_ENTER:
                return "\n";
            case KeyEvent.VK_EQUALS:
                return "=";
            case KeyEvent.VK_EURO_SIGN:
                return "\\20ac";
            case KeyEvent.VK_EXCLAMATION_MARK:
                return "!";
            case KeyEvent.VK_GREATER:
                return ">";
            case KeyEvent.VK_INVERTED_EXCLAMATION_MARK:
                return "\\00a1";
            case KeyEvent.VK_LEFT_PARENTHESIS:
                return "(";
            case KeyEvent.VK_LESS:
                return "<";
            case KeyEvent.VK_MINUS:
                return "-";
            case KeyEvent.VK_MULTIPLY:
                return "\\00d7";
            case KeyEvent.VK_OPEN_BRACKET:
                return "[";
            case KeyEvent.VK_PERIOD:
                return ".";
            case KeyEvent.VK_PLUS:
                return "+";
            case KeyEvent.VK_QUOTE:
                return "'";
            case KeyEvent.VK_QUOTEDBL:
                return "\"";
            case KeyEvent.VK_RIGHT_PARENTHESIS:
                return ")";
            case KeyEvent.VK_SEMICOLON:
                return ";";
            case KeyEvent.VK_SLASH:
                return "/";
            case KeyEvent.VK_SPACE:
                return " ";
            case KeyEvent.VK_SUBTRACT:
                return "-"; // TODO?!
            case KeyEvent.VK_TAB:
                return "\t";
            case KeyEvent.VK_UNDERSCORE:
                return "_";
            default:
                // Fall through
        }

        int expected_modifiers = (Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL);

        Field[] fields = KeyEvent.class.getDeclaredFields();

        for (Field field : fields) {
            try {
                if (field.getModifiers() == expected_modifiers
                        && field.getType() == Integer.TYPE
                        && field.getName().startsWith("VK_")
                        && field.getInt(KeyEvent.class) == keyCode) {
                    String name = field.getName();

                    return name.substring(3).toLowerCase();
                }
            }
            catch (IllegalAccessException e) {
                assert (false);
            }
        }

        return "";
    }

    private static KeyStroke getKeyStroke(NSMenuItem item) {
        String keyEquivalent = item.keyEquivalent();

        if (keyEquivalent == null || "".equals(keyEquivalent)) {
            return null;
        }

        long mask = item.keyEquivalentModifierMask();
        /*
         enum {
            NSAlphaShiftKeyMask         = 1 << 16,
            NSShiftKeyMask              = 1 << 17,
            NSControlKeyMask            = 1 << 18,
            NSAlternateKeyMask          = 1 << 19,
            NSCommandKeyMask            = 1 << 20,
            NSNumericPadKeyMask         = 1 << 21,
            NSHelpKeyMask               = 1 << 22,
            NSFunctionKeyMask           = 1 << 23,
        #if MAC_OS_X_VERSION_MAX_ALLOWED >= MAC_OS_X_VERSION_10_4
            NSDeviceIndependentModifierFlagsMask    = 0xffff0000UL
        #endif
        };
        */
        int modifier = 0;
        if ((mask & (1 << 17)) != 0) {
            modifier |= KeyEvent.SHIFT_DOWN_MASK;
        }
        if ((mask & (1 << 18)) != 0) {
            modifier |= KeyEvent.CTRL_DOWN_MASK;
        }
        if ((mask & (1 << 19)) != 0) {
            modifier |= KeyEvent.ALT_DOWN_MASK;
        }
        if ((mask & (1 << 20)) != 0) {
            modifier |= KeyEvent.META_DOWN_MASK;
        }

        // TODO: Map to KeyEvent.VK_...
        // Currently it works by accident (because ASCII and VK_.. corresponds for alphanumeric chars) :-P
        int code = Character.toUpperCase(keyEquivalent.charAt(0));
        return KeyStroke.getKeyStroke(code, modifier);
    }

    @Override
    public JMenuItem getItem(int pos) {
        final NSMenuItem item = mNativeAppMenu.itemAtIndex(pos);

        if (item.isSeparatorItem()) {
            return null;
        }

        // Swing originating items should be in the map
        for (Map.Entry<JMenuItem, NSMenuItem> entry : mSwingToNative.entrySet()) {
            if (entry.getValue().id().equals(item.id())) {
                return entry.getKey();
            }
        }

        // Otherwise, create proxy
        JMenuItem swingItem = new NativeItemProxy(item);
        mSwingToNative.put(swingItem, item);

        return swingItem;
    }

    @Override
    public void remove(final JMenuItem item) {
        NSMenuItem nativeItem = mSwingToNative.get(item);
        mNativeAppMenu.removeItem(nativeItem);
    }

    @Override
    public void remove(int pos) {
        mNativeAppMenu.removeItemAtIndex(pos);
    }

    @Override
    public void remove(Component c) {
        if (c instanceof JMenuItem) {
            JMenuItem item = (JMenuItem) c;
            remove(item);
        }
    }

    @Override
    public void removeAll() {
        // TODO: Additional cleanup
        mNativeAppMenu.removeAllItems();
    }

    private static class NativeItemProxy extends JMenuItem {
        private final NSMenuItem mItem;

        public NativeItemProxy(final NSMenuItem pItem) {
            super(pItem.title());
            mItem = pItem;
            // Install the action proxy now, this alters execution a bit,
            // but will produce the same effect in the end
            // (native -> java proxy -> original native target)
            setAction(new NativeActionProxy(mItem));

            addPropertyChangeListener(new PropertyChangeListener() {
                public void propertyChange(final PropertyChangeEvent evt) {
                    if ("text".equals(evt.getPropertyName())) {
                        mItem.setTitle((String) evt.getNewValue());
                    }
                }
            });
        }

        @Override
        public void setAction(final Action a) {
            super.setAction(a); // Side effect: configures title/acccelerator etc.

            ApplicationMenu.MenuItemActionProxy actionProxy = new ApplicationMenu.MenuItemActionProxy(this);
            NSObject proxy = Rococoa.proxy(actionProxy);
//                proxy.retain();
//                putClientProperty("com.twelvemonkeys.spice.ApplicationMenu.B.actionProxy", actionProxy);
//                putClientProperty("com.twelvemonkeys.spice.ApplicationMenu.B.nativeActionProxy", proxy);

            mItem.setAction(Foundation.selector("actionPerformed:"));
            System.out.println("a: " + a);
            System.out.println("proxy.id(): " + proxy.id());
            mItem.setTarget(proxy.id());
        }
    }

    private static class NativeActionProxy extends AbstractAction {
        private final ID mTarget;
        private final Selector mSelector;

        public NativeActionProxy(final NSMenuItem item) {
            super(item.title());

            // TODO: Expand proxy action
            mSelector = item.action();
            mTarget = item.target();

            putValue(Action.ACCELERATOR_KEY, getKeyStroke(item));
            setEnabled(item.isEnabled());
        }

        public void actionPerformed(final ActionEvent e) {
            EXECUTOR.execute(new Runnable() {
                public void run() {
                    // NOTE: Must use send(ID, Selector, Class, Object...) here,
                    // as the returned selector does not have a name set
                    Foundation.callOnMainThread(new Callable<Object>() {
                        public Object call() throws Exception {
                            return Foundation.send(mTarget, mSelector, Void.class);
                        }
                    });
                }
            });
        }
    }
}
