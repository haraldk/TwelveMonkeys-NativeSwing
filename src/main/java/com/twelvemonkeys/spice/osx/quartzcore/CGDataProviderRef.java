package com.twelvemonkeys.spice.osx.quartzcore;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByReference;

/**
 * CGDataProviderRef
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: CGDataProviderRef.java,v 1.0 Mar 6, 2010 1:04:55 PM haraldk Exp$
 */
public class CGDataProviderRef extends ByReference {
    public CGDataProviderRef() {
        super(Pointer.SIZE);
    }
}
