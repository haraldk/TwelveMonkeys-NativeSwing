package com.twelvemonkeys.spice.osx.foundation;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByReference;
import org.rococoa.cocoa.foundation.NSURL;

/**
* CFURLRef
*
* @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
* @author last modified by $Author: haraldk$
* @version $Id: CFURLRef.java,v 1.0 Mar 8, 2010 1:19:53 PM haraldk Exp$
*/
public class CFURLRef extends ByReference {
    public CFURLRef() {
        super(Pointer.SIZE);
    }


    public CFURLRef(final NSURL url) {
        this();
        setPointer(Pointer.createConstant(url.id().longValue()));
    }
}
