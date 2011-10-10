package com.twelvemonkeys.spice.osx.appkit;

import org.rococoa.*;
import org.rococoa.cocoa.foundation.NSUInteger;

/**
 * NSMenuItem
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: NSMenuItem.java,v 1.0 Apr 15, 2008 1:26:36 PM haraldk Exp$
 */
@RunOnMainThread
public abstract class NSMenuItem implements NSObject {
    private static final _Class CLASS = Rococoa.createClass("NSMenuItem", _Class.class);

    public abstract Selector action();

    public abstract void performSelector(Selector selector);

    public abstract void setAction(Selector selector);

    @RunOnMainThread
    private static interface _Class extends NSClass {
        NSMenuItem separatorItem();
    }

    abstract NSMenuItem initWithTitle_action_keyEquivalent(String title, Selector action, String keyCode);
    public final NSMenuItem init(String title, Selector action, String keyCode) {
        return initWithTitle_action_keyEquivalent(title, action, keyCode);
    }
//    abstract NSMenuItem initWithTitle_action_keyEquivalent(String title, ID action, String keyCode);
//    public final NSMenuItem init(String title, ID action, String keyCode) {
//        return initWithTitle_action_keyEquivalent(title, action, keyCode);
//    }

    public static NSMenuItem separatorItem() {
        return CLASS.separatorItem();
    }

    public abstract boolean isEnabled();
    public abstract void setEnabled(boolean enabled);

    public abstract String title();
    public abstract void setTitle(String title);

    public abstract NSMenu submenu();
    public abstract void setSubmenu(NSMenu menu);

    public abstract void setTarget(ID target);
    public abstract ID target();

    public abstract void setKeyEquivalent(String pKey);
    public abstract String keyEquivalent();

    public abstract void setKeyEquivalentModifierMask(NSUInteger pMask);
    public final void setKeyEquivalentModifierMask(long pMask) {
        setKeyEquivalentModifierMask(new NSUInteger(pMask));
    }
    public abstract long keyEquivalentModifierMask();

    public abstract boolean isSeparatorItem();
}
