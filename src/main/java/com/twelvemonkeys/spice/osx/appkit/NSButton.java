package com.twelvemonkeys.spice.osx.appkit;

import com.twelvemonkeys.spice.osx.OSXImageUtil;
import org.rococoa.RunOnMainThread;

import java.awt.image.BufferedImage;

/**
 * NSButton
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: NSButton.java,v 1.0 Jan 11, 2009 10:34:32 PM haraldk Exp$
 */
@RunOnMainThread
public abstract class NSButton implements NSControl {
    public abstract String title();
    public abstract void setTitle(String title);

    public abstract void setBezelStyle(int style);

    public final void setImage(BufferedImage image) {
        NSImage nsImage = OSXImageUtil.toNSImage(image);
        nsImage.setTemplate(true);
        setImage(nsImage);
    }

    protected abstract void setImage(NSImage image);
}
