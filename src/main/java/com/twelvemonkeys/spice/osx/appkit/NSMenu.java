package com.twelvemonkeys.spice.osx.appkit;

import org.rococoa.*;

/**
 * NSMenu
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: NSMenu.java,v 1.0 Apr 15, 2008 12:56:57 PM haraldk Exp$
 */
@RunOnMainThread
public abstract class NSMenu implements NSObject {

    private static final _Class CLASS = Rococoa.createClass("NSMenu", _Class.class);

    @RunOnMainThread
    private static interface _Class extends NSClass {
        boolean menuBarVisible();
        void setMenuBarVisible(boolean visible);
    }

    public static NSMenu init(String title) {
        return Rococoa.create("NSMenu", NSMenu.class).initWithTitle(title);
    }

    protected abstract NSMenu initWithTitle(String title);

    public static boolean menuBarVisible() {
        return CLASS.menuBarVisible();
    }
    public static void setMenuBarVisible(boolean visible) {
        CLASS.setMenuBarVisible(visible);
    }

    public abstract void setDelegate(ID delegate);
    public abstract ID delegate();

    public abstract String title();
    public abstract void setTitle(String title);

    public abstract int menuBarHeight();

    public abstract int numberOfItems();
    
    public abstract int indexOfItemWithTitle(String title);

    public abstract NSMenuItem itemAtIndex(int index);

    abstract void insertItem_atIndex(NSMenuItem item, int index);
    public final void insertItem(NSMenuItem item, int index) {
        insertItem_atIndex(item, index);
    }
    abstract NSMenuItem insertItemWithTitle_action_keyEquivalent_atIndex(String title, Selector action, String keyCode, int index);
    public final NSMenuItem insertItem(String title, Selector action, String keyCode, int index) {
        return insertItemWithTitle_action_keyEquivalent_atIndex(title, action, keyCode, index);
    }
//    abstract NSMenuItem insertItemWithTitle_action_keyEquivalent_atIndex(String title, ID action, String keyCode, int index);
//    public final NSMenuItem insertItem(String title, ID action, String keyCode, int index) {
//        return insertItemWithTitle_action_keyEquivalent_atIndex(title, action, keyCode, index);
//    }

    public abstract void addItem(NSMenuItem item);
    abstract NSMenuItem addItemWithTitle_action_keyEquivalent(String title, Selector action, String keyCode);
    public final NSMenuItem addItem(String title, Selector action, String keyCode) {
        return addItemWithTitle_action_keyEquivalent(title, action, keyCode);
    }
//    abstract NSMenuItem addItemWithTitle_action_keyEquivalent(String title, ID action, String keyCode);
//    public final NSMenuItem addItem(String title, ID action, String keyCode) {
//        return addItemWithTitle_action_keyEquivalent(title, action, keyCode);
//    }

    public abstract void removeItemAtIndex(int index);
    public abstract void removeItem(NSMenuItem item);
    public abstract void removeAllItems();
    
    public abstract void update();
    
    public abstract void setAutoenablesItems(boolean auto);
    public abstract boolean autoenablesItems();

}
