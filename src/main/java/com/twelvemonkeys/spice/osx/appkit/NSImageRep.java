package com.twelvemonkeys.spice.osx.appkit;

import org.rococoa.NSClass;
import org.rococoa.NSObject;
import org.rococoa.Rococoa;
import org.rococoa.cocoa.foundation.NSSize;

/**
 * NSImageRep
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: NSImageRep.java,v 1.0 Jan 16, 2010 2:59:39 PM haraldk Exp$
 */
public abstract class NSImageRep implements NSObject {
    public static final _Class CLASS = Rococoa.createClass("NSImageRep", _Class.class);

    private interface _Class extends NSClass {
    }

    public abstract NSSize size();
}
