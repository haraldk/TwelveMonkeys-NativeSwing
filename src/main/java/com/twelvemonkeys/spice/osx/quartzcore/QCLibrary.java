package com.twelvemonkeys.spice.osx.quartzcore;

import com.sun.jna.Library;
import com.twelvemonkeys.spice.osx.foundation.CFDataRef;

/**
 * QCLibrary
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: QCLibrary.java,v 1.0 Mar 6, 2010 1:01:35 PM haraldk Exp$
 */
interface QCLibrary extends Library {
    CGDataProviderRef CGImageGetDataProvider(CGImageRef image);

    CFDataRef CGDataProviderCopyData(CGDataProviderRef provider);

    int CGImageGetWidth(CGImageRef ref);

    int CGImageGetHeight(CGImageRef ref);

    int CGImageGetBytesPerRow(CGImageRef ref);

    int CGImageGetBitmapInfo(CGImageRef ref);

    int CGImageGetBitsPerPixel(CGImageRef ref);
}
