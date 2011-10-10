package com.twelvemonkeys.spice.osx.quartzcore;

import com.sun.jna.Native;
import com.twelvemonkeys.spice.osx.foundation.CFDataRef;

/**
 * QuartzCore
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: QuartzCore.java,v 1.0 Mar 8, 2010 1:06:58 PM haraldk Exp$
 */
public final class QuartzCore {
    private static final QCLibrary INSTANCE = (QCLibrary) Native.loadLibrary("QuartzCore", QCLibrary.class);

    public static CGDataProviderRef CGImageGetDataProvider(CGImageRef image) {
        return INSTANCE.CGImageGetDataProvider(image);
    }

    public static CFDataRef CGDataProviderCopyData(CGDataProviderRef provider) {
        return INSTANCE.CGDataProviderCopyData(provider);
    }

    public static int CGImageGetWidth(CGImageRef ref) {
        return INSTANCE.CGImageGetWidth(ref);
    }

    public static int CGImageGetHeight(CGImageRef ref) {
        return INSTANCE.CGImageGetHeight(ref);
    }

    public static int CGImageGetBytesPerRow(CGImageRef ref) {
        return INSTANCE.CGImageGetBytesPerRow(ref);
    }

    public static int CGImageGetBitmapInfo(CGImageRef ref) {
        return INSTANCE.CGImageGetBitmapInfo(ref);
    }

    public static int CGImageGetBitsPerPixel(CGImageRef ref) {
        return INSTANCE.CGImageGetBitsPerPixel(ref);
    }
}
