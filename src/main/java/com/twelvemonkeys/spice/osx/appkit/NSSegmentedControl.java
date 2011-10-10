package com.twelvemonkeys.spice.osx.appkit;

import com.twelvemonkeys.spice.osx.OSXImageUtil;
import org.rococoa.cocoa.CGFloat;

import java.awt.image.BufferedImage;

/**
 * NSSegmentedControl
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: NSSegmentedControl.java,v 1.0 Dec 20, 2010 10:40:04 AM haraldk Exp$
 */
public abstract class NSSegmentedControl implements NSControl {
    static public final int NSSegmentStyleAutomatic = 0,
            NSSegmentStyleRounded = 1,
            NSSegmentStyleTexturedRounded = 2,
            NSSegmentStyleRoundRect = 3,
            NSSegmentStyleTexturedSquare = 4,
            NSSegmentStyleCapsule = 5,
            NSSegmentStyleSmallSquare = 6;

    public abstract void setSegmentCount(int segments);

    public abstract int segmentCount();

    public final void setLabelForSegment(String label, int segment) {
        setLabel_forSegment(label, segment);
    }

    protected abstract void setLabel_forSegment(String label, int segment);

    public final String getLabelForSegment(int segment) {
        return labelForSegment(segment);
    }

    protected abstract String labelForSegment(int segment);

    public abstract void setSegmentStyle(int style);

    public final void setWidthForSegment(float width, int segment) {
        setWidth_forSegment(new CGFloat(width), segment);
    }

    protected abstract void setWidth_forSegment(CGFloat width, int segment);

    public final float getWidthForSegment(int segment) {
        return widthForSegment(segment).floatValue();
    }

    protected abstract CGFloat widthForSegment(int segment);

    public final void setImageForSegment(BufferedImage image, int segment) {
        NSImage nsImage = OSXImageUtil.toNSImage(image);
        // TODO: Should we always do this? Add parameter to method?
        nsImage.setTemplate(true);
        setImage_forSegment(nsImage, segment);
    }

    protected abstract void setImage_forSegment(NSImage image, int segment);

    public final BufferedImage getImageForSegment(int segment) {
        return OSXImageUtil.toBufferedImage(imageForSegment(segment));
    }

    protected abstract NSImage imageForSegment(int segment);

    public abstract NSSegmentedCell cell();

    public abstract void setSelectedSegment(int segment);
    
    public final void setSelectedForSegment(boolean selected, int segment) {
        setSelected_forSegment(selected, segment);
    }

    protected abstract void setSelected_forSegment(boolean selected, int segment);

    public abstract boolean isSelectedForSegment(int segment);
}
