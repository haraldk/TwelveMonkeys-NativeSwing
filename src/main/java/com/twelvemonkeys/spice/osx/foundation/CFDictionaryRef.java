package com.twelvemonkeys.spice.osx.foundation;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByReference;
import org.rococoa.cocoa.foundation.NSDictionary;

/**
* CFDictionaryRef
*
* @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
* @author last modified by $Author: haraldk$
* @version $Id: CFDictionaryRef.java,v 1.0 Mar 8, 2010 1:20:04 PM haraldk Exp$
*/
public class CFDictionaryRef extends ByReference {
    public CFDictionaryRef() {
        super(Pointer.SIZE);
    }

    public CFDictionaryRef(final NSDictionary dict) {
        this();
        setPointer(Pointer.createConstant(dict.id().longValue()));
    }
}
