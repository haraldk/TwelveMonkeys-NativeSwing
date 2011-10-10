package com.twelvemonkeys.spice.osx.appkit;

import org.rococoa.NSObject;
import org.rococoa.RunOnMainThread;

/**
 * NSCell
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: NSCell.java,v 1.0 Jan 12, 2009 2:17:57 PM haraldk Exp$
 */
@RunOnMainThread
public interface NSCell extends NSObject {
    String title();
    void setTitle(String title);
}
