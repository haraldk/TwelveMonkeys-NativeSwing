package com.twelvemonkeys.spice.osx.quartzcore;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByReference;

/**
* CGImageRef
*
* @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
* @author last modified by $Author: haraldk$
* @version $Id: com.twelvemonkeys.spice.osx.quartzcore.CGImageRef.java,v 1.0 Jan 17, 2010 11:15:13 PM haraldk Exp$
*/
public class CGImageRef extends ByReference {
    public CGImageRef() {
        super(Pointer.SIZE);
    }

    public CGImage getValue() {
        Pointer p = getPointer().getPointer(0);

        if (p == null) {
            return null;
        }

        CGImage h = new CGImage();
        h.setPointer(p);

        return h;
    }
}
