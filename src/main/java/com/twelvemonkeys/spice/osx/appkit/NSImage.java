package com.twelvemonkeys.spice.osx.appkit;

import com.twelvemonkeys.spice.osx.foundation.NSData;
import com.twelvemonkeys.spice.osx.foundation.NSRect;
import org.rococoa.cocoa.CGFloat;
import org.rococoa.cocoa.foundation.NSArray;
import org.rococoa.cocoa.foundation.NSDictionary;
import org.rococoa.cocoa.foundation.NSSize;

/**
 * NSImage
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: NSImage.java,v 1.0 Jan 14, 2010 9:34:10 PM haraldk Exp$
 */
public abstract class NSImage implements org.rococoa.cocoa.foundation.NSImage {
    public abstract NSImage initWithData(org.rococoa.cocoa.foundation.NSData data);
    public abstract NSImage initWithSize(NSSize size);

    public final NSImageRep bestRepresentation(NSRect rect, NSGraphicsContext referenceContext, NSDictionary hints) {
        return bestRepresentationForRect_context_hints(rect, referenceContext, hints);
    }
    protected abstract NSImageRep bestRepresentationForRect_context_hints(NSRect rect, NSGraphicsContext referenceContext, NSDictionary hints);
    public abstract NSArray representations();

    public abstract NSSize size();

    public final NSData TIFFRepresentationUsingCompression(int compression, float factor) {
        return TIFFRepresentationUsingCompression_factor(compression, factor);
    }

    protected abstract NSData TIFFRepresentationUsingCompression_factor(int pCompression, float pFactor);

    public abstract void lockFocus();
    public abstract void unlockFocus();

    public final void drawInRect(NSRect dstRect, NSRect srcRect, int compositeOperation, double fraction) {
        drawInRect_fromRect_operation_fraction(dstRect, srcRect, compositeOperation, new CGFloat(fraction));
    }

    protected abstract void drawInRect_fromRect_operation_fraction(NSRect pDstRect, NSRect pSrcRect, int pCompositeOperation, CGFloat fraction);

    public abstract void setTemplate(boolean flag);
}
