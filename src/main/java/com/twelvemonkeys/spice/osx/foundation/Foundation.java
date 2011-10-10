package com.twelvemonkeys.spice.osx.foundation;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByReference;

/**
 * Foundation
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: Foundation.java,v 1.0 Mar 8, 2010 2:23:18 PM haraldk Exp$
 */
public final class Foundation {
    private static FoundationLibrary INSTANCE = (FoundationLibrary) Native.loadLibrary("Foundation", FoundationLibrary.class);

    private static interface FoundationLibrary extends Library {
        void CFRelease(ByReference ref);

        int CFDataGetLength(CFDataRef ref);

        Pointer CFDataGetBytePtr(CFDataRef ref);
    }
    
    public static void CFRelease(ByReference ref) {
        INSTANCE.CFRelease(ref);
    }

    public static int CFDataGetLength(CFDataRef ref) {
        return INSTANCE.CFDataGetLength(ref);
    }

    public static Pointer CFDataGetBytePtr(CFDataRef ref) {
        return INSTANCE.CFDataGetBytePtr(ref);
    }
}
