package com.twelvemonkeys.spice.osx.foundation;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByReference;

/**
* CFAllocatorRef
*
* @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
* @author last modified by $Author: haraldk$
* @version $Id: CFAllocatorRef.java,v 1.0 Mar 8, 2010 1:19:26 PM haraldk Exp$
*/
public class CFAllocatorRef extends ByReference {
    public static CFAllocatorRef DEFAULT = null; // kDefault is just a synonym for NIL

    public CFAllocatorRef() {
        super(Pointer.SIZE);
    }
}
