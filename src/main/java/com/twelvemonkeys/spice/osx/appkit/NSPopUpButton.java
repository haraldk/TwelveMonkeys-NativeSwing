package com.twelvemonkeys.spice.osx.appkit;

import com.twelvemonkeys.spice.osx.foundation.NSRect;
import org.rococoa.ID;
import org.rococoa.RunOnMainThread;

/**
 * NSPopUpButton
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: NSPopUpButton.java,v 1.0 Jan 12, 2009 12:23:21 AM haraldk Exp$
 */
@RunOnMainThread
public interface NSPopUpButton extends NSControl {
    ID initWithFrame_pullsDown(NSRect frameRect, boolean pullsDown);

    void addItemWithTitle(String title);

    boolean pullsDown();
    void setPullsDown(boolean pullsDown);

    NSMenu menu();

    int indexOfSelectedItem();
    void selectItemAtIndex(int index);

    void removeAllItems();

    NSMenuItem itemAtIndex(int pIndex);
}
