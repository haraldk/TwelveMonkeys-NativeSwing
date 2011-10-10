package com.twelvemonkeys.spice.osx.foundation;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByReference;
import org.rococoa.Rococoa;

/**
 * CFDataRef
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: CFDataRef.java,v 1.0 Mar 8, 2010 12:33:40 PM haraldk Exp$
 */
public class CFDataRef extends ByReference {
    public CFDataRef() {
        super(Pointer.SIZE);
    }

    public final NSData getValue() {
        return Rococoa.create("NSData", NSData.class, "dataWithData:", this);
    }

    public final void setValue(final NSData data) {
        setPointer(data != null ? Pointer.createConstant(data.id().longValue()) : null);
    }
}
