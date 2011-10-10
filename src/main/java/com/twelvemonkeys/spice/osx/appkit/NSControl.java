package com.twelvemonkeys.spice.osx.appkit;

import com.twelvemonkeys.spice.osx.foundation.NSRect;
import org.rococoa.ID;
import org.rococoa.RunOnMainThread;
import org.rococoa.Selector;

/**
 * NSControl
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: NSControl.java,v 1.0 Jan 11, 2009 9:23:03 PM haraldk Exp$
 */
@RunOnMainThread
public interface NSControl extends NSView {
    NSControl initWithFrame(NSRect frameRect);
    
    boolean isEnabled();
    void setEnabled(boolean enabled);

    void sizeToFit();

    NSCell cell();
    void setCell(NSCell cell);
    

    Selector action();
    void setAction(Selector action);
    
    ID target();
    void setTarget(ID target);
}
