package com.twelvemonkeys.spice.osx.appkit;

import com.twelvemonkeys.spice.osx.quartzcore.CGImageRef;
import com.twelvemonkeys.spice.osx.foundation.NSData;
import org.rococoa.NSClass;
import org.rococoa.Rococoa;

/**
 * NSBitmapImageRep
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: NSBitmapImageRep.java,v 1.0 Jan 16, 2010 3:04:29 PM haraldk Exp$
 */
public abstract class NSBitmapImageRep extends NSImageRep {
    public static final _Class CLASS = Rococoa.createClass("NSBitmapImageRep", _Class.class);

    public interface _Class extends NSClass {
    }

    public abstract NSData TIFFRepresentation();
    
    public final NSData TIFFRepresentationUsingCompression(int compression, float factor) {
        return TIFFRepresentationUsingCompression_factor(compression, factor);
    }

    protected abstract NSData TIFFRepresentationUsingCompression_factor(int compression, float factor);

    public abstract NSBitmapImageRep initWithCGImage(CGImageRef image);

    public static NSBitmapImageRep createFromCGImage(CGImageRef ref) {
        return Rococoa.create("NSBitmapImageRep", NSBitmapImageRep.class, "alloc").initWithCGImage(ref);
    }
}
