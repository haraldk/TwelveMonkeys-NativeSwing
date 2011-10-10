package com.twelvemonkeys.spice.osx.appkit;

import com.twelvemonkeys.spice.osx.foundation.NSPoint;
import com.twelvemonkeys.spice.osx.foundation.NSRect;
import org.rococoa.RunOnMainThread;
import org.rococoa.cocoa.foundation.NSArray;
import org.rococoa.cocoa.foundation.NSInteger;
import org.rococoa.cocoa.foundation.NSSize;
import org.rococoa.cocoa.foundation.NSUInteger;

/**
 * NSView
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: NSView.java,v 1.0 Mar 23, 2008 3:23:47 PM haraldk Exp$
 */
@RunOnMainThread
public interface NSView extends NSResponder {
    NSView initWithFrame(NSRect frameRect);

    void addSubview(NSView view);

    NSRect frame();
    void setFrame(NSRect rect);
    void setFrameOrigin(NSPoint point);
    void setFrameSize(NSSize size);

    NSRect bounds();
    void setBounds(NSRect rect);

    void setNeedsDisplay(boolean b);
    void setNeedsDisplayInRect(NSRect rect);
    boolean needsDisplay();

    void display();

    NSView superview();
    NSArray subviews();

    void setHidden(boolean hidden);
//    void setHidden(int hidden);
    boolean isHidden();

    void setPostsFrameChangedNotifications(boolean flag);
    boolean postsFrameChangedNotifications();

    NSView viewWithTag(NSInteger tag);

    NSWindow window();

    boolean isOpaque();

    void setAutoresizesSubviews(boolean resize);
    boolean autoresizesSubviews();

    void setAutoresizingMask(NSUInteger mask);
    int autoresizingMask();
    void setAutoresizingMask(int mask);

    int NSViewNotSizable = 0;
    int NSViewMinXMargin = 1;
    int NSViewWidthSizable = 2;
    int NSViewMaxXMargin = 4;
    int NSViewMinYMargin = 8;
    int NSViewHeightSizable = 16;
    int NSViewMaxYMargin = 32;

}
