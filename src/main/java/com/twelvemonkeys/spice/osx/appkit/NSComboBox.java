package com.twelvemonkeys.spice.osx.appkit;

import org.rococoa.ID;
import org.rococoa.RunOnMainThread;

/**
 * NSComboBox
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: NSComboBox.java,v 1.0 Jan 11, 2009 9:21:38 PM haraldk Exp$
 */
@RunOnMainThread
public interface NSComboBox extends NSTextField {
    void addItemWithObjectValue(ID object);
}
