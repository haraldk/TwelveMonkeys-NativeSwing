package com.twelvemonkeys.spice.osx.appkit;

import org.rococoa.ID;
import org.rococoa.cocoa.foundation.NSDictionary;

/**
 * NSNotification
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: NSNotification.java,v 1.0 Jan 14, 2010 9:36:17 PM haraldk Exp$
 */
public interface NSNotification extends org.rococoa.cocoa.foundation.NSNotification {
    String name();

    NSDictionary userInfo();

    ID object();
}
