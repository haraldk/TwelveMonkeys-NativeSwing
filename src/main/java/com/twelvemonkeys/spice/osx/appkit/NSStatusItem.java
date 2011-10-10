package com.twelvemonkeys.spice.osx.appkit;

import com.twelvemonkeys.spice.osx.OSXImageUtil;
import org.rococoa.NSObject;
import org.rococoa.RunOnMainThread;
import org.rococoa.cocoa.CGFloat;

import java.awt.image.BufferedImage;

/**
 * NSStatusItem
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: NSStatusItem.java,v 1.0 Dec 15, 2010 9:10:41 PM haraldk Exp$
 */
@RunOnMainThread
public abstract class NSStatusItem implements NSObject {
    public abstract NSStatusBar statusBar();

    public abstract void setTitle(String title);
    public abstract String title();

    public abstract boolean highlightMode();
    public abstract void setHighlightMode(boolean flag);

    protected abstract void setLength(CGFloat length);
    public final void setLength(float length) {
        setLength(new CGFloat(length));
    }

    protected abstract CGFloat length();
    public final float getLength() {
        return length().floatValue();
    }


    protected abstract void setImage(NSImage image);
    public final void setImage(final BufferedImage image) {
        setImage(OSXImageUtil.toNSImage(image));
    }

    protected abstract NSImage image();
    public final BufferedImage getImage() {
        return OSXImageUtil.toBufferedImage(image());
    }

    public final void setAlternateImage(BufferedImage image) {
        setAlternateImage(OSXImageUtil.toNSImage(image));
    }
    protected abstract void setAlternateImage(NSImage image);

    protected abstract NSImage alternateImage();
    public final BufferedImage getAlternateImage() {
        return OSXImageUtil.toBufferedImage(alternateImage());
    }

    public abstract void setMenu(NSMenu nsMenu);
    public abstract NSMenu menu();

    public abstract void setToolTip(String toolTip);
    public abstract String toolTip();

}
