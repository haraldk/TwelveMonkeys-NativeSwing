package com.twelvemonkeys.spice.osx.foundation;

import org.rococoa.NSClass;
import org.rococoa.Rococoa;
import org.rococoa.cocoa.foundation.NSRange;

public abstract class NSData implements org.rococoa.cocoa.foundation.NSData {
    public static final _Class CLASS = Rococoa.createClass("NSData", _Class.class);  //$NON-NLS-1$

    public interface _Class extends NSClass {
        NSData dataWithBytes_length(byte[] bytes, int length);
    }

    public abstract void getBytes_range(byte[] buffer, NSRange range);

    // TODO: Throws NSRangeException??
    public final void getBytes(byte[] buffer, int offset, int length) {
        getBytes_range(buffer, new NSRange(new CFIndex(offset), new CFIndex(length)));
    }
}