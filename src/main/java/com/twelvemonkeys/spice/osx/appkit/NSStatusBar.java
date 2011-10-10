package com.twelvemonkeys.spice.osx.appkit;

import org.rococoa.NSClass;
import org.rococoa.NSObject;
import org.rococoa.Rococoa;
import org.rococoa.RunOnMainThread;
import org.rococoa.cocoa.CGFloat;

/**
 * NSStatusBar
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: NSStatusBar.java,v 1.0 Dec 15, 2010 9:00:20 PM haraldk Exp$
 */
@RunOnMainThread
public abstract class NSStatusBar implements NSObject {

    public static final float NSVariableStatusItemLength = -1;
    public static final float NSSquareStatusItemLength = -2;

    public static final _Class CLASS = Rococoa.createClass("NSStatusBar", _Class.class);  //$NON-NLS-1$

    @RunOnMainThread
    private interface _Class extends NSClass {

        NSStatusBar systemStatusBar();
    }

    public static NSStatusBar systemStatusBar() {
        return CLASS.systemStatusBar();
    }

    protected abstract NSStatusItem statusItemWithLength(CGFloat length);

    public final NSStatusItem statusItemWithLength(float length) {
        return statusItemWithLength(new CGFloat(length));
    }

    public abstract void removeStatusItem(NSStatusItem item);

    public abstract CGFloat thickness();
}
