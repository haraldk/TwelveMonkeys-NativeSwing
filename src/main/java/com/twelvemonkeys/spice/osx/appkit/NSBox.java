package com.twelvemonkeys.spice.osx.appkit;

import org.rococoa.RunOnMainThread;

/**
 * NSBox
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: NSBox.java,v 1.0 Jan 12, 2009 12:04:28 AM haraldk Exp$
 */
@RunOnMainThread
public interface NSBox extends NSView {
    String title();
    void setTitle(String title);
}
